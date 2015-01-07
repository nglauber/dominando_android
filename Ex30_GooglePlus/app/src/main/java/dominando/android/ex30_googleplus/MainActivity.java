package dominando.android.ex30_googleplus;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>,
        View.OnClickListener {

    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private PessoaAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private List<Pessoa> mPessoas;
    private String mProxPagina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mListView = (ListView)findViewById(R.id.listView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button
                && !mGoogleApiClient.isConnected()
                && !mGoogleApiClient.isConnecting()) {

            mSignInClicked = true;
            login();

        } else if (view.getId() == R.id.sign_out_button) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();

                mAdapter.clear();
                mAdapter.notifyDataSetChanged();

                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "Conectado!", Toast.LENGTH_LONG).show();

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);
        mProgressBar.setVisibility(View.VISIBLE);
        if (mPessoas != null) mPessoas.clear();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                login();
            }
        }
    }

    private void login() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);

            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        mProgressBar.setVisibility(View.GONE);

        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            /*
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                        String accountID = GoogleAuthUtil.getAccountId(MainActivity.this, accountName);
                        Log.d("NGVL", "ACCOUNT_ID:"+ accountID);
                        // Envie o ID para o servidor...
                    } catch (GoogleAuthException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
            */

            mProxPagina = loadPeopleResult.getNextPageToken();

            if (mPessoas == null) {
                mPessoas = new ArrayList<Pessoa>();
                mAdapter = new PessoaAdapter(this, mPessoas);
                mListView.setAdapter(mAdapter);
                mListView.setOnScrollListener(new ScrollPaginado(5) {
                    @Override
                    public void aoPrecisarCarregarMais() {
                        if (mProxPagina != null) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            Plus.PeopleApi.loadVisible(mGoogleApiClient, mProxPagina)
                                    .setResultCallback(MainActivity.this);
                        }
                    }
                });
            }

            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    mPessoas.add(new Pessoa(
                            personBuffer.get(i).getDisplayName(),
                            personBuffer.get(i).getImage().getUrl()));
                }
                mAdapter.notifyDataSetChanged();

            } finally {
                personBuffer.close();
            }
        } else {
            Toast.makeText(this,
                    "Erro ao carregar contatos: "+ loadPeopleResult.getStatus(),
                    Toast.LENGTH_LONG).show();
        }
    }
}

