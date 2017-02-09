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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jp.wheelview.WheelView;
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
    private TextView carinfoProvinceAz;
    private EditText carinfoPlatenumber;
    private TextView carinfoName;
    private Button carinfoSearch, carinfoDial;
    private LinearLayout carinfoLinearLayout;

    private WheelView dialogProvince, dialogAz;
    private View dialogView;
    private String[] provinceArray = {
            "京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪", "苏",
            "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂",
            "琼", "川", "贵", "云", "渝", "藏", "陕", "甘", "青", "宁",
            "新"
    };
    private String[] azArray = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "G",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
    private ArrayList<String> provinceList, azList;

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
        carinfoProvinceAz = (TextView) findViewById(R.id.carinfoProvinceAz);
        carinfoPlatenumber = (EditText) findViewById(R.id.carinfoPlatenumber);
        carinfoName = (TextView) findViewById(R.id.carinfoName);
        carinfoSearch = (Button) findViewById(R.id.carinfoSearch);
        carinfoDial = (Button) findViewById(R.id.carinfoDial);
        carinfoLinearLayout = (LinearLayout) findViewById(R.id.carinfoLinearLayout);

        provinceList = new ArrayList<String>();
        for (int i = 0; i < provinceArray.length; i++) {
            provinceList.add(provinceArray[i]);
        }
        azList = new ArrayList<String>();
        for (int i = 0; i < azArray.length; i++) {
            azList.add(azArray[i]);
        }

        carinfoProvinceAz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = LayoutInflater.from(CarinfoActivity.this).inflate(R.layout.dialog_province_az, null);
                dialogProvince = (WheelView) dialogView.findViewById(R.id.dialogProvince);
                dialogAz = (WheelView) dialogView.findViewById(R.id.dialogAz);
                dialogProvince.setData(provinceList);
                dialogAz.setData(azList);

                dialogProvince.setDefault(0);
                dialogAz.setDefault(0);

                AlertDialog.Builder builder = new AlertDialog.Builder(CarinfoActivity.this);
                builder.setTitle(R.string.province_az_title);
                builder.setView(dialogView);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String provinceText = dialogProvince.getSelectedText();
                        String azText = dialogAz.getSelectedText();

                        String text = provinceText + azText;
                        carinfoProvinceAz.setText(text);
                    }
                });
                builder.show();
            }
        });

        carinfoDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialMobile(mobile);
            }
        });

        carinfoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String provinceAz = carinfoProvinceAz.getText().toString();
                String platenumber = carinfoPlatenumber.getText().toString().trim();
                platenumber = provinceAz + Config.platenumberSeparate + platenumber;

                getUserinfo(platenumber);

                changeButtonStatus(0);
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

                            carinfoName.setText(name);
                            if (carinfoLinearLayout.getVisibility() == View.GONE) {
                                carinfoLinearLayout.setVisibility(View.VISIBLE);
                            }
                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(CarinfoActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(CarinfoActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();
                }

                changeButtonStatus(1);
            }
        };

    }

    private void getUserinfo(final String plateNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String requestUrl = Config.httpUrl + "/web/car/getuserinfo";

                try {
                    String data = "platenumber=" + URLEncoder.encode(plateNumber, "UTF-8");

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

    private void changeButtonStatus(int status) {
        if (status == 0) {
            carinfoSearch.setEnabled(false);
            carinfoSearch.setBackgroundResource(R.color.colorButtonDisabled);
        } else {
            carinfoSearch.setEnabled(true);
            carinfoSearch.setBackgroundResource(R.drawable.selector_button_search);
        }
    }

}
