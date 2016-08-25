package com.tarasantoshchuk.custom_views_guide;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PositionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotating_layout);

        findViewById(R.id.clickable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this, "blue square clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
