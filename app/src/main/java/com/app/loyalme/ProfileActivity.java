package com.app.loyalme;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.RewardedUser;
import com.app.loyalme.model.UserInfo;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;
import com.app.loyalme.utils.Validation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    View view;
    EditText ed_totalPurchase, ed_rewardPoint;
    TextView tv_fullName, tv_point, tv_email, tv_subtract, tv_addPoint;
    CircleImageView civ_profileImage;
    AppCompatButton btn_done;
    ImageView iv_backBtn;

    RewardedUser rewardedUser;
    Font font;

    int operator = 1;

    Double rewardPoint, totalPerchase, userRewardPoint = 0.0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /* set custome action bar */
        ActionBar action = getSupportActionBar();
        action.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        action.setCustomView(R.layout.actionbar);

        Toolbar toolbar=(Toolbar)action.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            rewardedUser = (RewardedUser) bundle.getSerializable("rewardedUser");
        }

        setVariavles();


        iv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidData()){

                    Double finalPoints = (rewardedUser.getRewardPoint()+((totalPerchase+rewardPoint)*operator));
                    //Double rPoint = Util.round((totalPerchase+rewardPoint)+rewardedUser.getRewardPoint(),2);
                    Double rPoint = Util.round(finalPoints,2);
                    Double tPerchase = Util.round(totalPerchase+rewardedUser.getTotalPurchase(),2);

                    addRewardedPoint(ProfileActivity.this, rewardedUser.getUserInfo().getUserId(), rPoint, tPerchase);
                }
            }
        });

        tv_addPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operator = 1;
                tv_addPoint.setBackgroundResource(R.drawable.white_broder_left_rounded);
                tv_subtract.setBackgroundResource(0);
                tv_addPoint.setTextColor(Color.parseColor("#F75A53"));
                tv_subtract.setTextColor(Color.WHITE);

            }
        });

        tv_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operator = -1;
                tv_subtract.setBackgroundResource(R.drawable.white_broder_right_rounded);
                tv_addPoint.setBackgroundResource(0);
                tv_subtract.setTextColor(Color.parseColor("#F75A53"));
                tv_addPoint.setTextColor(Color.WHITE);
            }
        });

    }


    private void setVariavles(){

        view = (View) findViewById(R.id.activity_profile);
        civ_profileImage = (CircleImageView) findViewById(R.id.civ_profileImage);
        tv_fullName = (TextView) findViewById(R.id.tv_fullName);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_subtract = (TextView) findViewById(R.id.tv_subtract);
        tv_addPoint = (TextView) findViewById(R.id.tv_addPoint);

        ed_totalPurchase = (EditText) findViewById(R.id.ed_totalPurchase);
        ed_rewardPoint = (EditText) findViewById(R.id.ed_rewardPoint);
        btn_done = (AppCompatButton) findViewById(R.id.btn_done);

        font = new Font(this);
        font.setFont(tv_fullName, Font.Lato_Regular);
        font.setFont(tv_email, Font.Lato_Regular);
        font.setFont(tv_point, Font.Lato_Regular);
        font.setFont(ed_totalPurchase, Font.Lato_Regular);
        font.setFont(ed_rewardPoint, Font.Lato_Regular);
        font.setFont(tv_fullName, Font.Lato_Regular);
        font.setFont(btn_done, Font.Lato_Regular);

        TextView actionTitle = (TextView) findViewById(R.id.toolbat_title);
        iv_backBtn = (ImageView) findViewById(R.id.iv_backBtn);
        iv_backBtn.setVisibility(View.VISIBLE);
        font.setFont(actionTitle, Font.Lato_Regular);
        actionTitle.setText("Profile");
        updateUI();
    }

    private void updateUI(){
        ed_totalPurchase.setText("");
        ed_rewardPoint.setText("");

        if(rewardedUser!=null){
            UserInfo userInfo = rewardedUser.getUserInfo();
            tv_fullName.setText(userInfo.getFullName());
            tv_email.setText(userInfo.getEmail());

            userRewardPoint = rewardedUser.getRewardPoint();
            tv_point.setText(""+userRewardPoint.intValue()+" Points");


            if(!userInfo.getUserImage().isEmpty()){
                Picasso.with(this).load(rewardedUser.getUserInfo().getUserImage()).into(civ_profileImage);
            }
        }
    }


    public boolean isValidData() {

        Validation v = new Validation();
        rewardPoint = 0.0d;
        totalPerchase = 0.0d;

        if(!ed_rewardPoint.getText().toString().trim().isEmpty()){
            rewardPoint = Double.parseDouble(ed_rewardPoint.getText().toString().trim());
        }

        if(!ed_totalPurchase.getText().toString().trim().isEmpty()){
            totalPerchase = Double.parseDouble(ed_totalPurchase.getText().toString().trim());
        }

        if(ed_totalPurchase.getText().toString().trim().isEmpty() && ed_rewardPoint.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "fields cannot be empty.", Toast.LENGTH_SHORT).show();
            ed_totalPurchase.requestFocus();
            return false;

        }else if(operator<1){

            Double finalPoints = totalPerchase+rewardPoint;
            if(userRewardPoint<=0){
                Toast.makeText(this, "User have no reward points.", Toast.LENGTH_SHORT).show();
                return false;
            }else if(finalPoints>userRewardPoint){
                Toast.makeText(this, "User have insufficient reward points.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        //deviceToken = FirebaseInstanceId.getInstance().getToken();
        return true;
    }


    public void addRewardedPoint(final Context context,final int userId ,final Double rewardPoint ,final Double totalPerchase) {

        if (Util.isConnectingToInternet(context)) {

            final SweetAlertDialog dialog = Util.getProgressDialog(context);
            dialog.show();
            StringRequest postRequest = new StringRequest(Request.Method.POST, WebServices.Add_POINTS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                dialog.dismiss();
                                JSONObject jsonResponse = new JSONObject(response);
                                //Log.v("Response",jsonResponse.toString());
                                String status = jsonResponse.getString("status");

                                if (status.equals("success")) {
                                    rewardedUser.setRewardPoint(rewardPoint);
                                    rewardedUser.setTotalPurchase(totalPerchase);
                                    updateUI();
                                    new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Success!")
                                            .setContentText("Reward point add successfully!")
                                            .show();

                                }else {
                                    Util.showSnakbar(view, "Something went to wrong please try again.",font);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            dialog.dismiss();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("authToken", new SessionManager(ProfileActivity.this).getUserInfo().getAuthToken());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userId", ""+userId);
                    params.put("rewardPoint", ""+rewardPoint);
                    params.put("totalPerchase", ""+totalPerchase);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);

        } else {
            Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
        }
    }

}
