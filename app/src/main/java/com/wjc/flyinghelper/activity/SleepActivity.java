package com.wjc.flyinghelper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jp.wheelview.WheelView;
import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.config.Config;
import com.wjc.flyinghelper.util.WheelData;

import java.util.ArrayList;

public class SleepActivity extends AppCompatActivity {

    private LinearLayout amTimeLinearLayout, pmTimeLinearLayout, switchCompatLinearLayout;
    private TextView amTimeTextView, pmTimeTextView;
    private SwitchCompat switchCompat;
    private View amDialogView, pmDialogView;
    private WheelView amHour, amMinute, pmHour, pmMinute;
    private FloatingActionButton sleepSave;

    private String[] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private String[] minutes = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
    private ArrayList<String> hourList, minuteList;

    private String amTimeWheel = "";
    private String pmTimeWheel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        initToolbar();
        initViewComponent();
        initTimeValue();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.life_sleep);
    }

    private void initViewComponent() {
        amTimeLinearLayout = (LinearLayout) findViewById(R.id.amTimeLinearLayout);
        pmTimeLinearLayout = (LinearLayout) findViewById(R.id.pmTimeLinearLayout);
        switchCompatLinearLayout = (LinearLayout) findViewById(R.id.switchCompatLinearLayout);

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

        sleepSave = (FloatingActionButton) findViewById(R.id.sleepSave);

        amTimeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amDialogView = LayoutInflater.from(SleepActivity.this).inflate(R.layout.dialog_am, null);
                amHour = (WheelView) amDialogView.findViewById(R.id.amHour);
                amMinute = (WheelView) amDialogView.findViewById(R.id.amMinute);
                amHour.setData(hourList);
                amMinute.setData(minuteList);

                amTimeWheel = amTimeTextView.getText().toString();
                pmTimeWheel = pmTimeTextView.getText().toString();

                int amTimeHourIndex = WheelData.getWheelDataIndex(Config.amHour, amTimeWheel, pmTimeWheel, hourList, minuteList);
                int amTimeMinuteIndex = WheelData.getWheelDataIndex(Config.amMinute, amTimeWheel, pmTimeWheel, hourList, minuteList);

                amHour.setDefault(amTimeHourIndex);
                amMinute.setDefault(amTimeMinuteIndex);

                AlertDialog.Builder builder = new AlertDialog.Builder(SleepActivity.this);
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
                pmDialogView = LayoutInflater.from(SleepActivity.this).inflate(R.layout.dialog_pm, null);
                pmHour = (WheelView) pmDialogView.findViewById(R.id.pmHour);
                pmMinute = (WheelView) pmDialogView.findViewById(R.id.pmMinute);
                pmHour.setData(hourList);
                pmMinute.setData(minuteList);

                amTimeWheel = amTimeTextView.getText().toString();
                pmTimeWheel = pmTimeTextView.getText().toString();

                int pmTimeHourIndex = WheelData.getWheelDataIndex(Config.pmHour, amTimeWheel, pmTimeWheel, hourList, minuteList);
                int pmTimeMinuteIndex = WheelData.getWheelDataIndex(Config.pmMinute, amTimeWheel, pmTimeWheel, hourList, minuteList);

                pmHour.setDefault(pmTimeHourIndex);
                pmMinute.setDefault(pmTimeMinuteIndex);

                AlertDialog.Builder builder = new AlertDialog.Builder(SleepActivity.this);
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

        switchCompatLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCompat.isChecked()) {
                    switchCompat.setChecked(false);
                } else {
                    switchCompat.setChecked(true);
                }
            }
        });

        sleepSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amTime = amTimeTextView.getText().toString();
                String pmTime = pmTimeTextView.getText().toString();
                int switchText = 0;

                if (switchCompat.isChecked()) {
                    switchText = 1;
                }

                SharedPreferences sharedPreferences = getSharedPreferences(Config.name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(Config.amTime, amTime);
                editor.putString(Config.pmTime, pmTime);
                editor.putInt(Config.switchCompat, switchText);

                editor.commit();

                Toast.makeText(SleepActivity.this, R.string.set_success, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initTimeValue() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.name, Context.MODE_PRIVATE);

        String amTime = sharedPreferences.getString(Config.amTime, "");
        String pmTime = sharedPreferences.getString(Config.pmTime, "");
        int switchText = sharedPreferences.getInt(Config.switchCompat, 0);

        if (!amTime.equals("")) {
            amTimeTextView.setText(amTime);
        }
        if (!pmTime.equals("")) {
            pmTimeTextView.setText(pmTime);
        }
        if (switchText == 1) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }

    }

}
