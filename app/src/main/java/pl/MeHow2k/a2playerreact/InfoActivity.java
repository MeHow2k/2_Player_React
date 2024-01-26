package pl.MeHow2k.a2playerreact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class InfoActivity extends AppCompatActivity {

    Button buttonBack;
    ImageButton buttonGitHub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        buttonBack=findViewById(R.id.buttonBACK);
        buttonGitHub=findViewById(R.id.buttonGit);
        //otworzenie strony www
        buttonGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MeHow2k/2_Player_React"));
                startActivity(intent);
            }
        });
        //zakończenie aktywności
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}