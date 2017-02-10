package com.wjc.flyinghelper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.config.Config;
import com.wjc.flyinghelper.util.HelperVerify;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class PasswordActivity extends AppCompatActivity {

    private EditText passwordOld, passwordNew, passwordRepeat;
    private Button passwordSave;

    private SharedPreferences sharedPreferences;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        initToolbar();
        initViewComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_password);
    }

    private void initViewComponent() {
        passwordOld = (EditText) findViewById(R.id.passwordOld);
        passwordNew = (EditText) findViewById(R.id.passwordNew);
        passwordRepeat = (EditText) findViewById(R.id.passwordRepeat);
        passwordSave = (Button) findViewById(R.id.passwordSave);

        passwordSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = passwordOld.getText().toString();
                String password = passwordNew.getText().toString();
                String repeatPassword = passwordRepeat.getText().toString();

                if (oldPassword.length() == 0 || password.length() == 0 || repeatPassword.length() == 0) {
                    return;
                }
                if (!password.equals(repeatPassword)) {
                    Toast.makeText(PasswordActivity.this, R.string.toast_repeat_error, Toast.LENGTH_LONG).show();
                    return;
                }

                sharedPreferences = getSharedPreferences(Config.userinfo, Context.MODE_PRIVATE);
                String id = sharedPreferences.getString(Config.userinfoId, "");

                savePassword(id, oldPassword, password);

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
                            String info = jsonObject.getString("info");
                            Toast.makeText(PasswordActivity.this, info, Toast.LENGTH_LONG).show();
                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(PasswordActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(PasswordActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();
                }

                changeButtonStatus(1);
            }
        };

    }


    private void savePassword(final String id, final String oldPassword, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestUrl = Config.httpUrl + "/web/car/savepassword";

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", id);
                hashMap.put("oldPassword", oldPassword);
                hashMap.put("password", password);
                String sign = HelperVerify.sign(hashMap);

                try {
                    String data = "id=" + URLEncoder.encode(id, "UTF-8")
                            + "&oldPassword=" + URLEncoder.encode(oldPassword, "UTF-8")
                            + "&password=" + URLEncoder.encode(password, "UTF-8")
                            + "&sign=" + URLEncoder.encode(sign, "UTF-8");

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
            passwordSave.setEnabled(false);
            passwordSave.setBackgroundResource(R.drawable.shape_button_disabled);
        } else {
            passwordSave.setEnabled(true);
            passwordSave.setBackgroundResource(R.drawable.selector_button);
        }
    }

}
