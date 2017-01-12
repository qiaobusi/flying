package com.wjc.flyinghelper.activity;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jp.wheelview.WheelView;
import com.mob.mobapi.API;
import com.mob.mobapi.APICallback;
import com.mob.mobapi.MobAPI;
import com.mob.mobapi.apis.Environment;
import com.wjc.flyinghelper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mob.tools.utils.R.forceCast;

public class EnvironmentActivity extends AppCompatActivity {

    private LinearLayout environmentLayout;
    private TextView environmentAddress;
    private Button environmentButton;

    private View environmentDialogView;
    private WheelView environmentProvince, environmentCity, environmentDistrict;

    private ArrayList<String> provinceList, cityList, districtList;

    private String json = null;

    private Handler handler;

    private static final int INIT_CITY = 1;
    private static final int INIT_DISTRICT = 2;
    private static final String SEPARATOR = "-";

    private TextView environmentResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);

        initToolbar();
        initViewComponent();
        initJson();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.life_environment);
    }

    private void initViewComponent() {
        provinceList = new ArrayList<String>();
        cityList = new ArrayList<String>();
        districtList = new ArrayList<String>();

        environmentLayout = (LinearLayout) findViewById(R.id.environmentLayout);
        environmentAddress = (TextView) findViewById(R.id.environmentAddress);
        environmentResult = (TextView) findViewById(R.id.environmentResult);

        environmentButton = (Button) findViewById(R.id.environmentButton);

        environmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProvinceFromJson();
                getCityFromJson(provinceList.get(0));
                getDistrictFromJson(provinceList.get(0), cityList.get(0));

                environmentDialogView = LayoutInflater.from(EnvironmentActivity.this).inflate(R.layout.dialog_environment, null);
                environmentProvince = (WheelView) environmentDialogView.findViewById(R.id.environmentProvince);
                environmentCity = (WheelView) environmentDialogView.findViewById(R.id.environmentCity);
                environmentDistrict = (WheelView) environmentDialogView.findViewById(R.id.environmentDistrict);

                environmentProvince.setData(provinceList);
                environmentCity.setData(cityList);
                environmentDistrict.setData(districtList);

                environmentProvince.setDefault(0);
                environmentCity.setDefault(0);
                environmentDistrict.setDefault(0);

                environmentProvince.setOnSelectListener(new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        getCityFromJson(text);

                        Message message = new Message();
                        message.what = INIT_CITY;

                        handler.sendMessage(message);
                    }

                    @Override
                    public void selecting(int id, String text) {

                    }
                });
                environmentCity.setOnSelectListener(new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        String provinceText = environmentProvince.getSelectedText();
                        getDistrictFromJson(provinceText, text);

                        Message message = new Message();
                        message.what = INIT_DISTRICT;

                        handler.sendMessage(message);
                    }

                    @Override
                    public void selecting(int id, String text) {

                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(EnvironmentActivity.this);
                builder.setTitle(R.string.environment_address);
                builder.setView(environmentDialogView);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String province = environmentProvince.getSelectedText();
                        String city = environmentCity.getSelectedText();
                        String district = environmentDistrict.getSelectedText();

                        String address = province + SEPARATOR + city + SEPARATOR + district;

                        environmentAddress.setText(address);
                    }
                });
                builder.show();
            }
        });
        environmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButtonStatus(0);

                String address = environmentAddress.getText().toString();
                String[] addressArray = address.split(SEPARATOR);

                Environment api = forceCast(MobAPI.getAPI(Environment.NAME));
                api.query(addressArray[2], addressArray[0], new APICallback() {
                    @Override
                    public void onSuccess(API api, int i, Map<String, Object> map) {
                        ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) map.get("result");
                        HashMap<String, Object> data = list.get(0);

                        updateEnvironment(data);

                        changeButtonStatus(1);
                    }

                    @Override
                    public void onError(API api, int i, Throwable throwable) {

                    }
                });

            }
        });

        handler = new Handler() {
            public void handleMessage(Message message) {
                if (message.what == INIT_CITY) {
                    environmentCity.refreshData(cityList);
                    environmentCity.setDefault(0);

                    String province = environmentProvince.getSelectedText();
                    String city = environmentCity.getSelectedText();
                    getDistrictFromJson(province, city);

                    environmentDistrict.refreshData(districtList);
                    environmentDistrict.setDefault(0);
                } else if (message.what == INIT_DISTRICT) {
                    environmentDistrict.refreshData(districtList);
                    environmentDistrict.setDefault(0);
                }
            }
        };

    }

    private void initJson() {
        try {
            InputStream inputStream = getAssets().open("environment.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getProvinceFromJson() {
        provinceList.clear();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray provinceArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < provinceArray.length(); i++) {
                JSONObject provinceObject = provinceArray.getJSONObject(i);
                String province = provinceObject.getString("province");
                provinceList.add(province);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCityFromJson(String provinceName) {
        cityList.clear();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray provinceArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < provinceArray.length(); i++) {
                JSONObject provinceObject = provinceArray.getJSONObject(i);
                String province = provinceObject.getString("province");
                if (province.equals(provinceName)) {
                    JSONArray cityArray = provinceObject.getJSONArray("city");
                    for (int j = 0; j < cityArray.length(); j++) {
                        JSONObject cityObject = cityArray.getJSONObject(j);
                        String city = cityObject.getString("city");
                        cityList.add(city);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDistrictFromJson(String provinceName, String cityName) {
        districtList.clear();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray provinceArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < provinceArray.length(); i++) {
                JSONObject provinceObject = provinceArray.getJSONObject(i);
                String province = provinceObject.getString("province");
                if (province.equals(provinceName)) {
                    JSONArray cityArray = provinceObject.getJSONArray("city");
                    for (int j = 0; j < cityArray.length(); j++) {
                        JSONObject cityObject = cityArray.getJSONObject(j);
                        String city = cityObject.getString("city");
                        if (city.equals(cityName)) {
                            JSONArray districtArray = cityObject.getJSONArray("district");
                            for (int k = 0; k < districtArray.length(); k++) {
                                JSONObject districtObject = districtArray.getJSONObject(k);
                                String district = districtObject.getString("district");
                                districtList.add(district);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeButtonStatus(int status) {
        if (status == 0) {
            environmentButton.setEnabled(false);
            environmentButton.setText(R.string.querying);
            environmentButton.setBackgroundResource(R.drawable.shape_button_disabled);
        } else {
            environmentButton.setEnabled(true);
            environmentButton.setText(R.string.query);
            environmentButton.setBackgroundResource(R.drawable.selector_button);
        }
    }

    private void updateEnvironment(HashMap<String, Object> data) {
        String aqi = com.mob.tools.utils.R.toString(data.get("aqi"));
        String no2 = com.mob.tools.utils.R.toString(data.get("no2"));
        String pm10 = com.mob.tools.utils.R.toString(data.get("pm10"));
        String pm25 = com.mob.tools.utils.R.toString(data.get("pm25"));
        String quality = com.mob.tools.utils.R.toString(data.get("quality"));
        String so2 = com.mob.tools.utils.R.toString(data.get("so2"));
        String updateTime = com.mob.tools.utils.R.toString(data.get("updateTime"));

        String result = "aqi:" + aqi + " no2:" + no2 + " pm10:" + pm10 + " pm25:" + pm25 + " quality:" + quality + " so2:" + so2 + " updateTime:" + updateTime;
        environmentResult.setText(result);
    }

}
