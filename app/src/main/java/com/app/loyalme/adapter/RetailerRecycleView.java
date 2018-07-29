package com.app.loyalme.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.loyalme.R;
import com.app.loyalme.RetailerDetailActivity;
import com.app.loyalme.model.Retailer;
import com.app.loyalme.utils.AnimationUtil;
import com.app.loyalme.utils.Font;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dharmraj on 23/9/16.
 */
public class RetailerRecycleView extends RecyclerView.Adapter<RetailerRecycleView.MyViewHolder> {

    private Context context;
    private List<Retailer> data;
    private LayoutInflater inflater;
    private int previousPosition = 0;
    private Font font;

    public RetailerRecycleView(Context context, List<Retailer> data, Font font) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.font = font;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.retailer_recycleview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {

        Retailer retailer = data.get(position);
        myViewHolder.tv_shopName.setText(retailer.getBusinessName());
        myViewHolder.tv_address.setText(retailer.getAddress());
        myViewHolder.tv_distance.setText(""+retailer.getDistance()+" mi");
        myViewHolder.tv_point.setText(retailer.getPoint()+" Points");

        if(!retailer.getImageUrl().isEmpty()){
            myViewHolder.iv_shopImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(retailer.getImageUrl()).into(myViewHolder.iv_shopImage);
        }else {
            myViewHolder.iv_shopImage.setVisibility(View.GONE);
        }


        if(position > previousPosition){ // We are scrolling DOWN

            AnimationUtil.animate(myViewHolder, true);

        }else{ // We are scrolling UP

            AnimationUtil.animate(myViewHolder, false);
        }

        previousPosition = position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        TextView tv_shopName, tv_address, tv_point, tv_distance;
        ImageView iv_shopImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_shopName = (TextView) itemView.findViewById(R.id.tv_shopName);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
            tv_point = (TextView) itemView.findViewById(R.id.tv_point);
            iv_shopImage = (ImageView)itemView.findViewById(R.id.iv_shopImage);
            font.setFont(tv_shopName, Font.Lato_Regular);
            font.setFont(tv_distance, Font.Lato_Regular);
            font.setFont(tv_address, Font.Lato_Light);
            font.setFont(tv_point, Font.Lato_Light);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, RetailerDetailActivity.class);
            Retailer retailer = data.get(getAdapterPosition());
            intent.putExtra("retailer", retailer);
            context.startActivity(intent);
        }
    }
}
