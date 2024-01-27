package pl.MeHow2k.a2playerreact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;


import java.util.Random;

public class GameLoop extends View implements View.OnTouchListener {

    int screenw=getScreenWidth(),screenh=getScreenHeight();//pobranie wymiarów ekranu
    int roundspassed=0;//liczba rozegranych rund
    //opoźnienia
    int game_start_delay=0;int round_start_delay=0;int white_col_start_delay=1;int nextQuestionDelay =0;
    //wartości opóźnień
    int GAME_START_DELAY_VALUE=400,ROUND_START_DELAY_VALUE=180,ROUND_NEXT_QUESTION_DELAY_VALUE=180;
    //flagi
    boolean islevelcreated=false;
    boolean islevelended;
    boolean isPlayer1scored=false,isPlayer2scored=false;
    boolean isFirstRound =true/*nieuzywanie*/, isPausedAfterPoint=false, timerResetRequest =false;
    //zmienne do porównania indeksów tabeli w grach
    int colorMatch_currentColor=0,colorMatch_currentColorName=0;
    int countriesCities_currentCountry =0, countriesCities_currentCity =0;
    //zmienna stosowana przy animacji tytułu gry
    int animationFrame=0;
    //flagi
    boolean canMakePoint =false;
    boolean canClick =false;
    boolean nextLevelRequest=true;
    String gameInfoText="";//tekst wyświetlający się na przycisku gracza
    String gameTimerSummary="";//test przechowujący wartość timera
    Paint paint;
    Random randomGlobal;
    Canvas canvas;
    Timer gameTimer;//utworzenie referenci do obiektu timera
    Thread gameThread;//i wątku gry
    public GameLoop(Context context) {
        super(context);
        randomGlobal = new Random();
        setOnTouchListener(this);
        this.setBackgroundColor(Color.BLACK);
        gameTimer = new Timer();
        gameTimer.start();//utworzenie timera i rozpoczęcie jego wątku
        //główna pętla gry
        gameThread =new Thread(new Runnable() {

            @SuppressLint("SuspiciousIndentation")
            @Override
            public void run() {
                //zmienne służące do zliczania 60 FPS
                long lastTime = System.nanoTime();
                double tickpersec = 60.0;
                double ns = 1000000000 / tickpersec;
                double delta = 0;
                int frames = 0;
                long timer = System.currentTimeMillis();

                while (true){
                    //porównanie czasu między przejściami pętli
                    long nowTime = System.nanoTime();
                    delta += (nowTime - lastTime) / ns;
                    lastTime = nowTime;

                    if (C.GAMESTATE==1 && (!isPausedAfterPoint) ) {//ingame
                        //obsługa pauzy
                        do {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }while(C.PAUSE || isPausedAfterPoint);

                        //sprawdzenie czy ktos wygrał
                        if(C.requiredRounds ==roundspassed) {
                            if (C.player1Wins > C.player2Wins) {
                                isFirstRound =true;
                                C.GAMESTATE = 111;//gracz1 wygrywa
                            }else if (C.player1Wins < C.player2Wins) {
                                isFirstRound =true;
                                C.GAMESTATE = 222;//gracz2 wygrywa
                            }else C.GAMESTATE = 333;//draw
                        }
                        //zmiana gry
                        if(nextLevelRequest) {
                            animationFrame=0;
                            game_start_delay=0;
                            isFirstRound =true;
                            gameInfoText="";
                            C.currentGame++;//zmień grę jeśli nie jest wyłączona
                            if(C.currentGame==4) C.currentGame=1;
                            if(C.currentGame==1 && !C.isGame1On || C.currentGame==2 && !C.isGame2On
                                    || C.currentGame==3 && !C.isGame3On) nextLevelRequest=true;
                                else nextLevelRequest=false;
                        }
                            if (game_start_delay > GAME_START_DELAY_VALUE) {//po opoźnieniu rozpocznij grę
                                //gra 1 biały kolor
                                if (C.currentGame == 1) {
                                    if (round_start_delay >= ROUND_START_DELAY_VALUE) {//opóźnienie po zakonczeniu rundy
                                        canClick = true;//możliwość naciśniecia przycisku przez gracza
                                        if (!islevelcreated) {//losowanie momentu pojawienia się białego koloru
                                            nextQuestionDelay = 0;
                                            Random random = new Random();
                                            white_col_start_delay = random.nextInt(350) + 50;
                                            islevelcreated = true;
                                        }
                                        if (white_col_start_delay < 0) {//po upływie tego czasu
                                            canMakePoint = true;//można zdobyć punkt
                                            nextQuestionDelay++;
                                            if(timerResetRequest) {//zresetuj timer jeśli nie został zresetowany
                                                gameTimer.reset();
                                                timerResetRequest =false;
                                            }
                                            gameTimer.startTimer();//uruchom timer
                                        }
                                    }
                                }//koniec petli bialy kolor
                                //gra 2 dopasuj kolor
                                if (C.currentGame == 2) {
                                    if (round_start_delay >= ROUND_START_DELAY_VALUE) {
                                        canClick = true;
                                        if (!islevelcreated) {
                                            nextQuestionDelay = 0;
                                            //losowanie koloru i nazwy
                                            Random random = new Random();
                                            if (random.nextInt(100) > 20) {
                                                colorMatch_currentColor = random.nextInt(C.colorsInts.length);
                                                colorMatch_currentColorName = random.nextInt(C.colorsNames.length);
                                            } else {//20% na wymuszenie dobrego zestawienia
                                                colorMatch_currentColor = random.nextInt(C.colorsInts.length);
                                                colorMatch_currentColorName = colorMatch_currentColor;
                                            }
                                            islevelcreated = true;
                                        }
                                        //gdy pasuja do siebie gracz moze zdobyc pkt
                                        if (colorMatch_currentColorName == colorMatch_currentColor)
                                            canMakePoint = true;
                                        if (nextQuestionDelay > ROUND_NEXT_QUESTION_DELAY_VALUE) {//co jakis czas nowe zestawienie
                                            islevelcreated = false;
                                            canMakePoint = false;
                                            nextQuestionDelay = 0;
                                        }
                                    }
                                }//dopasuj kolor
                                //gra 3 Państwa Miasta
                                if (C.currentGame == 3) {
                                    if (round_start_delay >= ROUND_START_DELAY_VALUE) {
                                        canClick = true;
                                        if (!islevelcreated) {
                                            nextQuestionDelay = 0;
                                            //losowanie panstwa i miasta
                                            Random random = new Random();
                                            if (random.nextInt(100) > 20) {
                                                countriesCities_currentCountry = random.nextInt(C.countries.length);
                                                countriesCities_currentCity = random.nextInt(C.cities.length);
                                            } else {//20% na wymuszenie dobrego zestawienia
                                                countriesCities_currentCountry = random.nextInt(C.countries.length);
                                                countriesCities_currentCity = countriesCities_currentCountry;
                                            }
                                            islevelcreated = true;
                                        }
                                        //gdy pasuja do siebie gracz moze zdobyc pkt
                                        if (countriesCities_currentCountry == countriesCities_currentCity)
                                            canMakePoint = true;
                                        if (nextQuestionDelay > ROUND_NEXT_QUESTION_DELAY_VALUE) {//co jakis czas nowe zestawienie
                                            islevelcreated = false;
                                            canMakePoint = false;
                                            nextQuestionDelay = 0;
                                        }
                                    }
                                }//panstwa miasta
                            }

                    }//gamestate 1
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            return; //?
                        }
                        //jeśli minął czas 1 klatki
                    if (delta >= 1) {
                        invalidate();//odśwież ekran
                        delta--;
                        frames++;//dodaj klatke do licznika
                        if (!C.PAUSE) {//gdy nie ma pauzy
                            if (!isPausedAfterPoint) {
                                //opóźnienia realizowane co klatkę
                                round_start_delay++;
                                game_start_delay++;
                                animationFrame++;
                                if (white_col_start_delay < 0) {
                                    nextQuestionDelay++;
                                } else white_col_start_delay--;
                            }
                        }
                    }
                    if (System.currentTimeMillis() - timer > 1000) { //co 1 s sprawdza liczbe narysowanych klatek
                        timer += 1000;
                        frames = 0;
                    }
                }
            }
        });
    gameThread.start();//utuchomienie od razu wątku
    }
    //funkcja rysująca
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);

        if(C.GAMESTATE==1){//gdy rozgrywka
            drawPlayerButtons(canvas);//rysowanie przycisków graczy
            drawGameInfo(gameInfoText,canvas);//rysowanie tekstu z informacjami
            if(C.currentGame==1) {//gra 1 biały kolor
                if (white_col_start_delay <= 0) {

                    if(canClick || isPausedAfterPoint)//rysowanie białego koloru
                        drawLevel_WhiteHit(canvas);

                    if (islevelended && isPausedAfterPoint)//po zakończeniu wyświetl czas reakcji
                       drawLevel_WhiteHitSummary(canvas);
                }

                if (round_start_delay >= ROUND_START_DELAY_VALUE) {//zmień informację po opóźnieniu rundy
                    gameInfoText = "Naciśnij kiedy pojawi się biały kolor";
                }
                if (game_start_delay < GAME_START_DELAY_VALUE) {//w trakcie opóźnienia między grami wyświetl animacje z tytułem gry
                    drawGameTitle("Biały kolor", canvas);
                }
            }
            //gra 2 dopasuj kolor
            if(C.currentGame==2) {
                if (white_col_start_delay <= 0) {
                    if (canClick || isPausedAfterPoint)
                        drawLevel_ColorMatch(C.colorsNames[colorMatch_currentColorName],C.colorsInts[colorMatch_currentColor],canvas);
                }
                if (round_start_delay >= ROUND_START_DELAY_VALUE) {//zmień informację po opóźnieniu rundy
                    gameInfoText = "Naciśnij kiedy słowo ma poprawny kolor";
                }
                if (game_start_delay < GAME_START_DELAY_VALUE) {//w trakcie opóźnienia między grami wyświetl animacje z tytułem gry
                    drawGameTitle("Dopasuj kolor", canvas);
                    if (game_start_delay>GAME_START_DELAY_VALUE) isFirstRound=false;//bez zastosowania
                }
            }
            //gra3 państwa miasta
            if(C.currentGame==3) {
                if (white_col_start_delay <= 0) {
                    if (canClick || isPausedAfterPoint)
                        drawLevel_CountriesCities(C.countries[countriesCities_currentCountry],C.cities[countriesCities_currentCity],canvas);
                }
                if (round_start_delay >= ROUND_START_DELAY_VALUE) {//zmień informację po opóźnieniu rundy
                    gameInfoText = "Naciśnij kiedy kraj i stolica pasują do siebie";
                }
                if (game_start_delay < GAME_START_DELAY_VALUE) {//w trakcie opóźnienia między grami wyświetl animacje z tytułem gry
                    drawGameTitle("Państwa Miasta", canvas);
                }
            }
            //wyświetlanie paska odmierzającego czas do nowego zestawienia w grze 2 i 3
            if(canClick && (C.currentGame==2 || C.currentGame==3)) drawGameTimeBar(canvas);
            drawPlayerWinsInfo(canvas);//wyświetl podgląd punktów graczy
            drawPlayerLabel(canvas);//wyświetl nazwy graczy
            if(C.PAUSE) drawPauseLabel(canvas);//jesli pauza wyświetl info o pauzie
        }
        //gdy wygral player1
        if(C.GAMESTATE==111){
            //narysuj kolorowe przyciski i info ze wygrał gracz1
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
            gameInfoText="";
        }
        //gdy wygrał player2
        if(C.GAMESTATE==222){
            //narysuj kolorowe przyciski i info ze wygrał gracz2
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
            gameInfoText="";
        }
        //gdy remis
        if(C.GAMESTATE==333){
            //narysuj kolorowe przyciski i info ze jest remis
            Paint paintplayerbutton =new Paint();
            drawPlayerButtons(canvas);
            drawPlayerLabel(canvas);
            drawPlayerWinsInfo(canvas);
            drawPlayerWonInfo(canvas);
            gameInfoText="";
        }
        //wyświetlanie napisu używanego do debugowania
        //drawDebugLabel(canvas, String.valueOf(isPausedAfterPoint));
    }
    //obsługa zdarzeń dotyku w ekran
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x=(int)event.getX();//pobranie wspołrzędnych dotknięcia ekranu
        int y=(int)event.getY();
        final int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) {//gdy jest dotyk->
            //gdy jest pauza po punkcie:
            if(isPausedAfterPoint) {
                isPausedAfterPoint =false;//wyłącz pauzę
                isPlayer1scored=false;isPlayer2scored=false;//usuń info kto dostał punkt i przywróć opis gry
                if(C.currentGame==1) gameInfoText = "Naciśnij kiedy pojawi się biały kolor";
                else if(C.currentGame==2) gameInfoText = "Naciśnij kiedy słowo ma poprawny kolor";
                else if(C.currentGame==3) gameInfoText = "Naciśnij kiedy kraj i stolica pasuja do siebie";
                else gameInfoText = "";
            }
            if(C.GAMESTATE==1) {
                    if (canClick) {//gdzy można przycisnąć przycisk przez gracza:
                        if (y > screenh / 3 * 2 && y > 0) {//dla przycisku gracza 1
                            if (canMakePoint) {//jesli można zdobyć pkt dodaj punkt
                                C.player1Wins++;
                                islevelended = true;
                                isPlayer1scored=true;
                                gameInfoText = "Gracz 1 zdobył punkt! Stuknij, aby przejść dalej.";
                                if(C.currentGame==1) {//zczytaj wynik z timera w grze 1
                                    gameTimer.stopTimer();
                                    gameTimerSummary=gameTimer.info();
                                    timerResetRequest =true;
                                }
                            } else {//jesli nie można zdobyć pkt odejmij punkt
                                C.player1Wins--;
                                isPlayer2scored=true;
                                islevelended = true;
                                gameInfoText = "Gracz 1 stracił punkt! Stuknij, aby przejść dalej.";
                                gameTimerSummary="Za szybko!";//gdy za szybko, nie wyświetlaj wyniku timera (wyświetlał by sie stary odczyt)
                            }
                            endLevel();//wywołanie funkcji kończącej rundę
                            isPausedAfterPoint = true;//pauza po punkcie do momentu przyciśnięcia przez gracza
                        }

                        if (y < screenh / 3) {//dla przycisku gracza 2 identycznie
                            if (canMakePoint) {
                                C.player2Wins++;
                                isPlayer2scored=true;
                                gameInfoText = "Gracz 2 zdobył punkt! Stuknij, aby przejść dalej.";
                                if(C.currentGame==1) {
                                    gameTimer.stopTimer();
                                    gameTimerSummary=gameTimer.info();
                                    timerResetRequest =true;
                                }
                            } else {
                                C.player2Wins--;
                                isPlayer1scored=true;
                                gameInfoText = "Gracz 2 stracił punkt! Stuknij, aby przejść dalej.";
                                gameTimerSummary="Za szybko!";
                            }
                            islevelended = true;
                            endLevel();
                            isPausedAfterPoint = true;
                        }
                    }
                if(y > screenh / 3 && y < screenh / 3 * 2) {
                    //możliwość zapauzowania dotykając na obszar między przyciskami graczy
                    if(C.PAUSE==true) {
                        C.PAUSE=false;
                    }
                    else C.PAUSE=true;
                }
            }

            //kończenie aktywności po dotknięciu gdziekolwiek podczas podsumowania wyników rozgrywki
            if(C.GAMESTATE==111 || C.GAMESTATE==222|| C.GAMESTATE==333) {
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
    //rysowanie przycisków gracza i kolorowanie ich w zależności czy zdobyto pkt
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
    //rysowanie informacji na przyciskach graczy
    protected void drawGameInfo(String text, Canvas canvas) {
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(50);
        drawCenteredText(text,canvas,paintText,screenh / 3 * 2 + 200);
        // P2 (obrocony)
        paintText.setTextSize(50);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText(text,canvas,paintText,screenh / 3 * 2 + 200);
        canvas.restore();
    }
    protected void drawPlayerWonInfo(Canvas canvas) {
        // Rysowanie informacji o wygranej któregoś z graczy
        Paint paintText = new Paint();
        Random random = new Random();
        //dynamicznie zmieniajace sie kolory
        paintText.setColor(Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
        String winmessage;
        if(C.GAMESTATE==111) winmessage="Player 1 won!";else if(C.GAMESTATE==222) winmessage="Player 2 won!";else winmessage="Draw!";
        // P1
        paintText.setTextSize(100);
        drawCenteredText(winmessage,canvas,paintText,screenh / 3 * 2 + 200);
        // P2 (obrocony)
        paintText.setTextSize(100);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText(winmessage,canvas,paintText,screenh / 3 * 2 + 200);
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
        // Rysowanie animacji z tytułem gry
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        //wartość animationFrame zmienia sie co klatke o 1
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
        // Rysowanie napisu z nazwą graczy na przyciskach
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        drawCenteredText("Gracz 1",canvas,paintText,screenh-100);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText("Gracz 2",canvas,paintText,screenh-100);
        canvas.restore();
    }
    protected void drawPauseLabel(Canvas canvas) {
        // Rysowanie napisu o pauzie
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        drawCenteredText("PAUZA",canvas,paintText,screenh-250);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText("PAUZA",canvas,paintText,screenh-250);
        canvas.restore();
    }
    protected void drawGameTimeBar(Canvas canvas) {
        // Rysowanie paska reprezentującego czas do następnego zestawienia w grach 2i3
        Paint paintText = new Paint();
        paintText.setColor(Color.GRAY);
        // P1
        paintText.setTextSize(80);
        canvas.drawRect(30,screenh/2,50,screenh/2+ROUND_NEXT_QUESTION_DELAY_VALUE-nextQuestionDelay,paintText);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu: połowa wysokości i x=40
        canvas.save();
        canvas.rotate(180, 40, screenh/2);
        canvas.drawRect(30,screenh/2,50,screenh/2+ROUND_NEXT_QUESTION_DELAY_VALUE-nextQuestionDelay,paintText);
        canvas.restore();
    }
    protected void drawLevel_WhiteHit(Canvas canvas) {
        //rysowanie białego prostokąta do gry 1
        Paint white =new Paint();
        white.setColor(Color.WHITE);
        canvas.drawRect(0,screenh/3,screenw,screenh/3*2,white);
    }
    protected void drawLevel_WhiteHitSummary(Canvas canvas) {
        //rysowanie wyników czasu reakcji
        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        // P1
        paintText.setTextSize(80);
        drawCenteredText(gameTimerSummary,canvas,paintText,screenh / 3 * 2 - 250);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText(gameTimerSummary,canvas,paintText,screenh / 3 * 2 - 250);
        canvas.restore();
    }
    protected void drawLevel_ColorMatch(String colorName,int colorInt,Canvas canvas) {
        //rysowanie nazwy koloru o podanym kolorze dla gry 2
        Paint paintText = new Paint();
        paintText.setColor(colorInt);
        // P1
        paintText.setTextSize(80);
        drawCenteredText(colorName,canvas,paintText,screenh / 3 * 2 - 250);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText(colorName,canvas,paintText,screenh / 3 * 2 - 250);
        canvas.restore();
    }
    protected void drawLevel_CountriesCities(String country,String city,Canvas canvas) {
        //rysowanie nazw państw i miast w grze 3
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        // P1
        paintText.setTextSize(80);
        drawCenteredText(country,canvas,paintText,screenh / 3 * 2 - 280);
        drawCenteredText(city,canvas,paintText,screenh / 3 * 2 - 250+100);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText(country,canvas,paintText,screenh / 3 * 2 - 280);
        drawCenteredText(city,canvas,paintText,screenh / 3 * 2 - 250+100);
        canvas.restore();
    }
    protected void drawDebugLabel(Canvas canvas,String s) {
        // Rysowanie napisu do debugowania
        Paint paintText = new Paint();
        paintText.setColor(Color.RED);
        // P1
        paintText.setTextSize(80);
        drawCenteredText(s,canvas,paintText,screenh-200);
        // P2 (obrocony)
        paintText.setTextSize(80);
        // obrocenie tekstu o 180 stopni z wpolrzednymi srodka obrotu xy(srodek ekranu)
        canvas.save();
        canvas.rotate(180, screenw/2, screenh/2);
        drawCenteredText(s,canvas,paintText,screenh-200);
        canvas.restore();
    }
    //funkcja kończąca grę
    protected void endLevel() {
        //reset zmiennych
        canMakePoint=false;
        islevelcreated=false;
        round_start_delay =0;
        white_col_start_delay=0;
        canClick=false;
        roundspassed++;
        nextQuestionDelay =0;

        //zmiana gry co 3 rundy
        if(roundspassed % 3 == 0) {
            nextLevelRequest=true;//flaga reprezentująca zgłoszenie że należy zmienić grę
        }
    }
    protected void resetVariables() {
        //reset zmiennych nieużywane
        roundspassed=0;
        game_start_delay=0;
        round_start_delay=0;
        white_col_start_delay=0;
        nextQuestionDelay =0;
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

    protected void drawCenteredText(String text, Canvas canvas, Paint paint, float y) {
        //rysowanie tekstu wyśrodkowanego względem szerokości ekranu
        // Ustawienie wyśrodkowania dla tekstu
        paint.setTextAlign(Paint.Align.CENTER);
        // Obliczenia dotyczące szerokości tekstu(wpisanie do prostokąta)
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        //rysowanie tekstu względem środka ekranu
        float x = screenw / 2f;
        canvas.drawText(text, x, y + bounds.height() / 2f, paint);
    }
    //pobieranie wysokości i szerokości ekranu
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenh=h;
        screenw=w;

    }
    //pobieranie wysokości i szerokości ekranu
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    //pobieranie wysokości i szerokości ekranu
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    //gettery dla wątków gry i timera by można było je przerwać w OnDestroy
    public Thread getGameThread() {
        return gameThread;
    }
    public Timer getGameTimerThread() {return gameTimer;}
}
