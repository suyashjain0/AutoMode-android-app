package automode.medi.com.automode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import automode.medi.com.automode.utils.AppUtils;

/**
 * Created by Lenovo on 08-05-2017.
 */
public class TimetableActivity extends Activity implements View.OnClickListener{

    Context mContext;
    ImageView mondayView,tuesdayView,wednesdayView,thursdayView,fridayView;
    ImageButton homeButton,searchButton,savedlocationButton,aboutButton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_timetable);
        mContext=TimetableActivity.this;
        initView();

    }


    private void initView() {

        mondayView = (ImageView) findViewById(R.id.tv_day_monday);
        tuesdayView = (ImageView) findViewById(R.id.tv_day_tuesday);
        wednesdayView = (ImageView) findViewById(R.id.tv_day_wednesday);
        thursdayView = (ImageView) findViewById(R.id.tv_day_thursday);
        fridayView = (ImageView) findViewById(R.id.tv_day_friday);
        homeButton = (ImageButton) findViewById(R.id.imageButton_home);
        searchButton = (ImageButton) findViewById(R.id.imageButton_search);
        savedlocationButton = (ImageButton) findViewById(R.id.imageButton_fav);
        aboutButton = (ImageButton) findViewById(R.id.imageButton_about);


        mondayView.setOnClickListener(this);
        tuesdayView.setOnClickListener(this);
        wednesdayView.setOnClickListener(this);
        thursdayView.setOnClickListener(this);
        fridayView.setOnClickListener(this);
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
            case R.id.tv_day_monday:
                Picasso.with(mContext)
                        .load("https://lh3.googleusercontent.com/DhWDuIlfAAkz0wl55BNZG5XLPNsoNvrvE5KdpVLtEQtG8qXO0EMSf7wSdh3YP6Sl2_2Rj6Z3oQ17YdM=w1279-h656")
                        .placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(mondayView);
                //new TimeTableUrlTask("monday").execute();
                break;
            case R.id.tv_day_tuesday:
                new TimeTableUrlTask("tuesday").execute();
                break;
        }
    }


    private void resetImages(){
        homeButton.setBackgroundResource(R.drawable.home_icon);
        aboutButton.setBackgroundResource(R.drawable.about_icon);
        searchButton.setBackgroundResource(R.drawable.search_icon);
        savedlocationButton.setBackgroundResource(R.drawable.favorite_icon);

    }
    class TimeTableUrlTask extends AsyncTask<Void, Void, Void> {

        String response;
        String dayName;
        private ProgressDialog dialog;

        public TimeTableUrlTask(String dayName) {
        this.dayName=dayName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setCancelable(false);
            dialog.setMessage("Downloading TimeTable...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url="http://"+IpPortActivity.ipWithPort+"/AutoModeServer/TimeTableNew";
            response= AppUtils.getInstance(mContext).getTimeTableUrlFormServer(url,dayName);
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
            if(response!=null && !response.isEmpty() && !response.equalsIgnoreCase("Failure")){
                new DownloadTimeTableFromDriveTask(response).execute();
                Toast.makeText(mContext, "URL"+response, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(mContext, "Credentails not valid ", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTimeTableFromDriveTask extends AsyncTask<Void, Void, Void> {
        String url;

        public DownloadTimeTableFromDriveTask(String URL) {
            url=URL;
        }
        @Override
        protected Void doInBackground(Void... params) {
            downloadTimeTable(url);
            return null;
        }
    }

    private void downloadTimeTable(String URL){
        try{
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "OAuth " + GoogleAuthUtil.getToken(mContext, "suyashj96@gmail.com", "read"));
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            //you will recive the file in input stream
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "filename.ext");

            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = conn.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
