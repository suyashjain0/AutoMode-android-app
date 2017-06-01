package automode.medi.com.automode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import automode.medi.com.automode.holder.UserDetails;
import automode.medi.com.automode.utils.AppUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ist on 1/5/17.
 */

public class AccountActivity extends Activity implements View.OnClickListener{
    Context mContext;
    private EditText etCampaignUserName, etCampaignPassword;
    Button btnLogin,btnLogout,btnRegister;
    private final String REQUIRED_FIELD = "Required field";
    private InputMethodManager inputManager;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    boolean chk1=false,check;
    ImageView tvSetting;
    boolean isValidUser = false;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_accounts);
        mContext=AccountActivity.this;
        initWidget();

    }

    private void initWidget() {
        sharedpreferences = mContext.getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        etCampaignUserName = (EditText) findViewById(R.id.et_campaign_user_name);
        etCampaignPassword = (EditText) findViewById(R.id.et_campaign_password);
        tvSetting=(ImageView)findViewById(R.id.tv_setting);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnRegister=(Button)findViewById(R.id.btn_register);
        etCampaignUserName.setOnClickListener(this);
        etCampaignPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        //checkUserLoginStatus();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting:
                createPopupMenu(getApplicationContext(),tvSetting);
                break;
            case R.id.et_campaign_user_name:
                validateCampaignUsername();
                break;
            case R.id.et_campaign_password:
                validateCampaignPassword();
                break;
            case R.id.btn_login:
                checkCampaignCredential();
                break;
            case R.id.btn_register:
                Intent intentreg = new Intent(mContext, RegistrationActivity.class);
                startActivity(intentreg);
                break;
           case R.id.btn_logout:
                editor.putBoolean("loginStatus", false);
                editor.putString("userName", null);
                editor.putString("password", null);
                editor.commit();
                btnLogin.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.GONE);
                etCampaignUserName.setText("");
                etCampaignPassword.setText("");
                etCampaignUserName.setEnabled(true);
                etCampaignPassword.setEnabled(true);
                Toast.makeText(mContext, "Logout successfully", Toast.LENGTH_SHORT).show();
                break;
        }
        }
    private void validateCampaignUsername() {
        etCampaignUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = etCampaignUserName.getText().toString();
                if (etCampaignUserName.getText() != null && username.trim().equals("")) {
                    etCampaignUserName.setError(REQUIRED_FIELD);
                } else {
                    etCampaignUserName.setError(null);
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
    private void validateCampaignPassword() {
        etCampaignPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String password = etCampaignPassword.getText().toString();

                if (etCampaignPassword.getText() != null && password.trim().equals("")) {
                    etCampaignPassword.setError(REQUIRED_FIELD);
                } else {
                    etCampaignPassword.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }
    private void checkCampaignCredential() {

        String fullUsername = etCampaignUserName.getText().toString();
        String[] splittedUname = fullUsername.split("@");
        String username = splittedUname[0];
        String password = etCampaignPassword.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {
            if (AppUtils.isNetworkAvailable(mContext)) {
                new LoginTask(username, password).execute();
            } else {
                Toast.makeText(mContext, "Please connect to internet", Toast.LENGTH_LONG).show();
            }
            inputManager.hideSoftInputFromWindow(etCampaignUserName.getWindowToken(), 0);


        } else {
            if (etCampaignUserName.getText() != null && etCampaignUserName.getText().toString().trim().equals("")) {
                etCampaignUserName.setError(REQUIRED_FIELD);
            } else if (etCampaignPassword.getText() != null && etCampaignPassword.getText().toString().trim().equals("")) {
                etCampaignPassword.setError(REQUIRED_FIELD);
            } else {
                etCampaignUserName.setError(null);
                etCampaignPassword.setError(null);
            }

        }
    }

    class LoginTask extends AsyncTask<Void, Void, Void> {

        String response;
        String user, pass;
        private ProgressDialog dialog;
        public LoginTask(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setCancelable(false);
            dialog.setMessage("Logging in...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url="http://"+IpPortActivity.ipWithPort+"/AutoModeServer/LoginUser";
            response=uploadLoginData(url,user,pass);
            if(response!=null && response.equalsIgnoreCase("Login success")){
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

                editor.putBoolean("loginStatus", false);
                editor.putString("userName", null);
                editor.putString("password", null);
                editor.commit();
                etCampaignPassword.setText("");
                etCampaignPassword.setError(null);
                Toast.makeText(mContext, "Unable to connect server", Toast.LENGTH_SHORT).show();
                return;
            }
            if(isValidUser && response!=null && response.equalsIgnoreCase("Login success")){
                btnLogin.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.GONE);
                editor = sharedpreferences.edit();
                editor.putBoolean("loginStatus", true);
                editor.putString("userName", user);
                editor.putString("password", pass);
                editor.commit();
                etCampaignUserName.setEnabled(false);
                etCampaignPassword.setEnabled(false);
                Toast.makeText(mContext, "Login Successfully", Toast.LENGTH_SHORT).show();
                Intent intentlogin = new Intent(mContext, HomeActivity.class);
                startActivity(intentlogin);

            }
            else{
                editor.putBoolean("loginStatus", false);
                editor.putString("userName", null);
                editor.putString("password", null);
                editor.commit();
                etCampaignPassword.setText("");
                etCampaignPassword.setError(null);
                Toast.makeText(mContext, "Credentails not valid ", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    public String uploadLoginData(String postUrl, String userName,String password) {
        Log.d("uploadUrl", "url=" + postUrl);

        String sBuilder = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .build();
            Request request;
            request = new Request.Builder()
                    .url(postUrl).addHeader("Content-Type", "text/plain")
                    .addHeader("userId", userName)
                    .addHeader("password", password)
                    .get().build();
            Response response = client.newCall(request).execute();
            sBuilder = response.body().string().toString();

            Log.d("data", "upload response: " + sBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("data", "response()" + postUrl + " " + e.getMessage());
        }

        return sBuilder;
    }


    private void checkUserLoginStatus(){
         boolean loginStatus = sharedpreferences.getBoolean("loginStatus", false);
       if(loginStatus){
           String user=sharedpreferences.getString("userName", null);
           String password=sharedpreferences.getString("password", null);
           etCampaignUserName.setText(""+user);
           etCampaignPassword.setText(""+password);
           etCampaignUserName.setEnabled(false);
           etCampaignPassword.setEnabled(false);
           btnLogin.setVisibility(View.GONE);
           btnLogout.setVisibility(View.VISIBLE);
       }
    }
    public void createPopupMenu(final Context mContext, View view) {
        try {
            PopupMenu popupMenu;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                popupMenu = new PopupMenu(mContext, view, Gravity.END);
            } else {
                popupMenu = new PopupMenu(mContext, view);
            }
            setTheme(R.style.MenuTheme);
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                }
            });
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    // TODO Auto-generated method stub
                    switch (item.getItemId()) {
                        case R.id.menu_ip:
                            Intent intentIpIntent = new Intent(getApplicationContext(), IpPortActivity.class);
                            startActivity(intentIpIntent);
                            return true;
                        case R.id.log_out:
                            Intent intentlogout = new Intent(getApplicationContext(), AccountActivity.class);
                            startActivity(intentlogout);
                            break;

                    }
                    return false;
                }
            });
            popupMenu.inflate(R.menu.setting_menu1);

            Object menuHelper;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                popupMenu.show();
                return;
            }
            popupMenu.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(mContext==AccountActivity.this){
        //THIS BLOCK WILL NOT DO ANYTHING AND WOULD DISABLE BACK BUTTON

        }else{
            super.onBackPressed();
//THIS BLOCK WILL BE CALLED IF ABOVE COND IS FALSE, AND WOULD ENABLE BACK BUTTON
        }

    }


}
