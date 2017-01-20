package com.wjc.flyinghelper.activity;

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

import java.util.ArrayList;

public class CarinfoActivity extends AppCompatActivity {

    private Spinner provinceShort, a2z;
    private EditText plateNumber;
    private TextView carInfoName;
    private Button dial;

    private FloatingActionButton carSearch;

    private ArrayList<String> provinceShortList, a2zList;
    private ArrayAdapter provinceShortAdapter, a2zAdapter;

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

    private String provinceShortVal, a2zVal;

    private Handler handler;

    private static final int REQUEST_SUCCESS = 1;
    private static final int REQUEST_ERROR = 0;

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
        toolbarText.setText(R.string.car_info);
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
                //拨号操作，调用匿名拨号sdk
            }
        });

        carSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCarInfo();
            }
        });


        handler = new Handler() {
            public void handleMessage(Message message) {
                if (message.what == REQUEST_SUCCESS) {
                    Bundle bundle = message.getData();
                    String mobile = bundle.getString("mobile", "");
                    String name = bundle.getString("name", "");

                    carInfoName.setText(name);
                } else if (message.what == REQUEST_ERROR){
                    Toast.makeText(CarinfoActivity.this, R.string.query_error, Toast.LENGTH_LONG).show();
                }
            }
        };

    }

    private void getCarInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //网络请求获取车主信息
                //此处暂时省略一部分代码

                Bundle bundle = new Bundle();
                bundle.putString("mobile", "15639713083");
                bundle.putString("name", "王先生");

                Message message = new Message();
                message.setData(bundle);
                message.what = REQUEST_SUCCESS;

                handler.sendMessage(message);
            }
        }).start();

    }

}
