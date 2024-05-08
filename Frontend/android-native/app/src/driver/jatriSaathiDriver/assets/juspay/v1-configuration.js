window.version = window.version || {};
let version = "1.0.0";
if (typeof __VERSION__ !== "undefined") {
  version = __VERSION__
}
window.version["configuration"]= version;
window.getMerchantConfig = function () {
  return JSON.stringify({
    "APP_LINK": "https://play.google.com/store/apps/details?id=in.juspay.jatrisaathidriver",
    "USER_APP_LINK" : "https://nammayatri.in/link/rider/kTZ1",
    "PRIVACY_POLICY_LINK": "https://docs.google.com/document/d/1-bcjLOZ_gR0Rda2BNmkKnqVds8Pm23v1e7JbSDdM70E",
    "SPECIAL_ZONE_OTP_VIEW": "true",
    "StringKeys": [
      "NEED_IT_TO_ENABLE_LOCATION",
      "CURRENTLY_WE_ALLOW_ONLY_KARNATAKA_REGISTERED_NUMBER",
      "YOU_ARE_ABOUT_TO_CALL_NAMMA_YATRI_SUPPORT",
      "YOUR_LOCATION_HELPS_OUR_SYSTEM",
      "EARNED_ON_APP",
      "TRAVELLED_ON_APP",
      "REPORT_ISSUE_CHAT_PLACEHOLDER",
      "MY_PLAN_TITLE",
      "CHOOSE_YOUR_PLAN",
      "OFFER_CARD_BANNER_TITLE",
      "TO_CONTINUE_USING_YATRI_SATHI",
      "YATRI_SATHI_FEE_PAYABLE_FOR_DATE",
      "PAYMENT_FAILED_DESC",
      "AADHAAR_LINKING_REQUIRED_DESCRIPTION",
      "COMPLETE_PAYMENT_TO_CONTINUE",
      "GET_READY_FOR_YS_SUBSCRIPTION",
      "SUBSCRIPTION_PLAN_STR",
      "FIND_HELP_CENTRE",
      "HOW_IT_WORKS",
      "GET_SPECIAL_OFFERS",
      "PAYMENT_PENDING_ALERT_DESC",
      "NO_OPEN_MARKET_RIDES",
      "DOWNLOAD_NAMMA_YATRI",
      "START_TAKING_RIDES_AND_REFER",
      "REFERRED_DRIVERS_INFO",
      "REFERRED_CUSTOMERS_INFO",
      "SHARE_NAMMA_YATRI",
      "YATRI_COINS_FAQS_QUES1_ANS1",
      "YATRI_COINS_FAQS_QUES1_ANS2",
      "YATRI_COINS_FAQS_QUES1_ANS3",
      "YATRI_COINS_FAQS_QUES5_ANS1",
      "YATRI_COINS_FAQS_QUES3_ANS1",
      "YATRI_COINS_FAQS_QUES3_ANS2",
      "YATRI_COINS_FAQS_QUES4_ANS1",
      "YATRI_COINS_USAGE_POPUP",
      "EARN_COINS_BY_TAKING_RIDES_AND_REFERRING_THE_APP_TO_OTHERS",
      "NOW_EARN_COINS_FOR_EVERY_RIDE_AND_REFERRAL_AND_USE_THEM_TO_GET_REWARDS",
      "REFER_NAMMA_YATRI_APP_TO_CUSTOMERS_AND_EARN_COINS"
    ],
    "leaderBoard": {
      "isMaskedName": false
    },
    "gotoConfig" : {
      "maxGotoLocations" : 5,
      "enableGoto" : true
    },
    "fontType": "Assets",
    "currency": "₹",
    "isGradient" : "false",
    "BONUS_EARNED" : "false",
    "gradient": [],
    "addFavouriteScreenBackArrow" : "ny_ic_chevron_left_white,https://assets.juspay.in/nammayatri/images/user/ny_ic_chevron_left_white.png",
    "popupBackground" : "#FFFFFF",
    "apiLoaderLottie": "primary_button_loader.json",
    "primaryTextColor": "#FCC32C",
    "primaryBackground": "#2C2F3A",
    "showCorporateAddress" : false,
    "imageUploadOptional" : true,
    "clientName" : "Yatri Sathi",
    "languageList": [{
      "name": "English",
      "value": "EN_US",
      "subtitle": "ইংরেজি"
    },
    {
      "name": "বাংলা",
      "value": "BN_IN",
      "subtitle": "Bengali"
    },
    {
      "name": "हिंदी",
      "value": "HI_IN",
      "subtitle": "Hindi"
    }
    ],
    "engilshInNative" : "ইংরেজি",
    "englishStrings": {
      "MERCHANT_NAME" : "Yatri Sathi",
      "NEED_IT_TO_ENABLE_LOCATION": "Yatri Sathi Driver collect location data to enable share your location to monitor driver current location, even when the app is closed or not in use.",
      "CURRENTLY_WE_ALLOW_ONLY_KARNATAKA_REGISTERED_NUMBER": "Currently,We allow only West Bengal registered number",
      "YOU_ARE_ABOUT_TO_CALL_NAMMA_YATRI_SUPPORT": "You are about to place a call to the Yatri Sathi Support Team. Do you want to proceed?",
      "YOUR_LOCATION_HELPS_OUR_SYSTEM": "Your location helps our system to map down all the near by taxis and get you the quickest ride possible.",
      "EARNED_ON_APP" : "Earned on YS",
      "TRAVELLED_ON_APP" : "Travelled On Yatri Sathi",
      "REPORT_ISSUE_CHAT_PLACEHOLDER" : "Describe your issue. Yatri Sathi will try to resolve it in under 24 hours.",
      "MY_PLAN_TITLE" : "Yatri Sathi Plans",
      "OFFER_CARD_BANNER_TITLE" : "Setup Autopay and pay only ₹1/ride from Jan 1-31",
      "TO_CONTINUE_USING_YATRI_SATHI" : "To continue using Yatri Sathi, please complete your payment for",
      "YATRI_SATHI_FEE_PAYABLE_FOR_DATE" : "Yatri Sathi fee payable for",
      "PAYMENT_FAILED_DESC" : "You may retry payment, or make the payment at your nearest Yatri Sathi booth",
      "AADHAAR_LINKING_REQUIRED_DESCRIPTION" : "To start driving for Yatri Sathi, please \n link your Aadhaar ID",
      "COMPLETE_PAYMENT_TO_CONTINUE" : "To continue using Yatri Sathi, please complete your payment",
      "GET_READY_FOR_YS_SUBSCRIPTION" : "Get ready for\nYatri Sathi Plans!",
      "SUBSCRIPTION_PLAN_STR" : "Yatri Sathi Plan",
      "CHOOSE_YOUR_PLAN" : "Activate Plan Now!",
      "FIND_HELP_CENTRE" : "Find Help Desk",
      "HOW_IT_WORKS" : "How Autopay works?",
      "GET_SPECIAL_OFFERS" : "Guaranteed fixed price until\nJan 1, 2025",
      "PAYMENT_PENDING_ALERT_DESC" : "To continue taking rides on Yatri Sathi, clear your payment dues",
      "NO_OPEN_MARKET_RIDES" : "0 open market rides",
      "DOWNLOAD_NAMMA_YATRI" : "Download Yatri Sathi",
      "START_TAKING_RIDES_AND_REFER" : "Start taking rides and referring drivers to sign up on Yatri Sathi Driver App",
      "REFERRED_DRIVERS_INFO" : "Referred Drivers who have registered on Yatri Sathi",
      "REFERRED_CUSTOMERS_INFO" : "Referred Customers who have registered on Yatri Sathi",
      "SHARE_NAMMA_YATRI" : "Share Yatri Sathi",
      "YATRI_COINS_FAQS_QUES1_ANS1" : "Yatri Coins are the rewards you earn on day-to-day activities on Yatri Sathi like rides, good ratings etc.",
      "YATRI_COINS_FAQS_QUES1_ANS2" : "The earned coins can be used to avail discounts in payments to Yatri Sathi.",
      "YATRI_COINS_FAQS_QUES1_ANS3" : "Each Yatri Coin can be converted to a discount of 0.1 and can be used for discounts on payments to Yatri Sathi. More avenues to avail discounts will be added in the future",
      "YATRI_COINS_FAQS_QUES5_ANS1" : "You must complete your first ride with Yatri Sathi to start earning Yatri Coins.",
      "YATRI_COINS_FAQS_QUES3_ANS1" : "Coins are earned through day-to-day activities on the app like rides, good ratings etc. You don’t need to do anything different.",
      "YATRI_COINS_FAQS_QUES3_ANS2" : "You can earn more coins by exhibiting good behavior like low cancellations, getting good ratings from customers, etc. You lose coins by exhibiting bad behaviour like high unjustified cancellations, poor (1 or 2 star ratings) etc.",
      "YATRI_COINS_FAQS_QUES4_ANS1" : "You can use the earned coins to pay your subscription dues free-of-cost within the validity period",
      "YATRI_COINS_USAGE_POPUP" : "Yatri Coins will be converted into discounts that you can avail against your ride charges.",
      "EARN_COINS_BY_TAKING_RIDES_AND_REFERRING_THE_APP_TO_OTHERS" : "Coins kamane ke liye app par active rahein",
      "NOW_EARN_COINS_FOR_EVERY_RIDE_AND_REFERRAL_AND_USE_THEM_TO_GET_REWARDS" : "Ab app par har kriya ke liye coins kamayein aur in coins ko bhugtan me use karein.",
      "REFER_NAMMA_YATRI_APP_TO_CUSTOMERS_AND_EARN_COINS" : "Refer Yatri Sathi app to customers and earn coins"
    },
    "hindiStrings": {
      "MERCHANT_NAME" : "यात्री साथी",
      "NEED_IT_TO_ENABLE_LOCATION": "Yatri Sathi partner ड्राइवर के लोकेशन की निगरानी के लिए अपना स्थान साझा करने के लिए लोकेशन डेटा एकत्र करता है, तब भी जब ऐप बंद हो या उपयोग में न हो।",
      "CURRENTLY_WE_ALLOW_ONLY_KARNATAKA_REGISTERED_NUMBER": "Currently,We allow only West Bengal registered number",
      "YOU_ARE_ABOUT_TO_CALL_NAMMA_YATRI_SUPPORT": "आप जात्री साथी सपोर्ट टीम को कॉल करने वाले हैं। क्या आपकी आगे बढ़ने की इच्छा है?",
      "YOUR_LOCATION_HELPS_OUR_SYSTEM": "आपका स्थान हमारे सिस्टम को आस पास के सभी टैक्सियों को मैप करने में सहायता कर्ता है और आपको सबसे तेज सवारी प्रदान करता है",
      "EARNED_ON_APP" : "YS पर अर्जित मूल्य",
      "TRAVELLED_ON_APP" : "यात्री साथी पर तय की गई दूरी",
      "REPORT_ISSUE_CHAT_PLACEHOLDER" : "अपनी समस्या बताएं। यात्री साथी 24 घंटे के अंदर इसका समाधान करने का प्रयास करेगा।",
      "MY_PLAN_TITLE" : "यात्री साथी प्लान्स",
      "OFFER_CARD_BANNER_TITLE" : "ऑटोपे सेटअप करें और 1-31 जनवरी तक केवल ₹1/सवारी का पेमेंट करें",
      "TO_CONTINUE_USING_YATRI_SATHI" : "यात्री साथी का उपयोग जारी रखने के लिए",
      "YATRI_SATHI_FEE_PAYABLE_FOR_DATE" : "यात्री साथी शुल्क लागू" ,
      "PAYMENT_FAILED_DESC" : "आप भुगतान को पुनः प्रयास कर सकते हैं, या अपने नजदीकी यात्री साथी बूथ पर भुगतान कर सकते हैं",
      "AADHAAR_LINKING_REQUIRED_DESCRIPTION" : "यात्री साथी के लिए ड्राइविंग शुरू करने के लिए, कृपया अपना आधार आईडी लिंक करें",
      "COMPLETE_PAYMENT_TO_CONTINUE" : "यात्री साथी का उपयोग जारी रखने के लिए, कृपया अपना भुगतान पूरा करें",
      "GET_READY_FOR_YS_SUBSCRIPTION" : "यात्री साथी योजनाओं के लिए तैयार रहें!",
      "SUBSCRIPTION_PLAN_STR" : "यात्री साथी योजना",
      "CHOOSE_YOUR_PLAN" : "योजना अभी सक्रिय करें!",
      "FIND_HELP_CENTRE" : "सहायता डेस्क ढूंढें",
      "HOW_IT_WORKS" : "ऑटोपे कैसे काम करता है?",
      "GET_SPECIAL_OFFERS" : "1 जनवरी, 2025 तक निश्चित मूल्य की गारंटी",
      "PAYMENT_PENDING_ALERT_DESC" : "यात्री साथी पर यात्रा जारी रखने के लिए, अपनी शेष राशि चुकाएं",
      "NO_OPEN_MARKET_RIDES" : "0 ओपन मार्केट राइड",
      "DOWNLOAD_NAMMA_YATRI" : "यात्री साथी डाउनलोड करें",
      "START_TAKING_RIDES_AND_REFER" : "यात्री साथी ड्राइवर ऐप पर साइन अप करने के लिए सवारी लेना और ड्राइवरों को रेफर करना शुरू करें",
      "REFERRED_DRIVERS_INFO" : "यात्री साथी पर पंजीकृत हुए सुझाए गए ड्राइवर्स",
      "REFERRED_CUSTOMERS_INFO" : "यात्री साथी पर पंजीकृत हुए सुझाए गए ग्राहक",
      "SHARE_NAMMA_YATRI" : "यात्री साथी को साझा करें",
      "YATRI_COINS_FAQS_QUES1_ANS1" : "यात्री साथी पर दैनिक गतिविधियों जैसे कि सवारियों, अच्छी रेटिंग आदि पर आपकी प्राप्तियों को यात्री सिक्के कहा जाता है।",
      "YATRI_COINS_FAQS_QUES1_ANS2" : "प्राप्त किए गए सिक्के को यात्री साथी के भुगतानों में छूट प्राप्त करने के लिए इस्तेमाल किया जा सकता है।",
      "YATRI_COINS_FAQS_QUES1_ANS3" : "प्रत्येक यात्री सिक्का 0.1 की छूट में परिवर्तित किया जा सकता है और यात्री साथी के भुगतानों में छूट प्राप्त करने के लिए उपयोग किया जा सकता है। भविष्य में छूट पाने के और भी रास्ते जोड़े जाएंगे।",
      "YATRI_COINS_FAQS_QUES5_ANS1" : "यात्री कोइन्स अर्जित करना शुरू करने के लिए आपको यात्री साथी के साथ अपनी पहली यात्रा पूरी करनी होगी।",
      "YATRI_COINS_FAQS_QUES3_ANS1" : "सिक्के एप्लिकेशन पर दैनिक गतिविधियों जैसे कि राइड्स, अच्छी रेटिंग इत्यादि के माध्यम से कमाए जाते हैं। आपको कुछ अलग नहीं करना पड़ता।",
      "YATRI_COINS_FAQS_QUES3_ANS2" : "आप अच्छे व्यवहार का प्रदर्शन करके अधिक सिक्के कमा सकते हैं, जैसे कि कम रद्दीकरण, ग्राहकों से अच्छी रेटिंग प्राप्त करना, आदि। आप बुरे व्यवहार का प्रदर्शन करके सिक्के खो सकते हैं, जैसे कि अधिक अनावश्यक रद्दीकरण, कम रेटिंग (1 या 2 स्टार) इत्यादि।",
      "YATRI_COINS_FAQS_QUES4_ANS1" : "आप प्राप्त किए गए सिक्कों का उपयोग करके अपने सदस्यता के देयता का भुगतान मुफ्त में कर सकते हैं, जो योग्यता की अवधि के भीतर हो।",
      "YATRI_COINS_USAGE_POPUP" : "यात्री सिक्के छूटों में परिवर्तित किए जाएंगे जिन्हें आप अपने राइड शुल्क के खिलाफ लाभ उठा सकते हैं।",
      "EARN_COINS_BY_TAKING_RIDES_AND_REFERRING_THE_APP_TO_OTHERS" : "सिक्के कमाने के लिए एप्लिकेशन पर सक्रिय रहें।",
      "NOW_EARN_COINS_FOR_EVERY_RIDE_AND_REFERRAL_AND_USE_THEM_TO_GET_REWARDS" : "अब आप पर हर क्रिया के लीयते कॉइन कमाएं और इन साइंस को भुगतान में उसे करे।",
      "REFER_NAMMA_YATRI_APP_TO_CUSTOMERS_AND_EARN_COINS" : "ग्राहकों को यात्री साथी ऐप साझा करें और सिक्के अर्जित करें"
    },
    "bengaliStrings": {
      "MERCHANT_NAME" : "যাত্রী সাথী",
      "NEED_IT_TO_ENABLE_LOCATION": "জাটি সাথি ড্রাইভার ড্রাইভারের বর্তমান অবস্থান নিরীক্ষণের জন্য আপনার অবস্থানটি ভাগ করে নিতে সক্ষম করতে অবস্থানের ডেটা সংগ্রহ করে, এমনকি অ্যাপটি বন্ধ থাকলেও বা ব্যবহার না করা হয়।",
      "CURRENTLY_WE_ALLOW_ONLY_KARNATAKA_REGISTERED_NUMBER": "বর্তমানে, আমরা শুধুমাত্র পশ্চিমবঙ্গ নিবন্ধিত নম্বর অনুমোদন করি",
      "YOU_ARE_ABOUT_TO_CALL_NAMMA_YATRI_SUPPORT": "আপনি জাত্রি সাথি সমর্থন দলকে কল করতে চলেছেন। আপনি কি এগিয়ে যেতে চান?",
      "YOUR_LOCATION_HELPS_OUR_SYSTEM": "আপনার অবস্থান আমাদের সিস্টেমকে ট্যাক্সি দ্বারা আশেপাশের সমস্ত স্থান ম্যাপ করতে এবং আপনাকে দ্রুততম রাইড করতে সাহায্য করে৷",
      "EARNED_ON_APP" : "YS এ অর্জিত মূল্য",
      "TRAVELLED_ON_APP" : "যাত্রী সাথীতে দূরত্ব ভ্রমণ",
      "REPORT_ISSUE_CHAT_PLACEHOLDER" : "আপনার সমস্যাটি বর্ণনা করুন। যাত্রী সাথী 24 ঘন্টার মধ্যে এটি সমাধান করার চেষ্টা করবেন।",
      "MY_PLAN_TITLE" : "যাত্রী সাথী পরিকল্পনা",
      "OFFER_CARD_BANNER_TITLE" : "অটোপে সেটআপ করুন এবং জানুয়ারী 1-31 এর মধ্যে শুধুমাত্র ₹1/রাইড প্রদান করুন",
      "TO_CONTINUE_USING_YATRI_SATHI" : "Yatri Sathi ব্যবহার চালিয়ে যেতে, অনুগ্রহ করে আপনার অর্থপ্রদান সম্পূর্ণ করুন",
      "YATRI_SATHI_FEE_PAYABLE_FOR_DATE" : "Yatri Sathi ফি জন্য প্রদেয়" ,
      "PAYMENT_FAILED_DESC" : "আপনি আবার অর্থপ্রদানের চেষ্টা করতে পারেন, অথবা আপনার নিকটস্থ Yatri Sathi বুথে অর্থপ্রদান করতে পারেন",
      "AADHAAR_LINKING_REQUIRED_DESCRIPTION" : "যাত্রী সাথীর জন্য গাড়ি চালানো শুরু করতে, দয়া করে \n আপনার আধার আইডি লিঙ্ক করুন",
      "COMPLETE_PAYMENT_TO_CONTINUE" : "Yatri Sathi ব্যবহার চালিয়ে যেতে, অনুগ্রহ করে আপনার অর্থপ্রদান সম্পূর্ণ করুন",
      "GET_READY_FOR_YS_SUBSCRIPTION" : "যাত্রী সাথী পরিকল্পনার জন্য প্রস্তুত হন!",
      "SUBSCRIPTION_PLAN_STR" : "যাত্রী সাথী পরিকল্পনা",
      "CHOOSE_YOUR_PLAN" : "এখনই প্ল্যান সক্রিয় করুন!",
      "FIND_HELP_CENTRE" : "সহায়তা ডেস্ক খুঁজুন",
      "HOW_IT_WORKS" : "স্বতঃপে কিভাবে কাজ করে?",
      "GET_SPECIAL_OFFERS" : "জানুয়ারি 1, 2025 পর্যন্ত গ্যারান্টিযুক্ত নির্দিষ্ট মূল্য",
      "PAYMENT_PENDING_ALERT_DESC" : "যাত্রী সাথীতে যাত্রা চালিয়ে যেতে, আপনার পেমেন্ট বকেয়া পরিশোধ করুন",
      "NO_OPEN_MARKET_RIDES" : "0 ওপেন মার্কেট রাইডস",
      "DOWNLOAD_NAMMA_YATRI" : "যাত্রী সাথী ডাউনলোড করুন",
      "START_TAKING_RIDES_AND_REFER" : "যাত্রী সাথী ড্রাইভার অ্যাপে সাইন আপ করতে রাইড করা এবং ড্রাইভারদের রেফার করা শুরু করুন",
      "REFERRED_DRIVERS_INFO" : "যাত্রী সাথীতে নিবেশন করেছেন যাদেরকে সূচনা দেওয়া হয়েছে",
      "REFERRED_CUSTOMERS_INFO" : "যাত্রী সাথীতে নিবেশন করেছেন যাদেরকে সূচনা দেওয়া হয়েছে",
      "SHARE_NAMMA_YATRI" : "যাত্রী সহযোগী ভাগ করুন",
      "YATRI_COINS_FAQS_QUES1_ANS1" : "যাত্রী সাথীতে দিনব্যাপী কার্যকলাপে রাইড, রেফারেন্স, ভালো রেটিং ইত্যাদি কিছু করে আপনি যাত্রী কয়েন অর্জন করেন।",
      "YATRI_COINS_FAQS_QUES1_ANS2" : "অর্জিত কয়েনগুলি যাত্রী সাথীতে অর্থ প্রদানে ছাড় পেতে ব্যবহার করা যেতে পারে।",
      "YATRI_COINS_FAQS_QUES1_ANS3" : "প্রতিটি যাত্রী কয়েনকে রূপান্তরিত করা যাবে টাকা ছাড়ে। 0.10 এবং সাবস্ক্রিপশন প্ল্যানে ডিসকাউন্ট পাওয়ার জন্য ব্যবহার করা যেতে পারে। ডিসকাউন্ট পাওয়ার আরও সুযোগ ভবিষ্যতে যোগ করা হবে",
      "YATRI_COINS_FAQS_QUES3_ANS1" : "রাইড, ভালো রেটিং ইত্যাদির মতো অ্যাপে প্রতিদিনের কার্যকলাপের মাধ্যমে কয়েন উপার্জন করা যেতে পারে। আপনাকে আলাদা কিছু করতে হবে না।",
      "YATRI_COINS_FAQS_QUES3_ANS2" : "আপনি ভাল আচরণ বজায় রেখে অতিরিক্ত কয়েন উপার্জন করতে পারেন, যেমন রাইড বাতিলকরণ কমিয়ে আনা এবং গ্রাহকদের কাছ থেকে ধারাবাহিকভাবে উচ্চ রেটিং পাওয়া। আপনি খারাপ আচরণ দেখানোর জন্য কয়েন হারাবেন যেমন ঘন ঘন অন্যায্য বাতিলকরণ বা খারাপ রেটিং পাওয়া (1 বা 2 স্টার রেটিং)",
      "YATRI_COINS_FAQS_QUES4_ANS1" : "আপনি বৈধ সময়ের মধ্যে বিনামূল্যে আপনার সাবস্ক্রিপশন ফি প্রদান করতে আপনার উপার্জন করা কয়েন ব্যবহার করতে পারেন।",
      "YATRI_COINS_FAQS_QUES5_ANS1" : "আপনার প্রথম ইউসাথী রাইড সম্পন্ন করতে হবে যাত্রী কয়েন উপার্জন করার জন্য।",
      "YATRI_COINS_USAGE_POPUP" : "আপনি অ্যাপে প্রতিটি কার্যের জন্য কয়েন অর্জন করতে পারেন এবং এই কয়েন গুলি অর্থ পরিশোধে ব্যবহার করতে পারেন।",
      "EARN_COINS_BY_TAKING_RIDES_AND_REFERRING_THE_APP_TO_OTHERS" : "যাত্রী কয়েনগুলি ডিসকাউন্টে পরিণত হবে যা আপনি আপনার যাত্রার খরচের জন্য ব্যবহার করতে পারেন।",
      "NOW_EARN_COINS_FOR_EVERY_RIDE_AND_REFERRAL_AND_USE_THEM_TO_GET_REWARDS" : "কয়েন আয় করতে অ্যাপ্লিকেশনে সক্রিয় থাকুন।",
      "REFER_NAMMA_YATRI_APP_TO_CUSTOMERS_AND_EARN_COINS" : "গ্রাহকদেরকে যাত্রী সাথী অ্যাপ সুপারিশ করুন এবং কয়েন পেতে।"
    },
    "logs": ["JUSPAY","FIREBASE","CLEVERTAP"]
    , "fontName" : "PlusJakartaSans"
    , "fontKannada" : "NotoSansKannada"
    , "allowAllMobileNumber" : false
    , "showGenderBanner" : false
    , "defaultLanguage" : "EN_US"
    , "navigationAppConfig" : {
      "query" : "google.navigation:q=%f,%f"
      , "packageName" : "com.google.android.apps.maps"
    }
    , "subscriptionConfig" : {
      "enableBlocking" : true,
      "completePaymentPopup" : false,
      "supportNumber" : "08069724949",
      "enableSubscriptionPopups" : true,
      "maxDuesLimit" : 500.0,
      "faqLink" : "https://yatrisathi.in/plans/",
      "optionsMenuItems" : {
        "viewFaqs" : true,
        "viewAutopayDetails" : true,
        "paymentHistory" : true,
        "kioskLocation" : true
      },
      "offerBannerConfig" : {
        "showDUOfferBanner" : true,
        "offerBannerValidTill" : "2023-12-01T00:00:00",
        "offerBannerDeadline" : "Jan 1-31-*$*-ಜನವರಿ 1-31-*$*-1-31 जनवरी-*$*-ஜனவரி 1-31-*$*-জানুয়ারী 1-31",
        "offerBannerPlans" : ["25ade579-fd9c-4288-a015-337af085e66c"],
      },
      "lowDuesLimit" : 150.0,
      "highDueWarningLimit" : 250.0,
      "gradientConfig" : [{"id" : "c1a27b2c-8287-4d79-a5d9-99e1a0026203", colors : ["#29FF4D35", "#29FFE588"]},{"id" : "5eed42c1-2388-4a86-b68b-d9da2f674091", colors : ["#29FF4D35", "#29FFE588"]},{"id" : "b6d61915-65bb-4ca9-bbb7-a90be735a722", colors : ["#29FF4D35", "#29FFE588"]}],
      "enableSubscriptionSupportPopup" : true,
      "myPlanYoutubeLink" : "https://www.youtube.com/playlist?list=PLvMgI4c44A9Y2bykEuDAtHzgcubXOYqgU-*$*-https://www.youtube.com/playlist?list=PLvMgI4c44A9Y2bykEuDAtHzgcubXOYqgU-*$*-https://www.youtube.com/playlist?list=PLvMgI4c44A9Zl0IIQcZa7ZJrSjWPLfxpA-*$*-https://www.youtube.com/playlist?list=PLvMgI4c44A9Y8NLs_8TXc7biX-JkobrGB",
      "overlayYoutubeLink" : "https://youtube.com/shorts/nyJ1bIOsGfo-*$*-https://youtube.com/shorts/nyJ1bIOsGfo-*$*-https://youtu.be/RSKNT3NccPo-*$*-https://youtu.be/RSKNT3NccPo",
      "earnAmountInADay" : 5000,
      "showFeeBreakup" : true
    } 
    , "OTP_MESSAGE_REGEX" : "is your OTP for login to [A-Za-z]+ [A-Za-z]+ [A-Za-z]+"
    , "autoPayBanner" : false
    , "referralType" : "QRScreen"
    , "profile" :
        { "bookingOptionMenuForTaxi" : true
        }
    , "profileVerification" : {
      "aadharVerificationRequired" : true
    } 
    , "bottomNavConfig" : {
      "subscription" : 
        { "isVisible" : true,
          "showNew" : true
        },
      "referral" : 
        { 
          "showNew" : true
        }
    }
    , "otpRegex" :  "is your OTP for login to [A-Za-z]+ [A-Za-z]+ [A-Za-z]+"
    , "termsLink" : "https://docs.google.com/document/d/19pQUgTWXBqcM7bjy4SU1-z33r-iXsdPMfZggBTXbdR4"
    , "termsVersion" : 1.0
    , "enableDriverReferral": true
    , "enableCustomerReferral": true
    , "privacyLink" : "https://docs.google.com/document/d/1-bcjLOZ_gR0Rda2BNmkKnqVds8Pm23v1e7JbSDdM70E"
    , "feature" : {
      "enableBonus" : false
      , "enableImageUpload" : true
      , "enableGender" : false
      , "enableOtpRide" : true
      , "enableYatriCoins" : true
    }
    , "appData" : {
      "link" : "https://play.google.com/store/apps/details?id=in.juspay.jatrisaathidriver"
      , "name" : "Yatri Sathi"
    }
    , "vehicle" : {
      "validationPrefix" :  "WB"
    }
    , "banners" :{
      "autoPay" : false
    }
    , "referral": {
      "type": "LeaderBoard",
      "link" : "https://nammayatri.in/link/rider/kTZ1",
      "customerAppId" : "in.juspay.jatrisaathi",
      "driverAppId" : "in.juspay.jatrisaathidriver"
    }
    , "enableMockLocation" : false
    , "flowConfig" : {
      "chooseCity" : {
        "runFlow" : false
      }
    }
    , "permissions" : {
      "locationPermission" : true,
      "notification" : false
    }
    , "homeScreen" : {
      "specialRideOtpView" : true,
      "showGenderBanner" : false
    }
    , "rideRequest" : {
        "negotiationUnit" : {
            "cab" : "10"
        }
    }
  , "rideCompletedCardConfig" : {
      "lottieQRAnim" : true
  }
  , "waitTimeConfig" : {
    "enableWaitTime" : false
  }
  , "coinsConfig" : {
    "minCoinSliderValue" : 200,
    "stepFunctionForCoinConversion" : 200,
    "numOfRideThresholdForCoins" : "8+",
    "eightPlusRidesCoins" : "+40",
    "rideCompletedCoins" : "+4",
    "fiveStarRatingCoins" : "+20",
    "oneOrTwoStarRatingCoins" : "-40",
    "rideCancellationCoins" : "-40",
    "whatAreYatriCoinFAQ" : "",
    "howToEarnYatriCoinFAQ" : "",
    "howToRedeemYatriCoinFAQ" : "",
    "rideCompletedCoinEvent" : true,
    "eightRideCoinEvent" : true,
    "bookingCancelCoinEvent" : true,
    "fiveStarCoinEvent" : true,
    "oneTwoStarCoinEvent" : true,
  }
  , "cityConfig" : [
    {
      "cityName" : "Kolkata",
      "mapImage" : "",
      "cityCode" : "std:033",
      "showSubscriptions" : true,
      "enableAdvancedBooking" : false,
      "advancedRidePopUpYoutubeLink" : "" ,
      "callDriverInfoPost": false, // Dummy link need to change
      "cityLat" : 22.5354064,
      "cityLong" : 88.2649516,
      "supportNumber" : "",
      "languageKey" : "BN_IN",
      "showDriverReferral" : true,
      "showCustomerReferral" : true,
      "uploadRCandDL" : true, 
      "enableYatriCoins" : true,
      "vehicleNSImg" : "",
      "showEarningSection" : true,
      "registration" : {
          "supportWAN" : "",
          "callSupport" : true,
          "whatsappSupport" : false
      },
      "variantSubscriptionConfig" : {
        "enableVariantBasedSubscription" : false,
        "variantList" : [],
        "enableCabsSubscriptionView" : false,
        "staticViewPlans" : []
      },
      "referral" : {
          "domain" : "https://www.yatrisathi.in"
        , "customerAppId" : "in.juspay.jatrisaathi"
        , "driverAppId" : "in.juspay.jatrisaathidriver"
      },
      "waitingCharges" : 1.50,
      "waitingChargesConfig" : {
           "cab" : {
             "freeSeconds" : 300,
             "perMinCharges" : 1.0
           },
           "auto" : {
             "freeSeconds" : 180,
             "perMinCharges" : 1.50
           }
         },
       "rateCardConfig" : defRateCardConfig,
      "assets" :{
        "auto_image" : "ny_ic_auto_side_view",
        "onboarding_auto_image" : "ny_ic_auto_side"
      }
    }
  ]
  })
}

let defRateCardConfig = {
    "showLearnMore" : false,
    "learnMoreVideoLink" : ""
  }