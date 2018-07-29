package com.app.loyalme;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.loyalme.helper.Constant;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.UserInfo;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.ImageUtil;
import com.app.loyalme.utils.Util;
import com.app.loyalme.utils.Validation;
import com.app.loyalme.volleymultipart.AppHelper;
import com.app.loyalme.volleymultipart.VolleyMultipartRequest;
import com.app.loyalme.volleymultipart.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.mvc.imagepicker.ImagePicker;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = RegistrationActivity.class.getName();
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 10;

    RelativeLayout view, bisuness_layout;
    LinearLayout login_layout;
    EditText ed_userName, ed_email, ed_password, ed_phoneNo;
    TextView ed_businessAddress;
    AppCompatButton btn_signup;
    RadioGroup radioGroup;
    Dialog dialog;
    Font font;

    private String fullName, email, password;  // variables for User.
    private String businessName, businessAddress="N/A", phoneNo ="N/A"; // variables for Retailer.
    private String address="", deviceToken="N/A", userType = "user";  // common variables.
    LatLng latLng;
    LatLng mCurrentlatLng;
    CircleImageView profilePic;
    Bitmap profileImageBitmap;

    //private double latitude, longitude;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mCurrentlatLng = new LatLng(0,0);
        setVariables();
        ImagePicker.setMinQuality(400, 400);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setProfilePic();
                getPermissionAndPicImage();
            }
        });

        login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(view);
                if (isValidData()) {
                    doRegistration(RegistrationActivity.this, view);
                }
            }
        });


        final ImageView iv_userOrBusiness = (ImageView) findViewById(R.id.iv_userOrBusiness);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_retailer){
                    bisuness_layout.setVisibility(View.VISIBLE);
                    ed_userName.setHint("Business Name");
                    iv_userOrBusiness.setImageDrawable(getResources().getDrawable(R.drawable.ico_business));
                    userType = "retailer";
                    address ="";
                    latLng = null;
                    ed_password.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    stopLocationUpdates();
                }else {
                    bisuness_layout.setVisibility(View.INVISIBLE);
                    ed_userName.setHint("Full Name");
                    iv_userOrBusiness.setImageDrawable(getResources().getDrawable(R.drawable.ico_user_name));
                    userType = "user";
                    ed_password.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    startLocationUpdates();
                }
            }
        });



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                address = place.getAddress().toString();
                businessAddress = place.getAddress().toString();
                latLng =  new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                ed_businessAddress.setText(place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });



        if(Build.VERSION.SDK_INT >= 23){

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constant.ACCESS_FINE_LOCATION);
            } else {
                startLocationUpdates();
            }
        } else {
            startLocationUpdates();
        }
    }

    private void setVariables() {

        view = (RelativeLayout) findViewById(R.id.activity_registration);
        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
        ed_userName = (EditText) findViewById(R.id.ed_userName);
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_password = (EditText) findViewById(R.id.ed_password);
        ed_phoneNo = (EditText) findViewById(R.id.ed_phoneNo);

        ed_businessAddress = (TextView) findViewById(R.id.ed_address);
        profilePic = (CircleImageView) findViewById(R.id.profilePic);
        login_layout = (LinearLayout) findViewById(R.id.login_layout);
        bisuness_layout = (RelativeLayout) findViewById(R.id.bisuness_layout);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
       // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        TextView text1 = (TextView)findViewById(R.id.text1);
        TextView text2 = (TextView)findViewById(R.id.text2);
        RadioButton button1 = (RadioButton) findViewById(R.id.rb_user);
        RadioButton button2 = (RadioButton) findViewById(R.id.rb_retailer);
        font = new Font(this);
        font.setFont(ed_userName, Font.Lato_Light);
        font.setFont(ed_password, Font.Lato_Light);
        font.setFont(btn_signup, Font.Lato_Regular);
        font.setFont(ed_email, Font.Lato_Light);
        font.setFont(ed_phoneNo, Font.Lato_Light);
        font.setFont(ed_businessAddress, Font.Lato_Light);
        font.setFont(text1, Font.Lato_Light);
        font.setFont(text2, Font.Lato_Regular);
        font.setFont(button1, Font.Lato_Light);
        font.setFont(button2, Font.Lato_Light);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void  showSnackBar(String msg){
        Util.showSnakbar(view, msg, font);
    }

    public boolean isValidData() {
        Validation v = new Validation();

        if (v.isEmpty(ed_userName)) {
            showSnackBar("field can't be empty");
            return false;

        }else if (v.isEmpty(ed_email)) {
            showSnackBar("field can't be empty");
            return false;

        } else if (!isEmailValid(ed_email)) {
            showSnackBar("enter valid email");
            ed_email.requestFocus();
            return false;
        } else if(radioGroup.getCheckedRadioButtonId()==R.id.rb_retailer){

            if (latLng==null) {
                showSnackBar("Please select address.");
                ed_businessAddress.requestFocus();
                return false;
            }else if(v.isEmpty(ed_phoneNo)){
                showSnackBar("field can't be empty");
                ed_phoneNo.requestFocus();
                return false;
            }if(profileImageBitmap==null){
                showSnackBar("Please choose bussiness image.");
                return false;
            }

            businessName = ed_userName.getText().toString().trim();
            phoneNo = ed_phoneNo.getText().toString().trim();

        } else if (v.isEmpty(ed_password)) {
            showSnackBar("field can't be empty");
            ed_password.requestFocus();
            return false;

        } else if (!isPasswordValid(ed_password)) {
            showSnackBar("At least 4 characters required");
            ed_password.requestFocus();
            return false;

        }

        fullName = ed_userName.getText().toString().trim();
        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();
        return true;
    }

    private boolean isEmailValid(EditText editText) {
        String getValue = editText.getText().toString().trim();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(getValue).matches();
        /*String getValue = editText.getText().toString().trim();
        return getValue.length() >5 && getValue.length()<15;*/
    }

    private boolean isPasswordValid(EditText editText) {
        String getValue = editText.getText().toString().trim();
        return getValue.length() > 3;
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


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

        mCurrentlatLng= new LatLng(location.getLatitude(), location.getLongitude());
        address = Util.latLngToAddress(RegistrationActivity.this, mCurrentlatLng.latitude, mCurrentlatLng.longitude);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if( !mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }else {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    Toast.makeText(RegistrationActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(RegistrationActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case  Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(RegistrationActivity.this);
                } else {
                    Toast.makeText(RegistrationActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;


            case  Constant.ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(RegistrationActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;
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
                ImagePicker.pickImage(RegistrationActivity.this);
            }
        }else {
            ImagePicker.pickImage(RegistrationActivity.this);
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
                profilePic.setImageBitmap(profileImageBitmap);


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
                    profilePic.setImageBitmap(profileImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == 234){
                profileImageBitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
                if(profileImageBitmap!=null)
                    profilePic.setImageBitmap(profileImageBitmap);
            }
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    public void doRegistration(final Activity context, final View view) {

        if (Util.isConnectingToInternet(context)) {

            final SweetAlertDialog pDialog = Util.getProgressDialog(context);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.Registration_Url, new Response.Listener<NetworkResponse>() {
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

                                JSONObject object = jsonObject.getJSONObject("userData");
                                UserInfo info = new UserInfo();
                                info.setUserId(Integer.parseInt(object.getString("id")));
                                info.setEmail(object.getString("email"));
                                info.setUserName(object.getString("userName"));
                                info.setFullName(object.getString("fullName"));
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

                                SessionManager sessionManager = new SessionManager(RegistrationActivity.this);
                                sessionManager.createSession(info);

                                if(info.getUserType().equalsIgnoreCase("user")){

                                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }else if(info.getUserType().equalsIgnoreCase("retailer")){
                                    Intent intent = new Intent(RegistrationActivity.this, RetailerActivity.class);
                                    startActivity(intent);
                                }
                                finish();


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

                    if(userType.equals("user")){
                        params.put("fullName", fullName);
                        params.put("businessName", "N/A");
                        params.put("userName", businessName);
                        params.put("contact", "N/A");
                        params.put("latitude", "" + mCurrentlatLng.latitude);
                        params.put("longitude", "" + mCurrentlatLng.longitude);
                        params.put("address", address);

                    }else {
                        params.put("fullName", "N/A");
                        params.put("userName", businessName);
                        params.put("businessName", businessName);
                        params.put("contact", phoneNo);
                        params.put("latitude", "" + latLng.latitude);
                        params.put("longitude", "" + latLng.longitude);
                        params.put("address", businessAddress);
                    }

                    params.put("email", email);
                    params.put("password", password);
                    params.put("userType", userType);
                    params.put("deviceToken", deviceToken);
                    //   params.put("fullName", fullName);
                    params.put("deviceType", "1");
                    return params;
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
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

        } else {
            //Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            Util.showSnakbar(view, "Please Check internet connection.!", font);
        }
    }


    private void showVerificationDialog(final String verifyCode) {

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle("Verification Code");
        dialog.setCancelable(true);
        //  LinearLayout.LayoutParams layoutParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.conformation_code_layout);


        final EditText ed_verification = (EditText) dialog.findViewById(R.id.ed_verification);
        final Button btn_verify = (Button) dialog.findViewById(R.id.btn_verify);
        final TextView tv_desc = (TextView) dialog.findViewById(R.id.tv_desc);
        final TextView tv_retry = (TextView) dialog.findViewById(R.id.tv_retry);

        tv_desc.setText("A verification code was send via email to " + email + " . When you receive the code, enter it below.");

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ed_verification.getText().length() != 4) {

                    ed_verification.setError("");
                    ed_verification.requestFocus();

                } else if (verifyCode.equals(ed_verification.getText().toString())) {
                    doVerificationProcess(verifyCode);
                } else {
                    ed_verification.setError("Code not match");
                    ed_verification.requestFocus();
                }
            }
        });

        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doRegistration(RegistrationActivity.this, view);
            }
        });


        dialog.show();
    }


    private void doVerificationProcess(final String verifyCode) {
        if (Util.isConnectingToInternet(RegistrationActivity.this)) {

            final SweetAlertDialog pDialog = Util.getProgressDialog(this);
            pDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServices.EMAIL_VERIFICATION_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("#" + response);
                            JSONObject jsonObject = null;
                            pDialog.dismiss();
                            try {

                                jsonObject = new JSONObject(response);

                                if (jsonObject != null) {

                                    String status = jsonObject.getString("status");
                                    String message = jsonObject.getString("message");

                                    if (status.equalsIgnoreCase(Constant.REQUEST_SUCCESS)) {

                                        JSONObject object = jsonObject.getJSONObject("userData");
                                        UserInfo info = new UserInfo();
                                        info.setUserId(Integer.parseInt(object.getString("id")));
                                        info.setEmail(object.getString("email"));
                                        info.setFullName(object.getString("fullName"));
                                        info.setBusinessName(object.getString("businessName"));
                                        info.setUserImage(object.getString("profileImage"));
                                        info.setUserImageThumb(object.getString("profileImageSmall"));
                                        info.setAddress(object.getString("address"));
                                        info.setUserType(object.getString("userType"));
                                        int isApproved = Integer.parseInt(object.getString("isApproved"));
                                        info.setIsApproved(isApproved);

                                        info.setAuthToken(object.getString("authToken"));

                                        SessionManager sessionManager = new SessionManager(RegistrationActivity.this);
                                        sessionManager.createSession(info);

                                        /*Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();*/

                                    } else {

                                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("ok", null).show();
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
                            //   Toast.makeText(RegistrationActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
                            Snackbar.make(view, "Something went wrong.", Snackbar.LENGTH_LONG).setAction("ok", null).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("verifyCode", verifyCode);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(9000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } else {

            Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            // Util.displayToast(LoginActivity.this, "Please Check internet connection.!", Util.SHORT_TOAST);
        }
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
}