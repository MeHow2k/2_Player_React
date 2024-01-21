package pl.MeHow2k.a2playerreact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    Button buttonBack,buttonSave;
    TextView textWins;
    Switch switchGame1,switchGame2,switchGame3;
    SeekBar seekBar;
    int seekBarVal=C.requiredRounds,uncheckedGames=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        textWins = findViewById(R.id.textWins);
        buttonBack = findViewById(R.id.buttonBACK);
        buttonSave = findViewById(R.id.buttonSave);
        switchGame1 = findViewById(R.id.switchGame1);
        switchGame2 = findViewById(R.id.switchGame2);
        switchGame3 = findViewById(R.id.switchGame3);
        seekBar = findViewById(R.id.seekBar1);
        textWins.setText(String.valueOf(C.requiredRounds));
        seekBar.setProgress(C.requiredRounds);
        if(C.isGame1On) {switchGame1.setChecked(true);} else {switchGame1.setChecked(false);uncheckedGames++;}
        if(C.isGame2On) {switchGame2.setChecked(true);} else {switchGame2.setChecked(false);uncheckedGames++;}
        if(C.isGame3On) {switchGame3.setChecked(true);} else {switchGame3.setChecked(false);uncheckedGames++;}
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarVal=progress;
                textWins.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        //wyłączanie gier
        switchGame1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    C.isGame1On = true;
                    uncheckedGames--;
                } else {
                    C.isGame1On = false;
                    uncheckedGames++;
                }
                if(uncheckedGames==3) {
                    switchGame1.setChecked(true);
                    C.isGame1On = true;
                    uncheckedGames--;
                    Toast toast = Toast.makeText(getApplicationContext(),"Minimum 1 gra musi być włączona!"
                            , Toast.LENGTH_SHORT); toast.show();
                }
                Log.i("","1"+C.isGame1On+" 2"+C.isGame2On+" 3"+C.isGame3On+" u"+uncheckedGames);
            }
        });
        switchGame2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    C.isGame2On = true;
                    uncheckedGames--;
                } else {
                    C.isGame2On = false;
                    uncheckedGames++;
                }
                if(uncheckedGames==3) {
                    switchGame2.setChecked(true);
                    C.isGame2On = true;
                    uncheckedGames--;
                    Toast toast = Toast.makeText(getApplicationContext(),"Minimum 1 gra musi być włączona!"
                            , Toast.LENGTH_SHORT); toast.show();
                }
                Log.i("","1"+C.isGame1On+" 2"+C.isGame2On+" 3"+C.isGame3On+" u"+uncheckedGames);
            }
        });
        switchGame3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    C.isGame3On = true;
                    uncheckedGames--;
                } else {
                    C.isGame3On = false;
                    uncheckedGames++;
                }
                if(uncheckedGames==3) {
                    switchGame3.setChecked(true);
                    C.isGame3On = true;
                    uncheckedGames--;
                    Toast toast = Toast.makeText(getApplicationContext(),"Minimum 1 gra musi być włączona!"
                            , Toast.LENGTH_SHORT); toast.show();
                }
                Log.i("","1"+C.isGame1On+" 2"+C.isGame2On+" 3"+C.isGame3On+" u"+uncheckedGames);
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //zapisz do shared prefs
                C.requiredRounds =seekBarVal;
                SharedPreferences sharedPreferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putInt("KEY_REQUIREDROUNDS",seekBarVal);
                editor.putBoolean("KEY_IS_GAME1_ON",C.isGame1On);
                editor.putBoolean("KEY_IS_GAME2_ON",C.isGame2On);
                editor.putBoolean("KEY_IS_GAME3_ON",C.isGame3On);
                editor.apply();
                Toast toast = Toast.makeText(getApplicationContext(),"Zapisano ustawienia" , Toast.LENGTH_SHORT); toast.show();
                finish();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}