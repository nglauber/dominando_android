package dominando.android.ex36_multimidia;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import java.util.ArrayList;
import java.util.Stack;

public class GerenciadorDeSom {

    private static final int QTDE_MAX_DE_SONS = 2;
    private static GerenciadorDeSom sInstancia;

    private SoundPool mSoundPool;
    private AudioManager mAudioManager;
    private ArrayList<Integer> mListaIdsSons;
    private Stack<Integer> mSonsEmExecucao;

    private Context mContext;

    private GerenciadorDeSom(Context ct) {
        mContext = ct;
        mListaIdsSons = new ArrayList<Integer>();
        mSonsEmExecucao = new Stack<Integer>();
        mSoundPool = new SoundPool(QTDE_MAX_DE_SONS, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int id, int status) {
                if (status == 0) {
                    mListaIdsSons.add(id);
                }
            }
        });
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public static GerenciadorDeSom getInstance(Context ctx) {
        if (sInstancia == null) {
            sInstancia = new GerenciadorDeSom(ctx);
        }
        sInstancia.mContext = ctx;
        return sInstancia;
    }

    public void adicionarSom(int idSom) {
        mSoundPool.load(mContext, idSom, 1);
    }

    public void tocarSom(int index) {
        if (index >= mListaIdsSons.size()) return;

        float volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int prioridade = 1;
        int repeticoes = 0;
        float rate = 1.0f;

        int playId = mSoundPool.play(
                mListaIdsSons.get(index), volume, volume, prioridade, repeticoes, rate);
        mSonsEmExecucao.push(playId);
    }

    public void pararTodosOsSons() {
        while (mSonsEmExecucao.size() > 0) {
            mSoundPool.stop(mSonsEmExecucao.pop());
        }
    }

    public void liberarRecursos() {
        mSoundPool.release();
        mSoundPool = null;
        mListaIdsSons.clear();
        mSonsEmExecucao.clear();
        mAudioManager.unloadSoundEffects();
    }
}
