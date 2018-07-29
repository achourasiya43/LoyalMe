package com.app.loyalme.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by dharmraj on 6/5/16.
 */
public class Font {

    private Context context;
    public static final int Lato_Light =1;
    public static final int Lato_Medium =2;
    public static final int Lato_Regular =3;


    public Font(Context context) {
        this.context = context;
    }

    public void setFont(TextView text, int font)
    {
        Typeface typeface = null ;

        switch (font)
        {
            case Lato_Light:

                typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Lato-Light.ttf");
                break;

            case Lato_Medium:

                typeface = Typeface.createFromAsset(context.getResources().getAssets(),"fonts/Lato-Medium.ttf");
                break;

            case Lato_Regular:

                typeface = Typeface.createFromAsset(context.getResources().getAssets(),"fonts/Lato-Regular.ttf");
                break;
        }

        text.setTypeface(typeface);
    }
}
