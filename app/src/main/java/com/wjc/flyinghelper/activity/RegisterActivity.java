package com.wjc.flyinghelper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class RegisterActivity extends AppCompatActivity {

    private TextView registerLogo;
    private EditText registerMobile, registerPassword, registerCode;
    private Button registerCodeButton, registerButton;

    private CountDownTimer countDownTimer;

    private int second = 120;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initToolbar();
        initViewComponent();
        initSecurityCodeSDK();
    }

    private void initSecurityCodeSDK() {
        try {
            EventHandler eventHandler = new EventHandler() {
                public void afterEvent(int event, int result, Object data) {
                    Message msg = new Message();
                    msg.arg1 = event;
                    msg.arg2 = result;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            };

            SMSSDK.registerEventHandler(eventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_register);
    }

    private void initViewComponent() {
        registerLogo = (TextView) findViewById(R.id.registerLogo);
        registerMobile = (EditText) findViewById(R.id.registerMobile);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
        registerCode = (EditText) findViewById(R.id.registerCode);
        registerCodeButton = (Button) findViewById(R.id.registerCodeButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        registerLogo.setTypeface(typeface);
        registerLogo.setText(getString(R.string.icon_account));

        countDownTimer = new CountDownTimer(second * 1000, 1000) {
            @Override
            public void onTick(long l) {
                if (registerCodeButton.isEnabled()) {
                    registerCodeButton.setEnabled(false);
                    registerCodeButton.setBackgroundResource(R.drawable.shape_button_disabled);
                }

                String text = String.valueOf(l / 1000);
                registerCodeButton.setText(text);
            }

            @Override
            public void onFinish() {
                registerCodeButton.setEnabled(true);
                registerCodeButton.setBackgroundResource(R.drawable.selector_button);
                registerCodeButton.setText(getString(R.string.register_code_button));
            }
        };

        registerCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = registerMobile.getText().toString().trim();
                if (mobile.length() == 0) {
                    return;
                }

                sendCode(mobile);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = registerMobile.getText().toString().trim();
                String password = registerPassword.getText().toString();
                String code = registerCode.getText().toString().trim();

                if (mobile.length() == 0 || password.length() == 0 || code.length() == 0) {
                    return;
                }

                register(mobile, password, code);

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
                            String id = dataObject.getString("id");
                            String mobile = dataObject.getString("mobile");
                            String name = dataObject.getString("name");
                            String platenumber = dataObject.getString("platenumber");

                            SharedPreferences sharedPreferences = getSharedPreferences(Config.userinfo, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Config.userinfoId, id);
                            editor.putString(Config.userinfoMobile, mobile);
                            editor.putString(Config.userinfoName, name);
                            editor.putString(Config.userinfoPlatenumber, platenumber);
                            editor.commit();

                            Intent intent = new Intent(RegisterActivity.this, UcenterActivity.class);
                            startActivity(intent);

                            setResult(999);

                            finish();
                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(RegisterActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    changeButtonStatus(1);
                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(RegisterActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();

                    changeButtonStatus(1);
                } else {
                    int event = message.arg1;
                    int result = message.arg2;
                    Object data = message.obj;
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            //验证成功
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                            //发送成功
                            countDownTimer.start();

                            Toast.makeText(RegisterActivity.this, R.string.code_send_success, Toast.LENGTH_SHORT).show();
                        } else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                            //返回支持发送验证码的国家列表
                        }
                    }else{
                        int status = 0;
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(throwable.getMessage());
                            String des = object.optString("detail");
                            status = object.optInt("status");
                            if (!TextUtils.isEmpty(des)) {
                                Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                    }
                }
            }
        };

    }

    private void sendCode(String mobile) {
        SMSSDK.getVerificationCode("86", mobile);
    }

    private void register(final String mobile, final String password, final String code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestUrl = Config.httpUrl + "/web/car/register";

                try {
                    String data = "mobile=" + URLEncoder.encode(mobile, "UTF-8")
                            + "&password=" + URLEncoder.encode(password, "UTF-8")
                            + "&code=" + URLEncoder.encode(code, "UTF-8");

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
            registerButton.setEnabled(false);
            registerButton.setBackgroundResource(R.drawable.shape_button_disabled);
        } else {
            registerButton.setEnabled(true);
            registerButton.setBackgroundResource(R.drawable.selector_button);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        countDownTimer.cancel();

        SMSSDK.unregisterAllEventHandler();
    }
}
