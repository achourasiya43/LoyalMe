package com.app.loyalme.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.app.loyalme.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.app.loyalme.R.id.view;

/**
 * Created by Dharmraj on 3/11/2016.
 */
public class Util {

    private static ProgressDialog progress;
    private static SweetAlertDialog pDialog;

    public static int SHORT_TOAST = 0;
    public static int LONG_TOAST = 1;

    Font font;

    public static boolean isConnectingToInternet(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)

                for (int i = 0; i < info.length; i++)

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }

        return false;
    }





    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "M") {
            dist = dist * 0.8684;
        }
        return (roundOff(dist,2));
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static double roundOff(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String StringToBase64(String string){

        byte[] data = new byte[0];
        try {
            data = string.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

    public static String Base64ToString(String string){

        String text="";
        try {
            byte[] data = Base64.decode(string, Base64.DEFAULT);
            text = new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }



    public static String latLngToAddress(Context context, double latitude, double longitude){

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String locality=null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 2);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {

                    if(i==0){

                    }else {
                        sb.append(address.getAddressLine(i)).append(" ");
                    }
                }
                locality = sb.toString().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locality;
    }

    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


    public Date getSimpleDate(String inputDate) {

        Date newDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm a");
        try {
             newDate = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public String getDateString(String inputDate) {
        String datetime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.ENGLISH);
        try {
            Date isoDate = dateFormat.parse(inputDate);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy");
            datetime = simpleDateFormat.format(isoDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return datetime;
    }

    public String getTimeString(String inputDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.ENGLISH);
        Date isoDate = null;
        try {
            isoDate = dateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String datetime = simpleDateFormat.format(isoDate);

        return datetime;
    }





    public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }


    public static void showProgressDialog(Activity activity) {
        showProgressDialog(activity,"Loading..!");
    }

    public static void showProgressDialog(Activity activity , String loadingTxt) {
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);
        progress.setMessage(loadingTxt);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    public static void dismissProgressDialog(){
        if(progress!=null)
            progress.dismiss();
    }


    public static void displayToast(Context caller, String toastMsg, int toastType){

        try {
            LayoutInflater inflater = LayoutInflater.from(caller);

            View mainLayout = inflater.inflate(R.layout.toast_layout, null);
            View rootLayout = mainLayout.findViewById(R.id.toast_layout_root);

            TextView text = (TextView) mainLayout.findViewById(R.id.text);
            text.setText(toastMsg);

            Toast toast = new Toast(caller);
            toast.setGravity(Gravity.CENTER, 0, 0);
            if (toastType==SHORT_TOAST)//(isShort)
                toast.setDuration(Toast.LENGTH_SHORT);
            else
                toast.setDuration(Toast.LENGTH_LONG);

            toast.setView(rootLayout);
            toast.show();
        }
        catch(Exception ex) {// to avoid stupid app crashes
            Log.w("", ex.toString());
        }
    }


    public static void showSnakbar(View rootView, String message, Font font){
        if(rootView!=null){
            Snackbar snack = Snackbar.make(rootView, ""+message, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            font.setFont(tv, Font.Lato_Regular);
            snack.show();
        }
    }

    public static SweetAlertDialog getProgressDialog(Context context){

        if(pDialog!=null){
            pDialog.dismiss();
            pDialog = null;
        }
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        return pDialog;
    }
}