package com.wjc.flyinghelper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.mobapi.API;
import com.mob.mobapi.APICallback;
import com.mob.mobapi.MobAPI;
import com.mob.mobapi.apis.IDCard;
import com.wjc.flyinghelper.R;

import java.util.HashMap;
import java.util.Map;

import static com.mob.tools.utils.R.forceCast;

public class IdcardActivity extends AppCompatActivity {

    private EditText idcard;
    private TextView idcardArea, idcardBirth, idcardSex;
    private Button idcardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);

        initToolbar();
        initViewComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText(R.string.life_idcard);
    }

    private void initViewComponent() {
        idcard = (EditText) findViewById(R.id.idcard);
        idcardArea = (TextView) findViewById(R.id.idcardArea);
        idcardBirth = (TextView) findViewById(R.id.idcardBirth);
        idcardSex = (TextView) findViewById(R.id.idcardSex);

        idcardButton = (Button) findViewById(R.id.idcardButton);

        idcardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idcardText = idcard.getText().toString().trim();
                if (idcardText.length() == 0) {
                    return;
                }

                changeButtonStatus(0);

                IDCard api = forceCast(MobAPI.getAPI(IDCard.NAME));
                api.queryIDCard(idcardText, new APICallback() {
                    @Override
                    public void onSuccess(API api, int i, Map<String, Object> result) {
                        HashMap<String, Object> address = forceCast(result.get("result"));

                        String area = com.mob.tools.utils.R.toString(address.get("area"));
                        String birth = com.mob.tools.utils.R.toString(address.get("birthday"));
                        String sex = com.mob.tools.utils.R.toString(address.get("sex"));

                        idcardArea.setText(area);
                        idcardBirth.setText(birth);
                        idcardSex.setText(sex);

                        if (idcardArea.getVisibility() == View.GONE) {
                            idcardArea.setVisibility(View.VISIBLE);
                            idcardBirth.setVisibility(View.VISIBLE);
                            idcardSex.setVisibility(View.VISIBLE);
                        }

                        changeButtonStatus(1);
                    }

                    @Override
                    public void onError(API api, int i, Throwable throwable) {
                        Toast.makeText(IdcardActivity.this, R.string.query_error, Toast.LENGTH_LONG).show();

                        changeButtonStatus(1);
                    }
                });

            }
        });

    }

    private void changeButtonStatus(int status) {
        if (status == 0) {
            idcardButton.setEnabled(false);
            idcardButton.setText(R.string.querying);
            idcardButton.setBackgroundResource(R.drawable.shape_button_disabled);
        } else {
            idcardButton.setEnabled(true);
            idcardButton.setText(R.string.query);
            idcardButton.setBackgroundResource(R.drawable.selector_button);
        }
    }

}
