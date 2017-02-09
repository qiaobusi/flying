package com.wjc.flyinghelper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    private TextView loginLogo;
    private EditText loginMobile, loginPassword;
    private Button loginButton;
    private TextView loginForgetPassword, loginRegister;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initToolbar();
        initViewComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_login);
    }

    private void initViewComponent() {
        loginLogo = (TextView) findViewById(R.id.loginLogo);
        loginMobile = (EditText) findViewById(R.id.loginMobile);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginForgetPassword = (TextView) findViewById(R.id.loginForgetPassword);
        loginRegister = (TextView) findViewById(R.id.loginRegister);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        loginLogo.setTypeface(typeface);
        loginLogo.setText(getString(R.string.icon_account));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = loginMobile.getText().toString().trim();
                String password = loginPassword.getText().toString();

                if (mobile.length() == 0 || password.length() == 0) {
                    return;
                }

                login(mobile, password);

                changeButtonStatus(0);
            }
        });
        loginForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 999);
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

                            Intent intent = new Intent(LoginActivity.this, UcenterActivity.class);
                            startActivity(intent);

                            finish();
                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(LoginActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(LoginActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();
                }

                changeButtonStatus(1);
            }
        };

    }

    private void login(final String mobile, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestUrl = Config.httpUrl + "/web/car/login";

                try {
                    String data = "mobile=" + URLEncoder.encode(mobile, "UTF-8")
                            + "&password=" + URLEncoder.encode(password, "UTF-8");

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
            loginButton.setEnabled(false);
            loginButton.setBackgroundResource(R.drawable.shape_button_disabled);
        } else {
            loginButton.setEnabled(true);
            loginButton.setBackgroundResource(R.drawable.selector_button);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == 999) {
            finish();
        }
    }
}
