package pl.MeHow2k.a2playerreact;

import android.graphics.Color;

public class C {
    public static int GAMESTATE=0;
    public static int player1Wins=0;
    public static int player2Wins=0;
    public static int requiredWins=9;
    public static int currentGame=2;
    public static boolean PAUSE=false;
    static String[] colorsNames={"CZERWONY","NIEBIESKI","ŻÓŁTY","ZIELONY","SZARY", "BIAŁY" };
    static int[] colorsInts={Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN,Color.GRAY,Color.WHITE};

    static String[] countries = {"Polska", "Niemcy", "Francja", "Włochy", "Hiszpania", "Wielka Brytania", "Rosja",
            "Chiny", "Stany Zjednoczone", "Japonia", "Brazylia", "Indie", "Kanada", "Australia", "Egipt",
            "Meksyk", "Turcja", "Grecja", "Argentyna", "Ukraina","Białoruś","Litwa","Łotwa","Norwegia"};
    static String[] cities = {"Warszawa", "Berlin", "Paryż", "Rzym", "Madryt", "Londyn", "Moskwa",
            "Pekin", "Waszyngton", "Tokio", "Brasília", "Nowe Delhi", "Ottawa", "Canberra", "Kair",
            "Meksyk", "Ankara", "Ateny", "Buenos Aires", "Kijów","Mińsk","Wilno","Ryga","Oslo"};

    public static void resetToDefaults(){
        GAMESTATE=0;
        player1Wins=0;
        player2Wins=0;
        requiredWins=9;
        currentGame=2;
        PAUSE=false;
    }

}
