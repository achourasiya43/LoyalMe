package com.app.loyalme.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.loyalme.R;
import com.squareup.picasso.Picasso;


public class InfoFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private int imageSource[] = {R.drawable.image_one, R.drawable.image_two, R.drawable.image_three, R.drawable.image_four, R.drawable.image_five, R.drawable.image_six, R.drawable.image_seven, R.drawable.image_eight};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.info_view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getActivity(), imageSource));
        return view;
    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private int[] mResources;

        public ViewPagerAdapter(Context mContext, int[] mResources) {
            this.mContext = mContext;
            this.mResources = mResources;
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.view_pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image);
            //     imageLoader.DisplayImage(mResources[position], imageView);
            //   aq.id(imageView).image(mResources[position], false, false);
            Picasso.with(getActivity())
                    .load(mResources[position])
                    //  .placeholder(R.drawable.logout_icon)   // optional
                    //  .error(R.drawable.ic_launcher)      // optional
                    .into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
