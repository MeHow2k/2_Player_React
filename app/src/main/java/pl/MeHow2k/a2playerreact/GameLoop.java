package pl.MeHow2k.a2playerreact;

import android.annotation.SuppressLint;
import android.app.Activity;
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
            quitButtonX=screenw/2-100,quitButtonY=screenh*3/4,
    playButtonW=200,playButtonH=100;
    int roundspassed=0;
    int game_start_delay=0;int round_start_delay=0;int white_col_start_delay=1;int white_col_timer=0;
    int GAME_START_DELAY_VALUE=400,ROUND_START_DELAY_VALUE=180,ROUND_NEXT_QUESTION_DELAY_VALUE=180;
    boolean islevelcreated=false;
    boolean islevelended;
    boolean isPlayer1scored=false,isPlayer2scored=false;
    boolean isFirstRound =true/*nieuzywanie*/, isPausedAfterPoint=false;
    int colorMatch_currentColor=0,colorMatch_currentColorName=0;
    int countriesCities_currentCountry =0, countriesCities_currentCity =0;
    int animationFrame=0;
    boolean canMakePoint =false;
    boolean canClick =false;
    boolean nextLevelRequest=true;
    boolean breakThread=false;
    String gameInfoText="";
    Paint paint;
    Random randomGlobal;
    Canvas canvas;
    Thread gameThread;
    public GameLoop(Context context) {
        super(context);
        randomGlobal = new Random();
        setOnTouchListener(this);
        this.setBackgroundColor(Color.BLACK);

        gameThread =new Thread(new Runnable() {

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

                    if (C.GAMESTATE==1 && (!isPausedAfterPoint) ) {//ingame
                        //sprawdzenie czy ktos wygrał
                        if(C.requiredRounds ==roundspassed) {
                            if (C.player1Wins > C.player2Wins) {
                                isFirstRound =true;
                                C.GAMESTATE = 111;
                            }else if (C.player1Wins < C.player2Wins) {
                                isFirstRound =true;
                                C.GAMESTATE = 222;
                            }else C.GAMESTATE = 333;//draw
                        }
                        //zmiana gry
                        if(nextLevelRequest) {
                            animationFrame=0;
                            game_start_delay=0;
                            isFirstRound =true;
                            gameInfoText="";
                            C.currentGame++;
                            if(C.currentGame==4) C.currentGame=1;
                            if(C.currentGame==1 && !C.isGame1On || C.currentGame==2 && !C.isGame2On
                                    || C.currentGame==3 && !C.isGame3On) nextLevelRequest=true;
                                else nextLevelRequest=false;
                        }
                            if (game_start_delay > GAME_START_DELAY_VALUE) {
                                //white color hit
                                if (C.currentGame == 1) {
                                    if (round_start_delay >= ROUND_START_DELAY_VALUE) {
                                        canClick = true;
                                        if (!islevelcreated) {
                                            white_col_timer = 0;
                                            Random random = new Random();
                                            white_col_start_delay = random.nextInt(350) + 50;
                                            islevelcreated = true;
                                        }
                                        if (white_col_start_delay < 0) {
                                            canMakePoint = true;
                                            white_col_timer++;
                                        }
                                    }
                                }//white color hit

                                if (C.currentGame == 2) {
                                    if (round_start_delay >= ROUND_START_DELAY_VALUE) {
                                        canClick = true;
                                        if (!islevelcreated) {
                                            white_col_timer = 0;
                                            //losowanie koloru i nazwy
                                            Random random = new Random();
                                            if (random.nextInt(100) > 20) {
                                                colorMatch_currentColor = random.nextInt(C.colorsInts.length);
                                                colorMatch_currentColorName = random.nextInt(C.colorsNames.length);
                                            } else {
                                                colorMatch_currentColor = random.nextInt(C.colorsInts.length);
                                                colorMatch_currentColorName = colorMatch_currentColor;
                                            }
                                            islevelcreated = true;
                                        }
                                        //gdy pasuja do siebie gracz moze zdobyc pkt
                                        if (colorMatch_currentColorName == colorMatch_currentColor)
                                            canMakePoint = true;
                                        if (white_col_timer > ROUND_NEXT_QUESTION_DELAY_VALUE) {//co jakis czas nowe zestawienie
                                            islevelcreated = false;
                                            canMakePoint = false;
                                            white_col_timer = 0;
                                        }
                                    }
                                }//color match

                                if (C.currentGame == 3) {
                                    if (round_start_delay >= ROUND_START_DELAY_VALUE) {
                                        canClick = true;
                                        if (!islevelcreated) {
                                            white_col_timer = 0;
                                            //losowanie panstwa i miasta
                                            Random random = new Random();
                                            if (random.nextInt(100) > 20) {
                                                countriesCities_currentCountry = random.nextInt(C.countries.length);
                                                countriesCities_currentCity = random.nextInt(C.cities.length);
                                            } else {
                                                countriesCities_currentCountry = random.nextInt(C.countries.length);
                                                countriesCities_currentCity = countriesCities_currentCountry;
                                            }
                                            islevelcreated = true;
                                        }
                                        //gdzy pasuja do siebie gracz moze zdobyc pkt
                                        if (countriesCities_currentCountry == countriesCities_currentCity)
                                            canMakePoint = true;
                                        if (white_col_timer > ROUND_NEXT_QUESTION_DELAY_VALUE) {//co jakis czas nowe zestawienie
                                            islevelcreated = false;
                                            canMakePoint = false;
                                            white_col_timer = 0;
                                        }
                                    }
                                }//panstwa miasta
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
                        Log.i("",""+ white_col_start_delay+" pause:"+ C.PAUSE);
                        if(!isPausedAfterPoint || C.PAUSE){
                            round_start_delay++;
                            game_start_delay++;
                            animationFrame++;

                            if(white_col_start_delay<0){
                                white_col_timer++;
                            }else white_col_start_delay--;
                        }
                    }
                    if (System.currentTimeMillis() - timer > 1000) { //co 1 s sprawdza liczbe narysowanych klatek
                        timer += 1000;
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

        if(C.GAMESTATE==1){
            drawPlayerButtons(canvas);
            drawGameInfo(gameInfoText,canvas);
            if(C.currentGame==1) {
                if (white_col_start_delay <= 0) {

                        if(canClick|| isPausedAfterPoint)drawLevel_WhiteHit(canvas);
                    //int-timer test
                    //if (islevelended && roundNumber != 0)
                        //drawGameInfo(String.valueOf(white_col_timer), canvas);
                }

                if (round_start_delay >= 120) {
                    gameInfoText = "Nacisnij kiedy pojawi sie bialy kolor";
                }
                if (game_start_delay < 180) {
                    drawGameTitle("Biały kolor", canvas);
                }
            }

            if(C.currentGame==2) {
                if (white_col_start_delay <= 0) {
                    if (canClick || isPausedAfterPoint)
                        drawLevel_ColorMatch(C.colorsNames[colorMatch_currentColorName],C.colorsInts[colorMatch_currentColor],canvas);
                    //int timer test
                    //                    if (islevelended && roundNumber != 0)
                      //  drawGameInfo(String.valueOf(white_col_timer), canvas);
                }
                if (round_start_delay >= 120) {
                    gameInfoText = "Nacisnij kiedy słowo ma poprawny kolor";
                }
                if (game_start_delay < 180) {
                    drawGameTitle("Dopasuj kolor", canvas);
                    if (game_start_delay>179) isFirstRound=false;
                }
            }
            if(C.currentGame==3) {
                if (white_col_start_delay <= 0) {
                    if (canClick || isPausedAfterPoint)
                        drawLevel_CountriesCities(C.countries[countriesCities_currentCountry],C.cities[countriesCities_currentCity],canvas);
                    //int timer test
                    //                    if (islevelended && roundNumber != 0)
//                        drawGameInfo(String.valueOf(white_col_timer), canvas);
                }
                if (round_start_delay >= 120) {
                    gameInfoText = "Nacisnij kiedy kraj i stolica pasują do siebie";
                }
                if (game_start_delay < 180) {
                    drawGameTitle("Państwa Miasta", canvas);
                }
            }

            drawPlayerWinsInfo(canvas);
            drawPlayerLabel(canvas);
            if(C.PAUSE) drawPauseLabel(canvas);
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


            if(isPausedAfterPoint) {
                isPausedAfterPoint =false;
                isPlayer1scored=false;isPlayer2scored=false;
                if(C.currentGame==1) gameInfoText = "Naciśnij kiedy pojawi się bialy kolor";
                else if(C.currentGame==2) gameInfoText = "Naciśnij kiedy słowo ma poprawny kolor";
                else if(C.currentGame==3) gameInfoText = "Naciśnij kiedy kraj i stolica pasuja do siebie";
                else gameInfoText = "";
            }
            if(C.GAMESTATE==1) {
                    if (canClick) {
                        if (y > screenh / 3 * 2 && y > 0) {
                            if (canMakePoint) {
                                C.player1Wins++;
                                islevelended = true;
                                isPlayer1scored=true;
                                gameInfoText = "Gracz 1 zdobył punkt! Stuknij, aby przejść dalej.";
                            } else {
                                C.player1Wins--;
                                isPlayer2scored=true;
                                islevelended = true;
                                gameInfoText = "Gracz 1 stracił punkt! Stuknij, aby przejść dalej.";
                            }
                            endLevel();
                            isPausedAfterPoint = true;
                        }

                        if (y < screenh / 3) {
                            if (canMakePoint) {
                                C.player2Wins++;
                                isPlayer2scored=true;
                                gameInfoText = "Gracz 2 zdobył punkt! Stuknij, aby przejść dalej.";
                            } else {
                                C.player2Wins--;
                                isPlayer1scored=true;
                                gameInfoText = "Gracz 2 stracił punkt! Stuknij, aby przejść dalej.";
                            }
                            islevelended = true;
                            endLevel();
                            isPausedAfterPoint = true;
                        }
                        if(y > screenh / 3 && y < screenh / 3 * 2) {
                            if(C.PAUSE==true) C.PAUSE=false;
                            else C.PAUSE=true;
                        }
                    }

            }

            //end game/summary
            if(C.GAMESTATE==111 || C.GAMESTATE==222|| C.GAMESTATE==333) {
                    C.GAMESTATE=0;C.player1Wins=0;C.player2Wins=0; resetVariables();
                Activity activity = (Activity)getContext();
                activity.finish();
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
        if(isPlayer2scored) paintplayerbutton.setColor(Color.GREEN);
        if(isPlayer1scored) paintplayerbutton.setColor(Color.RED);
        canvas.drawRect(0,0,screenw,screenh/3,paintplayerbutton);//player2
        paintplayerbutton.setColor(Color.GRAY);
        if (C.GAMESTATE==111 || C.GAMESTATE==333) paintplayerbutton.setColor(Color.GREEN);
        if (C.GAMESTATE==222) paintplayerbutton.setColor(Color.RED);
        if(isPlayer1scored) paintplayerbutton.setColor(Color.GREEN);
        if(isPlayer2scored) paintplayerbutton.setColor(Color.RED);
        canvas.drawRect(0,screenh/3*2,screenw,screenh,paintplayerbutton);//player1
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
        canvas.drawText(text, animationFrame*9-400, screenh / 3 * 2 - 250, paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText(text, animationFrame*9-400, screenh / 3 * 2 - 250, paintText);
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
    protected void drawPauseLabel(Canvas canvas) {
        // Rysowanie napisu o pauzie
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        canvas.drawText("PAUZA",screenw/2-120,screenh-250,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText("PAUZA",screenw/2-120,screenh-250,paintText);
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
        canvas.drawText(colorName,screenw/2-120,screenh / 3 * 2 - 250,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText(colorName,screenw/2-120,screenh / 3 * 2 - 250,paintText);
        canvas.restore();
    }
    protected void drawLevel_CountriesCities(String country,String city,Canvas canvas) {
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        canvas.drawText(country,screenw/2-120,screenh / 3 * 2 - 250,paintText);
        canvas.drawText(city,screenw/2-120,screenh / 3 * 2 - 250+100,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        canvas.drawText(country,screenw/2-120,screenh / 3 * 2 - 250,paintText);
        canvas.drawText(city,screenw/2-120,screenh / 3 * 2 - 250+100,paintText);
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
        isFirstRound =true;
        colorMatch_currentColor=0;
        colorMatch_currentColorName=0;
        countriesCities_currentCountry =0;
        countriesCities_currentCity =0;
        canMakePoint =false;
        canClick =false;
        nextLevelRequest=false;
        gameInfoText="";
        isPlayer2scored=false;
        isPlayer1scored=false;
    }

    public static void stopThread() {

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
