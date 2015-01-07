package dominando.android.ex25_mp3service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileInputStream;

public class Mp3ServiceImpl extends Service implements Mp3Service {

    public static final String EXTRA_ARQUIVO = "arquivo";
    public static final String EXTRA_ACAO = "acao";
    public static final String ACAO_PLAY  = "play";
    public static final String ACAO_PAUSE = "pause";
    public static final String ACAO_STOP  = "stop";

    private MediaPlayer mPlayer;
    private String mArquivo;
    private boolean mPausado;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new Mp3Binder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (ACAO_PLAY.equals(intent.getStringExtra(EXTRA_ACAO))) {
                play(intent.getStringExtra(EXTRA_ARQUIVO));
            } else if (ACAO_PAUSE.equals(intent.getStringExtra(EXTRA_ACAO))) {
                pause();
            } else if (ACAO_STOP.equals(intent.getStringExtra(EXTRA_ACAO))) {
                stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // Implementação da interface Mp3Service
    @Override
    public void play(String musica) {
        if (musica != null && !mPlayer.isPlaying() && !mPausado) {
            try {
                mPlayer.reset();
                FileInputStream fis = new FileInputStream(musica);
                mPlayer.setDataSource(fis.getFD());
                mPlayer.prepare();
                mArquivo = musica;

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        mPausado = false;
        mPlayer.start();
        criarNotificacao();
    }

    @Override
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPausado = true;
            mPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mPlayer.isPlaying() || mPausado) {
            mPausado = false;
            mPlayer.stop();
            mPlayer.reset();
        }
        removerNotificacao();
    }

    @Override
    public String getMusicaAtual() {
        return mArquivo;
    }

    @Override
    public int getTempoTotal() {
        if (mPlayer.isPlaying() || mPausado) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getTempoDecorrido() {
        if (mPlayer.isPlaying() || mPausado) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    private void criarNotificacao(){
        Intent itPlay = new Intent(this, Mp3ServiceImpl.class);
        itPlay.putExtra(EXTRA_ACAO, ACAO_PLAY);
        Intent itPause = new Intent(this, Mp3ServiceImpl.class);
        itPause.putExtra(EXTRA_ACAO, ACAO_PAUSE);
        Intent itStop = new Intent(this, Mp3ServiceImpl.class);
        itStop.putExtra(EXTRA_ACAO, ACAO_STOP);

        PendingIntent pitPlay = PendingIntent.getService(this, 1, itPlay, 0);
        PendingIntent pitPause = PendingIntent.getService(this, 2, itPause, 0);
        PendingIntent pitStop = PendingIntent.getService(this, 3, itStop, 0);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.layout_notificacao);

        views.setOnClickPendingIntent(R.id.imgBtnPlay, pitPlay);
        views.setOnClickPendingIntent(R.id.imgBtnPause, pitPause);
        views.setOnClickPendingIntent(R.id.imgBtnClose, pitStop);
        views.setTextViewText(R.id.txtMusica,
                mArquivo.substring(mArquivo.lastIndexOf(File.separator)+1));

        Notification n = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContent(views)
                .setOngoing(true)
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(1, n);
    }

    private void removerNotificacao() {
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.cancel(1);
    }

}

