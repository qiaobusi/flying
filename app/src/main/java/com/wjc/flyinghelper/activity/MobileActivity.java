package com.wjc.flyinghelper.activity;

import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.mobapi.API;
import com.mob.mobapi.APICallback;
import com.mob.mobapi.MobAPI;
import com.mob.mobapi.apis.Mobile;
import com.wjc.flyinghelper.R;

import java.util.HashMap;
import java.util.Map;

public class MobileActivity extends AppCompatActivity {

    private EditText mobileNumber;
    private TextView mobileResult;
    private FloatingActionButton mobileSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);

        initToolbar();
        initViewComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_mobile);
    }

    private void initViewComponent() {
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        mobileResult = (TextView) findViewById(R.id.mobileResult);
        mobileSearch = (FloatingActionButton) findViewById(R.id.mobileSearch);

        mobileSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = mobileNumber.getText().toString().trim();
                if (mobile.length() == 0) {
                    return;
                }

                changeFabStatus(0);

                Mobile api = (Mobile) MobAPI.getAPI(Mobile.NAME);
                api.phoneNumberToAddress(mobile, new APICallback() {
                    @Override
                    public void onSuccess(API api, int i, Map<String, Object> map) {
                        HashMap<String, Object> address = (HashMap<String, Object>) map.get("result");

                        String province = com.mob.tools.utils.R.toString(address.get("province"));
                        String city = com.mob.tools.utils.R.toString(address.get("city"));
                        String operator = com.mob.tools.utils.R.toString(address.get("operator"));
                        String cityCode = com.mob.tools.utils.R.toString(address.get("cityCode"));

                        String result = province + city + " " + operator + "（" + cityCode + "）";
                        mobileResult.setText(result);

                        if (mobileResult.getVisibility() == View.GONE) {
                            mobileResult.setVisibility(View.VISIBLE);
                        }

                        changeFabStatus(1);
                    }

                    @Override
                    public void onError(API api, int i, Throwable throwable) {
                        Toast.makeText(MobileActivity.this, R.string.query_error, Toast.LENGTH_LONG).show();

                        changeFabStatus(1);
                    }
                });
            }
        });

    }

    private void changeFabStatus(int status) {
        if (status == 0) {
            mobileSearch.setEnabled(false);
            mobileSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButtonDisabled)));
        } else {
            mobileSearch.setEnabled(true);
            mobileSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButton)));
        }
    }


}
