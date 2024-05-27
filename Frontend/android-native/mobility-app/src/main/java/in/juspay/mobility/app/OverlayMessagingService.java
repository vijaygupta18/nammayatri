/*
 * Copyright 2022-23, Juspay India Pvt Ltd
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

/* Required Payload template
"message" : {
    "token": "<DEVICE_TOKEN>",
    "android": {
        "data": {
        "notification_type": "DRIVER_NOTIFY",
        "show_notification": "true",
        "entity_type": "Case",
        "entity_ids": "",
        "notification_json": "{
            \"title\": \"Demo message\",
            \"body\": \"Body of demo message\",
            \"icon\": \"<ICON_URL>\",
            \"tag\": \"DRIVER_NOTIFY\",
            \"sound\": \"default\",
            \"channel_id\": \"General\"
        }",
            "driver_notification_payload": "{
                \"title\" : \"This is a demo message\",
                \"description\" :\"This is the description of demo message\",
                \"imageUrl\" : \"<IMAGE_URL>\",
                \"okButtonText\" : \"Ok\",
                \"cancelButtonText\" : \"Cancel\",
                \"actions\" : [\"SET_DRIVER_ONLINE\", \"OPEN_APP\", \"OPEN_LINK\"],
                \"link\" : \"http://www.google.com\",
                \"endPoint\": \"<EP>\",
                \"method\": \"POST\",
                \"reqBody\" : {},
                \"titleVisibility\" : true,
                \"descriptionVisibility\" : true,
                \"buttonOkVisibility\" : true,
                \"buttonCancelVisibility\" : true,
                \"buttonLayoutVisibility\" : true,
                \"imageVisibility\" : true
            }"
        }
    }
    }
}
 */


package in.juspay.mobility.app;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import in.juspay.mobility.app.RemoteConfigs.MobilityRemoteConfigs;
import in.juspay.mobility.common.services.MobilityAPIResponse;
import in.juspay.mobility.common.services.MobilityCallAPI;

public class OverlayMessagingService extends Service {
    private WindowManager windowManager;
    private View messageView;
    private String currency;
    private String lang;
    private String link, endPoint, method;
    private JSONArray actions;
    private JSONArray actions2;
    private JSONArray secondaryActions;
    private JSONArray secondaryActions2;
    private CountDownTimer countDownTimer;
    private String reqBody;
    private static MobilityRemoteConfigs remoteConfigs = new MobilityRemoteConfigs(false, true);
    private String toastMessage;
    private String supportPhoneNumber;

