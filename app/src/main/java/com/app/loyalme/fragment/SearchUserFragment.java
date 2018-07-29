package com.app.loyalme.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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


public class SearchUserFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RewardedRecycleView recycleViewAdapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tv_message;
    View view;
    Font font;
    CharSequence lastSearchkey = "";

    List<RewardedUser> rewardedUsers;

    public SearchUserFragment() {
        // Required empty public constructor
    }


    public static SearchUserFragment newInstance(String param1, String param2) {
        SearchUserFragment fragment = new SearchUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        font = new Font(getActivity());
        font = new Font(getActivity());
        rewardedUsers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_user, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());recyclerView.addItemDecoration(dividerItemDecoration);
        recycleViewAdapter = new RewardedRecycleView();
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tv_message = (TextView) view.findViewById(R.id.tv_message);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText ed_searchText = (EditText) view.findViewById(R.id.ed_searchText);
        final ImageView iv_search = (ImageView) view.findViewById(R.id.iv_search);
       /* ed_searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    iv_search.setImageDrawable(getResources().getDrawable(R.drawable.ico_search_list_active));
                }else {
                    iv_search.setImageDrawable(getResources().getDrawable(R.drawable.ico_search_list));
                }
            }
        });
*/


        ed_searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 0) {
                    iv_search.setImageDrawable(getResources().getDrawable(R.drawable.ico_search_list));
                    tv_message.setText("Search User");
                    rewardedUsers.clear();
                    searchUsers(getContext(), "");
                } else {
                    searchUsers(getContext(), "" + s);
                    lastSearchkey = s;
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ed_searchText.clearFocus();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        searchUsers(getContext(), "" + lastSearchkey);
    }

    public void searchUsers(final Context context, final String searchingKey) {

        if (Util.isConnectingToInternet(context)) {

            progressBar.setVisibility(View.VISIBLE);
            tv_message.setText("Loading...");
            StringRequest postRequest = new StringRequest(Request.Method.POST, WebServices.SEARCH_USER_BY_NAME_OR_EMAILURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                progressBar.setVisibility(View.GONE);
                                JSONObject jsonResponse = new JSONObject(response);
                                //Log.v("Response",jsonResponse.toString());
                                String status = jsonResponse.getString("status");
                                rewardedUsers.clear();
                                if (status.equals("success")) {

                                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject userDetail = jsonArray.getJSONObject(i);

                                        UserInfo userInfo = new UserInfo();
                                        userInfo.setUserId(Integer.parseInt(userDetail.getString("id")));
                                        userInfo.setUserName(userDetail.getString("userName"));
                                        userInfo.setFullName(userDetail.getString("fullName"));
                                        userInfo.setUserImage(userDetail.getString("profileImage"));
                                        userInfo.setBusinessName("N/A");
                                        userInfo.setEmail(userDetail.getString("email"));
                                        userInfo.setAddress(userDetail.getString("address"));


                                        RewardedUser rewardedUser = new RewardedUser();
                                        rewardedUser.setUserInfo(userInfo);
                                        if (userDetail.has("point")) {

                                            JSONObject point = userDetail.getJSONObject("point");
                                            int id = point.getInt("id");
                                            String retailerId = point.getString("retailerId");
                                            String totalPurchase = point.getString("totalPurchase");
                                            String rewardPoint = point.getString("rewardPoint");
                                            rewardedUser.setId(id);
                                            rewardedUser.setRetailerId(Integer.parseInt(retailerId));
                                            rewardedUser.setRewardPoint(Double.parseDouble(rewardPoint));
                                            rewardedUser.setTotalPurchase(Double.parseDouble(totalPurchase));
                                        }
                                        rewardedUsers.add(rewardedUser);
                                    }

                                    if (rewardedUsers.size() > 0) {
                                        tv_message.setVisibility(View.GONE);
                                        recycleViewAdapter.notifyDataSetChanged();
                                    } else tv_message.setText("User not found.");
                                } else if (status.equalsIgnoreCase("fail")) {
                                    tv_message.setVisibility(View.VISIBLE);
                                    recycleViewAdapter.notifyDataSetChanged();
                                    tv_message.setText("User not found.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    params.put("authToken", new SessionManager(getActivity()).getUserInfo().getAuthToken());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", searchingKey);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);

        } else {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(view, "Please Check internet connection.!", Snackbar.LENGTH_LONG).setAction("ok", null).show();
            tv_message.setText("Please Check internet connection.!");
        }
    }


    public class RewardedRecycleView extends RecyclerView.Adapter<RewardedRecycleView.MyViewHolder> {

        private int lastPosition = -1;

        @Override
        public RewardedRecycleView.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

            // final View view = inflater.inflate(R.layout.adapter_rewarded_user_list, parent, false);
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rewarded_user_list, parent, false);
            RewardedRecycleView.MyViewHolder holder = new RewardedRecycleView.MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RewardedRecycleView.MyViewHolder myViewHolder, final int position) {

            RewardedUser rewardedUser = rewardedUsers.get(position);
            myViewHolder.tv_userName.setText(rewardedUser.getUserInfo().getFullName());
            myViewHolder.tv_email.setText(rewardedUser.getUserInfo().getEmail());
            myViewHolder.tv_points.setText("" + rewardedUser.getRewardPoint());


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

        private void setAnimation(View viewToAnimate, int position) {

            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public void onViewDetachedFromWindow(MyViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            ((MyViewHolder) holder).clearAnimation();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv_userName, tv_email, tv_points;
            CircleImageView civ_profileImage;
            LinearLayout pointLayout, container;

            public MyViewHolder(View itemView) {
                super(itemView);

                container = (LinearLayout) itemView.findViewById(R.id.container);
                tv_userName = (TextView) itemView.findViewById(R.id.tv_userName);
                tv_email = (TextView) itemView.findViewById(R.id.tv_email);
                tv_points = (TextView) itemView.findViewById(R.id.tv_points);
                TextView textview1 = (TextView) itemView.findViewById(R.id.textview1);
                civ_profileImage = (CircleImageView) itemView.findViewById(R.id.civ_profileImage);
                pointLayout = (LinearLayout) itemView.findViewById(R.id.pointLayout);
                pointLayout.setVisibility(View.GONE);
                font.setFont(tv_userName, Font.Lato_Regular);
                font.setFont(tv_email, Font.Lato_Regular);
                font.setFont(tv_points, Font.Lato_Light);
                font.setFont(textview1, Font.Lato_Light);
                itemView.setOnClickListener(this);
                tv_email.setVisibility(View.VISIBLE);

            }

            public void clearAnimation() {
                container.clearAnimation();
            }

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                RewardedUser rewardedUser = rewardedUsers.get(getAdapterPosition());
                intent.putExtra("rewardedUser", rewardedUser);
                getActivity().startActivity(intent);
            }
        }

    }
}
