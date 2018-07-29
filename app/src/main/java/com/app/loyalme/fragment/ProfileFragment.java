package com.app.loyalme.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.app.loyalme.R;

import com.app.loyalme.helper.Constant;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.UserInfo;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;
import com.app.loyalme.utils.Validation;
import com.app.loyalme.volleymultipart.AppHelper;
import com.app.loyalme.volleymultipart.VolleyMultipartRequest;
import com.app.loyalme.volleymultipart.VolleySingleton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.internal.zzs.TAG;


public class ProfileFragment extends Fragment {

    private static boolean fragmentOpen = false;
    SessionManager sessionManager;

    View view, viewOne, viewTwo;
    CoordinatorLayout snackbarlocation;
    ProgressBar progress;
    CircleImageView iv_profilePic;
    RelativeLayout point_layout, getAddress;
    LinearLayout place_layout;
    ImageView iv_profileEditBtn, iv_addImage;
    ProgressBar progressBarProfile;
    EditText tv_fullName, tv_email, tv_contact;
    TextView tv_point, tv_address;
    AppCompatButton btn_update;

    UserInfo userInfo;
    Font font;
    Bitmap profileImageBitmap;
    boolean isProfileEditMode;
    String userType;
    LatLng latLng;
    PlaceAutocompleteFragment autocompleteFragment;
    // Profile value hold variables
    private String email = "N/A", contact = "N/A", fullName = "N/A", businessName = "N/A", address = "N/A";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImagePicker.setMinQuality(400, 400);
        sessionManager = new SessionManager(getActivity());
        userInfo = sessionManager.getUserInfo();
        font = new Font(getActivity());

