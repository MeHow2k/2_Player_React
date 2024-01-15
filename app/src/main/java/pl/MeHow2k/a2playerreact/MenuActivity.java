package pl.MeHow2k.a2playerreact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    Button buttonPlay,buttonSettings,buttonQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new GameLoop(this));
        setContentView(R.layout.activity_menu);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        buttonPlay=findViewById(R.id.buttonPLAY);
        buttonSettings=findViewById(R.id.buttonSETTINGS);
        buttonQuit=findViewById(R.id.buttonQUIT);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGame = new Intent(MenuActivity.this, GameLoopActivity.class);
                startActivity(intentGame);
                C.resetToDefaults();
                C.GAMESTATE=1;
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),"not implemented yet" , Toast.LENGTH_SHORT); toast.show();
                Intent intentSettings = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
            }
        });
        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }
}

/*do zrobienia


popraw napis z info jak grac
mozliwe ze czasem za zle odpowiedzi sie dostaje pkt
timery zamiast delay na int'ach

*/