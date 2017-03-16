package com.wjc.flyinghelper.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;;

import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.activity.EnvironmentActivity;
import com.wjc.flyinghelper.activity.ExpressActivity;
import com.wjc.flyinghelper.activity.GoodevilActivity;
import com.wjc.flyinghelper.activity.IdcardActivity;
import com.wjc.flyinghelper.activity.MobileActivity;
import com.wjc.flyinghelper.activity.SleepActivity;
import com.wjc.flyinghelper.adapter.LifeGridViewAdapter;

import java.util.ArrayList;


public class LifeFragment extends Fragment {

    private Activity activity;
    private View view;

    private GridView lifeGridView;
    private LifeGridViewAdapter adapter;

    private int[] card = {
            R.string.life_sleep, R.string.life_environment, R.string.life_express,
            R.string.life_mobile, R.string.life_idcard, R.string.life_goodevil,
    };
    private ArrayList<String> arrayList;

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
        arrayList = new ArrayList<String>();
        for (int i = 0; i < card.length; i++) {
            arrayList.add(getText(card[i]).toString());
        }

        adapter = new LifeGridViewAdapter(activity, arrayList);

        lifeGridView = (GridView) view.findViewById(R.id.lifeGridView);
        lifeGridView.setAdapter(adapter);
        lifeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;

                switch (i) {
                    case 0 :
                        intent = new Intent(activity, SleepActivity.class);
                        startActivity(intent);
                        break;
                    case 1 :
                        intent = new Intent(activity, EnvironmentActivity.class);
                        startActivity(intent);
                        break;
                    case 2 :
                        intent = new Intent(activity, ExpressActivity.class);
                        startActivity(intent);
                        break;
                    case 3 :
                        intent = new Intent(activity, MobileActivity.class);
                        startActivity(intent);
                        break;
                    case 4 :
                        intent = new Intent(activity, IdcardActivity.class);
                        startActivity(intent);
                        break;
                    case 5 :
                        intent = new Intent(activity, GoodevilActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }


}