        if (userInfo != null) {
            businessName = userInfo.getFullName();
            fullName = userInfo.getBusinessName();
            email = userInfo.getEmail();
            userType = userInfo.getUserType();
            contact = userInfo.getContact();
            address = userInfo.getAddress();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.e(TAG, "onCreateView: " + fragmentOpen);
        if (fragmentOpen) {
            Log.e(TAG, "onCreateView: " + "in");
            if (view != null) {
                Log.e(TAG, "onCreateView: " + "inn");
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null)
                    parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
            fragmentOpen = true;
            Log.e(TAG, "onCreateView2: " + fragmentOpen);
            setvariables();
        } catch (InflateException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iv_profileEditBtn = (ImageView) getActivity().findViewById(R.id.iv_profileEditBtn);
        iv_profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isProfileEditMode) {
                    setEnableDisableVariables(false);
                } else setEnableDisableVariables(true);

            }
        });

        iv_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionAndPicImage();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidData()) {
                    updareProfile(getActivity());
                }
            }
        });

        setEnableDisableVariables(false);
        if (userType.equals("user")) {
            getProfile(getActivity());
        }

    }


    private void setEnableDisableVariables(boolean bool) {

        if (bool) {
            iv_addImage.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);
            if (userInfo.getUserType().equals("user")) {
                point_layout.setVisibility(View.GONE);
                place_layout.setVisibility(View.INVISIBLE);
                getAddress.setVisibility(View.INVISIBLE);
                viewOne.setVisibility(View.INVISIBLE);
                viewTwo.setVisibility(View.INVISIBLE);
            }
            place_layout.setVisibility(View.VISIBLE);
        } else {
            iv_addImage.setVisibility(View.INVISIBLE);
            btn_update.setVisibility(View.GONE);
            if (userInfo.getUserType().equals("user")) {
                point_layout.setVisibility(View.VISIBLE);
                place_layout.setVisibility(View.INVISIBLE);
                getAddress.setVisibility(View.INVISIBLE);
                viewOne.setVisibility(View.INVISIBLE);
                viewTwo.setVisibility(View.INVISIBLE);
            }
            place_layout.setVisibility(View.INVISIBLE);
        }

        iv_profilePic.setEnabled(bool);
        isProfileEditMode = bool;
        tv_fullName.setEnabled(bool);
        tv_email.setEnabled(bool);
        tv_address.setEnabled(bool);
        tv_contact.setEnabled(bool);
    }

    private void setvariables() {

        progress = (ProgressBar) view.findViewById(R.id.circle_progress_bar);
        // progress.getIndeterminateDrawable().setColorFilter(0x1a715d, android.graphics.PorterDuff.Mode.MULTIPLY);

        //progress.setProgress(85);
        autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_profile);

        iv_profilePic = (CircleImageView) view.findViewById(R.id.iv_profilePic);
        tv_fullName = (EditText) view.findViewById(R.id.tv_fullName);
        tv_email = (EditText) view.findViewById(R.id.tv_email);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_contact = (EditText) view.findViewById(R.id.tv_contact);
        snackbarlocation = (CoordinatorLayout) getActivity().findViewById(R.id.snackbarlocation);

        tv_point = (TextView) view.findViewById(R.id.tv_point);
        iv_addImage = (ImageView) view.findViewById(R.id.iv_addImage);
        progressBarProfile = (ProgressBar) view.findViewById(R.id.progressBarProfile);
        btn_update = (AppCompatButton) view.findViewById(R.id.btn_update);
        point_layout = (RelativeLayout) view.findViewById(R.id.point_layout);

        getAddress = (RelativeLayout) view.findViewById(R.id.getAddress);
        place_layout = (LinearLayout) view.findViewById(R.id.place_layout);
        viewOne = view.findViewById(R.id.view_one);
        viewTwo = view.findViewById(R.id.view_two);

        if (font == null) {
            font = new Font(getActivity());
        }

        ImageView iv_userName = (ImageView) view.findViewById(R.id.iv_userName);
        iv_profilePic.setEnabled(false);

        if (sessionManager != null) {

            tv_fullName.setText(userInfo.getFullName());
            tv_email.setText(userInfo.getEmail());
            if (!userInfo.getUserImage().equals(""))
                Picasso.with(getContext()).load(sessionManager.getUserInfo().getUserImage()).placeholder(R.drawable.user_place_holder).into(iv_profilePic);

            TextView tv_rewardPoint = (TextView) view.findViewById(R.id.tv_rewardPoint);
            font.setFont(tv_email, Font.Lato_Regular);
            font.setFont(tv_fullName, Font.Lato_Regular);
            font.setFont(tv_point, Font.Lato_Regular);
            font.setFont(tv_rewardPoint, Font.Lato_Regular);

            if (userInfo.getUserType().equalsIgnoreCase("retailer")) {
                point_layout.setVisibility(View.GONE);
                iv_userName.setImageDrawable(getResources().getDrawable(R.drawable.ico_business));
                tv_fullName.setText(sessionManager.getUserInfo().getBusinessName());

                LinearLayout ed_address_layout = (LinearLayout) view.findViewById(R.id.ed_address_layout);
                LinearLayout ed_contact_layout = (LinearLayout) view.findViewById(R.id.ed_contact_layout);

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        Log.i(TAG, "Place: " + place.getName());
                        address = place.getAddress().toString();
                        latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                        tv_address.setText(place.getName());
                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i(TAG, "An error occurred: " + status);
                    }
                });
                ed_address_layout.setVisibility(View.VISIBLE);
                ed_contact_layout.setVisibility(View.VISIBLE);
                getAddress.setVisibility(View.VISIBLE);

                font.setFont(tv_address, Font.Lato_Regular);
                font.setFont(tv_contact, Font.Lato_Regular);

                String address = userInfo.getAddress();
                tv_address.setText(address.replace("\n", " "));
                tv_contact.setText(contact);
            }
        }
    }

    private boolean isValidData() {

        Validation v = new Validation();

        if (v.isEmpty(tv_fullName)) {
            tv_fullName.requestFocus();
            return false;

        } else if (v.isEmpty(tv_email)) {
            tv_email.requestFocus();
            return false;

        } else if (userType.equals("retailer")) {

            if (v.isEmpty(tv_address)) {
                tv_address.requestFocus();
                return false;
            } else if (v.isEmpty(tv_contact)) {
                tv_contact.requestFocus();
                return false;
            }

            businessName = tv_fullName.getText().toString().trim();
            address = tv_address.getText().toString().trim();
            contact = tv_contact.getText().toString().trim();

        } else {

            fullName = tv_fullName.getText().toString().trim();
            email = tv_email.getText().toString().trim();
        }

        return true;
    }

    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(this);
            }
        } else {
            ImagePicker.pickImage(this);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            if (requestCode == 234) {
                profileImageBitmap = ImagePicker.getImageFromResult(getContext(), requestCode, resultCode, data);
                if (profileImageBitmap != null) {

                    updareProfileImage(getContext(), profileImageBitmap);
                }

            }
        }
    }

    private synchronized void updareProfileImage(Context context, final Bitmap profileImageBitmap) {

        if (Util.isConnectingToInternet(context)) {

            progressBarProfile.setVisibility(View.VISIBLE);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    WebServices.UPDATE_PROFILE_URL, new Response.Listener<NetworkResponse>() {

                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    progressBarProfile.setVisibility(View.GONE);
                    try {

                        JSONObject jsonObject = new JSONObject(resultResponse);

                        if (jsonObject != null) {

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equalsIgnoreCase(Constant.REQUEST_SUCCESS)) {

                                JSONObject object = jsonObject.getJSONObject("data");
                                UserInfo info = new UserInfo();
                                info.setUserId(Integer.parseInt(object.getString("id")));
                                info.setEmail(object.getString("email"));
                                info.setUserName(object.getString("userName"));
                                info.setFullName(object.getString("fullName"));
                                info.setBusinessName(object.getString("businessName"));
                                info.setUserImage(object.getString("profileImage"));
                                info.setUserImageThumb(object.getString("profileImageSmall"));
                                info.setAddress(object.getString("address"));
                                info.setContact(object.getString("contact"));
                                info.setUserType(object.getString("userType"));
                                info.setQrCodeImage(object.getString("qrCodeImage"));
                                /*int isApproved = Integer.parseInt(object.getString("isApproved"));
                                info.setIsApproved(isApproved);*/

                                if (object.has("rewardPoint")) {
                                    tv_point.setText(object.getString("rewardPoint"));
                                }

                                sessionManager.updateProfile(info);
                                setEnableDisableVariables(false);
                                iv_profilePic.setImageBitmap(profileImageBitmap);

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
                    progressBarProfile.setVisibility(View.GONE);
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("authToken", userInfo.getAuthToken());
                    return header;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    if (profileImageBitmap != null)
                        params.put("profileImage", new VolleyMultipartRequest.DataPart("profileImage.jpg", AppHelper.getFileDataFromBitmap(profileImageBitmap), "image/jpeg"));
                    return params;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);

        } else {
            //Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            Util.showSnakbar(view, "Please Check internet connection.!", font);
            progressBarProfile.setVisibility(View.GONE);
        }
    }

    public void updareProfile(final Activity context) {

        if (Util.isConnectingToInternet(context)) {

            final SweetAlertDialog pDialog = Util.getProgressDialog(context);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    WebServices.UPDATE_PROFILE_URL, new Response.Listener<NetworkResponse>() {
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

                                JSONObject object = jsonObject.getJSONObject("data");
                                UserInfo info = new UserInfo();
                                info.setUserId(Integer.parseInt(object.getString("id")));
                                info.setEmail(object.getString("email"));
                                info.setUserName(object.getString("userName"));
                                info.setFullName(object.getString("fullName"));
                                info.setBusinessName(object.getString("businessName"));
                                info.setUserImage(object.getString("profileImage"));
                                info.setUserImageThumb(object.getString("profileImageSmall"));
                                info.setAddress(object.getString("address"));
                                info.setContact(object.getString("contact"));
                                info.setUserType(object.getString("userType"));
                                info.setQrCodeImage(object.getString("qrCodeImage"));
                                /*int isApproved = Integer.parseInt(object.getString("isApproved"));
                                info.setIsApproved(isApproved);*/

                                if (object.has("rewardPoint")) {
                                    tv_point.setText(object.getString("rewardPoint"));
                                }

                                sessionManager.updateProfile(info);
                                setEnableDisableVariables(false);
                                Util.showSnakbar(snackbarlocation, "Update successfully.", font);

                            } else {
                                //  Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("ok", null).show();
                                Util.showSnakbar(snackbarlocation, message, font);
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
                            Util.showSnakbar(snackbarlocation, message, font);
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


                    if (!userInfo.getEmail().equalsIgnoreCase(email)) {
                        params.put("email", email);
                    }
                    params.put("fullName", fullName);
                    params.put("userName", businessName);
                    params.put("businessName", businessName);
                    params.put("contact", contact);
                    params.put("address", address);

                    if(latLng!=null){
                        params.put("latitude", String.valueOf(latLng.latitude));
                        params.put("longitude", String.valueOf(latLng.longitude));
                    }

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("authToken", userInfo.getAuthToken());
                    return header;
                }


                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    if (profileImageBitmap != null)
                        params.put("profileImage", new VolleyMultipartRequest.DataPart("profileImage.jpg", AppHelper.getFileDataFromBitmap(profileImageBitmap), "image/jpeg"));
                    return params;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);

        } else {
            //Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            Util.showSnakbar(snackbarlocation, "Please Check internet connection.!", font);
        }
    }


    private void getProfile(Context context) {
        if (Util.isConnectingToInternet(context)) {

           /* final SweetAlertDialog pDialog = Util.getProgressDialog(context);
            pDialog.show();*/
            progress.setVisibility(View.VISIBLE);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, WebServices.GET_PROFILE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("#" + response);
                            JSONObject jsonObject = null;

                            try {

                                jsonObject = new JSONObject(response);

                                if (jsonObject != null) {

                                    String status = jsonObject.getString("status");
                                    String message = jsonObject.getString("message");

                                    if (status.equalsIgnoreCase(Constant.REQUEST_SUCCESS)) {

                                        JSONObject object = jsonObject.getJSONObject("data");
                                       /* UserInfo info = new UserInfo();
                                        info.setUserId(Integer.parseInt(object.getString("id")));
                                        info.setEmail(object.getString("email"));
                                        info.setUserName(object.getString("userName"));
                                        info.setFullName(object.getString("fullName"));
                                        info.setBusinessName(object.getString("businessName"));
                                        info.setUserImage(object.getString("profileImage"));
                                        info.setUserImageThumb(object.getString("profileImageSmall"));
                                        info.setAddress(object.getString("address"));
                                        info.setContact(object.getString("contact"));
                                        info.setUserType(object.getString("userType"));
                                        info.setQrCodeImage(object.getString("qrCodeImage"));*/

                                        if (object.has("rewardPoint")) {
                                            String rewardPoints = object.getString("rewardPoint");
                                            if(!rewardPoints.equals("null")){
                                                tv_point.setText(object.getString("rewardPoint"));
                                            }
                                        }

                                    } else {
                                        Util.showSnakbar(snackbarlocation, message, font);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            progress.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.setVisibility(View.GONE);
                            Util.showSnakbar(snackbarlocation, error.getMessage(), font);
                            //Toast.makeText(LoginActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("authToken", userInfo.getAuthToken());
                    return header;
                }

            };


            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);

        } else {
            Util.showSnakbar(snackbarlocation, "Please Check internet connection.!", font);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
    }

}
