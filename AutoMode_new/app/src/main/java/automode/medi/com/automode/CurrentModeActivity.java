package automode.medi.com.automode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by ist on 23/4/17.
 */

public class CurrentModeActivity extends Activity implements View.OnClickListener {
    Context mContext;
    ImageView soundModeView,silentModeView,vibrateModeView;
    AudioManager audioManager;
    ImageButton homeButton,searchButton,savedlocationButton,aboutButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_currentmode);
        mContext=CurrentModeActivity.this;
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initWidgets();
//        setCurrentMode();

    }

    private  void setCurrentMode(){
        if(audioManager.getMode()==0){
            soundModeView.setBackgroundResource(R.drawable.sound_disable);
            vibrateModeView.setBackgroundResource(R.drawable.vibrate_disable);
            silentModeView.setBackgroundResource(R.drawable.mut_enable);

        }
        else if(audioManager.getMode()==1){
            soundModeView.setBackgroundResource(R.drawable.sound_disable);
            vibrateModeView.setBackgroundResource(R.drawable.vibrate_enable);
            silentModeView.setBackgroundResource(R.drawable.mut_disable);

        }
        else if(audioManager.getMode()==2){
            soundModeView.setBackgroundResource(R.drawable.sound_enable);
            vibrateModeView.setBackgroundResource(R.drawable.vibrate_disable);
            silentModeView.setBackgroundResource(R.drawable.mut_disable);
        }

    }
    private void initWidgets() {
        soundModeView=(ImageView)findViewById(R.id.tv_sound);
        silentModeView=(ImageView)findViewById(R.id.tv_silent);
        vibrateModeView=(ImageView)findViewById(R.id.tv_vibrate);
        homeButton=(ImageButton)findViewById(R.id.imageButton_home);
        searchButton=(ImageButton)findViewById(R.id.imageButton_search);
        savedlocationButton=(ImageButton)findViewById(R.id.imageButton_fav);
        aboutButton=(ImageButton)findViewById(R.id.imageButton_about);

        homeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        savedlocationButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);

        soundModeView.setOnClickListener(this);
        silentModeView.setOnClickListener(this);
        vibrateModeView.setOnClickListener(this);

    }

        @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_sound:{
                    Toast.makeText(mContext,"Sound Mode Enabled",Toast.LENGTH_LONG).show();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
                }
                case R.id.tv_silent:{
                    Toast.makeText(mContext,"Silent Mode Enabled",Toast.LENGTH_LONG).show();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;
                }
                case R.id.tv_vibrate:{
                    Toast.makeText(mContext,"Vibrate Mode Enabled",Toast.LENGTH_LONG).show();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                }
                case R.id.imageButton_about:
                    Intent intent3 = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent3);

                    break;
                case R.id.imageButton_fav:
                    Intent intent2 = new Intent(getApplicationContext(), SavedLocationHistoryActivity.class);
                    startActivity(intent2);

                    break;
                case R.id.imageButton_home:
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.imageButton_search:
                    Intent intent1 = new Intent(getApplicationContext(), InputModeActivity.class);
                    startActivity(intent1);
                    break;


            }
        }

    private void resetTestImage() {
        soundModeView.setImageResource(R.drawable.sound_disable);
        silentModeView.setImageResource(R.drawable.mut_disable);
        vibrateModeView.setImageResource(R.drawable.vibrate_disable);
    }
    private void resetImagesFormOnCreate(){
        homeButton.setBackgroundResource(R.drawable.home_icon);
        aboutButton.setBackgroundResource(R.drawable.about_icon);
        searchButton.setBackgroundResource(R.drawable.search_icon_enable);
        savedlocationButton.setBackgroundResource(R.drawable.favorite_icon);

    }



}
