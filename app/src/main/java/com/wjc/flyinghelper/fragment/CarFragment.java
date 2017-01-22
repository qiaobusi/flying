package com.wjc.flyinghelper.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.activity.CarinfoActivity;
import com.wjc.flyinghelper.activity.LoginActivity;
import com.wjc.flyinghelper.activity.UcenterActivity;
import com.wjc.flyinghelper.config.Config;

public class CarFragment extends Fragment {

    private Activity activity;
    private View view;

    private LinearLayout carInfo, carUserinfo;
    private TextView carInfoMore, carUcenterMore;

    public CarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_car, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        initViewComponent();
    }

    private void initViewComponent() {
        carInfo = (LinearLayout) activity.findViewById(R.id.carInfo);
        carUserinfo = (LinearLayout) activity.findViewById(R.id.carUserinfo);

        carInfoMore = (TextView) activity.findViewById(R.id.carInfoMore);
        carUcenterMore = (TextView) activity.findViewById(R.id.carUcenterMore);

        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "iconfont.ttf");
        carInfoMore.setTypeface(typeface);
        carInfoMore.setText(activity.getString(R.string.icon_forward));
        carUcenterMore.setTypeface(typeface);
        carUcenterMore.setText(activity.getString(R.string.icon_forward));

        carInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin()) {
                    Intent intent = new Intent(activity, CarinfoActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        carUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin()) {
                    Intent intent = new Intent(activity, UcenterActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean isLogin() {
        boolean isLogin = false;

        SharedPreferences sharedPreferences = activity.getSharedPreferences(Config.userinfo, Context.MODE_PRIVATE);

        String userinfoMobile = sharedPreferences.getString(Config.userinfoMobile, "");
        if (!userinfoMobile.equals("")) {
            isLogin = true;
        }

        return isLogin;
    }


}
