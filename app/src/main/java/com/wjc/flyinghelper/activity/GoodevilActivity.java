package com.wjc.flyinghelper.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoodevilActivity extends AppCompatActivity {

    private ListView goodevilListView;
    private Button evilPublish, goodPublish;

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodevil);

        initToolbar();
        initViewComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.toolbar_goodevil);
    }

    private void initViewComponent() {
        arrayList = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            arrayList.add("Hello+" + i);
        }
        arrayAdapter = new ArrayAdapter<String>(GoodevilActivity.this, android.R.layout.simple_list_item_1, arrayList);


        goodevilListView = (ListView) findViewById(R.id.goodevilListView);
        evilPublish = (Button) findViewById(R.id.evilPublish);
        goodPublish = (Button) findViewById(R.id.goodPublish);



        goodevilListView.setAdapter(arrayAdapter);


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



                        } else if (status == Config.EXEC_ERROR){
                            String info = jsonObject.getString("info");
                            Toast.makeText(GoodevilActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (message.what == Config.REQUEST_ERROR){
                    Toast.makeText(GoodevilActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();
                }
            }
        };

    }


}
