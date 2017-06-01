package automode.medi.com.automode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Lenovo on 14-05-2017.
 */
public class AboutActivity  extends Activity implements View.OnClickListener{

    Context mContext;
    ImageButton homeButton,searchButton,savedlocationButton,aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_abtus);
        mContext=AboutActivity.this;
        initView();

    }


    private void initView() {

        homeButton = (ImageButton) findViewById(R.id.imageButton_home);
        searchButton = (ImageButton) findViewById(R.id.imageButton_search);
        savedlocationButton = (ImageButton) findViewById(R.id.imageButton_fav);
        aboutButton = (ImageButton) findViewById(R.id.imageButton_about);

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




}
