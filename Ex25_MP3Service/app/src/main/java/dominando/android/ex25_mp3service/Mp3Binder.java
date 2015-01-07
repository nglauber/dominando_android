package dominando.android.ex25_mp3service;

import android.os.Binder;

public class Mp3Binder extends Binder {

    private Mp3Service mServico;

    public Mp3Binder(Mp3Service s) {
        mServico = s;
    }

    public Mp3Service getServico() {
        return mServico;
    }
}

