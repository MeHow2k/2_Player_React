package pl.MeHow2k.a2playerreact;

import android.util.Log;

public class Timer extends Thread {
    private long startTime;
    private int milis = 0;
    private int seconds = 0;
    private boolean isStopped = true;
    private boolean isStarted = false;

    public Timer() {
    }

    public void reset() {
        milis = 0;
        seconds = 0;
        isStarted=false;
        isStopped = true;
    }

    public void stopTimer() {
        isStopped = true;
    }

    public void startTimer() {
        isStopped = false;
        if (!isStarted) {
            startTime = System.currentTimeMillis(); // Zapisz czas startu
            isStarted = true;
        }
    }

    public int getMilis() {
        return milis;
    }

    public int getSeconds() {
        return seconds;
    }

    public String info() {
        return String.format("%d:%02d s", seconds, milis);
    }

    @Override
    public void run() {
        while (true) {
            if (!C.PAUSE){
                    if (!isStopped) {
                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime; // Oblicz czas, który minął od startu

                        // Konwersja czasu na sekundy i milisekundy
                        seconds = (int) (elapsedTime / 1000);
                        milis = (int) (elapsedTime % 1000);

                    }
                }
            else {

            }
                //Log.i("timer",info());
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return;
                }
        }
    }
}
