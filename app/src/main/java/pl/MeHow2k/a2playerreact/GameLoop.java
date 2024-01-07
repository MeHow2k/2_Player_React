package pl.MeHow2k.a2playerreact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import java.util.Random;

public class GameLoop extends View implements View.OnTouchListener {
    String[] colorsNames={"CZERWONY","NIEBIESKI","ŻÓŁTY","ZIELONY","SZARY", "BIAŁY" };
    int[] colorsInts={Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN,Color.GRAY,Color.WHITE};
    int screenw=getScreenWidth(),screenh=getScreenHeight(),
    playButtonX=screenw/2-100,playButtonY=screenh*3/5,
            quitButtonX=screenw/2-100,quitButtonY=screenh*3/4,
    playButtonW=200,playButtonH=100;
    int roundspassed=0;
    int game_start_delay=0;int round_start_delay=0;int white_col_start_delay=0;int white_col_timer=0;
    boolean islevelcreated=false;
    boolean islevelended;
    int roundNumber =0;
    int colorMatch_currentColor=0,colorMatch_currentColorName=0;
    boolean canMakePoint =false;
    boolean canClick =false;
    boolean nextLevelRequest=false;
    String gameInfoText="";
    Paint paint;
    Random randomGlobal;
    Canvas canvas;
    public GameLoop(Context context) {
        super(context);
        randomGlobal = new Random();
        setOnTouchListener(this);
        this.setBackgroundColor(Color.BLACK);


        Thread gameThread =new Thread(new Runnable() {

            @SuppressLint("SuspiciousIndentation")
            @Override
            public void run() {
                long lastTime = System.nanoTime();
                double tickpersec = 60.0;
                double ns = 1000000000 / tickpersec;
                double delta = 0;
                int frames = 0;
                long timer = System.currentTimeMillis();

                while (true){
                    long nowTime = System.nanoTime();
                    delta += (nowTime - lastTime) / ns;
                    lastTime = nowTime;

                    if (C.GAMESTATE==1 && !C.PAUSE) {//ingame
                        //sprawdzenie czy ktos wygrał
                        if(C.requiredWins==roundspassed) {
                            if (C.player1Wins > C.player2Wins) {
                                roundNumber=0;
                                C.GAMESTATE = 111;
                            }else if (C.player1Wins < C.player2Wins) {
                                roundNumber=0;
                                C.GAMESTATE = 222;
                            }else C.GAMESTATE = 333;//draw
                        }
                        //zmiana gry
                        if(nextLevelRequest) {
                            game_start_delay=0;
                            roundNumber=0;
                            gameInfoText="";
                            //C.currentGame=randomGlobal.nextInt(1)+1;
                            C.currentGame++;
                            if(C.currentGame==3) C.currentGame=1;
                            nextLevelRequest=false;
                        }

                        game_start_delay++;
                        if(game_start_delay>2000){
                        //white color hit
                            if(C.currentGame==1){
                                round_start_delay++;
                                if(round_start_delay >=500){
                                    canClick=true;
                                    if(!islevelcreated){
                                        white_col_timer=0;
                                        Random random = new Random();
                                        white_col_start_delay=random.nextInt(3500)+500;
                                        islevelcreated=true;
                                    }
                                    if(white_col_start_delay<0){
                                        canMakePoint=true;
                                        roundNumber++;
                                        white_col_timer++;
                                    }else white_col_start_delay--;
                                }
                            }//white color hit

                            if(C.currentGame==2){
                                round_start_delay++;
                                if(round_start_delay >=500){
                                    canClick=true;
                                    if(!islevelcreated){
                                        white_col_timer=0;
                                        //losowanie koloru i nazwy
                                        Random random = new Random();
                                        if(random.nextInt(100)>20){
                                        colorMatch_currentColor=random.nextInt(colorsInts.length);
                                        colorMatch_currentColorName = random.nextInt(colorsNames.length);
                                        }
                                        else{
                                            colorMatch_currentColor=random.nextInt(colorsInts.length);
                                            colorMatch_currentColorName=colorMatch_currentColor;
                                        }
                                        islevelcreated=true;
                                    }
                                    //gdzy pasuja do siebie gracz moze zdobyc pkt
                                    if(colorMatch_currentColorName==colorMatch_currentColor)canMakePoint=true;
                                    if(white_col_timer>1600) {//co jakis czas nowe zestawienie
                                        islevelcreated=false;
                                        white_col_timer=0;
                                    }
                                    if(white_col_start_delay<0){
                                        roundNumber++;
                                        white_col_timer++;
                                    }else white_col_start_delay--;
                                }
                            }//color match


                    }

                    }//gamestate 1
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    if (delta >= 1) {
                        invalidate();
                        delta--;
                        frames++;
                    }
                    if (System.currentTimeMillis() - timer > 1000) { //co 1 s sprawdza liczbe narysowanych klatek
                        timer += 1000;
                        //wypisywanie liczby klatek na sekundę w etykiecie
                        Log.i("FPS metrer", String.valueOf(frames));
                        frames = 0;
                    }
                }
            }
        });
    gameThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        //canvas.drawText(String.valueOf(roundspassed),playButtonX,playButtonY,paint);//testowo wypisz ile rund bylo
        if(C.GAMESTATE==0){
        canvas.drawText("2 Player React",screenw/2-300,screenh/6,paint);
        //canvas.drawText("Play",screenw/2-100,screenh*3/5,paint);
        //canvas.drawRect(playButtonX,playButtonY,playButtonX+playButtonW,playButtonY-playButtonH,paint);
        canvas.drawText("Play",playButtonX,playButtonY,paint);

        canvas.drawText("Quit",quitButtonX,quitButtonY,paint);
        }
        if(C.GAMESTATE==1){
            drawPlayerButtons(canvas);
            drawGameInfo(gameInfoText,canvas);
            if(C.currentGame==1) {
                if (white_col_start_delay <= 0) {
                    if (roundNumber != 0) drawLevel_WhiteHit(canvas);
                    if (islevelended && roundNumber != 0)
                        drawGameInfo(String.valueOf(white_col_timer), canvas);
                }

                if (round_start_delay >= 500) {
                    gameInfoText = "Nacisnij kiedy pojawi sie bialy kolor";
                }
                if (game_start_delay < 3000) {
                    drawGameTitle("Reaction Test", canvas);
                }
            }

            if(C.currentGame==2) {
                if (white_col_start_delay <= 0) {
                    if (roundNumber != 0)
                        drawLevel_ColorMatch(colorsNames[colorMatch_currentColorName],colorsInts[colorMatch_currentColor],canvas);
                    if (islevelended && roundNumber != 0)
                        drawGameInfo(String.valueOf(white_col_timer), canvas);
                }
                if (round_start_delay >= 500) {
                    gameInfoText = "Nacisnij kiedy słowo ma poprawny kolor";
                }
                if (game_start_delay < 3000) {
                    drawGameTitle("Match color", canvas);
                }
            }
            drawPlayerWinsInfo(canvas);
            drawPlayerLabel(canvas);
        }
        //gdy wygral player1
        if(C.GAMESTATE==111){
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
            gameInfoText="";
        }
        //gdy wygrał player2
        if(C.GAMESTATE==222){
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
            gameInfoText="";
        }
        //gdy remis
        if(C.GAMESTATE==333){
            //drawing players buttons
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
            gameInfoText="";
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x=(int)event.getX();
        int y=(int)event.getY();
        final int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) {
            //play button collision

            if(C.PAUSE) {
                C.PAUSE=false;

                //do poprawy w ondraw()? zrobione bo tekst znika po kliknieciu i zdobyciu pkt todo
                if(C.currentGame==1) gameInfoText = "Naciśnij kiedy pojawi się bialy kolor";
                if(C.currentGame==2) gameInfoText = "Naciśnij kiedy słowo ma poprawny kolor";
                else gameInfoText = "";
            }
            if(C.GAMESTATE==1) {
                    if (canClick) {
                        if (y > screenh / 3 * 2 && y > 0) {
                            if (canMakePoint) {
                                C.player1Wins++;
                                islevelended = true;
                                gameInfoText = "Gracz 1 zdobył punkt! Stuknij, aby przejść dalej.";
                            } else {
                                C.player1Wins--;
                                islevelended = true;
                                gameInfoText = "Gracz 2 zdobył punkt! Stuknij, aby przejść dalej.";
                            }
                            endLevel();
                            C.PAUSE = true;
                        }

                        if (y < screenh / 3) {
                            if (canMakePoint) {
                                C.player2Wins++;
                                gameInfoText = "Gracz 2 zdobył punkt! Stuknij, aby przejść dalej.";
                            } else {
                                C.player2Wins--;
                                gameInfoText = "Gracz 1 zdobył punkt! Stuknij, aby przejść dalej.";
                            }
                            islevelended = true;
                            endLevel();
                            C.PAUSE = true;
                        }
                    }

            }
            //menu
            if(C.GAMESTATE==0) {
                if (x > playButtonX && x < playButtonX + playButtonW && y > playButtonY - playButtonH && y < playButtonY) {
                    C.GAMESTATE=1;
                }
                if (x > quitButtonX && x < quitButtonX + playButtonW && y > quitButtonY - playButtonH && y < quitButtonY) {
                    System.exit(0);
                }
            }
            //end game/summary
            if(C.GAMESTATE==111 || C.GAMESTATE==222|| C.GAMESTATE==333) {
                    C.GAMESTATE=0;C.player1Wins=0;C.player2Wins=0; resetVariables();
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
        if (C.GAMESTATE==222 || C.GAMESTATE==333) paintplayerbutton.setColor(Color.GREEN);
        canvas.drawRect(0,0,screenw,screenh/3,paintplayerbutton);
        if (C.GAMESTATE==111 || C.GAMESTATE==333) paintplayerbutton.setColor(Color.GREEN);
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
        if(C.GAMESTATE==111) winmessage="Player 1 won!";else if(C.GAMESTATE==222) winmessage="Player 2 won!";else winmessage="Draw!";
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
        if(C.GAMESTATE==111) paintText.setColor(Color.GREEN);
        if(C.GAMESTATE==222) paintText.setColor(Color.RED);
        if(C.GAMESTATE==333) paintText.setColor(Color.YELLOW);
        canvas.drawText(String.valueOf(C.player1Wins),screenw-100,screenh/3*2-100,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        if(C.GAMESTATE==111) paintText.setColor(Color.RED);
        if(C.GAMESTATE==222) paintText.setColor(Color.GREEN);
        if(C.GAMESTATE==333) paintText.setColor(Color.YELLOW);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu x(prawy bok -75px)  y(srodek)
        canvas.save();
        canvas.rotate(180, screenw-75, screenh/2);
        canvas.drawText(String.valueOf(C.player2Wins),screenw-100,screenh/3*2-100,paintText);
        canvas.restore();
    }
    protected void drawGameTitle(String text, Canvas canvas) {
        // Rysowanie informacji nt zadania po dwoch stronach
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        canvas.drawText(text, game_start_delay-400, screenh / 3 * 2 - 250, paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText(text, game_start_delay-400, screenh / 3 * 2 - 250, paintText);
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
    protected void drawLevel_WhiteHit(Canvas canvas) {
        //drawing white rectangle
        Paint white =new Paint();
        white.setColor(Color.WHITE);
        canvas.drawRect(0,screenh/3,screenw,screenh/3*2,white);
    }
    protected void drawLevel_ColorMatch(String colorName,int colorInt,Canvas canvas) {
        Paint paintText = new Paint();
        paintText.setColor(colorInt);
        // P1
        paintText.setTextSize(80);
        canvas.drawText(colorName,screenw/2-120,screenh/2-50,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText(colorName,screenw/2-120,screenh/2-50,paintText);
        canvas.restore();
    }

    protected void endLevel() {
        canMakePoint=false;
        islevelcreated=false;
        round_start_delay =0;
        white_col_start_delay=0;
        canClick=false;
        roundspassed++;
        white_col_timer=0;


        //zmiana gry co 3 rundy
        if(roundspassed % 3 == 0) {
            nextLevelRequest=true;
        }
    }
    protected void resetVariables() {
        roundspassed=0;
        game_start_delay=0;
        round_start_delay=0;
        white_col_start_delay=0;
        white_col_timer=0;
        islevelcreated=false;
        roundNumber =0;
        colorMatch_currentColor=0;
        colorMatch_currentColorName=0;
        canMakePoint =false;
        canClick =false;
        nextLevelRequest=false;
        gameInfoText="";
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
