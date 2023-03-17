package com.labbserver.barnappkai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        scoreText = findViewById(R.id.score);
    }

    public void setScoreText(int score) {
        scoreText.setText(Integer.toString(score));
    }
}