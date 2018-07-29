package com.app.loyalme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.loyalme.R;
import com.app.loyalme.model.Retailer;
import com.app.loyalme.utils.Font;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dharmraj on 2/3/17.
 */

public class AdapterRetailerList extends BaseAdapter {

    private Context context;
    private List<Retailer> data;
    private LayoutInflater inflater;
    private Font font;

    public AdapterRetailerList(Context context, List<Retailer> datav, Font font) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = datav;
        this.font = font;

    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final View view;
        ViewHolder myViewHolder;

        if (convertView == null) {

            // inflate the layout
            view = inflater.inflate(R.layout.retailer_recycleview, viewGroup, false);
            setViewHolder(view);

        } else {
            view = convertView;
        }

        myViewHolder = (ViewHolder) view.getTag();

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

        return view;
    }


    private class ViewHolder {
        TextView tv_shopName, tv_address, tv_point, tv_distance;
        ImageView iv_shopImage;

    }


    private void setViewHolder(View itemView) {
        ViewHolder holder = new ViewHolder();
        holder.tv_shopName = (TextView) itemView.findViewById(R.id.tv_shopName);
        holder.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
        holder.tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
        holder.tv_point = (TextView) itemView.findViewById(R.id.tv_point);
        holder.iv_shopImage = (ImageView)itemView.findViewById(R.id.iv_shopImage);
        font.setFont(holder.tv_shopName, Font.Lato_Regular);
        font.setFont(holder.tv_distance, Font.Lato_Regular);
        font.setFont(holder.tv_address, Font.Lato_Light);
        font.setFont(holder.tv_point, Font.Lato_Light);
        itemView.setTag(holder);
    }

}
