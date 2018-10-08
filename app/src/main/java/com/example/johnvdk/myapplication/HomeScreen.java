package com.example.johnvdk.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    private Button onePlayer, twoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        onePlayer = (Button) findViewById(R.id.player_1);
        twoPlayer = (Button) findViewById(R.id.player_2);

        onePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGame(1);
            }
        });
        twoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGame(2);
            }
        });
    }

    private void goToGame(int numPlayer){
        Intent i = new Intent(HomeScreen.this, GameBoard.class);
        i.putExtra("numPlayer", numPlayer);//Pass the number of players selected
        startActivity(i);
    }
}
