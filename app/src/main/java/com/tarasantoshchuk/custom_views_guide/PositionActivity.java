package com.tarasantoshchuk.custom_views_guide;

import android.app.Activity;
import android.os.Bundle;

public class PositionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotating_layout);
    }
}
