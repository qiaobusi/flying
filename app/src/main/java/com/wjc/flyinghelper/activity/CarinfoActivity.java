package com.wjc.flyinghelper.activity;

import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CarinfoActivity extends AppCompatActivity {
    private Spinner provinceShort, a2z;
    private EditText plateNumber;
    private TextView carInfoName;
    private Button dial;
    private FloatingActionButton carSearch;

    private String[] provinceShorts = {
            "京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪", "苏",
            "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂",
            "琼", "川", "贵", "云", "渝", "藏", "陕", "甘", "青", "宁",
            "新"
    };
    private String[] a2zs = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "G",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
    private ArrayList<String> provinceShortList, a2zList;
    private ArrayAdapter provinceShortAdapter, a2zAdapter;

    private String provinceShortVal, a2zVal;
    private String name, mobile;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carinfo);

        initToolbar();
        initViewComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_car_info);
    }

    private void initViewComponent() {
        provinceShortList = new ArrayList<String>();
        a2zList = new ArrayList<String>();
        for (int i = 0; i < provinceShorts.length; i++) {
            provinceShortList.add(provinceShorts[i]);
        }
        for (int i = 0; i < a2zs.length; i++) {
            a2zList.add(a2zs[i]);
        }

        provinceShort = (Spinner) findViewById(R.id.provinceShort);
        a2z = (Spinner) findViewById(R.id.a2z);
        plateNumber = (EditText) findViewById(R.id.plateNumber);
        carInfoName = (TextView) findViewById(R.id.carInfoName);
        dial = (Button) findViewById(R.id.dial);
        carSearch = (FloatingActionButton) findViewById(R.id.carSearch);

        provinceShortAdapter = new ArrayAdapter<String>(CarinfoActivity.this, android.R.layout.simple_spinner_item, provinceShortList);
        provinceShortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a2zAdapter = new ArrayAdapter<String>(CarinfoActivity.this, android.R.layout.simple_spinner_item, a2zList);
        a2zAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        provinceShort.setAdapter(provinceShortAdapter);
        a2z.setAdapter(a2zAdapter);

        provinceShort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                provinceShortVal = (String) provinceShort.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        a2z.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                a2zVal = (String) a2z.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialMobile(mobile);
            }
        });

        carSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plate = plateNumber.getText().toString().trim();
                plate = provinceShortVal + a2zVal + plate;

                getCarInfo(plate);

                changeFabStatus(0);
            }
        });


        handler = new Handler() {
            public void handleMessage(Message message) {
                if (message.what == Config.REQUEST_SUCCESS) {
                    Bundle bundle = message.getData();
                    String result = bundle.getString(Config.RETURN_RESULT);

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        int status = jsonObject.getInt("status");
                        if (status == Config.EXEC_SUCCESS) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            name = dataObject.getString("name");
                            mobile = dataObject.getString("mobile");

                            carInfoName.setText(name);
                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(CarinfoActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(CarinfoActivity.this, R.string.query_error, Toast.LENGTH_LONG).show();
                }

                changeFabStatus(1);
            }
        };

    }

    private void getCarInfo(final String plateNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String requestUrl = Config.httpUrl + "/web/api/getcarinfo";

                try {
                    String data = "plate_number=" + URLEncoder.encode(plateNumber, "UTF-8");

                    URL url = new URL(requestUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(3000);

                    urlConnection.connect();

                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    Message message = new Message();

                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = urlConnection.getInputStream();

                        byte[] bytes = new byte[1024];
                        int length = 0;
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        while ((length = inputStream.read(bytes)) != -1) {
                            byteArrayOutputStream.write(bytes, 0, length);
                        }

                        inputStream.close();

                        String result = new String(byteArrayOutputStream.toByteArray());

                        Bundle bundle = new Bundle();
                        bundle.putString(Config.RETURN_RESULT, result);

                        message.setData(bundle);
                        message.what = Config.REQUEST_SUCCESS;
                    } else {
                        message.what = Config.REQUEST_ERROR;
                    }

                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void dialMobile(String mobile) {

    }

    private void changeFabStatus(int status) {
        if (status == 0) {
            carSearch.setEnabled(false);
            carSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButtonDisabled)));
        } else {
            carSearch.setEnabled(true);
            carSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButton)));
        }
    }

}
