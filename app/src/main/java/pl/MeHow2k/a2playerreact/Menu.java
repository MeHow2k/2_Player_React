package pl.MeHow2k.a2playerreact;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Menu extends View implements View.OnTouchListener {
    int screenw=getScreenWidth(),screenh=getScreenHeight(),
    playButtonX=screenw/2-100,playButtonY=screenh*3/5,
    playButtonW=200,playButtonH=100;
    Paint paint;
    Canvas canvas;
    public Menu(Context context) {
        super(context);

        setOnTouchListener(this);
        this.setBackgroundColor(Color.BLACK);


        new Thread(new Runnable() {
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
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            paintplayerbutton.setColor(Color.GRAY);
            canvas.drawRect(0,0,screenw,screenh/3,paintplayerbutton);
            canvas.drawRect(0,screenh/3*2,screenw,screenh,paintplayerbutton);
            canvas.drawText(String.valueOf(C.player2Wins),screenw-100,100,paint);
            canvas.drawText(String.valueOf(C.player1Wins),screenw-100,screenh-100,paint);
        }
        //gdy wygral player1
        if(C.GAMESTATE==111){
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            paintplayerbutton.setColor(Color.RED);
            canvas.drawRect(0,0,screenw,screenh/3,paintplayerbutton);
            paintplayerbutton.setColor(Color.GREEN);
            canvas.drawRect(0,screenh/3*2,screenw,screenh,paintplayerbutton);
            canvas.drawText(String.valueOf(C.player2Wins),screenw-100,100,paint);
            canvas.drawText(String.valueOf(C.player1Wins),screenw-100,screenh-100,paint);
            canvas.drawText("Wygrał Gracz1",screenw/2-100,screenh/2-100,paint);
        }
        //gdy wygrał player2
        if(C.GAMESTATE==222){
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            paintplayerbutton.setColor(Color.GREEN);
            canvas.drawRect(0,0,screenw,screenh/3,paintplayerbutton);
            paintplayerbutton.setColor(Color.RED);
            canvas.drawRect(0,screenh/3*2,screenw,screenh,paintplayerbutton);
            canvas.drawText(String.valueOf(C.player2Wins),screenw-100,100,paint);
            canvas.drawText(String.valueOf(C.player1Wins),screenw-100,screenh-100,paint);
            canvas.drawText("Wygrał Gracz2",screenw/2-100,screenh/2-100,paint);
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
