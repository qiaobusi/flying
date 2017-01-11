package com.wjc.flyinghelper.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        environmentButton = (Button) findViewById(R.id.environmentButton);

        environmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                provinceList = getProvinceFromJson();

                if (provinceList.size() > 0) {
                    for (int i = 0; i < provinceList.size(); i++) {
                        Log.i("province", provinceList.get(i));
                    }
                } else {
                    Log.i("province", "100");
                }


                cityList = getCityFromJson(provinceList.get(0));

                if (cityList.size() > 0) {
                    for (int i = 0; i < provinceList.size(); i++) {
                        Log.i("city", provinceList.get(i));
                    }
                } else {
                    Log.i("city", "200");
                }


                districtList = getDistrictFromJson(provinceList.get(0), cityList.get(0));

                environmentDialogView = LayoutInflater.from(EnvironmentActivity.this).inflate(R.layout.dialog_environment, null);
                environmentProvince = (WheelView) environmentDialogView.findViewById(R.id.environmentProvince);
                environmentCity = (WheelView) environmentDialogView.findViewById(R.id.environmentCity);
                environmentDistrict = (WheelView) environmentDialogView.findViewById(R.id.environmentDistrict);
                environmentProvince.setOnSelectListener(new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        cityList = getCityFromJson(text);
                        environmentCity.setData(cityList);
                    }

                    @Override
                    public void selecting(int id, String text) {

                    }
                });
                environmentCity.setOnSelectListener(new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        districtList = getCityFromJson(text);
                        environmentDistrict.setData(districtList);
                    }

                    @Override
                    public void selecting(int id, String text) {

                    }
                });

                environmentProvince.setData(provinceList);
                environmentCity.setData(cityList);
                environmentDistrict.setData(districtList);

                AlertDialog.Builder builder = new AlertDialog.Builder(EnvironmentActivity.this);
                builder.setTitle(R.string.environment_address);
                builder.setView(R.layout.dialog_am);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String province = environmentProvince.getSelectedText();
                        String city = environmentCity.getSelectedText();

                        String address = province + "-" + city;
                        environmentAddress.setText(address);
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });
        environmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = environmentAddress.getText().toString();

                Environment api = forceCast(MobAPI.getAPI(Environment.NAME));
                api.query(environmentAddress.getText().toString().trim(), null, new APICallback() {
                    @Override
                    public void onSuccess(API api, int i, Map<String, Object> map) {
                        Toast.makeText(EnvironmentActivity.this, "ok", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(API api, int i, Throwable throwable) {

                    }
                });

            }
        });

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

    private ArrayList<String> getProvinceFromJson() {
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

        return provinceList;
    }

    private ArrayList<String> getCityFromJson(String provinceName) {
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
                } else {
                    Log.i("getCityFromJson", "300");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cityList;
    }

    private ArrayList<String> getDistrictFromJson(String provinceName, String cityName) {
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
                                JSONObject districtObject = cityArray.getJSONObject(k);
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

        return districtList;
    }

}
