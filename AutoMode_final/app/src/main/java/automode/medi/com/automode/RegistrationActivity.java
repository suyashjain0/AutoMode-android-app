package automode.medi.com.automode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import automode.medi.com.automode.holder.UserDetails;
import automode.medi.com.automode.utils.AppUtils;

/**
 * Created by ist on 1/5/17.
 */

public class RegistrationActivity extends Activity implements View.OnClickListener {
    Context mContext;
    private EditText etUserName,etPassword,etConfirmPassword,etEmail;
    Button btnRegister;
    private final String REQUIRED_FIELD = "Required field";
    private InputMethodManager inputManager;
    boolean chk=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        mContext=RegistrationActivity.this;
        initWidget();

    }
    private void initWidget() {
        inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        etUserName = (EditText) findViewById(R.id.et_campaign_user_name_reg);
        etPassword = (EditText) findViewById(R.id.et_campaign_password_reg);
        etConfirmPassword = (EditText) findViewById(R.id.et_campaign_confirm_password_reg);
        etEmail = (EditText) findViewById(R.id.et_campaign_email_reg);
        btnRegister=(Button)findViewById(R.id.btn_register_mode);
        btnRegister.setOnClickListener(this);
    }
    private void checkCampaignCredential() {

        String fullUsername = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String email = etEmail.getText().toString();

        if(confirmPassword!=null && password!=null && !password.equalsIgnoreCase(confirmPassword))
        {
            Toast.makeText(mContext, "Confrim Password dosen't match", Toast.LENGTH_LONG).show();
            return ;
        }

        if (!fullUsername.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()&& !email.isEmpty()) {
            if (AppUtils.isNetworkAvailable(mContext)) {
                new RegistrationTask(fullUsername, password,confirmPassword,email).execute();
            } else {
                Toast.makeText(mContext, "Please connect to internet", Toast.LENGTH_LONG).show();
            }
            inputManager.hideSoftInputFromWindow(etUserName.getWindowToken(), 0);

        }

        else {
            if (etUserName.getText() != null && etUserName.getText().toString().trim().equals("")) {
                etUserName.setError(REQUIRED_FIELD);
            } else if (etEmail.getText() != null && etEmail.getText().toString().trim().equals("")) {
                etEmail.setError(REQUIRED_FIELD);
            }
            else if (etPassword.getText() != null && etPassword.getText().toString().trim().equals("")) {
                etPassword.setError(REQUIRED_FIELD);
            }
            else if (etConfirmPassword.getText() != null && etConfirmPassword.getText().toString().trim().equals("")) {
                etConfirmPassword.setError(REQUIRED_FIELD);
            }
            else {
                etUserName.setError(null);
                etEmail.setError(null);
                etPassword.setError(null);
                etConfirmPassword.setError(null);

            }

        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_mode:
                checkCampaignCredential();
                   break;

        }

    }
    class RegistrationTask extends AsyncTask<Void, Void, Void> {

        String response;
        String json, user, pass,email,cpass;
        UserDetails userDetails;
        private ProgressDialog dialog;
        private boolean isValidUser = false;

        public RegistrationTask(String user, String pass,String cpass,String email) {
            this.user = user;
            this.pass = pass;
            this.cpass = cpass;
            this.email = email;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(RegistrationActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("Logging in...");
            dialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            String url="http://"+IpPortActivity.ipWithPort+"/AutoModeServer/RegistrationAutoMode";
            try {
                response=AppUtils.getInstance(mContext).sendRegistrationCallDataToServer(url,user,pass,cpass,email);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(response!=null && response.equalsIgnoreCase("Success")){
                isValidUser=true;
            }
            else{
                isValidUser=false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (response == null) {

                etUserName.setText("");
                etUserName.setError(null);
                Toast.makeText(mContext, "Unable to connect server", Toast.LENGTH_SHORT).show();
                return;
            }
            if(isValidUser && response!=null  && response.equalsIgnoreCase("Success")){
                etUserName.setEnabled(false);
                etPassword.setEnabled(false);
                etEmail.setEnabled(false);
                etConfirmPassword.setEnabled(false);
                Toast.makeText(mContext, "Registered Successfully", Toast.LENGTH_SHORT).show();
                Intent intentAccount = new Intent(mContext, AccountActivity.class);
                startActivity(intentAccount);
            }
            else
            {
                etUserName.setEnabled(true);
                etPassword.setEnabled(true);
                etEmail.setEnabled(true);
                etConfirmPassword.setEnabled(true);
                Toast.makeText(mContext, "Registered UnSuccessfully", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

}
