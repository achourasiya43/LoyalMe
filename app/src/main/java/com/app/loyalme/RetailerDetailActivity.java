package com.app.loyalme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.adapter.AdapterOffers;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.Offer;
import com.app.loyalme.model.Retailer;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetailerDetailActivity extends AppCompatActivity implements Animation.AnimationListener {

    TextView tv_shopName, tv_distance, tv_email, tv_address, tv_point, tv_phoneNo, tv_contactDetail, tv_checkOffer,tv_message;
    ImageView iv_backBtn, iv_shopImage, iv_map;
    LinearLayout contactDetail_layout;
    LinearLayout offerLayout;
    ScrollView activity_retailer_detail;
    Font font;
    Retailer retailer;
    int zoomLevel=16;
    boolean isContactDetailActive;

    List<Offer> offers;
    AdapterOffers adapterOffers;
    RecyclerView recyclerView;
    SessionManager sessionManager;

    Animation animFadeIn, animFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_detail);

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
            retailer = (Retailer) bundle.getSerializable("retailer");
        }

        setVariables();

        tv_contactDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_contactDetail.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background_primary));
                tv_checkOffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_broder_primary));
                tv_checkOffer.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_contactDetail.setTextColor(Color.WHITE);
                recyclerView.setVisibility(View.GONE);

                if(!isContactDetailActive){

                    // Make fade in elements Visible first
                    contactDetail_layout.setVisibility(View.VISIBLE);
                    // start fade in animation
                    contactDetail_layout.startAnimation(animFadeIn);

                    // start fade out animation
                    offerLayout.startAnimation(animFadeOut);
                    offerLayout.setVisibility(View.GONE);

                    isContactDetailActive=true;
                }
            }
        });

        tv_checkOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_checkOffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background_primary));
                tv_contactDetail.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_broder_primary));
                tv_contactDetail.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_checkOffer.setTextColor(Color.WHITE);

                if(isContactDetailActive){

                    recyclerView.setVisibility(View.VISIBLE);
                    if(offers.size()>0){
                        adapterOffers.notifyDataSetChanged();
                    }else {
                        tv_message.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                    offerLayout.setVisibility(View.VISIBLE);
                    // start fade in animation
                    offerLayout.startAnimation(animFadeIn);
                    // start fade out animation
                    contactDetail_layout.startAnimation(animFadeOut);
                    contactDetail_layout.setVisibility(View.GONE);
                    isContactDetailActive=false;
                }

            }
        });

        iv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /*iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(zoomLevel==20){
                    zoomLevel = 13;
                }
                getStaticMapView(zoomLevel);
            }
        });*/

        tv_phoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!retailer.getPhoneNo().equals("N/A")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+retailer.getPhoneNo()));
                    startActivity(intent);
                }
            }
        });

        getRetailerOffers(this, retailer);
    }


    private void setVariables(){
        tv_shopName = (TextView) findViewById(R.id.tv_shopName);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_phoneNo = (TextView) findViewById(R.id.tv_phoneNo);
        tv_contactDetail = (TextView) findViewById(R.id.tv_contactDetail);
        tv_checkOffer = (TextView) findViewById(R.id.tv_checkOffer);
        tv_message = (TextView) findViewById(R.id.tv_message);

        iv_backBtn = (ImageView) findViewById(R.id.iv_backBtn);
        iv_shopImage = (ImageView) findViewById(R.id.iv_shopImage);
        iv_map = (ImageView) findViewById(R.id.iv_map);
        iv_backBtn.setVisibility(View.VISIBLE);

        sessionManager = new SessionManager(this);
        offers = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterOffers = new AdapterOffers(this, offers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterOffers);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        contactDetail_layout = (LinearLayout) findViewById(R.id.contactDetail_layout);
        offerLayout = (LinearLayout) findViewById(R.id.offerLayout);
        activity_retailer_detail = (ScrollView) findViewById(R.id.activity_retailer_detail);

        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        // set animation listeners
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);

        /* initialize font class and set custome font */
        font = new Font(this);
        font.setFont(tv_shopName, Font.Lato_Regular);
        font.setFont(tv_address, Font.Lato_Regular);
        font.setFont(tv_point, Font.Lato_Regular);
        font.setFont(tv_contactDetail, Font.Lato_Regular);
        font.setFont(tv_checkOffer, Font.Lato_Regular);
        font.setFont(tv_email, Font.Lato_Regular);
        font.setFont(tv_phoneNo, Font.Lato_Regular);

        TextView actionTitle = (TextView) findViewById(R.id.toolbat_title);
        font.setFont(actionTitle, Font.Lato_Regular);
        actionTitle.setText("Location Profile");

        if(retailer!=null){
            tv_shopName.setText(retailer.getBusinessName());
            tv_address.setText(retailer.getAddress());
            tv_email.setText(retailer.getEmail());
            tv_point.setText(""+retailer.getPoint()+" Points");

            if(!retailer.getPhoneNo().equals("N/A")){
                tv_phoneNo.setText("Call : "+retailer.getPhoneNo());
            }
            tv_distance.setText(retailer.getDistance()+" Miles");

            if(retailer.getImageUrl()!= null && !retailer.getImageUrl().isEmpty()){
                Picasso.with(this).load(retailer.getImageUrl()).into(iv_shopImage);
            }

            getStaticMapView(16);
        }
    }

    private void getStaticMapView(int zoomLevel){
        String API_KEY = "AIzaSyBnFGTrGe8dJKMnrcinn1edleHCB_yZI5U";
        String url = "https://maps.googleapis.com/maps/api/staticmap?center="+"&zoom="+zoomLevel+"&size=640x300&maptype=roadmap"+
                "&markers=color:red%7Clabel:S%7C"+ retailer.getLat()+","+retailer.getLng()+"&key="+API_KEY;
        Picasso.with(this).load(url).into(iv_map);
        /*this.zoomLevel++;*/
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }



    public void getRetailerOffers(final Context context, final Retailer retailer){

        if(Util.isConnectingToInternet(context)){

            tv_message.setText("Loading...");
            tv_message.setVisibility(View.VISIBLE);
            StringRequest postRequest = new StringRequest(Request.Method.POST, WebServices.GET_RETAILER_OFFERS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                recyclerView.setVisibility(View.VISIBLE);
                                JSONObject jsonResponse = new JSONObject(response);
                                //Log.v("Response",jsonResponse.toString());
                                String status = jsonResponse.getString("status");

                                if (status.equals("success")){

                                    offers.clear();
                                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                    for (int i=0; i<jsonArray.length(); i++){

                                        JSONObject data = jsonArray.getJSONObject(i);
                                        int id = data.getInt("id");
                                        String title = data.getString("title");
                                        String image = data.getString("image");
                                        String expiryDate = data.getString("expiryDate");
                                        String rewardPoint = data.getString("rewardPoint");
                                        String offerStatus = data.getString("status");

                                        Offer offer = new Offer();
                                        offer.setId(id);
                                        offer.setName(title);
                                        offer.setImage(image);
                                        offer.setPoints(rewardPoint);
                                        offer.setExpiryDate(expiryDate);
                                        offers.add(offer);
                                    }
                                    if(offers.size()>0){
                                        tv_message.setVisibility(View.GONE);
                                    }
                                    adapterOffers.notifyDataSetChanged();
                                }else {
                                    recyclerView.setVisibility(View.GONE);
                                    tv_message.setVisibility(View.VISIBLE);
                                    tv_message.setText("No Offers yet");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                tv_message.setVisibility(View.GONE);
                            }catch (Exception e) {
                                tv_message.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            tv_message.setVisibility(View.GONE);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders()  {
                    Map<String,String> params = new HashMap<>();
                    params.put("authToken", sessionManager.getUserInfo().getAuthToken());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("retailerId", ""+ retailer.getId());
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);

        } else{
            Snackbar.make(activity_retailer_detail, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
        }
    }
}
