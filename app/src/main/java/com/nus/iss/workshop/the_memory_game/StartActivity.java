package com.nus.iss.workshop.the_memory_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button startBtn=(Button)findViewById(R.id.start);
        System.out.println(startBtn);
        if (startBtn != null) {
            startBtn.setOnClickListener(this);
        }
        Button aboutBtn = findViewById(R.id.about);
        System.out.println(aboutBtn);
        if (aboutBtn != null) {
            aboutBtn.setOnClickListener(this);
        }
        Button descBtn = findViewById(R.id.description);
        if (descBtn != null) {
            descBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            Intent imageIntent = new Intent(this, selectImageActivity.class);
            startActivity(imageIntent);

        } else if (v.getId() == R.id.about) {
           Intent aboutIntent = new Intent(this, aboutUsActivity.class);
           startActivity(aboutIntent);
        }
        else if (v.getId() == R.id.description) {
            Intent aboutIntent = new Intent(this, descriptionActivity.class);
            startActivity(aboutIntent);
        }
    }
}