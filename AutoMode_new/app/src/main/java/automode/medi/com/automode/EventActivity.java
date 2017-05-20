package automode.medi.com.automode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import automode.medi.com.automode.utils.AppUtils;

/**
 * Created by Lenovo on 08-05-2017.
 */
public class EventActivity extends Activity implements View.OnClickListener {


    Context mContext;
    ImageView event1View,event2View;
    ImageButton homeButton,searchButton,savedlocationButton,aboutButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_event);
        mContext=EventActivity.this;
        initView();


    }
    private void initView() {

        event1View = (ImageView) findViewById(R.id.tv_event_one);
        event2View = (ImageView) findViewById(R.id.tv_event_two);
        homeButton = (ImageButton) findViewById(R.id.imageButton_home);
        searchButton = (ImageButton) findViewById(R.id.imageButton_search);
        savedlocationButton = (ImageButton) findViewById(R.id.imageButton_fav);
        aboutButton = (ImageButton) findViewById(R.id.imageButton_about);

        event1View.setOnClickListener(this);
        event2View.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        savedlocationButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_about:
                Intent intent3 = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.imageButton_fav:
                Intent intent = new Intent(getApplicationContext(), SavedLocationHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.imageButton_home:
                Intent intent2 = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent2);
                break;
            case R.id.imageButton_search:
                Intent intent1 = new Intent(getApplicationContext(), InputModeActivity.class);
                startActivity(intent1);
                break;
        }
    }


    private void resetImages(){
        homeButton.setBackgroundResource(R.drawable.home_icon);
        aboutButton.setBackgroundResource(R.drawable.about_icon);
        searchButton.setBackgroundResource(R.drawable.search_icon);
        savedlocationButton.setBackgroundResource(R.drawable.favorite_icon);

    }
    class EventNameUrlTask extends AsyncTask<Void, Void, Void> {

        String response;
        String eventName;
        private ProgressDialog dialog;

        public EventNameUrlTask(String eventName) {
            this.eventName=eventName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setCancelable(false);
            dialog.setMessage("Downloading Event...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url="http://"+IpPortActivity.ipWithPort+"/AutoModeServer/EventList";
            response= AppUtils.getInstance(mContext).getEventUrlFormServer(url,eventName);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (response == null) {
                Toast.makeText(mContext, "Unable to connect server", Toast.LENGTH_SHORT).show();
                return;
            }
            if(response!=null && !response.isEmpty()){
                Toast.makeText(mContext, "URL"+response, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(mContext, "Credentails not valid ", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }




}
