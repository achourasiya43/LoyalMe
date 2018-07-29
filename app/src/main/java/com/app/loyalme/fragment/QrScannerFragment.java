package com.app.loyalme.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.ProfileActivity;
import com.app.loyalme.R;
import com.app.loyalme.RegistrationActivity;
import com.app.loyalme.helper.Constant;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.RewardedUser;
import com.app.loyalme.model.UserInfo;
import com.app.loyalme.utils.Util;
import com.google.zxing.Result;
import com.mvc.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QrScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    SessionManager sessionManager;
    View view;
    CoordinatorLayout snackbarlocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
        sessionManager = new SessionManager(getActivity());
        getPermission();
        snackbarlocation = (CoordinatorLayout) getActivity().findViewById(R.id.snackbarlocation);
        mScannerView = new ZXingScannerView(getActivity());   // Programmatically initialize the scanner view
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.

        return mScannerView;
    }

    private void getPermission(){
        if(Build.VERSION.SDK_INT >= 23){

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA);
            }
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.startCamera();
    }



    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        // Stop camera on pause
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onResume();
                } else {
                    Toast.makeText(getActivity(), "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d("Log:",rawResult.getText());
        searchUsersByQRcode(getContext(), rawResult.getText());
    }


    public void searchUsersByQRcode(final Context context, final String qrCode) {

        if (Util.isConnectingToInternet(context)) {

           final SweetAlertDialog dialog = Util.getProgressDialog(context);
            dialog.show();
            StringRequest postRequest = new StringRequest(Request.Method.POST, WebServices.QR_SCAN_USER_URL,
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

                                    JSONObject userDetail = jsonResponse.getJSONObject("data");
                                    int id = userDetail.getInt("id");
                                    String retailerId = userDetail.getString("retailerId");
                                    String totalPurchase = userDetail.getString("totalPurchase");
                                    String rewardPoint = userDetail.getString("rewardPoint");

                                    UserInfo userInfo = new UserInfo();
                                    userInfo.setUserId(Integer.parseInt(userDetail.getString("id")));
                                    userInfo.setUserName(userDetail.getString("userName"));
                                    userInfo.setFullName(userDetail.getString("fullName"));
                                    userInfo.setUserImage(userDetail.getString("profileImage"));
                                    userInfo.setBusinessName("N/A");
                                    userInfo.setEmail(userDetail.getString("email"));
                                    userInfo.setAddress(userDetail.getString("address"));

                                    RewardedUser rewardedUser = new RewardedUser();
                                    rewardedUser.setId(id);
                                    rewardedUser.setRetailerId(Integer.parseInt(retailerId));
                                    rewardedUser.setRewardPoint(Double.parseDouble(rewardPoint));
                                    rewardedUser.setTotalPurchase(Double.parseDouble(totalPurchase));
                                    rewardedUser.setUserInfo(userInfo);

                                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                    intent.putExtra("rewardedUser", rewardedUser);
                                    getActivity().startActivity(intent);

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
                    params.put("authToken", new SessionManager(getActivity()).getUserInfo().getAuthToken());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", qrCode);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);

        } else {
            Snackbar.make(snackbarlocation, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
        }
    }
}
