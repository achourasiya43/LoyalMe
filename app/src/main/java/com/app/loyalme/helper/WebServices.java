package com.app.loyalme.helper;

/**
 * Created by Dharmraj on 9/5/16.
 */
public class WebServices {


    public static final String BASE_URL = "http://mindiii.com/loyalMe/service/" ;
  //  private static final String BASE_URL = "http://mindiii.com/barber/service/" ;

    public static final String Login_Url = BASE_URL + "userLogin";
    public static final String Registration_Url = BASE_URL + "userRegistration";
    public static final String FORGOT_PASSWORD = BASE_URL + "forgotPassword";
    public static final String EMAIL_VERIFICATION_URL = BASE_URL + "userMailvarify";

    public static final String GET_RETAILERS_LIST_URL = BASE_URL + "user/retailerList";

    public static final String GET_REWARDED_USER_LIST_URL = BASE_URL + "user/userList";
    public static final String QR_SCAN_USER_URL = BASE_URL + "user/qrcodeScanData";
    public static final String SEARCH_USER_BY_NAME_OR_EMAILURL = BASE_URL + "user/searchUser";
    public static final String Add_POINTS_URL = BASE_URL + "user/rewardPointAdd";
    public static final String GET_RETAILER_OFFERS_URL = BASE_URL + "user/offerList";
    public static final String POST_OFFERS_URL = BASE_URL + "user/offerAdd";
    public static final String DELETE_OFFERS_URL = BASE_URL + "user/offerRemove";

    public static final String UPDATE_PROFILE_URL = BASE_URL + "user/profileUpdate";
    public static final String GET_PROFILE_URL = BASE_URL + "user/userInfo";
}
