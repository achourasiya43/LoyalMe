package com.app.loyalme;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.fragment.DatePickerFragment;
import com.app.loyalme.helper.Constant;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.Offer;
import com.app.loyalme.model.Retailer;
import com.app.loyalme.model.UserInfo;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.ImageUtil;
import com.app.loyalme.utils.Util;
import com.app.loyalme.utils.Validation;
import com.app.loyalme.volleymultipart.AppHelper;
import com.app.loyalme.volleymultipart.VolleyMultipartRequest;
import com.app.loyalme.volleymultipart.VolleySingleton;
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OffersActivity extends AppCompatActivity {

    View view;
    RecyclerView recyclerView;
    ImageView iv_backBtn, iv_addOffers;
    TextView tv_message, ed_expiryDate;
    ProgressBar progressBar;

    RewardedRecycleView recycleViewAdapter;
    SessionManager sessionManager;
    List<Offer> offers;
    Font font;

    Bitmap profileImageBitmap;
    ImageView imageView;

    String offerTitle ="";
    String expiryDate ="";
    int rewardPoint = 0;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        myCalendar = Calendar.getInstance();

        /* set custome action bar */
        ActionBar action = getSupportActionBar();
        action.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        action.setCustomView(R.layout.actionbar);

        Toolbar toolbar=(Toolbar)action.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);
        initVariables();

        iv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_addOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddOfferDialog();
            }
        });

        ImagePicker.setMinQuality(400, 400);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRetailerOffers(OffersActivity.this);
    }

    private void initVariables(){
        font = new Font(this);
        view = (View) findViewById(R.id.activity_offers);
        tv_message = (TextView) findViewById(R.id.tv_message);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        iv_backBtn = (ImageView) findViewById(R.id.iv_backBtn);
        iv_addOffers = (ImageView) findViewById(R.id.iv_profileEditBtn);
        iv_addOffers.setImageResource(R.drawable.ico_add_offer);
        iv_backBtn.setVisibility(View.VISIBLE);
        iv_addOffers.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recycleViewAdapter = new RewardedRecycleView();
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        TextView actionTitle = (TextView) findViewById(R.id.toolbat_title);
        font.setFont(actionTitle, Font.Lato_Regular);
        actionTitle.setText("Offers");

        font.setFont(tv_message, Font.Lato_Regular);
        offers = new ArrayList<>();
        sessionManager = new SessionManager(this);
    }


    private void openAddOfferDialog(){
        final Dialog dialog = new Dialog(this);
        rewardPoint = 0;
        expiryDate = "";
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_offers);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;

        imageView = (ImageView) dialog.findViewById(R.id.imageView);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        ImageView iv_calender = (ImageView) dialog.findViewById(R.id.iv_calender);
        AppCompatButton btn_done = (AppCompatButton) dialog.findViewById(R.id.btn_done);
        final LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.linearLayout);
       // RelativeLayout expiryDateRelativeLayout = (RelativeLayout) dialog.findViewById(R.id.expiryDateRelativeLayout);

        final EditText ed_offerTitle = (EditText) dialog.findViewById(R.id.ed_offerTitle);
        final EditText ed_rewardPoint = (EditText) dialog.findViewById(R.id.ed_rewardPoint);
        ed_expiryDate = (TextView) dialog.findViewById(R.id.ed_expiryDate);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionAndPicImage();
            }
        });

        iv_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(OffersActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation v = new Validation();
                if(v.isEmptyN(ed_offerTitle)){
                    Toast.makeText(OffersActivity.this, "Enter offer title.", Toast.LENGTH_SHORT).show();
                }else if(v.isEmptyN(ed_rewardPoint)){
                    Toast.makeText(OffersActivity.this, "Enter reward point.", Toast.LENGTH_SHORT).show();
                }else if(expiryDate.isEmpty()){
                    Toast.makeText(OffersActivity.this, "Select expiry date first.", Toast.LENGTH_SHORT).show();
                }else if(profileImageBitmap==null){
                    Toast.makeText(OffersActivity.this, "Add offer image first.", Toast.LENGTH_SHORT).show();
                }else {

                    offerTitle = ed_offerTitle.getText().toString().trim();
                    rewardPoint = Integer.parseInt(ed_rewardPoint.getText().toString().trim());
                    uploadOfferToServer(OffersActivity.this, linearLayout, dialog);
                }
            }
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy/MM/dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            expiryDate = sdf.format(myCalendar.getTime());
            ed_expiryDate.setText(expiryDate);
        }

    };



    public void uploadOfferToServer(final Activity context, final View view, final Dialog dialog) {

        if (Util.isConnectingToInternet(context)) {

            final SweetAlertDialog pDialog = Util.getProgressDialog(context);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.POST_OFFERS_URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    pDialog.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(resultResponse);

                        if (jsonObject != null) {

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equalsIgnoreCase(Constant.REQUEST_SUCCESS)) {
                                profileImageBitmap = null;
                                dialog.dismiss();
                                getRetailerOffers(context);

                            } else {
                                //  Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("ok", null).show();
                                Util.showSnakbar(view, message, font);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Unknown error";
                    pDialog.dismiss();
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                        }
                    } else {
                        String result = new String(networkResponse.data);
                        try {
                            JSONObject response = new JSONObject(result);
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Log.e("Error Status", "" + status);
                            Log.e("Error Message", message);

                            if (networkResponse.statusCode == 404) {
                                errorMessage = "Resource not found";
                            } else if (networkResponse.statusCode == 401) {
                                errorMessage = message + " Please login again";
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message + " Check your inputs";
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message + " Something is getting wrong";
                            }
                            // Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("ok", null).show();
                            Util.showSnakbar(view, message, font);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("Error", errorMessage);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("title", offerTitle);
                    params.put("expiryDate", expiryDate);
                    params.put("rewardPoint", ""+rewardPoint);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("authToken", sessionManager.getUserInfo().getAuthToken());
                    return header;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    if (profileImageBitmap != null)
                        params.put("image", new VolleyMultipartRequest.DataPart("image.jpg", AppHelper.getFileDataFromBitmap(profileImageBitmap), "image/jpeg"));
                    return params;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

        } else {
            //Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            Util.showSnakbar(view, "Please Check internet connection.!", font);
        }
    }

    public void getRetailerOffers(final Context context){

        if(Util.isConnectingToInternet(context)){

            tv_message.setText("Loading...");
            progressBar.setVisibility(View.VISIBLE);
            StringRequest postRequest = new StringRequest(Request.Method.POST, WebServices.GET_RETAILER_OFFERS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                progressBar.setVisibility(View.GONE);
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
                                        offer.setExpiryDate(expiryDate);
                                        offer.setPoints(rewardPoint);
                                        offers.add(offer);
                                    }

                                    if(offers.size()>0){
                                        tv_message.setVisibility(View.GONE);
                                    }else {
                                        tv_message.setText("No offers yet");
                                    }

                                    recycleViewAdapter.notifyDataSetChanged();
                                }else {
                                    tv_message.setText("No offers yet");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                tv_message.setVisibility(View.GONE);
                            }catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressBar.setVisibility(View.GONE);
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
                    params.put("retailerId", ""+ sessionManager.getUserInfo().getUserId());
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);

        } else{
            Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
        }
    }



    public void getPermissionAndPicImage(){

        if(Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            }
            else {
                ImagePicker.pickImage(this);
            }
        }else {
            ImagePicker.pickImage(this);
        }
    }



    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Constant.REQUEST_CAMERA) {

                profileImageBitmap = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                imageView.setImageBitmap(profileImageBitmap);


            } else if (requestCode == Constant.SELECT_FILE) {

                Uri selectedImageUri = data.getData();

                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);

                profileImageBitmap = ImageUtil.decodeFile(selectedImagePath);
                try {

                    profileImageBitmap = ImageUtil.modifyOrientation(profileImageBitmap, selectedImagePath);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    imageView.setImageBitmap(profileImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == 234){
                profileImageBitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
                if(profileImageBitmap!=null)
                    imageView.setImageBitmap(profileImageBitmap);
            }
        }

    }

    public class RewardedRecycleView extends RecyclerView.Adapter<RewardedRecycleView.MyViewHolder> {

        private int lastPosition = -1;

        @Override
        public RewardedRecycleView.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

            // final View view = inflater.inflate(R.layout.adapter_rewarded_user_list, parent, false);
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_offer_layour, parent, false);
            RewardedRecycleView.MyViewHolder holder = new RewardedRecycleView.MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RewardedRecycleView.MyViewHolder myViewHolder, final int position) {

            Offer offer = offers.get(position);
            myViewHolder.tv_offerTitle.setText(offer.getName());
            myViewHolder.tv_expiryDate.setText("Expiry Date : "+offer.getExpiryDate());
            myViewHolder.tv_points.setText("Reward Points : "+offer.getPoints());

            if(offer.getPoints().isEmpty())
                myViewHolder.tv_points.setVisibility(View.GONE);

            if (!offer.getImage().isEmpty()) {
                Picasso.with(OffersActivity.this).load(offer.getImage()).into(myViewHolder.imageView);
            }

            setAnimation(myViewHolder.container, position);
        }

        @Override
        public int getItemCount() {
            return offers.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv_offerTitle, tv_expiryDate, tv_points;
            ImageView imageView, iv_deleteBtn;
            LinearLayout container;

            public MyViewHolder(View itemView) {
                super(itemView);

                container = (LinearLayout) itemView.findViewById(R.id.container);
                tv_offerTitle = (TextView) itemView.findViewById(R.id.tv_offerTitle);
                tv_expiryDate = (TextView) itemView.findViewById(R.id.tv_expiryDate);
                tv_points = (TextView) itemView.findViewById(R.id.tv_points);
                iv_deleteBtn = (ImageView) itemView.findViewById(R.id.iv_deleteBtn);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);

                font.setFont(tv_offerTitle, Font.Lato_Regular);
                font.setFont(tv_expiryDate, Font.Lato_Regular);
                font.setFont(tv_points, Font.Lato_Regular);
                iv_deleteBtn.setOnClickListener(this);

            }

            public void clearAnimation() {
                container.clearAnimation();
            }

            @Override
            public void onClick(View v) {

               if(iv_deleteBtn.getId()==v.getId()){

                   new SweetAlertDialog(OffersActivity.this, SweetAlertDialog.WARNING_TYPE)
                           .setTitleText("Are you sure?")
                           .setContentText("Do you want to delete this offer.")
                           .setCancelText("No")
                           .setConfirmText("Yes,delete it!")
                           .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                               @Override
                               public void onClick(SweetAlertDialog sDialog) {
                                   sDialog.dismissWithAnimation();
                                   deleteOffer(OffersActivity.this, getAdapterPosition());
                               }
                           })
                           .show();
               }
            }
        }

        // This removes the data from our Dataset and Updates the Recycler View.
        private void removeItem(int position) {
            //int currPosition = offers.indexOf(offer);
            offers.remove(position);
            notifyItemRemoved(position);
        }

        private void setAnimation(View viewToAnimate, int position) {

            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(OffersActivity.this, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public void onViewDetachedFromWindow(MyViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            ((MyViewHolder)holder).clearAnimation();
        }

        private void deleteOffer(Context context, final int position) {
            if(Util.isConnectingToInternet(context)) {

                final SweetAlertDialog pDialog = Util.getProgressDialog(context);
                pDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServices.DELETE_OFFERS_URL,
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
                                            removeItem(position);

                                        }else{
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
                                Util.showSnakbar(view, error.getMessage(), font);
                                //Toast.makeText(LoginActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }){

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("offerId", ""+offers.get(position).getId());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("authToken", sessionManager.getUserInfo().getAuthToken());
                        return header;
                    }
                };


                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);

            }else{
                Util.showSnakbar(view, "Please Check internet connection.!", font);
            }
        }

    }
}
