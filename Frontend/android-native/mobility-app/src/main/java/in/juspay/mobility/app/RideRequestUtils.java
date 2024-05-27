/*
 *  Copyright 2022-23, Juspay India Pvt Ltd
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 *  is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of
 *  the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package in.juspay.mobility.app;
import static android.graphics.Color.rgb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import in.juspay.hyper.core.ExecutorManager;
import in.juspay.mobility.app.RemoteConfigs.MobilityRemoteConfigs;
import in.juspay.mobility.app.dataModel.VariantConfig;
import in.juspay.mobility.common.services.TLSSocketFactory;
import static in.juspay.mobility.common.MobilityCommonBridge.isServiceRunning;

public class RideRequestUtils {
    private final static int rideReqNotificationId = 5032023;
    private final static String RIDE_REQUEST_CHANNEL = "in.juspay.mobility.riderequest";
    private final static int rideReqNotificationReqCode = 6032023;
    private static final String LOG_TAG = "RideRequestUtils";
    private static final String KOLKATA = "kolkata";
    private static final String KOCHI = "kochi";
    private static final MobilityRemoteConfigs remoteConfigs = new MobilityRemoteConfigs(false, true);

    public static Boolean driverRespondApi(String searchRequestId, double offeredPrice, boolean isAccept, Context context, int slotNumber) {
        Handler mainLooper = new Handler(Looper.getMainLooper());
        StringBuilder result = new StringBuilder();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String bundle_version = sharedPref.getString("BUNDLE_VERSION", "null");
        String version = sharedPref.getString("VERSION_NAME", "null");
        String deviceDetails = sharedPref.getString("DEVICE_DETAILS", "null");
        try {
            String orderUrl = sharedPref.getString("BASE_URL", "null") + "/driver/searchRequest/quote/respond";
            HttpURLConnection connection = (HttpURLConnection) (new URL(orderUrl).openConnection());
            if (connection instanceof HttpsURLConnection)
                ((HttpsURLConnection) connection).setSSLSocketFactory(new TLSSocketFactory());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-client-version", version);
            connection.setRequestProperty("token", sharedPref.getString(context.getResources().getString(R.string.REGISTERATION_TOKEN), "null"));
            connection.setRequestProperty("x-bundle-version", bundle_version);
            connection.setRequestProperty("x-device", deviceDetails);
            connection.setDoOutput(true);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            JSONObject payload = new JSONObject();
            if (!isAccept || offeredPrice == 0) {
                payload.put(context.getResources().getString(R.string.OFFERED_FARE), null);
            } else {
                payload.put(context.getResources().getString(R.string.OFFERED_FARE), (offeredPrice));
            }
            payload.put(context.getResources().getString(R.string.SEARCH_REQUEST_ID), searchRequestId);
            if (isAccept) payload.put("response", "Accept");
            else payload.put("response", "Reject");
            payload.put("slotNumber", slotNumber);
            OutputStream stream = connection.getOutputStream();
            stream.write(payload.toString().getBytes());
            connection.connect();
            int respCode = connection.getResponseCode();
            InputStreamReader respReader;
            if ((respCode < 200 || respCode >= 300) && respCode != 302) {
                respReader = new InputStreamReader(connection.getErrorStream());
                BufferedReader in = new BufferedReader(respReader);
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                JSONObject errorPayload = new JSONObject(result.toString());
                if (errorPayload.has(context.getResources().getString(R.string.ERROR_MESSAGE))) {
                    mainLooper.post(() -> {
                        try {
                            Toast.makeText(context.getApplicationContext(), errorPayload.getString(context.getResources().getString(R.string.ERROR_MESSAGE)), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } else {
                //API Success
                return true;
            }
            return false;
        } catch (SocketTimeoutException e) {
            mainLooper.post(() -> Toast.makeText(context.getApplicationContext(), "Request Timeout", Toast.LENGTH_SHORT).show());
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void updateViewFromMlTranslation(SheetAdapter.SheetViewHolder holder, SheetModel model, SharedPreferences sharedPref, Context context){
        String lang = sharedPref.getString( "LANGUAGE_KEY", "ENGLISH");
        TranslatorMLKit translate = new TranslatorMLKit("en", lang, context);
        translate.translateStringInTextView(removeCommas(model.getSourceArea()), holder.sourceArea);
        translate.translateStringInTextView(model.getSourceAddress(),  holder.sourceAddress);
        translate.translateStringInTextView(removeCommas(model.getDestinationArea()), holder.destinationArea);
        translate.translateStringInTextView(model.getDestinationAddress(),  holder.destinationAddress);
    }

    public static String removeCommas(String input) {
        String str = input;
        input = input.trim();
        input = input.replaceAll(",+\\s*$", "");
        if (str.trim().endsWith(",")) {
            input += " ,";
        }
        return input;
    }


    public static int calculateExpireTimer(String expireTimeTemp, String currTimeTemp) {
        if (expireTimeTemp == null || currTimeTemp == null) return 0;
        String[] arrOfA = expireTimeTemp.split("T");
        String[] arrOfB = currTimeTemp.split("T");
        if (!arrOfA[0].equals(arrOfB[0])) return -1;

        String[] timeTempExpire = arrOfA[1].split(":");
        String[] timeTempCurrent = arrOfB[1].split(":");
        timeTempExpire[2] = timeTempExpire[2].substring(0, 2);
        timeTempCurrent[2] = timeTempCurrent[2].substring(0, 2);
        int currTime = 0, expireTime = 0, calculate = 3600;
        for (int i = 0; i < timeTempCurrent.length; i++) {
            currTime += (Integer.parseInt(timeTempCurrent[i]) * calculate);
            expireTime += (Integer.parseInt(timeTempExpire[i]) * calculate);
            calculate = calculate / 60;
        }
        return Math.max((expireTime - currTime), 0);
    }
    public static int timeDifferenceInMinutes(Long expireTimeTemp, Long currTimeTemp){
        return (int) (((expireTimeTemp-currTimeTemp)/1000)/60);
    }

    public static void createRideRequestNotification(Context context) {
        long[] vibrationPattern = {1000, 1000, 1000, 800, 800, 800, 800, 800, 800, 800, 800, 800};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(RIDE_REQUEST_CHANNEL, "RideRequestChannel", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(vibrationPattern);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setGroup("2_ride_related");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent;
        if (RideRequestActivity.getInstance() == null) {
            notificationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        } else {
            notificationIntent = new Intent(context, RideRequestActivity.class);
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, rideReqNotificationReqCode, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, RIDE_REQUEST_CHANNEL);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        mBuilder.setContentTitle(context.getString(R.string.new_ride_req))
                .setContentText(context.getString(R.string.new_ride_available_for_offering))
                .setSmallIcon(Utils.getResIdentifier(context, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? "ic_launcher_small_icon" : "ny_ic_launcher", (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? "drawable" : "mipmap"))
                .setAutoCancel(true)
                .setVibrate(vibrationPattern)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "no notification permission");
        }
        managerCompat.notify(rideReqNotificationId, mBuilder.build());
    }


    public static void cancelRideReqNotification(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(ns);
        notificationManager.cancel(rideReqNotificationId);
    }

    public static void firebaseLogEventWithParams(String event, String paramKey, String paramValue, Context context) {
        Bundle params = new Bundle();
        params.putString(paramKey, paramValue);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.logEvent(event, params);
    }

    public static String getPinCodeFromRR(double latitude, double longitude, Context context) {
        try {
            Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if ( geocoder.isPresent() && addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return matchRegex(address.getAddressLine(0), "\\b\\d{6}\\b");
            } else {
                return null;
            }
        } catch (Exception e) {
            Exception exception = new Exception("Error in FetchingPinCode " + e);
            FirebaseCrashlytics.getInstance().recordException(exception);
            e.printStackTrace();
            return null;
        }
    };

    private static String matchRegex(String input, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }
    public static void restartLocationService(Context context, String title) {
        Intent locationService = new Intent(context, LocationUpdateService.class);
        if (title.equals("You were inactive")) locationService.putExtra("StartingSource", "TRIGGER_SERVICE_INACTIVE");
        locationService.putExtra("StartingSource", "TRIGGER_SERVICE");
        locationService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(locationService);
            else
                context.startService(locationService);

        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage());
        }
        Intent restartIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String activityStatus = sharedPrefs.getString("ACTIVITY_STATUS", "null");
        if(Settings.canDrawOverlays(context) && activityStatus.equals("onDestroy")){
            try{
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    context.startActivity(restartIntent);
                    Utils.minimizeApp(context);
                }, 5000);
            } catch (Exception e) {
                Log.e("BootUpReceiver", "Unable to Start Widget Service");
            }
        }
    }

    public static void callAPIViaFCM(String orderUrl, JSONObject requestBody, String method, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString("REGISTERATION_TOKEN", "null");
        String deviceDetails = sharedPref.getString("DEVICE_DETAILS", "null");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            StringBuilder result = new StringBuilder();
            try {
                System.out.println("in callAPIViaFCM");
                HttpURLConnection connection = (HttpURLConnection) (new URL(orderUrl).openConnection());
                if (connection instanceof HttpsURLConnection)
                    ((HttpsURLConnection) connection).setSSLSocketFactory(new TLSSocketFactory());
                connection.setRequestMethod(method);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", token);
                connection.setRequestProperty("x-device", deviceDetails);
                connection.setDoOutput(true);

                OutputStream stream = connection.getOutputStream();
                if (requestBody != null) {
                    stream.write(requestBody.toString().getBytes());
                }
                connection.connect();
                int respCode = connection.getResponseCode();
                InputStreamReader respReader;

                if ((respCode < 200 || respCode >= 300) && respCode != 302) {
                    respReader = new InputStreamReader(connection.getErrorStream());
                    firebaseLogEventWithParams("ny_fcm_error_calling_api", "status_code", String.valueOf(respCode), context);
                    System.out.println("in error : " + respReader);
                } else {
                    respReader = new InputStreamReader(connection.getInputStream());
                    firebaseLogEventWithParams("ny_fcm_success_calling_api", "status_code", String.valueOf(respCode), context);
                    System.out.println("in 200 : " + respReader);
                }

                BufferedReader in = new BufferedReader(respReader);
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                Log.i(LOG_TAG, "in result : " + result);

            } catch (Exception e) {
                Log.i(LOG_TAG, "Catch in callAPIViaFCM : " + e);
            }
            handler.post(executor::shutdown);
        });
    }

    public static void openApplication(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception in openApplication");
        }
    }


    public static  JSONObject getZoneConfig(String tag, Context context){
        try {
            String key;
            String[] arrOfStr = tag.split("_");
            String pickup = arrOfStr[0];
            String drop = arrOfStr[1];
            String priority = arrOfStr[2];
            if (priority.equals("PriorityPickup")) {
                key = pickup + "_Pickup";
            } else if (priority.equals("PriorityDrop")){
                key = drop + "_Drop";
            } else {
                return new JSONObject();
            }
            InputStream is = context.getAssets().open("juspay/zone_config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            return new JSONObject(json).getJSONObject(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new JSONObject();
        }
    }

    public static void setSpecialZoneAttrs(SheetAdapter.SheetViewHolder holder, String specialLocationTag, Context context) {
        try{
            JSONObject zoneConfig = getZoneConfig(specialLocationTag,context);
            holder.assetZonePickup.setImageURI(Uri.parse("android.resource://"+ context.getPackageName() +"/drawable/"+ zoneConfig.get("imageUrl")));
            holder.assetZoneDrop.setImageURI(Uri.parse("android.resource://"+ context.getPackageName() +"/drawable/"+ zoneConfig.get("imageUrl")));
            holder.assetZonePickup.setVisibility(zoneConfig.getInt("assetZonePickupVisibility"));
            holder.assetZoneDrop.setVisibility(zoneConfig.getInt("assetZoneDropVisibility"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateDriverStatus(Boolean status, String mode, Context context, Boolean startWidget) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->
        {
            StringBuilder result = new StringBuilder();
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString("REGISTERATION_TOKEN", "null");
            String bundle_version = sharedPref.getString("BUNDLE_VERSION","null");
            String baseUrl = sharedPref.getString("BASE_URL", "null");
            String deviceDetails = sharedPref.getString("DEVICE_DETAILS", "null");
            String versionName = sharedPref.getString("VERSION_NAME", "null");
            try
            {
                //endPoint for driver status
                String orderUrl = baseUrl + "/driver/setActivity?active=" + status + "&mode=\"" + mode + "\"";
                Log.d(LOG_TAG, "orderUrl " + orderUrl);
                //Http connection to make API call
                HttpURLConnection connection = (HttpURLConnection) (new URL(orderUrl).openConnection());
                if (connection instanceof HttpsURLConnection)
                    ((HttpsURLConnection) connection).setSSLSocketFactory(new TLSSocketFactory());
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("x-client-version", versionName);
                connection.setRequestProperty("token", token);
                connection.setRequestProperty("x-bundle-version", bundle_version);
                connection.setRequestProperty("x-device", deviceDetails);
                connection.setDoOutput(true);
                connection.connect();

                // validating the response code
                int respCode = connection.getResponseCode();
                InputStreamReader respReader;
                Log.d(LOG_TAG, "respCode "+ respCode);

                if ((respCode < 200 || respCode >= 300) && respCode != 302) {
                    respReader = new InputStreamReader(connection.getErrorStream());
                    Log.d(LOG_TAG, "in error "+ respReader);
                } else {
                    if (startWidget && Settings.canDrawOverlays(context)  && !sharedPref.getString(context.getResources().getString(R.string.REGISTERATION_TOKEN), "null").equals("null") && !sharedPref.getString("ANOTHER_ACTIVITY_LAUNCHED", "false").equals("true") && (sharedPref.getString(context.getResources().getString(R.string.ACTIVITY_STATUS), "null").equals("onPause") || sharedPref.getString(context.getResources().getString(R.string.ACTIVITY_STATUS), "null").equals("onDestroy"))) {
                            Intent widgetService = new Intent(context, WidgetService.class);
                            context.startService(widgetService);
                    }
                    respReader = new InputStreamReader(connection.getInputStream());
                    Log.d(LOG_TAG, "in 200 "+ respReader);
                }

                BufferedReader in = new BufferedReader(respReader);
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                sharedPref.edit().putString("DRIVER_STATUS","__failed").apply();
                Log.d(LOG_TAG, "in result "+ result);
            }
            catch (Exception error)
            {
                Log.d(LOG_TAG, "Catch in updateDriverStatus : "+error);
            }
        });
    }

    public static void addRideReceivedEvent(JSONObject entity_payload, Bundle rideRequestBundle, SheetModel model, String event, Context context) {
        ExecutorManager.runOnBackgroundThread(() -> {
            try {
                if (!remoteConfigs.hasKey("enable_clevertap_events")) return;
                String merchantId = context.getResources().getString(R.string.merchant_id);
                JSONObject clevertapConfig = new JSONObject(remoteConfigs.getString("enable_clevertap_events"));
                SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                if (clevertapConfig.has(merchantId)) {
                    boolean enableCleverTapEvents = clevertapConfig.getBoolean(merchantId);
                    if (enableCleverTapEvents) {
                        HashMap<String, Object> cleverTapParams = new HashMap<>();
                        if (entity_payload != null) {
                            cleverTapParams.put("searchRequestId", entity_payload.getString("searchRequestId"));
                            cleverTapParams.put("rideRequestPopupDelayDuration", entity_payload.has("rideRequestPopupDelayDuration") ? entity_payload.getInt("rideRequestPopupDelayDuration") : 0);
                            cleverTapParams.put("keepHiddenForSeconds", (entity_payload.has("keepHiddenForSeconds") && !entity_payload.isNull("keepHiddenForSeconds") ? entity_payload.getInt("keepHiddenForSeconds") : 0));
                            cleverTapParams.put("requestedVehicleVariant", (entity_payload.has("requestedVehicleVariant") && !entity_payload.isNull("requestedVehicleVariant")) ? NotificationUtils.getCategorizedVariant(entity_payload.getString("requestedVehicleVariant"), context) : NotificationUtils.NO_VARIANT);
                        } else if (rideRequestBundle != null) {
                            cleverTapParams.put("searchRequestId", rideRequestBundle.getString("searchRequestId"));
                            cleverTapParams.put("rideRequestPopupDelayDuration", rideRequestBundle.getInt("rideRequestPopupDelayDuration"));
                            cleverTapParams.put("keepHiddenForSeconds", rideRequestBundle.getInt("keepHiddenForSeconds", 0));
                            cleverTapParams.put("requestedVehicleVariant", rideRequestBundle.getString("requestedVehicleVariant"));
                        } else if (model != null) {
                            cleverTapParams.put("searchRequestId", model.getSearchRequestId());
                            cleverTapParams.put("rideRequestPopupDelayDuration", model.getRideRequestPopupDelayDuration());
                            cleverTapParams.put("vehicleVariant", sharedPref.getString("VEHICLE_VARIANT", "null"));
                        }
                        cleverTapParams.put("driverId", sharedPref.getString("DRIVER_ID", "null"));
                        clevertapConfig.put("driverMode", sharedPref.getString("DRIVER_STATUS_N", ""));
                        clevertapConfig.put("overlayNotAvailable", NotificationUtils.overlayFeatureNotAvailable(context));
                        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(context);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalDateTime utcDateTime = LocalDateTime.now(ZoneOffset.UTC);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                            String formattedDateTime = utcDateTime.format(formatter);
                            long millisecondsSinceEpoch = Instant.now().toEpochMilli();
                            cleverTapParams.put("timeStampString", formattedDateTime);
                            cleverTapParams.put("timeStamp", millisecondsSinceEpoch);
                        }
                        if (clevertapDefaultInstance != null) {
                            clevertapDefaultInstance.pushEvent(event, cleverTapParams);
                        }
                    }
                }
            } catch (Exception e) {
                firebaseLogEventWithParams("exception_in_logging_ride_req_event", "exception", Objects.requireNonNull(e.getMessage()).substring(0, 40), context);
            }
        });
    }

    public static String calculateDp(String durationToPickup, DecimalFormat df) {
        try {
            return df.format(Integer.parseInt(durationToPickup) / 60);
        } catch (NumberFormatException e) {
            Log.e("ParseInt Error", e.toString());
            return "";
        }
    }

    public static String getCityWithFallback (Context context){
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String city = sharedPref.getString("DRIVER_LOCATION", "null").toLowerCase();
        if (!city.equals("null")){
            return city;
        }else {
            // fallback
            String buildType = context.getResources().getString(R.string.service);
            if (buildType.equals("yatrisathiprovider")){
                return KOLKATA;
            } else if (buildType.equals("yatriprovider")) {
                return KOCHI;
            }else {
                return city;
            }
        }

    }

    public static VariantConfig getVariantConfig(String myVariant, Context context) throws JSONException {
        JSONObject myCityVariantConfig = currentCityObject("variant_config", context);
        if (myCityVariantConfig.has(myVariant)){
            JSONObject myVariantConfig = myCityVariantConfig.getJSONObject(myVariant);
            String text = myVariantConfig.optString("text", "");
            String textColor = myVariantConfig.optString("textColor", "");
            String background = myVariantConfig.optString("background", "");
            String icon = myVariantConfig.optString("icon", "");
            boolean visible = myVariantConfig.optBoolean("visible", false);
            return new VariantConfig(text, textColor, background, icon, visible);
        }
        return new VariantConfig("","","","",false);
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private static GradientDrawable getGradientDrawable(String background, Context context){
        // Create a new shape drawable
        GradientDrawable shapeDrawable = new GradientDrawable();
        shapeDrawable.setShape(GradientDrawable.RECTANGLE);
        int cornerVal = dpToPx(13, context );
        shapeDrawable.setCornerRadii(new float[]{cornerVal,cornerVal,cornerVal,cornerVal,cornerVal,cornerVal,cornerVal,cornerVal}); // setting corners
        shapeDrawable.setStroke(1, Color.parseColor(background)); // setting stroke color and width
        shapeDrawable.setColor(Color.parseColor(background)); // setting solid color
        return shapeDrawable;
    }

    public static boolean handleVariant(SheetAdapter.SheetViewHolder holder, SheetModel model, Context context){
        String vehicleVariant = model.getRequestedVehicleVariant();
        try {
            VariantConfig variantConfig = getVariantConfig(vehicleVariant, context);
            holder.rideTypeTag.setBackground(getGradientDrawable(variantConfig.getBackground(), context));
            holder.rideTypeText.setText(variantConfig.getText());
            holder.rideTypeImage.setVisibility(variantConfig.getIcon().isEmpty() ? View.GONE : View.VISIBLE);
            holder.rideTypeImage.setImageURI(Uri.parse("android.resource://"+ context.getPackageName() +"/drawable/"+ variantConfig.getIcon()));
            holder.rideTypeText.setTextColor(Color.parseColor(variantConfig.getTextColor()));
            return variantConfig.isVisible();
        }catch (Exception e){
            holder.rideTypeTag.setVisibility(View.GONE);
            return false;
        }
    }

    public static String getUptoDecStr(float val, int digit) {
        DecimalFormat df = new DecimalFormat("###.##", new DecimalFormatSymbols(new Locale("en", "us")));
        df.setMaximumFractionDigits(digit);
        return df.format(val);
    }

    public static void updateTierAndAC(SheetAdapter.SheetViewHolder holder, SheetModel model, Context context) {
        boolean showTier = model.getVehicleServiceTier() != null;
        int acRide = model.isAirConditioned();
        boolean showAC = acRide == 1 && showAcConfig(context);
        holder.vcTierAndACView.setVisibility((showTier || showAC) ? View.VISIBLE : View.GONE);
        holder.vehicleServiceTier.setText(model.getVehicleServiceTier() != null ? getSTMapping (model.getVehicleServiceTier(), context) : "");
        holder.vehicleServiceTier.setVisibility(showTier ? View.VISIBLE : View.GONE);
        holder.acNonAcView.setVisibility( showAC ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("SetTextI18n")
    public static void updateRateView(SheetAdapter.SheetViewHolder holder, SheetModel model) {
        double baseFare = model.getBaseFare() + model.getOfferedPrice();
        float dist = model.getDistanceToBeCovFloat()/1000;
        String rate = RideRequestUtils.getUptoDecStr((float) (baseFare/dist), 1);
        String currency = model.getCurrency();;
        holder.rateText.setText("Rate: " + currency + rate +"/km");
    }

    public static void updateRentalView(SheetAdapter.SheetViewHolder holder, SheetModel model, Context context) {
        Handler mainLooper = new Handler(Looper.getMainLooper());
        mainLooper.post(() -> {
            if(!model.getRideProductType().equals(NotificationUtils.RENTAL)){
                return;
            }
            holder.tagsBlock.setVisibility(View.VISIBLE);
            holder.reqButton.setTextColor(context.getColor(R.color.white));
            holder.reqButton.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.turquoise)));
            holder.rentalRideTypeTag.setVisibility(View.VISIBLE);
            holder.rideStartDateTimeTag.setVisibility(View.VISIBLE);
            holder.rideStartTime.setText(model.getRideStartTime());
            holder.rideStartDate.setVisibility(View.VISIBLE);
            holder.rideStartDate.setText(model.getRideStartDate());
            holder.rentalDurationDistanceTag.setVisibility(View.VISIBLE);
            holder.rideDuration.setText(model.getRideDuration());
            holder.rideDistance.setText(model.getRideDistance());
            holder.destinationArea.setVisibility(View.GONE);
            holder.destinationAddress.setVisibility(View.GONE);
            holder.distanceToBeCovered.setVisibility(View.GONE);
            holder.destinationPinCode.setVisibility(View.GONE);
            holder.locationDashedLine.setVisibility(View.GONE);
            holder.locationDestinationPinTag.setVisibility(View.GONE);
            holder.gotoTag.setVisibility(View.GONE);
        });
    }

    public static void updateIntercityView(SheetAdapter.SheetViewHolder holder, SheetModel model, Context context) {
        Handler mainLooper = new Handler(Looper.getMainLooper());
        mainLooper.post(() -> {
            if (!model.getRideProductType().equals(NotificationUtils.INTERCITY)) {
                return;
            }
            holder.tagsBlock.setVisibility(View.VISIBLE);
            holder.reqButton.setTextColor(context.getColor(R.color.white));
            holder.reqButton.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue800)));
            holder.intercityRideTypeTag.setVisibility(View.VISIBLE);
            holder.gotoTag.setVisibility(View.GONE);
            holder.rideStartDateTimeTag.setVisibility(View.VISIBLE);
            holder.rideStartTime.setText(model.getRideStartTime());
            holder.rideStartDate.setVisibility(View.VISIBLE);
            holder.rideStartDate.setText(model.getRideStartDate());
            holder.buttonIncreasePrice.setVisibility(View.GONE);
            holder.buttonDecreasePrice.setVisibility(View.GONE);
        });
    }

    public static void updateExtraChargesString(SheetAdapter.SheetViewHolder holder, SheetModel model, Context context) {
        boolean showPickupCharges = false;
        boolean hideZeroPickupCharges = true;
        int pickUpCharges = model.getDriverPickUpCharges();
        try {
            JSONObject currentCityConfig = currentCityObject("views_config", context);
            if (currentCityConfig.has("show_pickup_charges")){
                showPickupCharges = currentCityConfig.getBoolean("show_pickup_charges");
            }
            if (currentCityConfig.has("hide_zero_pickup_charges")){
                hideZeroPickupCharges = currentCityConfig.getBoolean("hide_zero_pickup_charges");
            }
        }catch (JSONException e){
            firebaseLogEventWithParams("exception_in_update_extra_charges", "exception", String.valueOf(e), context);
        }
        if ((pickUpCharges > 0 && !hideZeroPickupCharges) && showPickupCharges) {
            String formattedPickupChargesText = context.getString(R.string.includes_pickup_charges_10).replace("{#amount#}", Integer.toString(pickUpCharges));
            holder.textIncludesCharges.setText(formattedPickupChargesText);
            holder.textIncludesCharges.setVisibility(View.VISIBLE);
            holder.rateViewDot.setVisibility(View.VISIBLE);
        } else {
            holder.textIncludesCharges.setVisibility(View.GONE);
            holder.rateViewDot.setVisibility(View.GONE);
        }
    }

    private static boolean showAcConfig(Context context) {
        JSONObject myCityOb = currentCityObject("service_tier_mapping", context);
        if (myCityOb.has("ac_tag")){
            return myCityOb.optBoolean("ac_tag", true);
        }
        return true;
    }

    public static String getSTMapping (String serviceTier, Context context) {
        JSONObject myCityOb = currentCityObject("service_tier_mapping", context);
        try {
            if (myCityOb.has("mapping")){
                JSONObject mappingOb = myCityOb.getJSONObject("mapping");
                if(mappingOb.has(serviceTier)){
                    return mappingOb.getString(serviceTier);
                }
            }
        }catch (Exception e){
            firebaseLogEventWithParams("exception_in_get_st_mapping", "exception", String.valueOf(e), context);
        }
        return serviceTier;
    }

    public static JSONObject currentCityObject (String keyName, Context context){
        String city = getCityWithFallback(context);
        String allCities = remoteConfigs.getString(keyName);
        try {
            JSONObject allCitiesOb = new JSONObject(allCities);
            if (allCitiesOb.has(city)) {
                return allCitiesOb.getJSONObject(city);
            }
        }catch (Exception e){
            firebaseLogEventWithParams("exception_in_get_current_city_ob", "exception", String.valueOf(e), context);
        }
        return new JSONObject();
    }
}