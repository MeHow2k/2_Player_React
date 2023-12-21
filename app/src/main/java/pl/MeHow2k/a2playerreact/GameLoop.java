package pl.MeHow2k.a2playerreact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameLoop extends View implements View.OnTouchListener {
    int screenw=getScreenWidth(),screenh=getScreenHeight(),
    playButtonX=screenw/2-100,playButtonY=screenh*3/5,
    playButtonW=200,playButtonH=100;
    Paint paint;
    Canvas canvas;
    public GameLoop(Context context) {
        super(context);

        setOnTouchListener(this);
        this.setBackgroundColor(Color.BLACK);


        new Thread(new Runnable() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void run() {
                while (true){
                    if (C.GAMESTATE==1) {//ingame
                        //sprawdzenie czy ktos wygrał
                        if (C.player1Wins == 10) {
                            Log.i("p1", "won");
                            C.GAMESTATE = 111;
                        }
                        if (C.player2Wins == 10) {
                            Log.i("P2", "won");
                            C.GAMESTATE = 222;

                        }
                    }//gamestate 1

                        invalidate();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                }
            }
        }).start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        if(C.GAMESTATE==0){
        canvas.drawText("2 Player React",screenw/2-300,screenh/6,paint);
        //canvas.drawText("Play",screenw/2-100,screenh*3/5,paint);
        //canvas.drawRect(playButtonX,playButtonY,playButtonX+playButtonW,playButtonY-playButtonH,paint);
        canvas.drawText("Play",playButtonX,playButtonY,paint);

        canvas.drawText("Quit",screenw/2-100,screenh*3/4,paint);
        }
        if(C.GAMESTATE==1){
           drawPlayerButtons(canvas);
           drawPlayerLabel(canvas);
           drawGameInfo("Nacisnij kiedy pojawi sie bialy kolor",canvas);

        }
        //gdy wygral player1
        if(C.GAMESTATE==111){
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
        }
        //gdy wygrał player2
        if(C.GAMESTATE==222){
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x=(int)event.getX();
        int y=(int)event.getY();
        final int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) {
            //play button collision

            if(C.GAMESTATE==1) {

                if (y < screenh/3) {
                    Log.i("P2", "clicked");
                    C.player2Wins++;
                }
                if (y > screenh/3*2 && y>0) {
                    Log.i("p1", "clicked");
                    C.player1Wins++;
                }
            }
            //menu
            if(C.GAMESTATE==0) {
                if (x > playButtonX && x < playButtonX + playButtonW && y > playButtonY - playButtonH && y < playButtonY) {
                    Log.i("Play button", "clicked");
                    C.GAMESTATE=1;
                }
            }
            //end game/summary
            if(C.GAMESTATE==111 || C.GAMESTATE==222) {
                    C.GAMESTATE=0;C.player1Wins=0;C.player2Wins=0;
                }
            }

        //jeżeli przesuwamy palcem po ekranie
        if(action == MotionEvent.ACTION_MOVE){ }
        if(action == MotionEvent.ACTION_UP){ }
        invalidate();//odświeżenie grafiki
        return true;
    }
    protected void drawPlayerButtons(Canvas canvas) {
        //drawing players buttons
        Paint paintplayerbutton =new Paint();
        paintplayerbutton.setColor(Color.GRAY);
        if (C.GAMESTATE==111) paintplayerbutton.setColor(Color.RED);
        if (C.GAMESTATE==222) paintplayerbutton.setColor(Color.GREEN);
        canvas.drawRect(0,0,screenw,screenh/3,paintplayerbutton);
        if (C.GAMESTATE==111) paintplayerbutton.setColor(Color.GREEN);
        if (C.GAMESTATE==222) paintplayerbutton.setColor(Color.RED);
        canvas.drawRect(0,screenh/3*2,screenw,screenh,paintplayerbutton);
        drawPlayerWinsInfo(canvas);
    }
    protected void drawGameInfo(String text, Canvas canvas) {
        // Rysowanie informacji nt zadania po dwoch stronach
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(50);
        canvas.drawText(text, 0, screenh / 3 * 2 + 200, paintText);
        // P2 (obrocony)
        paintText.setTextSize(50);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText(text, 0, screenh / 3 * 2 + 200, paintText);
        canvas.restore();
    }
    protected void drawPlayerWonInfo(Canvas canvas) {
        // Rysowanie informacji nt zadania po dwoch stronach
        Paint paintText = new Paint();
        Random random = new Random();
        paintText.setColor(Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
        String winmessage;
        if(C.GAMESTATE==111) winmessage="Player 1 won!";else winmessage="Player 2 won!";
        // P1
        paintText.setTextSize(100);
        canvas.drawText(winmessage, screenw/2-300, screenh / 3 * 2 + 200, paintText);
        // P2 (obrocony)
        paintText.setTextSize(100);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText(winmessage, screenw/2-300, screenh / 3 * 2 + 200, paintText);
        canvas.restore();
    }
    protected void drawPlayerWinsInfo(Canvas canvas) {
        // Rysowanie informacji nt punktacji
        Paint paintText = new Paint();
        paintText.setColor(Color.rgb(84,84,84));
        // P1
        paintText.setTextSize(80);
        canvas.drawText(String.valueOf(C.player1Wins),screenw-100,screenh/3*2-100,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu x(prawy bok -75px)  y(srodek)
        canvas.save();
        canvas.rotate(180, screenw-75, screenh/2);
        canvas.drawText(String.valueOf(C.player2Wins),screenw-100,screenh/3*2-100,paintText);
        canvas.restore();
    }
    protected void drawPlayerLabel(Canvas canvas) {
        // Rysowanie napisu o graczach na przyciskach
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        canvas.drawText("Player 1",screenw/2-120,screenh-50,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText("Player 2",screenw/2-120,screenh-50,paintText);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenh=h;
        screenw=w;

    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
