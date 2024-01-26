package pl.MeHow2k.a2playerreact;

import android.graphics.Color;

import java.util.Random;

//plik ze zmiennymi globalnymi
public class C {
    public static int Localization=0; //0- ENG, 1- PL
    public static int GAMESTATE=1;//stan gry: 1-rozgrywka 111-wygrana 1 gracza 222-wygrana 2 gracza 333-remis
    public static int player1Wins=0;//liczba punktów gracza1
    public static int player2Wins=0;//liczba punktów gracza2
    public static int requiredRounds =9;//liczba rund wymagana do skończenia gry
    public static int currentGame=1;//aktualna gra
    public static boolean PAUSE =false;//pauza
    public static boolean isGame1On=true,isGame2On=true,isGame3On=true;//wartości dot. wyłączenia/włączenia gry
    //tablice używane do wyświetlania w trakcie gry
    static String[] colorsNames={"CZERWONY","NIEBIESKI","ŻÓŁTY","ZIELONY","SZARY", "BIAŁY" };
    static int[] colorsInts={Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN,Color.GRAY,Color.WHITE};

    static String[] countries = {"Polska", "Niemcy", "Francja", "Włochy", "Hiszpania", "Wielka Brytania", "Rosja",
            "Chiny", "Stany Zjednoczone", "Japonia", "Brazylia", "Indie", "Kanada", "Australia", "Egipt",
            "Meksyk", "Turcja", "Grecja", "Argentyna", "Ukraina","Białoruś","Litwa","Łotwa","Norwegia"};
    static String[] cities = {"Warszawa", "Berlin", "Paryż", "Rzym", "Madryt", "Londyn", "Moskwa",
            "Pekin", "Waszyngton", "Tokio", "Brasília", "Nowe Delhi", "Ottawa", "Canberra", "Kair",
            "Meksyk", "Ankara", "Ateny", "Buenos Aires", "Kijów","Mińsk","Wilno","Ryga","Oslo"};
    //resetowanie zmiennych do wartości domyślnych
    public static void resetToDefaults(){
        GAMESTATE=1;
        player1Wins=0;
        player2Wins=0;
        Random random= new Random();
        //losowanie początkowej gry
        currentGame=random.nextInt(2)+1;
        PAUSE =false;
    }

}
