package com.wjc.flyinghelper.activity;

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

    private EditText mobile;
    private TextView mobileResult;
    private Button mobileButton;

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
        toolbarText.setText(R.string.life_mobile);
    }

    private void initViewComponent() {
        mobile = (EditText) findViewById(R.id.mobile);
        mobileResult = (TextView) findViewById(R.id.mobileResult);
        mobileButton = (Button) findViewById(R.id.mobileButton);

        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileText = mobile.getText().toString().trim();
                if (mobileText.length() == 0) {
                    return;
                }

                changeButtonStatus(0);

                Mobile api = (Mobile) MobAPI.getAPI(Mobile.NAME);
                api.phoneNumberToAddress(mobileText, new APICallback() {
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

                        changeButtonStatus(1);
                    }

                    @Override
                    public void onError(API api, int i, Throwable throwable) {
                        Toast.makeText(MobileActivity.this, R.string.query_error, Toast.LENGTH_LONG).show();

                        changeButtonStatus(1);
                    }
                });

            }
        });

    }

    private void changeButtonStatus(int status) {
        if (status == 0) {
            mobileButton.setEnabled(false);
            mobileButton.setText(R.string.querying);
            mobileButton.setBackgroundResource(R.drawable.shape_button_disabled);
        } else {
            mobileButton.setEnabled(true);
            mobileButton.setText(R.string.query);
            mobileButton.setBackgroundResource(R.drawable.selector_button);
        }
    }


}
