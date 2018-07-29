package com.app.loyalme;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.helper.Constant;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;
import com.app.loyalme.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPasswordActivity extends AppCompatActivity {

    AppCompatButton btn_submit;
    RelativeLayout activity_forgot_password;
    EditText ed_email;

    String email;

    Handler handler = new Handler();
    Runnable runnable;
    Font font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        activity_forgot_password = (RelativeLayout) findViewById(R.id.activity_forgot_password);
        ed_email = (EditText) findViewById(R.id.ed_email);


        btn_submit = (AppCompatButton) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValidData()){
                    btn_submit.setEnabled(false);
                    doForgotPasswordProcess(ForgotPasswordActivity.this, activity_forgot_password);
                }

            }
        });

        font = new Font(this);
        font.setFont(ed_email, Font.Lato_Light);
        font.setFont(btn_submit, Font.Lato_Regular);
    }


    public boolean isValidData() {

        Validation v = new Validation();

        if (v.isEmpty(ed_email)) {
            ed_email.requestFocus();
            return false;

        }else if (!isEmailValid(ed_email)) {

            ed_email.setError("Invalid email id");
            ed_email.requestFocus();
            return false;
        }

        email = ed_email.getText().toString().trim();
        return true;
    }


    private boolean isEmailValid(EditText editText) {
        String getValue = editText.getText().toString().trim();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(getValue).matches();
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }



    private void doForgotPasswordProcess(final AppCompatActivity context, final View view) {

        if(Util.isConnectingToInternet(context)) {

            final SweetAlertDialog pDialog = Util.getProgressDialog(this);
            pDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServices.FORGOT_PASSWORD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("#"+response);
                            JSONObject jsonObject = null;
                            pDialog.dismiss();
                            btn_submit.setEnabled(true);
                            try {

                                jsonObject = new JSONObject(response);

                                if(jsonObject!=null){

                                    String status = jsonObject.getString("status");
                                    String message = jsonObject.getString("message");

                                    if(status.equalsIgnoreCase(Constant.REQUEST_SUCCESS)){

                                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("ok", null).show();
                                        int SPLASH_TIME_OUT = 2500;
                                        handler.postDelayed(runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                onBackPressed();
                                            }
                                        }, SPLASH_TIME_OUT);

                                    }else{

                                       // Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("ok", null).show();
                                        Util.showSnakbar(view, message, font);
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
                            //Toast.makeText(context,error.getMessage(), Toast.LENGTH_LONG).show();
                            Util.showSnakbar(view, error.getMessage(), font);
                            btn_submit.setEnabled(true);
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    params.put("email",email);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }else{

            Util.showSnakbar(view, "Please Check internet connection.!", font);
           // Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            btn_submit.setEnabled(true);
            // Util.displayToast(LoginActivity.this, "Please Check internet connection.!", Util.SHORT_TOAST);
        }
    }

}
