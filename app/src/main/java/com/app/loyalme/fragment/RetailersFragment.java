package com.app.loyalme.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.R;
import com.app.loyalme.RetailerDetailActivity;
import com.app.loyalme.adapter.AdapterRetailerList;
import com.app.loyalme.adapter.RetailerRecycleView;
import com.app.loyalme.helper.Constant;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.Retailer;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RetailersFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = RetailersFragment.class.getName();
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private static final long INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    public LatLng mCurrentLatLng;

    SessionManager sessionManager;
    //RetailerRecycleView recycleViewAdapter;
    //RecyclerView recyclerView;

    AdapterRetailerList adapter;
    ListView listView;

    CoordinatorLayout snackbarlocation;
    ProgressBar progressBar;
    TextView tv_msg;
    EditText ed_searchText;
    ImageView iv_filterBtn;
    View view;
    Font font;

    List<Retailer> retailerList;
    List<Retailer> retailerTmp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retailerList = new ArrayList<>();
        retailerTmp = new ArrayList<>();
        sessionManager = new SessionManager(getActivity());
        font = new Font(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_retailers_list, container, false);
        setVariables();

        createLocationRequest();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ed_searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerTmp.clear();
                if(count==0){
                    retailerTmp.addAll(retailerList);
                    adapter.notifyDataSetChanged();
                }else {
                    for (Retailer string : retailerList) {
                        if(string.getBusinessName().toLowerCase().contains(s.toString().toLowerCase())){
                            retailerTmp.add(string);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), RetailerDetailActivity.class);
                Retailer retailer = retailerTmp.get(i);
                intent.putExtra("retailer", retailer);
                startActivity(intent);
            }
        });

        //mCurrentLatLng = ((MainActivity)this.getActivity()).mCurrentLatLng;

        iv_filterBtn = (ImageView) getActivity().findViewById(R.id.iv_filterBtn);
        iv_filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

    }


    public void setVariables(){
        //recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        ed_searchText = (EditText) view.findViewById(R.id.ed_searchText);
        listView = (ListView) view.findViewById(R.id.listView);
        tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        font.setFont(ed_searchText, Font.Lato_Regular);
        font.setFont(tv_msg, Font.Lato_Regular);

        snackbarlocation = (CoordinatorLayout) getActivity().findViewById(R.id.snackbarlocation);

        /*recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleViewAdapter = new RetailerRecycleView(getActivity(), retailerTmp, font);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setHasFixedSize(true);*/

    }



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        checkFineLocationPermission();
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        checkFineLocationPermission();
                        break;
                    case Activity.RESULT_CANCELED:
                        createLocationRequest();
                        tv_msg.setText("Gps is disabled. please enable for gatting retailar list.");
                        break;
                }
        }
    }



    private void checkFineLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constant.ACCESS_FINE_LOCATION);
            } else {
                startLocationUpdates();
            }
        } else {
            startLocationUpdates();
        }
    }






    public void showFilterDialog() {

        try{

            Dialog dialog;
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_filter_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.TOP | Gravity.RIGHT;
            dialog.getWindow().setAttributes(lp);*/

            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.gravity = Gravity.TOP | Gravity.RIGHT;
            lp.x = (int) iv_filterBtn.getX()+30;   //x position
            lp.y = (int) iv_filterBtn.getY();  //y position


            dialog.show();

        }catch (Exception ex){

        }
    }


    public void getRetailersList(final Context context, final LatLng mCurrentLatLng){

        if(Util.isConnectingToInternet(context)){

            progressBar.setVisibility(View.VISIBLE);
            tv_msg.setText(R.string.LOADING);
            tv_msg.setVisibility(View.VISIBLE);
            StringRequest postRequest = new StringRequest(Request.Method.POST, WebServices.GET_RETAILERS_LIST_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                progressBar.setVisibility(View.GONE);
                                JSONObject jsonResponse = new JSONObject(response);
                                //Log.v("Response",jsonResponse.toString());
                                String status=jsonResponse.getString("status");

                                if (status.equals("success")){

                                    retailerTmp.clear();
                                    retailerList.clear();
                                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                    for (int i=0; i<jsonArray.length(); i++){

                                        JSONObject data = jsonArray.getJSONObject(i);
                                        int id = data.getInt("id");
                                        String email = data.getString("email");
                                        String userName = data.getString("userName");
                                       // String fullName = data.getString("fullName");
                                        String businessName = data.getString("businessName");
                                        String profileImage = data.getString("profileImage");
                                        String address = data.getString("address");
                                        String contact = data.getString("contact");

                                        int point = 0;
                                        String points = data.getString("point");
                                        if(!points.isEmpty()){
                                            point = Integer.parseInt(points);
                                        }

                                        double latitude = data.getDouble("latitude");
                                        double longitude = data.getDouble("longitude");

                                        double distance = 0.0f;
                                        if(mCurrentLatLng!=null){
                                             distance =  Util.distance(latitude, longitude, mCurrentLatLng.latitude, mCurrentLatLng.longitude,"M");
                                             //distanceTxt = ""+distance+" K";
                                        }

                                        Retailer retailer = new Retailer();
                                        retailer.setId(id);
                                        retailer.setImageUrl(profileImage);
                                        retailer.setEmail(email);
                                        retailer.setUserName(userName);
                                        retailer.setBusinessName(businessName);
                                        retailer.setAddress(address);
                                        retailer.setPhoneNo(contact);
                                        retailer.setPoint(point);
                                        retailer.setDistance(distance);
                                        retailer.setLat(latitude);
                                        retailer.setLng(longitude);
                                        retailerList.add(retailer);
                                    }


                                    Collections.sort(retailerList, new Comparator<Retailer>() {
                                        @Override
                                        public int compare(Retailer lhs, Retailer rhs) {
                                            if(lhs.getDistance()==rhs.getDistance())
                                                return 0;
                                            else if(lhs.getDistance()>rhs.getDistance())
                                                return 1;
                                            else
                                                return -1;
                                        }
                                    });
                                    tv_msg.setVisibility(View.GONE);
                                    retailerTmp.addAll(retailerList);
                                }else {
                                    tv_msg.setText("No result found.");
                                }

                                adapter = new AdapterRetailerList(getActivity(), retailerTmp, font);
                                listView.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                tv_msg.setVisibility(View.VISIBLE);
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            tv_msg.setVisibility(View.GONE);
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
                    params.put("latitude", ""+mCurrentLatLng.latitude);
                    params.put("latitude", ""+mCurrentLatLng.longitude);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);

        } else{
            //Toast.makeText(context, "Please Check internet connection.!", Toast.LENGTH_SHORT).show();
            Snackbar.make(snackbarlocation, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            tv_msg.setText(R.string.no_internet_connection);
            tv_msg.setVisibility(View.VISIBLE);
        }
    }




   /* protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        getRetailersList(getContext(), mCurrentLatLng);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        // register connection status listener
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case  Constant.ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(getActivity(), "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }else {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if(!Util.isConnectingToInternet(getActivity())){
                Snackbar.make(snackbarlocation, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
                tv_msg.setText(R.string.no_internet_connection);
                tv_msg.setVisibility(View.VISIBLE);
            }
        }

    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

}
