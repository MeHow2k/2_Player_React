package pl.MeHow2k.a2playerreact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameLoop(this));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
/*do zrobienia

napraw bialy kolor na poczatku gry
popraw napis z info jak grac

//akceptacje
zrob klikniecie po zakonczeniu rundy
zrob napis ready przed rozpoczeciem

intro do poziomu


*/