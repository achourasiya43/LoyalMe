package com.app.loyalme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.helper.Constant;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.UserInfo;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;
import com.app.loyalme.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout view;
    EditText ed_userName, ed_password;
    TextView tvForgotPassword;
    LinearLayout createAccount;
    AppCompatButton btn_login;
    CheckBox rememberMe;
    SessionManager sessionManager;
    String userName, password, deviceToken="N/A";
    Font font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setVariables();
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(view);
                if(isValidData()){
                    if(rememberMe.isChecked()){
                        HashMap<String, String> lastLogin = new HashMap<>();
                        lastLogin.put("userName",userName);
                        lastLogin.put("password",password);
                        sessionManager.setLastLogin(lastLogin);
                    }else{
                        HashMap<String, String> lastLogin = new HashMap<>();
                        lastLogin.put("userName","");
                        lastLogin.put("password","");
                        sessionManager.setLastLogin(lastLogin);
                    }
                    doLoginProcess();
                }
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
    }


    private void setVariables(){
        sessionManager = new SessionManager(this);
        view = (RelativeLayout) findViewById(R.id.activity_login);
        ed_userName = (EditText) findViewById(R.id.ed_userName);
        ed_password = (EditText) findViewById(R.id.ed_password);
        tvForgotPassword = (TextView) findViewById(R.id.tv_forgotPassword);
        createAccount = (LinearLayout) findViewById(R.id.tv_createAccount);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        rememberMe = (CheckBox) findViewById(R.id.rememberMe);

        HashMap<String, String> lastLogin = sessionManager.getLastLogin();
        if(!lastLogin.get("userName").isEmpty()){
            ed_userName.setText(lastLogin.get("userName"));
            ed_password.setText(lastLogin.get("password"));
            rememberMe.setChecked(true);
        }

        TextView text1 = (TextView)findViewById(R.id.text1);
        TextView text2 = (TextView)findViewById(R.id.text2);
        font = new Font(this);
        font.setFont(ed_userName, Font.Lato_Light);
        font.setFont(ed_password, Font.Lato_Light);
        font.setFont(btn_login, Font.Lato_Regular);
        font.setFont(tvForgotPassword, Font.Lato_Light);
        font.setFont(rememberMe, Font.Lato_Light);
        font.setFont(text1, Font.Lato_Light);
        font.setFont(text2, Font.Lato_Regular);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public boolean isValidData() {

        Validation v = new Validation();

        if (!v.isNullValue(ed_userName)) {
            ed_userName.requestFocus();
            showSnackBar("Field cannot be empty");
            return false;

        }else if(!v.isEmailValid(ed_userName)){
            showSnackBar("enter valid email");
            return false;
        } else if (!v.isNullValue(ed_password)) {
            ed_password.requestFocus();
            showSnackBar("Field cannot be empty");
            return false;

        }else if(!isPasswordValid(ed_password)) {

           // ed_password.setError("At least 4 characters required");
            showSnackBar("At least 4 characters required");
            ed_password.requestFocus();
            return false;
        }

        userName = ed_userName.getText().toString().trim();
        password = ed_password.getText().toString().trim();
        //deviceToken = FirebaseInstanceId.getInstance().getToken();
        return true;
    }


    private void  showSnackBar(String msg){
        Util.showSnakbar(view, msg, font);
    }

    private boolean isPasswordValid(EditText editText) {
        String getValue = editText.getText().toString().trim();
        return getValue.length() >3;
    }





    private void doLoginProcess() {
        if(Util.isConnectingToInternet(LoginActivity.this)) {

            final SweetAlertDialog pDialog = Util.getProgressDialog(this);
            pDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServices.Login_Url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("#"+response);
                            JSONObject jsonObject = null;
                            pDialog.dismiss();
                            try {

                                jsonObject = new JSONObject(response);

                                if(jsonObject!=null){

                                    String status = jsonObject.getString("status");
                                    String message = jsonObject.getString("message");

                                    if(status.equalsIgnoreCase(Constant.REQUEST_SUCCESS)){

                                        JSONObject object = jsonObject.getJSONObject("userData");
                                        UserInfo info = new UserInfo();
                                        info.setUserId(Integer.parseInt(object.getString("id")));
                                        info.setEmail(object.getString("email"));
                                        info.setFullName(object.getString("fullName"));
                                        info.setUserName(object.getString("userName"));
                                        info.setBusinessName(object.getString("businessName"));
                                        info.setUserImage(object.getString("profileImage"));
                                        info.setUserImageThumb(object.getString("profileImageSmall"));
                                        info.setAddress(object.getString("address"));
                                        info.setUserType(object.getString("userType"));
                                        info.setQrCodeImage(object.getString("qrCodeImage"));
                                        int isApproved = Integer.parseInt(object.getString("isApproved"));
                                        info.setIsApproved(isApproved);

                                        info.setAuthToken(object.getString("authToken"));

                                        if(object.has("contact"))
                                            info.setContact(object.getString("contact"));

                                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                        sessionManager.createSession(info);

                                        if(info.getUserType().equalsIgnoreCase("user")){
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }else  if(info.getUserType().equalsIgnoreCase("retailer")){
                                            Intent intent = new Intent(LoginActivity.this, RetailerActivity.class);
                                            startActivity(intent);
                                        }
                                        finish();

                                    }else{
                                        Util.showSnakbar(view, message, font);
                                        //Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("ok", null).show();
                                        //Util.displayToast(LoginActivity.this, message, Util.SHORT_TOAST);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Util.showSnakbar(view, error.getMessage(), font);
                            //Toast.makeText(LoginActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    params.put("email",userName);
                    params.put("password", password);
                    params.put("deviceToken", ""+deviceToken);
                    params.put("deviceType","1");
                    return params;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }else{
            Util.showSnakbar(view, "Please Check internet connection.!", font);
          //  Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
           // Util.displayToast(LoginActivity.this, "Please Check internet connection.!", Util.SHORT_TOAST);
        }
    }


}
