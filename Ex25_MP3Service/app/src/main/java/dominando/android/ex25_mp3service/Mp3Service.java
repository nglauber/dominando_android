package dominando.android.ex25_mp3service;

public interface Mp3Service {
    void play(String arquivo);
    void pause();
    void stop();
    String getMusicaAtual();
    int getTempoTotal();
    int getTempoDecorrido();
}

