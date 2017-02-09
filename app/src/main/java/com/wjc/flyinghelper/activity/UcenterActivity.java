package com.wjc.flyinghelper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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


public class UcenterActivity extends AppCompatActivity {

    private EditText ucenterName, ucenterPlatenumber;
    private TextView ucenterProvinceAz;
    private LinearLayout ucenterPassword;
    private TextView ucenterPasswordMore;
    private Button ucenterSave, ucenterQuit;

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

    private String id, mobile, name, platenumber;
    private SharedPreferences sharedPreferences;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucenter);

        initToolbar();
        initViewComponent();
        initValue();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_ucenter);
    }

    private void initValue() {
        sharedPreferences = getSharedPreferences(Config.userinfo, Context.MODE_PRIVATE);

        id = sharedPreferences.getString(Config.userinfoId, "");
        mobile = sharedPreferences.getString(Config.userinfoMobile, "");
        name = sharedPreferences.getString(Config.userinfoName, "");
        platenumber = sharedPreferences.getString(Config.userinfoPlatenumber, "");

        if (!name.equals("")) {
            ucenterName.setText(name);
        }
        if (!platenumber.equals("")) {
            String[] platenumberArray = platenumber.split(Config.platenumberSeparate);
            ucenterPlatenumber.setText(platenumberArray[1]);
            ucenterProvinceAz.setText(platenumberArray[0]);
        }
    }

    private void initViewComponent() {
        ucenterName = (EditText) findViewById(R.id.ucenterName);
        ucenterPlatenumber = (EditText) findViewById(R.id.ucenterPlatenumber);
        ucenterProvinceAz = (TextView) findViewById(R.id.ucenterProvinceAz);
        ucenterPassword = (LinearLayout) findViewById(R.id.ucenterPassword);
        ucenterPasswordMore = (TextView) findViewById(R.id.ucenterPasswordMore);
        ucenterSave = (Button) findViewById(R.id.ucenterSave);
        ucenterQuit = (Button) findViewById(R.id.ucenterQuit);

        provinceList = new ArrayList<String>();
        for (int i = 0; i < provinceArray.length; i++) {
            provinceList.add(provinceArray[i]);
        }
        azList = new ArrayList<String>();
        for (int i = 0; i < azArray.length; i++) {
            azList.add(azArray[i]);
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        ucenterPasswordMore.setTypeface(typeface);
        ucenterPasswordMore.setText(getString(R.string.icon_forward));

        ucenterProvinceAz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = LayoutInflater.from(UcenterActivity.this).inflate(R.layout.dialog_province_az, null);
                dialogProvince = (WheelView) dialogView.findViewById(R.id.dialogProvince);
                dialogAz = (WheelView) dialogView.findViewById(R.id.dialogAz);
                dialogProvince.setData(provinceList);
                dialogAz.setData(azList);

                dialogProvince.setDefault(0);
                dialogAz.setDefault(0);

                AlertDialog.Builder builder = new AlertDialog.Builder(UcenterActivity.this);
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
                        ucenterProvinceAz.setText(text);
                    }
                });
                builder.show();
            }
        });

        ucenterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UcenterActivity.this, PasswordActivity.class);
                startActivity(intent);
            }
        });

        ucenterSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String provinceAz = ucenterProvinceAz.getText().toString();
                String name = ucenterName.getText().toString().trim();
                String platenumber = ucenterPlatenumber.getText().toString().trim();
                platenumber = provinceAz + Config.platenumberSeparate + platenumber;

                saveUserinfo(id, name, platenumber);

                changeButtonStatus(0);
            }
        });

        ucenterQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.userinfoId, "");
                editor.putString(Config.userinfoMobile, "");
                editor.putString(Config.userinfoName, "");
                editor.putString(Config.userinfoPlatenumber, "");
                editor.commit();

                UcenterActivity.this.finish();
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
                            String info = jsonObject.getString("info");
                            Toast.makeText(UcenterActivity.this, info, Toast.LENGTH_LONG).show();

                            name = ucenterName.getText().toString().trim();
                            String provinceAz = ucenterProvinceAz.getText().toString();
                            platenumber = ucenterPlatenumber.getText().toString().trim();
                            platenumber = provinceAz + Config.platenumberSeparate + platenumber;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Config.userinfoName, name);
                            editor.putString(Config.userinfoPlatenumber, platenumber);
                            editor.commit();
                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(UcenterActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    changeButtonStatus(1);
                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(UcenterActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();

                    changeButtonStatus(1);
                }
            }
        };

    }

    private void saveUserinfo(final String id, final String name, final String platenumber){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestUrl = Config.httpUrl + "/web/car/saveuserinfo";

                try {
                    String data = "id=" + URLEncoder.encode(id, "UTF-8")
                            + "&name=" + URLEncoder.encode(name, "UTF-8")
                            + "&platenumber=" + URLEncoder.encode(platenumber, "UTF-8");

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

    private void changeButtonStatus(int status) {
        if (status == 0) {
            ucenterSave.setEnabled(false);
            ucenterSave.setBackgroundResource(R.drawable.shape_button_disabled);
        } else {
            ucenterSave.setEnabled(true);
            ucenterSave.setBackgroundResource(R.drawable.selector_button);
        }
    }

}
