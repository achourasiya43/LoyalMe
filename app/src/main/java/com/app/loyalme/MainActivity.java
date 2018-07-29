package com.app.loyalme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.loyalme.fragment.InfoFragment;
import com.app.loyalme.fragment.MyQrCodeFragment;
import com.app.loyalme.fragment.ProfileFragment;
import com.app.loyalme.fragment.RetailersFragment;
import com.app.loyalme.fragment.SettingFragment;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private ImageView iv_home, iv_myQrcode, iv_qrScanner, iv_profile, iv_setting, iv_profileEditBtn, iv_filterBtn;
    private FrameLayout container;
    private TextView actionTitle;
    private Font font;

    private Boolean doubleBackToExitPressedOnce = false;
    static Runnable runnable;
    int currentFragmentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* set custome action bar */
        ActionBar action = getSupportActionBar();
        action.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        action.setCustomView(R.layout.actionbar);

        Toolbar toolbar=(Toolbar)action.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);
       /* getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);*/

        setVariables();

        /* call first fragment  */
        //iv_filterBtn.setVisibility(View.VISIBLE);
        addFragment(new InfoFragment(), false, R.id.container);

        /* Handle all bootombar items click */
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomBar(view.getId());
            }
        });

        iv_myQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomBar(view.getId());
            }
        });

        iv_qrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomBar(view.getId());
            }
        });

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomBar(view.getId());
            }
        });

        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomBar(view.getId());
            }
        });
    }

    /* initialize the all variables or view */
    private void setVariables(){
        container = (FrameLayout) findViewById(R.id.container);
        iv_home = (ImageView) findViewById(R.id.iv_home);
        iv_myQrcode = (ImageView) findViewById(R.id.iv_myQrcode);
        iv_qrScanner = (ImageView) findViewById(R.id.iv_qrScanner);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);

        iv_filterBtn = (ImageView) findViewById(R.id.iv_filterBtn);
        iv_profileEditBtn = (ImageView) findViewById(R.id.iv_profileEditBtn);
        actionTitle = (TextView) findViewById(R.id.toolbat_title);

        /* initialize font class and set custome font */
        font = new Font(this);
        font.setFont(actionTitle, Font.Lato_Regular);

        actionTitle.setText("Home");
        currentFragmentId = iv_home.getId();
    }

    private void setBottomBar(int id){

        /* Re initialize the all image view */
        iv_home.setImageResource(R.drawable.ico_home);
        iv_myQrcode.setImageResource(R.drawable.ico_map_list);
        iv_qrScanner.setImageResource(R.drawable.ico_myqr);
        iv_profile.setImageResource(R.drawable.ico_profile);
        iv_setting.setImageResource(R.drawable.ico_setting);

        iv_filterBtn.setVisibility(View.GONE);
        iv_profileEditBtn.setVisibility(View.GONE);

        /* Re initialize the all image view */
        iv_home.setBackgroundColor(Color.WHITE);
        iv_myQrcode.setBackgroundColor(Color.WHITE);
        iv_qrScanner.setBackgroundColor(Color.WHITE);
        iv_profile.setBackgroundColor(Color.WHITE);
        iv_setting.setBackgroundColor(Color.WHITE);

        switch(id) {

            case R.id.iv_home :
                actionTitle.setText("Home");
                iv_home.setImageResource(R.drawable.ico_home_active);
                iv_home.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                //iv_filterBtn.setVisibility(View.VISIBLE);
              //  if(currentFragmentId != id){ addFragment(new RetailersFragment(), false, R.id.container);}
                if(currentFragmentId != id){ addFragment(new InfoFragment(), false, R.id.container);}
                break;

            case R.id.iv_myQrcode :
                actionTitle.setText("Near Me");
                iv_myQrcode.setImageResource(R.drawable.ico_map_list_active);
                iv_myQrcode.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
               // if(currentFragmentId != id) { addFragment(new MyQrCodeFragment(), false, R.id.container); }
                if(currentFragmentId != id) { addFragment(new RetailersFragment(), false, R.id.container); }
                break;

            case R.id.iv_qrScanner :
                actionTitle.setText("My QR Code");
                iv_qrScanner.setImageResource(R.drawable.ico_myqr_active);
                iv_qrScanner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
             //   if(currentFragmentId != id) {addFragment(new InfoFragment(),false,R.id.container);}
                if(currentFragmentId != id) {addFragment(new MyQrCodeFragment(),false,R.id.container);}
                break;

            case R.id.iv_profile :
                actionTitle.setText("My Profile");
                iv_profileEditBtn.setVisibility(View.VISIBLE);
                iv_profile.setImageResource(R.drawable.ico_profile_active);
                iv_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                if(currentFragmentId != id) { addFragment(new ProfileFragment(), false, R.id.container);}
                break;

            case R.id.iv_setting :
                actionTitle.setText("Setting");
                iv_setting.setImageResource(R.drawable.ico_setting_active);
                iv_setting.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                if(currentFragmentId != id) {addFragment(new SettingFragment(), false, R.id.container);}
                break;
        }
        currentFragmentId = id;
    }

    /* frangment calling code */
    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if (addToBackStack)
               transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }


    @Override
    public void onBackPressed() {

        /* Handle double click to finish activity*/
        Handler handler = new Handler();
        if (!doubleBackToExitPressedOnce) {

            this.doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
            Util.showSnakbar(findViewById(R.id.snackbarlocation), "Click again to exit", font);
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            /*super.onBackPressed();*/
            handler.removeCallbacks(runnable);
            finish();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String TAG = RetailersFragment.class.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
