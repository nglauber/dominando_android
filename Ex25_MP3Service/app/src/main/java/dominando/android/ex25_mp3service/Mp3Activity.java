package dominando.android.ex25_mp3service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Mp3Activity extends Activity
        implements ServiceConnection, AdapterView.OnItemClickListener {

    private Mp3Service mMP3Service;
    private ProgressBar mPrgDuracao;
    private TextView mTxtMusica;
    private TextView mTxtDuracao;
    private String mMusica;

    private Handler mHandler = new Handler();
    private Thread mThreadProgresso = new Thread() {
        public void run() {
            atualizarTela();
            if (mMP3Service.getTempoTotal() > mMP3Service.getTempoDecorrido()) {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3);
        mPrgDuracao = (ProgressBar) findViewById(R.id.progressBar);
        mTxtMusica = (TextView) findViewById(R.id.txtMusica);
        mTxtDuracao = (TextView) findViewById(R.id.txtTempo);

        String[] colunas = new String[]{
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID
        };
        int[] componentes = new int[]{
                android.R.id.text1,
                android.R.id.text2
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                colunas,
                MediaStore.Audio.AudioColumns.IS_MUSIC +" = 1",
                null,
                null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                colunas,
                componentes,
                0);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent it = new Intent(this, Mp3ServiceImpl.class);
        startService(it);
        bindService(it, this, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
        mHandler.removeCallbacks(mThreadProgresso);
    }

    public void btnPlayClick(View v) {
        mHandler.removeCallbacks(mThreadProgresso);
        if (mMusica != null) {
            mMP3Service.play(mMusica);
            mHandler.post(mThreadProgresso);
        }
    }

    public void btnPauseClick(View v) {
        mMP3Service.pause();
        mHandler.removeCallbacks(mThreadProgresso);
    }

    public void btnStopClick(View v) {
        mMP3Service.stop();
        mHandler.removeCallbacks(mThreadProgresso);
        mPrgDuracao.setProgress(0);
        mTxtDuracao.setText(DateUtils.formatElapsedTime(0));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMP3Service = ((Mp3Binder) service).getServico();
        mHandler.post(mThreadProgresso);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMP3Service = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor)adapterView.getItemAtPosition(i);
        String musica = cursor.getString(
                cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
        mHandler.removeCallbacks(mThreadProgresso);
        if (musica != null) {
            mMP3Service.stop();
            mMP3Service.play(musica);
            mHandler.post(mThreadProgresso);
        }
    }

    private void atualizarTela() {
        mMusica = mMP3Service.getMusicaAtual();
        mTxtMusica.setText(mMusica);
        mPrgDuracao.setMax(mMP3Service.getTempoTotal());
        mPrgDuracao.setProgress(mMP3Service.getTempoDecorrido());
        mTxtDuracao.setText(
                DateUtils.formatElapsedTime(mMP3Service.getTempoDecorrido() / 1000));
    }
}

