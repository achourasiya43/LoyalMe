package com.app.loyalme.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

/**
 * Created by dharmraj on 23/9/16.
 */
public class AnimationUtil {


    public static void animate(RecyclerView.ViewHolder holder , boolean goesDown){


        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown==true ? 200 : -300, 0);
        animatorTranslateY.setDuration(500);


      /*  ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(holder.itemView,"translationX",-50,50,-30,30,-20,20,-5,5,0);
        animatorTranslateX.setDuration(1000);*/

        animatorSet.playTogether(animatorTranslateY);

        //animatorSet.playTogether(animatorTranslateY);
        animatorSet.start();

    }
}
