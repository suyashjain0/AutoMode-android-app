package automode.medi.com.automode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Lenovo on 08-05-2017.
 */
public class TimetableActivity extends Activity implements View.OnClickListener{

    Context mContext;
    ImageView mondayView,tuesdayView,wednesdayView,thursdayView,fridayView;
    ImageButton homeButton,searchButton,savedlocationButton,aboutButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                new TimeTableUrlTask("monday").execute();
                break;
            case R.id.tv_day_tuesday:
                new TimeTableUrlTask("tuesday").execute();
                break;
            case R.id.tv_day_wednesday:
                new TimeTableUrlTask("wednesday").execute();
                break;
            case R.id.tv_day_thursday:
                new TimeTableUrlTask("thursday").execute();
                break;
            case R.id.tv_day_friday:
                new TimeTableUrlTask("friday").execute();
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
                showPopUpForImage(response,dayName);
            }
            else{
                Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    WindowManager manager=null;
    View popipView=null;

        private void showPopUpForImage(String url,String day){
            manager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    PixelFormat.TRANSPARENT, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            );

            layoutParams.gravity = Gravity.CENTER;
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.windowAnimations = android.R.style.Animation_InputMethod;
            layoutParams.format=PixelFormat.TRANSPARENT;
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            layoutParams.packageName = mContext.getPackageName();
            layoutParams.setTitle("AutoMode");
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            popipView = View.inflate(mContext.getApplicationContext(), R.layout.layout_popup_image, null);

            manager.addView(popipView, layoutParams);

            ImageView imageView= (ImageView) popipView.findViewById(R.id.image_popup);
            Button cancelButton = (Button) popipView.findViewById(R.id.btn_cancel);

            if(url!=null) {
                Picasso.with(mContext)
                        .load(url)
                        .placeholder(getDrawableForDay(day)).error(getDrawableForDay(day)).into(imageView);
            }
            PhotoViewAttacher pAttacher;
            pAttacher = new PhotoViewAttacher(imageView);
            pAttacher.update();

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(popipView!=null && popipView.isShown()){
                        manager.removeView(popipView);

                    }
                }
            });



        }

        private int getDrawableForDay(String day) {
            Integer imageDay = null;
            if (day != null) {
                if (day.equalsIgnoreCase("monday")) {
                    imageDay = R.drawable.tt_monday;
                } else if (day.equalsIgnoreCase("tuesday")) {
                    imageDay = R.drawable.tt_tuesday;

                } else if (day.equalsIgnoreCase("wednesday")) {
                    imageDay = R.drawable.tt_wednesday;

                } else if (day.equalsIgnoreCase("thursday")) {
                    imageDay = R.drawable.tt_thursday;

                } else if (day.equalsIgnoreCase("friday")) {
                    imageDay = R.drawable.tt_friday;
                } else {
                    imageDay = R.drawable.ic_launcher;
                }
            }
            return imageDay;

        }
    @Override
    public void onBackPressed() {
        if(popipView!=null && popipView.isShown()){
            manager.removeView(popipView);
        }
        super.onBackPressed();
    }


}