    private double editLat, editlon;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (!Settings.canDrawOverlays(this)) return;
        super.onCreate();

    }

    @SuppressLint("InflateParams")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String intentMessage = intent != null && intent.hasExtra("payload") ? intent.getStringExtra("payload") : null;
        if (!Settings.canDrawOverlays(this) || intentMessage == null) return START_STICKY;
        int layoutParamsType = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        if (windowManager != null) {
            setDataToViews(intentMessage);
            return START_STICKY;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutParamsType,
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSPARENT);
        params.dimAmount = 0.6f;
        params.gravity = Gravity.CENTER;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        messageView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.message_overlay, null);

        setViewListeners(messageView);
        windowManager.addView(messageView, params);
        setDataToViews(intentMessage);
        return START_STICKY;
    }

    private void setDataToViews(String bundle) {
        try {
            JSONObject data = new JSONObject(bundle);
            TextView title = messageView.findViewById(R.id.title);
            FrameLayout updateLocDetailsComponent = messageView.findViewById(R.id.update_loc_details_component);
            TextView description = messageView.findViewById(R.id.description);
            ImageView imageView = messageView.findViewById(R.id.image);
            LinearLayout buttonLayout = messageView.findViewById(R.id.button_view);
            MaterialButton buttonOk = messageView.findViewById(R.id.button_ok);
            TextView buttonCancel = messageView.findViewById(R.id.button_cancel);
            ImageView cancelButtonImage = messageView.findViewById(R.id.cancel_button_image);
            TextView buttonCancelRight = messageView.findViewById(R.id.button_cancel_right);
            title.setText(data.has("title") ? data.getString("title") : "");
            buttonOk.setText(data.has("okButtonText") ? data.getString("okButtonText") : "Cancel");
            buttonCancel.setText(data.has("cancelButtonText") ? data.getString("cancelButtonText") : "Ok");
            description.setText(data.has("description") ? data.getString("description") : "");
            link = data.has("link") ? data.getString("link") : null;
            endPoint = data.has("endPoint") ? data.getString("endPoint") : null;
            method = data.has("method") ? data.getString("method") : null;
            reqBody = data.has("reqBody") ?  data.getString("reqBody") : null;
            actions = data.has("actions") ? data.getJSONArray("actions") : null;
            actions2 = data.has("actions2") ? data.getJSONArray("actions2") : null;
            secondaryActions = data.has("secondaryActions") ? data.getJSONArray("secondaryActions") : null;
            secondaryActions2 = data.has("secondaryActions2") ? data.getJSONArray("secondaryActions2") : null;
            toastMessage = data.optString("toastMessage", null);
            supportPhoneNumber = data.optString("contactSupportNumber", null);
            editLat = data.has("editlat") ? data.getDouble("editlat") : 0.0;
            editlon = data.has("editlon") ? data.getDouble("editlon") : 0.0;
            Glide.with(this).load(data.getString("imageUrl")).into(imageView);
            boolean titleVisibility = data.has("titleVisibility") && data.getBoolean("titleVisibility");
            boolean updateLocDetailsComponentVisibility = data.has("updateLocDetails");
            if (updateLocDetailsComponentVisibility) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                currency = sharedPref.getString("CURRENCY", "₹");
                lang = sharedPref.getString( "LANGUAGE_KEY", "ENGLISH");
                editor.putString("CALL_REFRESH", "true").apply(); 
            }
            boolean descriptionVisibility = data.has("descriptionVisibility") && data.getBoolean("descriptionVisibility");
            boolean buttonOkVisibility = data.has("buttonOkVisibility") && data.getBoolean("buttonOkVisibility");
            boolean buttonCancelVisibility = data.has("buttonCancelVisibility") && data.getBoolean("buttonCancelVisibility");
            boolean buttonLayoutVisibility = data.has("buttonLayoutVisibility") && data.getBoolean("buttonLayoutVisibility");
            boolean imageVisibility = data.has("imageVisibility") && data.getBoolean("imageVisibility");
            title.setVisibility(titleVisibility ? View.VISIBLE : View.GONE);
            updateLocDetailsComponent.setVisibility(updateLocDetailsComponentVisibility ? View.VISIBLE : View.GONE);
            description.setVisibility(descriptionVisibility ? View.VISIBLE : View.GONE);
            buttonLayout.setVisibility(buttonLayoutVisibility ? View.VISIBLE : View.GONE);
            buttonOk.setVisibility(buttonOkVisibility ? View.VISIBLE : View.GONE);
            buttonCancel.setVisibility(buttonCancelVisibility ? View.VISIBLE : View.GONE);
            imageView.setVisibility(imageVisibility ? View.VISIBLE : View.GONE);
            try {
                setDataToMediaView(data);
            } catch (Exception e){
                Exception exception = new Exception("Error in SetDataToView " + e);
                FirebaseCrashlytics.getInstance().recordException(exception);
                RideRequestUtils.firebaseLogEventWithParams("exception_in_construct_media_view", "CONSTRUCT_MEDIA_VIEW", Objects.requireNonNull(e.getMessage()).substring(0, 40), this);
            }
            Boolean showContactSupport = supportPhoneNumber != null;
            if (showContactSupport) {
                buttonCancelRight.setVisibility(View.VISIBLE);
                cancelButtonImage.setVisibility(View.VISIBLE);
                secondaryActions = secondaryActions != null ? secondaryActions.put("CALL_SUPPORT") : new JSONArray("CALL_SUPPORT");
                buttonCancel.setText(R.string.need_help);
            }

            if (updateLocDetailsComponentVisibility) {
                setDataToLocationDetailsComponent(data);
            }

        } catch (Exception e) {
            Exception exception = new Exception("Error in ConstructView " + e);
            FirebaseCrashlytics.getInstance().recordException(exception);
            RideRequestUtils.firebaseLogEventWithParams("exception_construct_view", "CONSTRUCT_VIEW", String.valueOf(e), this);
            stopSelf();
        }
    }


    public void setDataToLocationDetailsComponent (JSONObject data) throws JSONException {
        JSONObject updateLocDetails = data.getJSONObject("updateLocDetails");
        JSONObject mbDestination = updateLocDetails.has("destination") ? updateLocDetails.getJSONObject("destination") : null;
        if (mbDestination != null) {
            double destLat = mbDestination.optDouble("lat", 0.0);
            double destLon = mbDestination.optDouble("lon", 0.0);
            JSONObject destAddress = mbDestination.has("address") ? mbDestination.getJSONObject("address") : null;
            String destAddressString = destAddress.optString("fullAddress", "");
            String destPincode = RideRequestUtils.getPinCodeFromRR(destLat, destLon, OverlayMessagingService.this);
            TextView destination = messageView.findViewById(R.id.new_drop);
            String destinationAddress = "Drop: "+ destAddressString;
            updateViewFromMlTranslation(destination, destinationAddress, getApplicationContext());
            // destination.setText(destinationAddress);
            TextView pincode = messageView.findViewById(R.id.pin_code);
            pincode.setText(destPincode);
            pincode.setVisibility(View.VISIBLE);
            destination.setVisibility(View.VISIBLE);
            LinearLayout dropLayout = messageView.findViewById(R.id.drop_layout);
            dropLayout.setVisibility(View.VISIBLE);
        }
        double newEstimatedDistance = updateLocDetails.optDouble("newEstimatedDistance", 0.0);
        double newEstimatedDistInKm = newEstimatedDistance/1000;
        double newEstimatedFare = updateLocDetails.optDouble("newEstimatedFare", 0.0);
        double oldEstimatedDistance = updateLocDetails.optDouble("oldEstimatedDistance", 0.0);
        double oldEstimatedFare = updateLocDetails.optDouble("oldEstimatedFare", 0.0);
        TextView newFare = messageView.findViewById(R.id.new_fare_val);
        newFare.setText(currency +" " + String.format(Locale.getDefault(), "%.1f", newEstimatedFare));
        newFare.setVisibility(View.VISIBLE);
        double fareDiff = newEstimatedFare - oldEstimatedFare;
        ImageView fareIndicatorArrow = messageView.findViewById(R.id.fare_diff_indicator_arrow);
        MaterialCardView fareIndicator = messageView.findViewById(R.id.fare_diff_indicator);
        TextView fareDiffView = messageView.findViewById(R.id.fare_diff_indicator_val);
        String updateConfigs = remoteConfigs.hasKey("update_loc_details") ? remoteConfigs.getString("update_loc_details") : null;
        String distDiffColorPositive = "#53BB6F";
        String distDiffColorNegative = "#E55454";
        String distDiffColorNeutral = "#A7A7A7";
        String fareDiffColorPositive = "#53BB6F";
        String fareDiffColorNegative = "#E55454";
        String fareDiffColorNeutral = "#A7A7A7";
        int minTimeToShowPopupInMS = 50000;
        if (updateConfigs!=null)
        {
            JSONObject updateRemoteConfig = new JSONObject(updateConfigs);
            if (updateRemoteConfig!=null)
            {
                distDiffColorPositive = updateRemoteConfig.optString("distDiffColorPositive",distDiffColorPositive);
                distDiffColorNegative = updateRemoteConfig.optString("distDiffColorNegative",distDiffColorNegative);
                distDiffColorNeutral =  updateRemoteConfig.optString("distDiffColorNeutral",distDiffColorNeutral);
                fareDiffColorPositive = updateRemoteConfig.optString("fareDiffColorPositive",fareDiffColorPositive);
                fareDiffColorNegative = updateRemoteConfig.optString("fareDiffColorNegative",fareDiffColorNegative);
                fareDiffColorNeutral = updateRemoteConfig.optString("fareDiffColorNeutral",fareDiffColorNeutral);
                minTimeToShowPopupInMS = updateRemoteConfig.optInt("minTimeToShowPopupInMS", minTimeToShowPopupInMS);
            }
        }
        int fareDrawable = R.drawable.ny_ic_down_arrow_white;
        int fareDrawableVisibility;
        String fareDiffColor;
        String fareDiffValueText;
        int distDrawable = R.drawable.ny_ic_down_arrow_white;
        int distDrawableVisibility;
        String distDiffColor;
        String distDiffValueText;
        if (fareDiff < 0) {
            fareDiff = oldEstimatedFare - newEstimatedFare;
            fareDrawable = R.drawable.ny_ic_down_arrow_white;
            fareDrawableVisibility = View.VISIBLE;
            fareDiffColor = fareDiffColorNegative;
            fareDiffValueText = currency + " " + String.format(Locale.getDefault(), "%.1f", fareDiff);
        }
        else if (fareDiff > 0)
        {
            fareDrawable = R.drawable.ny_ic_up_arrow_white;
            fareDrawableVisibility = View.VISIBLE;
            fareDiffColor = fareDiffColorPositive;
            fareDiffValueText = currency +" " + String.format(Locale.getDefault(), "%.1f", fareDiff);
        }
        else {
            fareDrawableVisibility = View.GONE;
            fareDiffValueText = getString(R.string.no_change);
            fareDiffColor = fareDiffColorNeutral;
        }
        fareIndicator.setCardBackgroundColor(Color.parseColor(fareDiffColor));
        fareIndicatorArrow.setVisibility(fareDrawableVisibility);
        fareIndicatorArrow.setImageDrawable(getDrawable(fareDrawable));
        fareDiffView.setText(fareDiffValueText);
        TextView newDistance = messageView.findViewById(R.id.new_dist_val);
        newDistance.setText(String.format(Locale.getDefault(), "%.1f", newEstimatedDistInKm) + " km");
        double distDiff = newEstimatedDistance - oldEstimatedDistance;
        ImageView distIndicatorArrow = messageView.findViewById(R.id.dist_diff_indicator_arrow);
        TextView distDiffView = messageView.findViewById(R.id.dist_diff_indicator_val);
        MaterialCardView distIndicator = messageView.findViewById(R.id.dist_diff_indicator);
        if (distDiff < 0)
        {
            distDiff = oldEstimatedDistance - newEstimatedDistance;
            distDrawable = R.drawable.ny_ic_down_arrow_white;
            distDrawableVisibility = View.VISIBLE;
            distDiffColor = distDiffColorNegative;
            distDiffValueText = String.format(Locale.getDefault(), "%.1f", distDiff/1000) + " km";
        }
        else if (distDiff > 0){
            distDrawable = R.drawable.ny_ic_up_arrow_white;
            distDrawableVisibility = View.VISIBLE;
            distDiffColor = distDiffColorPositive;
            distDiffValueText = String.format(Locale.getDefault(), "%.1f", distDiff/1000) + " km";
        }
        else {
            distDrawableVisibility = View.GONE;
            distDiffColor = distDiffColorNeutral;
            distDiffValueText = getString(R.string.no_change);
        }
        distIndicatorArrow.setImageDrawable(getDrawable(distDrawable));
        distIndicatorArrow.setVisibility(distDrawableVisibility);
        distIndicator.setCardBackgroundColor(Color.parseColor(distDiffColor));
        distDiffView.setText(distDiffValueText);
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("en", "us"));
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        String getCurrTime = f.format(new Date());
        String validTill = updateLocDetails.optString("validTill", "");
        int calculatedTime = RideRequestUtils.calculateExpireTimer(validTill, getCurrTime);
        int calculatedTimeInMS = Math.min ((calculatedTime - 5) * 1000, minTimeToShowPopupInMS);
        LinearProgressIndicator progressIndicator = messageView.findViewById(R.id.progress_indicator_overlay);
        countDownTimer = new CountDownTimer(calculatedTimeInMS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progressCompat = (int) (millisUntilFinished/1000);
                progressIndicator.setProgressCompat(progressCompat*2, true);
                if (progressCompat <= 8) {
                    progressIndicator.setIndicatorColor(getColor(R.color.red900));
                } else {
                    progressIndicator.setIndicatorColor(getColor(R.color.green900));
                }

            }

            @Override
            public void onFinish() {
                onDestroy();

            }
        };
        countDownTimer.start();

    }

    private void updateViewFromMlTranslation(TextView destinationAddressHolder, String address, Context context){
        TranslatorMLKit translate = new TranslatorMLKit("en", lang, context);
        translate.translateStringInTextView(address, destinationAddressHolder);
    }

    public void setDataToMediaView (JSONObject data) throws JSONException {
        LinearLayout dynamicView = messageView.findViewById(R.id.dynamic_views);
        dynamicView.removeAllViews();
        JSONArray mediaViews = data.has("socialMediaLinks") ? data.getJSONArray("socialMediaLinks") : null;
        if(mediaViews != null){
            for (int i = 0; i < mediaViews.length(); i++){
                LinearLayout mediaView = new LinearLayout(this);
                JSONObject mediaViewData = mediaViews.getJSONObject(i);
                int imageHeight = mediaViewData.has("height") ? mediaViewData.getInt("height") : 0;
                int imageWidth = mediaViewData.has("width") ? mediaViewData.getInt("height") : 0;
                if(mediaViewData.has("prefixImage")) {
                    ImageView prefixImage = new ImageView(this);
                    Glide.with(this).load(mediaViewData.getString("prefixImage")).into(prefixImage);
                    prefixImage.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
                    boolean prefixImageVisibility = mediaViewData.has("prefixImage") && mediaViewData.getString("prefixImage").length() != 0;
                    prefixImage.setVisibility(prefixImageVisibility ? View.VISIBLE : View.GONE);
                    mediaView.addView(prefixImage);
                }

                TextView linkText = new TextView(this);
                linkText.setText(mediaViewData.has("linkText") ? mediaViewData.getString("linkText") : "");
                linkText.setTextColor(mediaViewData.has("linkTextColor") ? mediaViewData.getInt("linkTextColor") :  getResources().getColor(R.color.blue800, null)); //ask theme
                boolean isTextUnderLined = !mediaViewData.has("isTextUnderlined") || mediaViewData.getBoolean("isTextUnderlined");
                if (isTextUnderLined)
                    linkText.setPaintFlags(linkText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                boolean linkTextVisibility = mediaViewData.has("linkText") && mediaViewData.getString("linkText").length() != 0;
                linkText.setVisibility(linkTextVisibility ? View.VISIBLE : View.GONE);
                linkText.setPadding(10,0,10,0);
                mediaView.addView(linkText);

                if(mediaViewData.has("suffixImage")) {
                    ImageView suffixImage = new ImageView(this);
                    Glide.with(this).load(mediaViewData.getString("suffixImage")).into(suffixImage);
                    suffixImage.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
                    boolean suffixImageVisibility = mediaViewData.has("suffixImage") && mediaViewData.getString("suffixImage").length() != 0;
                    suffixImage.setVisibility(suffixImageVisibility ? View.VISIBLE : View.GONE);
                    mediaView.addView(suffixImage);
                }

                mediaView.setGravity(Gravity.CENTER);
                String mediaLink = mediaViewData.has("link") ? mediaViewData.getString("link") : null;
                if(mediaLink != null){
                    mediaView.setOnClickListener(view -> {
                        openLink(mediaLink);
                        stopSelf();
                    });
                }
                dynamicView.addView(mediaView);
            }
        }
    }

    private void setViewListeners(View messageView) {
        messageView.findViewById(R.id.button_ok).setOnClickListener(view -> {
            try {
                for (int i = 0; i < actions2.length(); i++) {
                    if (actions2.getJSONObject(i) != null ) {
                    JSONObject action2 = actions2.getJSONObject(i);
                    performAction2(action2);
                    }
                }
                if (actions2.length() == 0) {
                    for (int i = 0; i < actions.length(); i++) {
                        String action = String.valueOf(actions.get(i));
                        performAction(action);
                    }
                }
                if(countDownTimer != null){
                countDownTimer.cancel();}
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopSelf();
        });

        messageView.findViewById(R.id.secondary_button).setOnClickListener(view -> {
            try {
                for (int i = 0; i < secondaryActions2.length(); i++) {
                    if (secondaryActions2.getJSONObject(i) != null ) {
                        JSONObject action2 = secondaryActions2.getJSONObject(i);
                        performAction2(action2);
                    }
                }
                if (secondaryActions2.length() == 0) {
                for (int i = 0; i < secondaryActions.length(); i++) {
                    String action = String.valueOf(secondaryActions.get(i));
                    performAction(action);
                }
                }
                if(countDownTimer != null){
                countDownTimer.cancel();}
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopSelf();
        });
    }

    public void openNavigation(double lat, double lon) {
        try {
            Uri mapsURI = Uri.parse("google.navigation:q=" + lat + "," + lon);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsURI);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            RideRequestUtils.firebaseLogEventWithParams("exception_in_open_navigation", "OPEN_NAVIGATION", Objects.requireNonNull(e.getMessage()).substring(0, 40), this);
            e.printStackTrace();
        }
    }

    void performAction(String action){
        switch (action) {
            case "SET_DRIVER_ONLINE":
                RideRequestUtils.updateDriverStatus(true, "ONLINE", this, false);
                RideRequestUtils.restartLocationService(this, "");
                break;
            case "NAVIGATE":
                openNavigation(editLat, editlon);
                break;
            case "OPEN_LINK":
                openLink(link);
                break;
            case "CALL_API":
                try{
                    if (endPoint != null && reqBody != null && method != null) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MobilityCallAPI mobilityApiHandler = new MobilityCallAPI();
                                Map<String, String> baseHeaders = mobilityApiHandler.getBaseHeaders(OverlayMessagingService.this);
                                MobilityAPIResponse apiResponse = mobilityApiHandler.callAPI(endPoint, baseHeaders, reqBody, method);
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (toastMessage != null && apiResponse.getStatusCode() == 200)
                                            Toast.makeText(OverlayMessagingService.this, toastMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                        }).start();
                    }
                } catch (Exception e){
                    System.out.println("Error : " + e);
                }
                break;
            case "OPEN_APP":
                RideRequestUtils.openApplication(this);
                break;
            case "OPEN_SUBSCRIPTION" :
                Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
                intent.putExtra("notification_type", "PAYMENT_MODE_MANUAL");
                intent.putExtra("entity_ids", "");
                intent.putExtra("entity_type", "");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Utils.logEvent("ny_driver_overlay_join_now", getApplicationContext());
                break;
            case "CALL_SUPPORT" :
                try {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + supportPhoneNumber));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e){
                    System.out.println(e.toString());
                }

                break;
        }
        stopSelf();
    }

    void performAction2(JSONObject action){
        try {
            JSONObject primaryAction = action.has("primaryAction") ? action.getJSONObject("primaryAction") : null;
            if (primaryAction != null) {
            String actionName = primaryAction.optString("actionName", "");
            JSONObject actionDetails = primaryAction.has("actionDetails") ? primaryAction.getJSONObject("actionDetails") : null;
            JSONArray dependentActions = action.has("dependentActions") ? action.getJSONArray("dependentActions") : null;
            switch (actionName) {
                case "SET_DRIVER_ONLINE":
                    RideRequestUtils.updateDriverStatus(true, "ONLINE", this, false);
                    RideRequestUtils.restartLocationService(this);
                    performDependentAction(dependentActions);
                    break;
                case "NAVIGATE":
                    Double lat = actionDetails.optDouble("lat", 0.0);
                    Double lon = actionDetails.optDouble("long", 0.0);
                    openNavigation(lat, lon);
                    performDependentAction(dependentActions);
                    break;
                case "OPEN_LINK":
                    String link = actionDetails.optString("link", null);
                    openLink(link);
                    performDependentAction(dependentActions);
                    break;
                case "CALL_API":
                    try{
                        String endPoint = actionDetails.optString("endPoint", null);
                        String reqBody = actionDetails.optString("reqBody", null);
                        String method = actionDetails.optString("method", null);
                        if (endPoint != null && reqBody != null && method != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MobilityCallAPI mobilityApiHandler = new MobilityCallAPI();
                                    Map<String, String> baseHeaders = mobilityApiHandler.getBaseHeaders(OverlayMessagingService.this);
                                    MobilityAPIResponse apiResponse = mobilityApiHandler.callAPI(endPoint, baseHeaders, reqBody, method);
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (apiResponse.getStatusCode() == 200)
                                            {
                                                if (toastMessage != null)
                                                    Toast.makeText(OverlayMessagingService.this, toastMessage, Toast.LENGTH_SHORT).show();
                                                performDependentAction(dependentActions);
                                            }
                                            else
                                            {
                                                RideRequestUtils.firebaseLogEventWithParams("failure_call_api", "CALL_API", String.valueOf(apiResponse), getApplicationContext());
                                                toastMessage = "Something went Wrong.";
                                                Toast.makeText(OverlayMessagingService.this, toastMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                                }


                            }).start();
                        }
                    } catch (Exception e){
                        RideRequestUtils.firebaseLogEventWithParams("exception_call_api", "CALL_API", String.valueOf(e), this);
                    }
                    break;
                case "OPEN_APP":
                    RideRequestUtils.openApplication(this);
                    performDependentAction(dependentActions);
                    break;
                case "OPEN_SUBSCRIPTION" :
                    Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
                    intent.putExtra("notification_type", "PAYMENT_MODE_MANUAL");
                    intent.putExtra("entity_ids", "");
                    intent.putExtra("entity_type", "");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Utils.logEvent("ny_driver_overlay_join_now", getApplicationContext());
                    performDependentAction(dependentActions);
                    break;
                case "CALL_SUPPORT" :
                    try {
                        String contactSupportNumber = actionDetails.optString("contactSupportNumber", "");
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + contactSupportNumber));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        performDependentAction(dependentActions);
                    } catch (Exception e){
                        System.out.println("Error : " + e);
                    }

                    break;
            }
            stopSelf();
            }
        }
            catch (Exception e){
                RideRequestUtils.firebaseLogEventWithParams("exception_perform_action", "PERFORM_ACTION", String.valueOf(e), this);
            }
    }

    private void performDependentAction(JSONArray dependentActions)
    {
        try{
        if (dependentActions != null && dependentActions.length() > 0)
        {
            for (int i =0; i< dependentActions.length(); i++)
            {
                JSONObject dependentAction = dependentActions.getJSONObject(i);
                performAction2(dependentAction);
            }
        }
        }
        catch (Exception e)
        {
            System.out.println("Exception in performDependentAction: "+ e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowManager != null && messageView != null) {
            windowManager.removeView(messageView);
            messageView = null;
        }
    }

    void openLink (String link) {
        if (link != null) {
            Uri uri = Uri.parse(link); // missing 'http://' will cause crash
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                String message = this.getResources().getString(Utils.getResIdentifier(getApplicationContext(), "no_enabled_browser", "string"));
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                RideRequestUtils.firebaseLogEventWithParams("exception_in_open_link_no_activity", "OPEN_LINK", "ActivityNotFoundException", this);
            } catch (Exception e) {
                RideRequestUtils.firebaseLogEventWithParams("exception_in_open_link", "OPEN_LINK", Objects.requireNonNull(e.getMessage()).substring(0, 40), this);
            }
        }
    }
}
