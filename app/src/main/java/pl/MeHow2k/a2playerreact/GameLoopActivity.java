package pl.MeHow2k.a2playerreact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class GameLoopActivity extends AppCompatActivity {
    GameLoop gameLoop;//stwortzenie obiektu pętli gry

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gameLoop= new GameLoop(this));//ustawienie obiektu pętli gry jako widok
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//wył. obrót ekranu

    }
    //przy zakończeniu aktywności
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //przerwij wątki gry i timera
        gameLoop.getGameThread().interrupt();
        gameLoop.getGameTimerThread().interrupt();
    }
}