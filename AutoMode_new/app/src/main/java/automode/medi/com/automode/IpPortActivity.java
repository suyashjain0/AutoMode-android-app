package automode.medi.com.automode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ist on 3/5/17.
 */

public class IpPortActivity extends Activity implements View.OnClickListener{
    Context mContext;
    private EditText etIp, etPort;
    Button btnSave;
    public static String ipWithPort=null;
    private final String REQUIRED_FIELD = "Required field";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ipport_manual);
        mContext=IpPortActivity.this;
        initWidget();

    }
    private void initWidget() {
        etIp = (EditText) findViewById(R.id.et_ip);
        etPort = (EditText) findViewById(R.id.et_port);
        btnSave = (Button) findViewById(R.id.btn_save);
        etIp.setOnClickListener(this);
        etPort.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_ip:
                validateIp();
                break;
            case R.id.et_port:
                validatePort();
                break;
            case R.id.btn_save:
                setIpwithPort();
                break;
        }
    }
    private void validateIp() {
        etIp.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = etIp.getText().toString();
                if (etIp.getText() != null && username.trim().equals("")) {
                    etIp.setError(REQUIRED_FIELD);
                } else {
                    etIp.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

        });
    }
    private void validatePort() {
        etPort.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = etPort.getText().toString();
                if (etPort.getText() != null && username.trim().equals("")) {
                    etPort.setError(REQUIRED_FIELD);
                } else {
                    etPort.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

        });
    }


    private void setIpwithPort(){
        if (etIp.getText() != null && etIp.getText().toString().trim().equals("")) {
            etIp.setError(REQUIRED_FIELD);
        } else if (etPort.getText() != null && etPort.getText().toString().trim().equals("")) {
            etPort.setError(REQUIRED_FIELD);
        } else {
            etIp.setError(null);
            etPort.setError(null);
        }

        String port=null,ip=null;
        if(!etIp.getText().toString().isEmpty()) {
             ip = etIp.getText().toString();
        }
        if(!etPort.getText().toString().isEmpty()) {
             port = etPort.getText().toString();
        }
        if(ip!=null && port!=null) {
            ipWithPort = ip + ":" + port;
        }
        if(ip!=null  && port!=null){
            Toast.makeText(mContext,"IP && Port Saved",Toast.LENGTH_LONG).show();
            Intent intentAccount = new Intent(mContext, AccountActivity.class);
            startActivity(intentAccount);
        }

    }


}
