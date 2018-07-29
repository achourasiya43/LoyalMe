package com.app.loyalme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.loyalme.R;
import com.app.loyalme.model.Offer;
import com.app.loyalme.utils.Font;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dharmraj on 11/2/17.
 */

public class AdapterOffers extends RecyclerView.Adapter<AdapterOffers.MyViewHolder> {

    private int lastPosition = -1;
    private Context context;
    private List<Offer>offers;
    Font font;

    public AdapterOffers(Context context, List<Offer>offers){
        this.context = context;
        this.offers = offers;
        this.font = new Font(context);
    }

    @Override
    public AdapterOffers.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        // final View view = inflater.inflate(R.layout.adapter_rewarded_user_list, parent, false);
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_offer_for_user, parent, false);
        AdapterOffers.MyViewHolder holder = new AdapterOffers.MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterOffers.MyViewHolder myViewHolder, final int position) {

        Offer offer = offers.get(position);
        myViewHolder.tv_offerTitle.setText(offer.getName());
        myViewHolder.tv_expiryDate.setText("Expiry Date : "+offer.getExpiryDate());
        myViewHolder.tv_points.setText("Reward points : "+offer.getPoints());

        if(offer.getPoints().isEmpty()){
            myViewHolder.tv_points.setVisibility(View.GONE);
        }

        if (!offer.getImage().isEmpty()) {
            Picasso.with(context).load(offer.getImage()).into(myViewHolder.imageView);
        }

        setAnimation(myViewHolder.container, position);
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_offerTitle, tv_expiryDate, tv_points;
        ImageView imageView;
        LinearLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.container);
            tv_offerTitle = (TextView) itemView.findViewById(R.id.tv_offerTitle);
            tv_expiryDate = (TextView) itemView.findViewById(R.id.tv_expiryDate);
            tv_points = (TextView) itemView.findViewById(R.id.tv_points);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            font.setFont(tv_offerTitle, Font.Lato_Regular);
            font.setFont(tv_expiryDate, Font.Lato_Regular);
            font.setFont(tv_points, Font.Lato_Regular);

        }

        public void clearAnimation() {
            container.clearAnimation();
        }

        @Override
        public void onClick(View v) {

        }
    }

    private void setAnimation(View viewToAnimate, int position) {

        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(AdapterOffers.MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((AdapterOffers.MyViewHolder)holder).clearAnimation();
    }
}
