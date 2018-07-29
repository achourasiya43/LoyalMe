package com.app.loyalme.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.loyalme.ProfileActivity;
import com.app.loyalme.R;
import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.helper.WebServices;
import com.app.loyalme.model.RewardedUser;
import com.app.loyalme.model.UserInfo;
import com.app.loyalme.utils.ConnectivityReceiver;
import com.app.loyalme.utils.Font;
import com.app.loyalme.utils.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RewardedFragment extends Fragment  implements ConnectivityReceiver.ConnectivityReceiverListener{

    SessionManager sessionManager;
    CoordinatorLayout snackbarlocation;
    RewardedRecycleView recycleViewAdapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tv_message;
    View view;
    Font font;

    List<RewardedUser> rewardedUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rewardedUsers = new ArrayList<>();
        sessionManager = new SessionManager(getActivity());
        font = new Font(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rewarded, container, false);
        setVariables();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getRewardedUserList(getContext());
    }

    public void setVariables() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        snackbarlocation = (CoordinatorLayout) getActivity().findViewById(R.id.snackbarlocation);
        recycleViewAdapter = new RewardedRecycleView();
        recyclerView.setAdapter(recycleViewAdapter);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        font.setFont(tv_message, Font.Lato_Regular);
    }




    public void getRewardedUserList(final Context context) {

        if (Util.isConnectingToInternet(context)) {

            progressBar.setVisibility(View.VISIBLE);
            tv_message.setText("Loading...");
            StringRequest postRequest = new StringRequest(Request.Method.GET, WebServices.GET_REWARDED_USER_LIST_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                               // JSONObject jsonResponse = new JSONObject(response);
                                RestAsyncTask1 task1 = new RestAsyncTask1(response);
                                task1.execute();
                            } catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                tv_message.setText("Something went worng Please try again.");
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            tv_message.setText("Something went worng Please try again.");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("authToken", sessionManager.getUserInfo().getAuthToken());
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);

        } else {
           // Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            Util.showSnakbar(snackbarlocation, "Please Check internet connection.!", font);
            tv_message.setText("Please Check internet connection.!");
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if(isConnected){
            if(rewardedUsers!=null){
                if(rewardedUsers.size()==0){
                    getRewardedUserList(getContext());
                }
            }

        }
    }


    public class RestAsyncTask1 extends AsyncTask<Void, Void, Void> {

        String response;
        String status;

       public RestAsyncTask1(String response){
            this.response = response;
        }

        @Override
        protected Void doInBackground(Void... strings) {

            try {

                JSONObject jsonResponse = new JSONObject(response);
                //Log.v("Response",jsonResponse.toString());
                 status = jsonResponse.getString("status");

                if (status.equals("success")) {
                    rewardedUsers.clear();
                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.getInt("id");
                        String retailerId = data.getString("retailerId");
                        String totalPurchase = data.getString("totalPurchase");
                        String rewardPoint = data.getString("rewardPoint");

                        JSONObject userDetail = data.getJSONObject("userDetail");
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUserId(Integer.parseInt(userDetail.getString("id")));
                        userInfo.setUserName(userDetail.getString("userName"));
                        userInfo.setFullName(userDetail.getString("fullName"));
                        userInfo.setUserImage(userDetail.getString("profileImage"));
                        userInfo.setBusinessName("N/A");
                        userInfo.setEmail(userDetail.getString("email"));
                        userInfo.setAddress(userDetail.getString("address"));


                        RewardedUser rewardedUser = new RewardedUser();
                        rewardedUser.setId(id);
                        rewardedUser.setRetailerId(Integer.parseInt(retailerId));
                        rewardedUser.setRewardPoint(Double.parseDouble(rewardPoint));
                        rewardedUser.setTotalPurchase(Double.parseDouble(totalPurchase));
                        rewardedUser.setUserInfo(userInfo);
                        rewardedUsers.add(rewardedUser);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(rewardedUsers.size()>0){
                tv_message.setVisibility(View.GONE);
                recycleViewAdapter.notifyDataSetChanged();
            } else tv_message.setText("No Rewarded user found yet.");

            if(status.equalsIgnoreCase("fail")){
                tv_message.setText("No Rewarded user found yet.");
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressBar.setVisibility(View.GONE);
        }
    }

    public class RewardedRecycleView extends RecyclerView.Adapter<RewardedRecycleView.MyViewHolder> {

        private LayoutInflater inflater;
        private int lastPosition = -1;

        public RewardedRecycleView() {
            inflater = LayoutInflater.from(getContext());
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

            View view = inflater.inflate(R.layout.adapter_rewarded_user_list, parent, false);
            MyViewHolder holder = new MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {

            RewardedUser rewardedUser = rewardedUsers.get(position);
            myViewHolder.tv_userName.setText(rewardedUser.getUserInfo().getFullName());
            myViewHolder.tv_email.setText(rewardedUser.getUserInfo().getEmail());
            myViewHolder.tv_points.setText(""+rewardedUser.getRewardPoint().intValue());

            if (!rewardedUser.getUserInfo().getUserImage().isEmpty()) {
                myViewHolder.civ_profileImage.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(rewardedUser.getUserInfo().getUserImage()).into(myViewHolder.civ_profileImage);
            }

            setAnimation(myViewHolder.container, position);
        }

        @Override
        public int getItemCount() {
            return rewardedUsers.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv_userName, tv_email, tv_points;
            CircleImageView civ_profileImage;
            LinearLayout container;

            public MyViewHolder(View itemView) {
                super(itemView);

                container = (LinearLayout) itemView.findViewById(R.id.container);
                tv_userName = (TextView) itemView.findViewById(R.id.tv_userName);
                tv_email = (TextView) itemView.findViewById(R.id.tv_email);
                tv_points = (TextView) itemView.findViewById(R.id.tv_points);
                TextView textview1 = (TextView) itemView.findViewById(R.id.textview1);
                civ_profileImage = (CircleImageView) itemView.findViewById(R.id.civ_profileImage);
                font.setFont(tv_userName, Font.Lato_Regular);
                font.setFont(tv_email, Font.Lato_Regular);
                font.setFont(tv_points, Font.Lato_Regular);
                font.setFont(textview1, Font.Lato_Regular);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                RewardedUser rewardedUser = rewardedUsers.get(getAdapterPosition());
                intent.putExtra("rewardedUser", rewardedUser);
                getActivity().startActivity(intent);
            }
        }

        private void setAnimation(View viewToAnimate, int position) {

            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }
}