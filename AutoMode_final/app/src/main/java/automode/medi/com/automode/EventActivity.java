package automode.medi.com.automode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import automode.medi.com.automode.utils.AppUtils;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Lenovo on 08-05-2017.
 */
public class EventActivity extends Activity implements View.OnClickListener {


    Context mContext;
    ImageView event1View,event2View;
    ImageButton homeButton,searchButton,savedlocationButton,aboutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            case R.id.tv_event_one:
                new EventNameUrlTask("event1").execute();

                break;
            case R.id.tv_event_two:
                new EventNameUrlTask("event2").execute();
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
            if(response!=null && !response.isEmpty() &&!response.equalsIgnoreCase("failure")){
                showPopUpForImage(response,eventName);
            }
            else{
                Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    WindowManager manager=null;
    View popipView=null;

    private void showPopUpForImage(String url,String event){
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
                    .placeholder(getDrawableForEvent(event)).error(getDrawableForEvent(event)).into(imageView);
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

    private int getDrawableForEvent(String day) {
        Integer imageEvent = null;
        if (day != null) {
            if (day.equalsIgnoreCase("event1")) {
                imageEvent = R.drawable.event_1;
            } else if (day.equalsIgnoreCase("event2")) {
                imageEvent = R.drawable.event_2;
            }  else {
                imageEvent = R.drawable.ic_launcher;
            }
        }
        return imageEvent;

    }

    @Override
    public void onBackPressed() {
        if(popipView!=null && popipView.isShown()){
            manager.removeView(popipView);
        }
        super.onBackPressed();
    }
}
