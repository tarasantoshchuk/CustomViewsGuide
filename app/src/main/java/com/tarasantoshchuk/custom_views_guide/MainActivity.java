package com.tarasantoshchuk.custom_views_guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.position_n_size).setOnClickListener(this);
        findViewById(R.id.appearance).setOnClickListener(this);
        findViewById(R.id.interactions).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Class<? extends Activity> activityToStart;

        switch(view.getId()) {
            case R.id.appearance:
                activityToStart = AppearanceActivity.class;
                break;
            case R.id.position_n_size:
                activityToStart = PositionActivity.class;
                break;
            case R.id.interactions:
                activityToStart = InteractionActivity.class;
                break;
            default:
                throw new RuntimeException();
        }

        Intent intent = new Intent(this, activityToStart);
        startActivity(intent);
    }
}
