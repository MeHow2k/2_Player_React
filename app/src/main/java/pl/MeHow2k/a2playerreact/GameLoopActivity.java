package pl.MeHow2k.a2playerreact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class GameLoopActivity extends AppCompatActivity {
    GameLoop gameLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gameLoop= new GameLoop(this));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameLoop.getGameThread().interrupt();
        gameLoop.getGameTimerThread().interrupt();
    }
}