package pl.MeHow2k.a2playerreact;

//timer odmierzający czas w grze Biały kolor

public class Timer extends Thread {
    private long startTime;
    private int milis = 0;
    private int seconds = 0;
    private boolean isStopped = true;
    private boolean isStarted = false;

    public Timer() {
    }
    //reset timera
    public void reset() {
        milis = 0;
        seconds = 0;
        isStarted=false;
        isStopped = true;
    }
    //zatrzymanie zliczania czasu
    public void stopTimer() {
        isStopped = true;
    }
    //ustawienie czasu względem którego prowadzimy pomiar i zliczanie czasu
    public void startTimer() {
        isStopped = false;
        if (!isStarted) {
            startTime = System.currentTimeMillis(); //czas startu
            isStarted = true;
        }
    }

    public int getMilis() {
        return milis;
    }

    public int getSeconds() {
        return seconds;
    }
    //zwraca informację o czasie, jaki ubiegł od startTime
    public String info() {
        return String.format("%d,%02d s", seconds, milis);
    }

    @Override
    public void run() {
        while (true) {
            if (!C.PAUSE){
                    if (!isStopped) {
                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime; // oblicz czas, który minął od startu
                        // Konwersja czasu na sekundy i milisekundy
                        seconds = (int) (elapsedTime / 1000);
                        milis = (int) (elapsedTime % 1000);
                    }
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return;
                }
        }
    }
}
