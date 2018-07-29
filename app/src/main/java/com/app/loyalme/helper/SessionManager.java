package com.app.loyalme.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.loyalme.LoginActivity;
import com.app.loyalme.model.UserInfo;

import java.util.HashMap;


public class SessionManager {

    private SharedPreferences mypref ;
    private SharedPreferences lastLogin_pref;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor lastlogin_editor;
    private static final String PREF_NAME = "LoyalMe";
    private static final String PREF_LAST_LOGIN = "LoyalMe_Lastlogin";
    private static final String IS_LOGGEDIN = "isLoggedIn";


    public SessionManager(Context context ){
        mypref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();

        lastLogin_pref = context.getSharedPreferences(PREF_LAST_LOGIN, Context.MODE_PRIVATE);
        lastlogin_editor = lastLogin_pref.edit();
        lastlogin_editor.apply();

    }

    public void createSession(UserInfo userInfo){

        editor.putInt(Constant.USER_ID, userInfo.getUserId());
        editor.putInt(Constant.IS_APPROVED, userInfo.getIsApproved());
        editor.putString(Constant.FULL_NAME, userInfo.getFullName());
        editor.putString(Constant.USER_NAME, userInfo.getUserName());
        editor.putString(Constant.BUSINESS_NAME, userInfo.getBusinessName());
        editor.putString(Constant.EMAIL, userInfo.getEmail());
        editor.putString(Constant.CONTACT_NO, userInfo.getContact());
        editor.putString(Constant.ADDRESS, userInfo.getAddress());
        editor.putString(Constant.USER_IMAGE, userInfo.getUserImage());
        editor.putString(Constant.USER_IMAGE_THUMB, userInfo.getUserImageThumb());
        editor.putString(Constant.AUTH_TOKEN, userInfo.getAuthToken());
        editor.putString(Constant.USER_TYPE, userInfo.getUserType());
        editor.putString(Constant.USER_QR_IMAGE, userInfo.getQrCodeImage());
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.commit();
    }

    public void updateProfile(UserInfo userInfo) {

        editor.putInt(Constant.IS_APPROVED, userInfo.getIsApproved());
        editor.putString(Constant.FULL_NAME, userInfo.getFullName());
        editor.putString(Constant.USER_NAME, userInfo.getUserName());
        editor.putString(Constant.BUSINESS_NAME, userInfo.getBusinessName());
        editor.putString(Constant.EMAIL, userInfo.getEmail());
        editor.putString(Constant.CONTACT_NO, userInfo.getContact());
        editor.putString(Constant.ADDRESS, userInfo.getAddress());
        editor.putString(Constant.USER_IMAGE, userInfo.getUserImage());
        editor.putString(Constant.USER_IMAGE_THUMB, userInfo.getUserImageThumb());
        editor.commit();
    }


    public UserInfo getUserInfo(){

        UserInfo userInfo = new UserInfo();

        userInfo.setUserId(mypref.getInt(Constant.USER_ID, -1));
        userInfo.setIsApproved(mypref.getInt(Constant.IS_APPROVED, -1));
        userInfo.setFullName(mypref.getString(Constant.FULL_NAME, "N/A"));
        userInfo.setUserName(mypref.getString(Constant.USER_NAME, ""));
        userInfo.setBusinessName(mypref.getString(Constant.BUSINESS_NAME, "N/A"));
        userInfo.setEmail(mypref.getString(Constant.EMAIL, ""));
        userInfo.setUserImage(mypref.getString(Constant.USER_IMAGE, ""));
        userInfo.setUserImageThumb(mypref.getString(Constant.USER_IMAGE_THUMB, ""));
        userInfo.setUserType(mypref.getString(Constant.USER_TYPE, ""));
        userInfo.setAddress(mypref.getString(Constant.ADDRESS, "N/A"));
        userInfo.setContact(mypref.getString(Constant.CONTACT_NO, "N/A"));
        userInfo.setAuthToken(mypref.getString(Constant.AUTH_TOKEN, ""));
        userInfo.setQrCodeImage(mypref.getString(Constant.USER_QR_IMAGE, ""));

        return userInfo;
    }

    public boolean isLoggedIn(){
        return mypref.getBoolean(IS_LOGGEDIN, false);
    }

    public void logout(Activity activity){
        editor.clear();
        editor.commit();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void setLastLogin(HashMap<String,String> loginDetail){

        lastlogin_editor.putString("userName", loginDetail.get("userName"));
        lastlogin_editor.putString("password", loginDetail.get("password"));
        lastlogin_editor.commit();
    }

    public HashMap<String,String> getLastLogin(){

        HashMap<String,String> loginDetail = new HashMap<>();
        loginDetail.put("userName", lastLogin_pref.getString("userName", ""));
        loginDetail.put("password", lastLogin_pref.getString("password", ""));
        return loginDetail;
    }


}
