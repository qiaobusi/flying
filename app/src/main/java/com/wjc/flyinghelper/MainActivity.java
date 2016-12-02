package com.wjc.flyinghelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jp.wheelview.WheelView;
import com.wjc.flyinghelper.Config.Config;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LinearLayout amTimeLinearLayout, pmTimeLinearLayout;
    private TextView amTimeTextView, pmTimeTextView;
    private SwitchCompat switchCompat;
    private View amDialogView, pmDialogView;
    private WheelView amHour, amMinute, pmHour, pmMinute;
    private Button button;

    private String[] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private String[] minutes = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
    private ArrayList<String> hourList, minuteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();
        initViewComponent();
        initTimeValue();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViewComponent() {
        amTimeLinearLayout = (LinearLayout) findViewById(R.id.amTimeLinearLayout);
        pmTimeLinearLayout = (LinearLayout) findViewById(R.id.pmTimeLinearLayout);

        amTimeTextView = (TextView) findViewById(R.id.amTimeTextView);
        pmTimeTextView = (TextView) findViewById(R.id.pmTimeTextView);

        switchCompat = (SwitchCompat) findViewById(R.id.switchCompat);

        hourList = new ArrayList<String>();
        for (int i = 0; i < hours.length; i++) {
            hourList.add(hours[i]);
        }

        minuteList = new ArrayList<String>();
        for (int i = 0; i < minutes.length; i++) {
            minuteList.add(minutes[i]);
        }

        button = (Button) findViewById(R.id.button);

        amTimeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amDialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_am, null);
                amHour = (WheelView) amDialogView.findViewById(R.id.amHour);
                amMinute = (WheelView) amDialogView.findViewById(R.id.amMinute);
                amHour.setData(hourList);
                amMinute.setData(minuteList);
                amHour.setDefault(0);
                amMinute.setDefault(0);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.am_time);
                builder.setView(amDialogView);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String amHourText = String.format("%s", amHour.getSelectedText());
                        String amMinuteText = String.format("%s", amMinute.getSelectedText());

                        String amTimeText = amHourText + ":" + amMinuteText;
                        amTimeTextView.setText(amTimeText);
                    }
                });
                builder.show();
            }
        });

        pmTimeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pmDialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_pm, null);
                pmHour = (WheelView) pmDialogView.findViewById(R.id.pmHour);
                pmMinute = (WheelView) pmDialogView.findViewById(R.id.pmMinute);
                pmHour.setData(hourList);
                pmMinute.setData(minuteList);
                pmHour.setDefault(0);
                pmMinute.setDefault(0);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.pm_time);
                builder.setView(pmDialogView);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String pmHourText = String.format("%s", pmHour.getSelectedText());
                        String pmMinuteText = String.format("%s", pmMinute.getSelectedText());

                        String pmTimeText = pmHourText + ":" + pmMinuteText;
                        pmTimeTextView.setText(pmTimeText);
                    }
                });
                builder.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amText = amTimeTextView.getText().toString();
                String pmText = pmTimeTextView.getText().toString();
                int switchText = 0;

                if (switchCompat.isChecked()) {
                    switchText = 1;
                }

                SharedPreferences sharedPreferences = getSharedPreferences(Config.name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(Config.amTime, amText);
                editor.putString(Config.pmTime, pmText);
                editor.putInt(Config.switchCompat, switchText);

                editor.commit();

                Toast.makeText(MainActivity.this, R.string.set_success, Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void initTimeValue() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.name, Context.MODE_PRIVATE);

        String amText = sharedPreferences.getString(Config.amTime, "");
        String pmText = sharedPreferences.getString(Config.pmTime, "");
        int switchText = sharedPreferences.getInt(Config.switchCompat, 0);

        if (!amText.equals("")) {
            amTimeTextView.setText(amText);
        }
        if (!pmText.equals("")) {
            pmTimeTextView.setText(pmText);
        }
        if (switchText == 1) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }

    }

}
