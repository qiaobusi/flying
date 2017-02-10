package com.wjc.flyinghelper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mob.mobapi.MobAPI;
import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.adapter.ViewPagerAdapter;
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

import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewComponent();
        initMobApi();
        initSecurityCode();
    }

    private void initMobApi() {
        MobAPI.initSDK(this, Config.mobApiKey);
    }

    private void initSecurityCode() {
        SMSSDK.initSDK(MainActivity.this, Config.securityCodeKey, Config.securityCodeSecret);
    }

    private void initViewComponent() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab);
        }


        handler = new Handler() {
            public void handleMessage(Message message) {
                if (message.what == Config.REQUEST_SUCCESS) {
                    Bundle bundle = message.getData();
                    String result = bundle.getString(Config.RETURN_RESULT);

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        int status = jsonObject.getInt("status");
                        if (status == Config.EXEC_SUCCESS) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(R.string.version_title);
                            builder.setMessage(R.string.version_message);
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Uri uri = Uri.parse(Config.downloadUrl);

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                            builder.show();
                        } else if (status == Config.EXEC_ERROR){
                            //
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(MainActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();
                }
            }
        };


        checkversion();

    }

    private void checkversion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestUrl = Config.httpUrl + "/web/car/checkversion";

                String packageName = getPackageName();
                String version = null;
                try {
                    version = getPackageManager().getPackageInfo(packageName, 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("version", version);
                String sign = HelperVerify.sign(hashMap);

                try {
                    String data = "version=" + URLEncoder.encode(version, "UTF-8")
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


}
