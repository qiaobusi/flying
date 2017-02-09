package com.wjc.flyinghelper.activity;

import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.config.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ExpressActivity extends AppCompatActivity {

    private EditText express;
    private TextView expressResult;
    private ScrollView traceScrollView;
    private TextView trace;
    private FloatingActionButton expressSearch;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express);

        initToolbar();
        initViewComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_express);
    }

    private void initViewComponent() {
        express = (EditText) findViewById(R.id.express);
        expressResult = (TextView) findViewById(R.id.expressResult);
        traceScrollView = (ScrollView) findViewById(R.id.traceScrollView);
        trace = (TextView) findViewById(R.id.trace);
        expressSearch = (FloatingActionButton) findViewById(R.id.expressSearch);

        expressSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String logisticCode = express.getText().toString().trim();

                expressQuery(logisticCode);

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

                            int state = dataObject.getInt("State");
                            String stateString = "";
                            if (state == 2) {
                                //在途中
                                stateString = getString(R.string.express_state_2);
                            } else if (state == 3) {
                                //已签收
                                stateString = getString(R.string.express_state_3);
                            } else if (state == 4) {
                                //问题件
                                stateString = getString(R.string.express_state_4);
                            }
                            expressResult.setText(stateString);

                            JSONArray traceArray = dataObject.getJSONArray("Traces");
                            String traceString = "";
                            for (int i = 0; i < traceArray.length(); i++) {
                                JSONObject traceObject = traceArray.getJSONObject(i);
                                String acceptTime = traceObject.getString("AcceptTime");
                                String acceptStation = traceObject.getString("AcceptStation");

                                traceString += acceptStation + "--" + acceptTime + "\r\n";
                            }
                            trace.setText(traceString);

                            if (expressResult.getVisibility() == View.GONE) {
                                expressResult.setVisibility(View.VISIBLE);
                                traceScrollView.setVisibility(View.VISIBLE);
                            }
                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(ExpressActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(ExpressActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();
                }

                changeFabStatus(1);
            }
        };

    }

    private void expressQuery(final String logisticCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestUrl = Config.httpUrl + "/web/express/index";
                try {
                    String data = "logisticCode=" + URLEncoder.encode(logisticCode, "UTF-8");

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

    private void changeFabStatus(int status) {
        if (status == 0) {
            expressSearch.setEnabled(false);
            expressSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButtonDisabled)));
        } else {
            expressSearch.setEnabled(true);
            expressSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButton)));
        }
    }


}
