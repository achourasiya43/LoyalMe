package com.app.loyalme;

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
import android.widget.Toast;

import com.app.loyalme.fragment.ProfileFragment;
import com.app.loyalme.fragment.QrScannerFragment;
import com.app.loyalme.fragment.RetailersFragment;
import com.app.loyalme.fragment.RewardedFragment;
import com.app.loyalme.fragment.SearchUserFragment;
import com.app.loyalme.fragment.SettingFragment;
import com.app.loyalme.utils.Font;

public class RetailerActivity extends AppCompatActivity {

    ImageView iv_home, iv_search, iv_qrScanner, iv_profile, iv_setting, iv_profileEditBtn;
    FrameLayout container;
    TextView actionTitle;
    Font font;

    private Boolean doubleBackToExitPressedOnce = false;
    static Runnable runnable;
    int currentFragmentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer);

         /* set custome action bar */
        ActionBar action = getSupportActionBar();
        action.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        action.setCustomView(R.layout.actionbar);

        Toolbar toolbar=(Toolbar)action.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);


        setVariables();

        /* call first fragment  */
        addFragment(new RewardedFragment(), false, R.id.container);

        /* Handle all bootombar items click */
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomBar(view.getId());
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
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

    private void setVariables(){

        container = (FrameLayout) findViewById(R.id.container);
        iv_home = (ImageView) findViewById(R.id.iv_home);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_qrScanner = (ImageView) findViewById(R.id.iv_qrScanner);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_profileEditBtn = (ImageView) findViewById(R.id.iv_profileEditBtn);

          /* initialize font class and set custome font */
        font = new Font(this);
        actionTitle = (TextView) findViewById(R.id.toolbat_title);
        font.setFont(actionTitle, Font.Lato_Regular);

        actionTitle.setText("Rewarded");
        currentFragmentId = iv_home.getId();
    }


    private void setBottomBar(int id){

        iv_home.setImageResource(R.drawable.ico_home);
        iv_search.setImageResource(R.drawable.ico_search_list);
        iv_qrScanner.setImageResource(R.drawable.ico_scaneqr);
        iv_profile.setImageResource(R.drawable.ico_profile);
        iv_setting.setImageResource(R.drawable.ico_setting);
        iv_profileEditBtn.setVisibility(View.GONE);

         /* Re initialize the all image view */
        iv_home.setImageResource(R.drawable.ico_home);
        iv_search.setImageResource(R.drawable.ico_search_list);
        iv_qrScanner.setImageResource(R.drawable.ico_scaneqr);
        iv_profile.setImageResource(R.drawable.ico_profile);
        iv_setting.setImageResource(R.drawable.ico_setting);

        iv_profileEditBtn.setVisibility(View.GONE);

        /* Re initialize the all image view */
        iv_home.setBackgroundColor(Color.WHITE);
        iv_search.setBackgroundColor(Color.WHITE);
        iv_qrScanner.setBackgroundColor(Color.WHITE);
        iv_profile.setBackgroundColor(Color.WHITE);
        iv_setting.setBackgroundColor(Color.WHITE);

        switch(id) {
            case R.id.iv_home :

                actionTitle.setText("Rewarded");
                iv_home.setImageResource(R.drawable.ico_home_active);
                iv_home.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                if(currentFragmentId != id){ addFragment(new RewardedFragment(), false, R.id.container);}
                break;

            case R.id.iv_search :
                actionTitle.setText("Search Users");
                iv_search.setImageResource(R.drawable.ico_search_list_active);
                iv_search.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                if(currentFragmentId != id){ addFragment(new SearchUserFragment(), false, R.id.container);}
                break;

            case R.id.iv_qrScanner :
                actionTitle.setText("Scan QR Code");
                iv_qrScanner.setImageResource(R.drawable.ico_myqr_active);
                iv_qrScanner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                //if(currentFragmentId != id){ addFragment(new RewardedFragment(), false, R.id.container);}
                addFragment(new QrScannerFragment(), true, R.id.container);
                break;

            case R.id.iv_profile :
                actionTitle.setText("My Profile");
                iv_profileEditBtn.setVisibility(View.VISIBLE);
                iv_profile.setImageResource(R.drawable.ico_profile_active);
                iv_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                if(currentFragmentId != id){ addFragment(new ProfileFragment(), false, R.id.container);}
                break;

            case R.id.iv_setting :
                actionTitle.setText("Setting");
                iv_setting.setImageResource(R.drawable.ico_setting_active);
                iv_setting.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                if(currentFragmentId != id){ addFragment(new SettingFragment(), false, R.id.container);}
                break;
        }
        currentFragmentId = id;
    }


    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }


    @Override
    public void onBackPressed() {

        Handler handler = new Handler();
        if (!doubleBackToExitPressedOnce) {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
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


}
