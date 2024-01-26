package pl.MeHow2k.a2playerreact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    Button buttonPlay,buttonSettings,buttonQuit;
    ImageButton buttonPL,buttonENG,buttonInfo,buttonGitHub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        buttonPlay=findViewById(R.id.buttonPLAY);
        buttonSettings=findViewById(R.id.buttonSETTINGS);
        buttonQuit=findViewById(R.id.buttonQUIT);
        buttonInfo=findViewById(R.id.buttonInfo);
        buttonENG= findViewById(R.id.buttonENG);
        buttonPL= findViewById(R.id.buttonPL);
        buttonGitHub=findViewById(R.id.buttonGit);
        loadSettings();

        buttonGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MeHow2k/2_Player_React"));
                startActivity(intent);
            }
        });
        buttonENG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                C.Localization=0;
                updateLocalization();
            }
        });
        buttonPL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                C.Localization=1;
                updateLocalization();
            }
        });
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSettings = new Intent(MenuActivity.this, InfoActivity.class);
                startActivity(intentSettings);
            }
        });
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

    private void updateLocalization() {
        if(C.Localization==1){
            //zmien stringi na POL
            Toast toast = Toast.makeText(getApplicationContext(),"PL" , Toast.LENGTH_SHORT); toast.show();

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"ENG" , Toast.LENGTH_SHORT); toast.show();

        }
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        C.requiredRounds=sharedPreferences.getInt("KEY_REQUIREDROUNDS",9);
        C.isGame1On=sharedPreferences.getBoolean("KEY_IS_GAME1_ON",true);
        C.isGame2On=sharedPreferences.getBoolean("KEY_IS_GAME2_ON",true);
        C.isGame3On=sharedPreferences.getBoolean("KEY_IS_GAME3_ON",true);
    }
}

/*do zrobienia

*/