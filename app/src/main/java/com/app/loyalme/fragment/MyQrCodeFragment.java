package com.app.loyalme.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.loyalme.R;
import com.app.loyalme.helper.SessionManager;
import com.squareup.picasso.Picasso;


public class MyQrCodeFragment extends Fragment {

    SessionManager sessionManager;
    CoordinatorLayout snackbarlocation;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_qr_code, container, false);

        snackbarlocation = (CoordinatorLayout) getActivity().findViewById(R.id.snackbarlocation);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        String imageUrl = sessionManager.getUserInfo().getQrCodeImage();
        //CircleImageView imageView = (CircleImageView) view.findViewById(R.id.iv_QrCode);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_QrCode);
        if(!imageUrl.isEmpty()){
            Picasso.with(getContext()).load(imageUrl).into(imageView);
        }
    }

}
