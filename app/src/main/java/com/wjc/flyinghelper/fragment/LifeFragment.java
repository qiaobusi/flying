package com.wjc.flyinghelper.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.activity.EnvironmentActivity;
import com.wjc.flyinghelper.activity.IdcardActivity;
import com.wjc.flyinghelper.activity.MobileActivity;
import com.wjc.flyinghelper.view.SquareLayout;


public class LifeFragment extends Fragment {

    private Activity activity;

    private View view;

    private SquareLayout lifeEnvironment, lifeExpress, lifeMobile, lifeIdcard;


    public LifeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_life, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        initViewComponent();
    }

    private void initViewComponent() {
        lifeEnvironment = (SquareLayout) view.findViewById(R.id.lifeEnvironment);
        lifeExpress = (SquareLayout) view.findViewById(R.id.lifeExpress);
        lifeMobile = (SquareLayout) view.findViewById(R.id.lifeMobile);
        lifeIdcard = (SquareLayout) view.findViewById(R.id.lifeIdcard);

        lifeEnvironment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EnvironmentActivity.class);
                startActivity(intent);
            }
        });
        lifeExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "lifeExpress", Toast.LENGTH_LONG).show();
            }
        });
        lifeMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MobileActivity.class);
                startActivity(intent);
            }
        });
        lifeIdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, IdcardActivity.class);
                startActivity(intent);
            }
        });


    }


}
