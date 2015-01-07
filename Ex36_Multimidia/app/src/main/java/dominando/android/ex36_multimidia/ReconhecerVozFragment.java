package dominando.android.ex36_multimidia;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ReconhecerVozFragment extends Fragment implements View.OnClickListener {

    Intent mIntentVoz;
    SpeechRecognizer mSpeechRecognizer;

    ImageButton mBtnVoz;
    CheckBox mChkIntent;
    ProgressBar mPgrVoz;
    ListView mLstResultados;

    boolean mReconhecendo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_reconhecer_voz, container, false);

        mLstResultados = (ListView) layout.findViewById(R.id.listView);
        mChkIntent = (CheckBox) layout.findViewById(R.id.chkIntent);
        mPgrVoz = (ProgressBar) layout.findViewById(R.id.pgrVoz);
        mBtnVoz = (ImageButton) layout.findViewById(R.id.btnVoz);
        mBtnVoz.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PackageManager pm = getActivity().getPackageManager();
        mIntentVoz = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        List<ResolveInfo> activities = pm.queryIntentActivities(mIntentVoz, 0);
        if (activities.size() == 0) {
            mBtnVoz.setEnabled(false);
            Toast.makeText(getActivity(), "Aparelho não suporta comando de voz.",
                    Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
            mSpeechRecognizer.setRecognitionListener(mListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    public void onClick(View view) {
        if (mChkIntent.isChecked()) {

            mIntentVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mIntentVoz.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fala alguma coisa!");
            startActivityForResult(mIntentVoz, Util.REQUESTCODE_AUDIO);

        } else {
            if (mReconhecendo){
                mPgrVoz.setVisibility(View.INVISIBLE);
                mPgrVoz.setIndeterminate(false);
                mSpeechRecognizer.stopListening();
                mReconhecendo = false;

            } else {

                mPgrVoz.setVisibility(View.VISIBLE);
                mPgrVoz.setIndeterminate(true);

                mIntentVoz.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

                mSpeechRecognizer.startListening(mIntentVoz);
                mReconhecendo = true;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Util.REQUESTCODE_AUDIO && resultCode == Activity.RESULT_OK) {
            ArrayList<String> resultados = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            atualizarLista(resultados);
        }
    }

    private void atualizarLista(ArrayList<String> resultados){
        if (resultados != null) {
            mLstResultados.setAdapter(new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_list_item_1, resultados));
        }
        mPgrVoz.setVisibility(View.INVISIBLE);
        mReconhecendo = false;
    }

    private RecognitionListener mListener = new RecognitionListener() {

        @Override
        public void onBeginningOfSpeech() {
            mPgrVoz.setIndeterminate(false);
            mPgrVoz.setMax(10);
        }

        @Override
        public void onEndOfSpeech() {
            mPgrVoz.setIndeterminate(true);
        }

        @Override
        public void onRmsChanged(float v) {
            mPgrVoz.setProgress((int) v);
        }

        @Override
        public void onError(int i) {
            Toast.makeText(getActivity(), "Problemas no comando de voz. Erro: "+ i,
                    Toast.LENGTH_SHORT).show();
            mPgrVoz.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> resultados = bundle.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);

            atualizarLista(resultados);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };
}
