package com.labbserver.barnappkai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        getSupportActionBar().hide();

        scoreText = findViewById(R.id.endScoreText);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int score = extras.getInt("score");
            scoreText.setText("Score: " + Integer.toString(score));
        }

    }

    public void restartGame(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void returnHome(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
}