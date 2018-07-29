package com.app.loyalme.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.loyalme.OffersActivity;
import com.app.loyalme.R;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.model.SettingDto;
import com.app.loyalme.utils.Font;

import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment {

    private SessionManager sessionManager;
    private View view;
    private ListView listView;
    private SettingAdapter adapter;
    private Font font;
    private List<SettingDto> menuList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        sessionManager = new SessionManager(getActivity());
        menuList = new ArrayList<>();

        SettingDto aboutUs = new SettingDto("About Us", R.drawable.ico_about_us);
        SettingDto termCondition = new SettingDto("Terms & Condition", R.drawable.ico_terms);
        SettingDto Offers = new SettingDto("Offer", R.drawable.ico_offer_setting);
        SettingDto Logout = new SettingDto("Logout", R.drawable.ico_logout);

        menuList.add(aboutUs);
        menuList.add(termCondition);
        if(sessionManager.getUserInfo().getUserType().equals("user")){
            menuList.add(Logout);
        }else {
            menuList.add(Offers);
            menuList.add(Logout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        setvariables();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String selectMenu = menuList.get(position).getItemName();

                switch (selectMenu){

                    case "About Us":
                        Toast.makeText(getContext(), "About Us", Toast.LENGTH_SHORT).show();
                        break;

                    case "Terms & Condition":
                        Toast.makeText(getContext(), "Terms & Condition", Toast.LENGTH_SHORT).show();
                        break;

                    case "Offer":
                        startActivity(new Intent(getActivity(), OffersActivity.class));
                        break;

                    case "Logout":
                        sessionManager.logout(getActivity());
                        break;
                }
            }
        });
    }

    private void setvariables(){
        sessionManager = new SessionManager(getActivity());
        /*tv_aboutUs = (TextView)  view.findViewById(R.id.tv_aboutUs);
        tv_term_condition = (TextView)  view.findViewById(R.id.tv_term_condition);
        tv_logout = (TextView)  view.findViewById(R.id.tv_logout);*/

        font = new Font(getActivity());
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new SettingAdapter();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }






    public class SettingAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public SettingAdapter() {
            this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public Object getItem(int i) {
            return menuList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            final View view;
            ViewHolder holder;

            if (convertView == null) {

                // inflate the layout
                view = inflater.inflate(R.layout.adapter_setting, viewGroup, false);
                setViewHolder(view);

            } else {
                view = convertView;
            }

            holder = (ViewHolder) view.getTag();

            holder.tv_message.setText(menuList.get(position).getItemName());
            holder.iv_image.setImageResource(menuList.get(position).getImageId());

            return view;
        }


        private class ViewHolder {
            TextView tv_message;
            ImageView iv_image;
        }


        private void setViewHolder(View view) {
            ViewHolder holder = new ViewHolder();
            holder.tv_message = (TextView) view.findViewById(R.id.tv_text);
            holder.iv_image = (ImageView) view.findViewById(R.id.iv_image);
            font.setFont(holder.tv_message, Font.Lato_Regular);
            view.setTag(holder);
        }

    }

}
