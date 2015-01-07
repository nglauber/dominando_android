package dominando.android.ex31_mapas;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener,
        LocationClient.OnAddGeofencesResultListener {

    static final int LOADER_ENDERECO = 1;
    static final String EXTRA_ORIG = "orig";
    static final String EXTRA_DEST = "dest";
    static final int LOADER_ROTA = 2;
    static final String EXTRA_ROTA = "rota";

    EditText mEdtLocal;
    ImageButton mBtnBuscar;
    MessageDialogFragment mDialogEnderecos;
    TextView mTxtProgresso;
    LinearLayout mLayoutProgresso;

    LoaderManager mLoaderManager;
    LatLng mDestino;

    GoogleMap mGoogleMap;
    LocationClient mLocationClient;
    LatLng mOrigem;

    ArrayList<LatLng> mRota;

    Marker mMarkerLocalAtual;

    GeofenceInfo mGeofenceInfo;
    GeofenceDB mGeofenceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment fragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = fragment.getMap();

        mLocationClient = new LocationClient(this, this, this);

        mEdtLocal = (EditText) findViewById(R.id.edtLocal);
        mBtnBuscar = (ImageButton) findViewById(R.id.imgBtnBuscar);
        mBtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnBuscar.setEnabled(false);
                buscarEndereco();
            }
        });
        mLoaderManager = getSupportLoaderManager();
        mTxtProgresso = (TextView) findViewById(R.id.txtProgresso);
        mLayoutProgresso = (LinearLayout) findViewById(R.id.llProgresso);

        mGoogleMap.setOnMapLongClickListener(this);
        mGeofenceDB = new GeofenceDB(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PlayServicesUtils.REQUEST_CODE_ERRO_PLAY_SERVICES
                && resultCode == RESULT_OK) {
            mLocationClient.connect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_ORIG, mOrigem);
        outState.putParcelable(EXTRA_DEST, mDestino);
        outState.putParcelableArrayList(EXTRA_ROTA, mRota);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mOrigem = savedInstanceState.getParcelable(EXTRA_ORIG);
            mDestino = savedInstanceState.getParcelable(EXTRA_DEST);
            mRota = savedInstanceState.getParcelableArrayList(EXTRA_ROTA);
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        Log.d("Dominando", "onConnected");
        Location location = mLocationClient.getLastLocation();
        if (location != null) {
            mOrigem = new LatLng(location.getLatitude(), location.getLongitude());
            atualizarMapa();
        }

        if (estaCarregando(LOADER_ENDERECO) && mDestino == null) {
            mLoaderManager.initLoader(LOADER_ENDERECO, null, mBuscaLocalCallback);
            exibirProgresso("Buscando endereço...");

        } else if (estaCarregando(LOADER_ROTA) && mRota == null) {
            mLoaderManager.initLoader(LOADER_ROTA, null, mRotaCallback);
            exibirProgresso("Carregando rota...");
        }


    }

    @Override
    public void onDisconnected() {
        Log.d("Dominando", "onDisconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this,
                        PlayServicesUtils.REQUEST_CODE_ERRO_PLAY_SERVICES);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            PlayServicesUtils.exibirMensagemDeErro(this, connectionResult.getErrorCode());
        }
    }

    private void atualizarMapa() {
        mGoogleMap.clear();

        if (mOrigem != null) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(mOrigem)
                    .title("Origem"));
        }

        if (mDestino != null) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(mDestino)
                    .title("Destino"));
        }

        if (mOrigem != null) {
            if (mDestino != null) {
                LatLngBounds area = new LatLngBounds.Builder()
                        .include(mOrigem)
                        .include(mDestino)
                        .build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(area, 50));
            } else {
                mGoogleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(mOrigem, 17.0f));
            }
        }

        if (mRota != null && mRota.size() > 0) {
            BitmapDescriptor icon = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_launcher);

            mMarkerLocalAtual = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mDestino)
                    .title("Destino")
                    .icon(icon)
                    .position(mOrigem));

            iniciarDeteccaoDeLocal();


            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(mRota)
                    .width(5)
                    .color(Color.RED)
                    .visible(true);

            mGoogleMap.addPolyline(polylineOptions);
        }

        mGeofenceInfo = mGeofenceDB.getGeofence("1");
        if (mGeofenceInfo != null){
            LatLng posicao = new LatLng(
                    mGeofenceInfo.mLatitude, mGeofenceInfo.mLongitude);

            mGoogleMap.addCircle(new CircleOptions()
                    .strokeWidth(2)
                    .fillColor(0x990000FF)
                    .center(posicao)
                    .radius(mGeofenceInfo.mRadius));
        }

    }

    private void buscarEndereco() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtLocal.getWindowToken(), 0);

        mLoaderManager.restartLoader(LOADER_ENDERECO, null, mBuscaLocalCallback);
        exibirProgresso("Procurando endereço...");
    }

    private void exibirProgresso(String texto) {
        mTxtProgresso.setText(texto);
        mLayoutProgresso.setVisibility(View.VISIBLE);
    }

    private void ocultarProgresso() {
        mLayoutProgresso.setVisibility(View.GONE);
    }

    private void exibirListaEnderecos(
            final List<Address> enderecosEncontrados) {

        ocultarProgresso();
        mBtnBuscar.setEnabled(true);

        if (enderecosEncontrados != null && enderecosEncontrados.size() > 0) {
            String[] descricaoDosEnderecos = new String[enderecosEncontrados.size()];
            for (int i = 0; i < descricaoDosEnderecos.length; i++) {
                Address endereco = enderecosEncontrados.get(i);

                StringBuffer rua = new StringBuffer();
                for (int j = 0; j < endereco.getMaxAddressLineIndex(); j++) {
                    if (rua.length() > 0) {
                        rua.append('\n');
                    }
                    rua.append(endereco.getAddressLine(j));
                }

                String pais = endereco.getCountryName();

                String descricaoEndereco = String.format(
                        "%s, %s", rua, pais);

                descricaoDosEnderecos[i] = descricaoEndereco;
            }

            DialogInterface.OnClickListener selecionarEnderecoClick =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Address enderecoSelecionado = enderecosEncontrados.get(which);
                            mDestino = new LatLng(
                                    enderecoSelecionado.getLatitude(),
                                    enderecoSelecionado.getLongitude());
                            atualizarMapa();
                            carregarRota();
                        }
                    };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecione o destino")
                    .setItems(descricaoDosEnderecos, selecionarEnderecoClick);

            mDialogEnderecos = new MessageDialogFragment();
            mDialogEnderecos.setDialog(builder.create());
            mDialogEnderecos.show(getSupportFragmentManager(), "DIALOG_ENDERECO_DESTINO");
        }
    }

    private boolean estaCarregando(int id) {
        Loader<?> loader = mLoaderManager.getLoader(id);
        if (loader != null && loader.isStarted()) {
            return true;
        }
        return false;
    }

    LoaderManager.LoaderCallbacks<List<Address>> mBuscaLocalCallback =
            new LoaderManager.LoaderCallbacks<List<Address>>() {
                @Override
                public Loader<List<Address>> onCreateLoader(int i, Bundle bundle) {
                    return new BuscarLocalTask(MainActivity.this, mEdtLocal.getText().toString());
                }

                @Override
                public void onLoadFinished(Loader<List<Address>> listLoader,
                                           final List<Address> addresses) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            exibirListaEnderecos(addresses);
                        }
                    });
                }

                @Override
                public void onLoaderReset(Loader<List<Address>> listLoader) {
                }
            };

    private void carregarRota() {
        mRota = null;
        mLoaderManager.initLoader(LOADER_ROTA, null, mRotaCallback);
        exibirProgresso("Carregando rota...");
    }

    LoaderManager.LoaderCallbacks<List<LatLng>> mRotaCallback =
            new LoaderManager.LoaderCallbacks<List<LatLng>>() {

                @Override
                public Loader<List<LatLng>> onCreateLoader(int i, Bundle bundle) {
                    return new RotaTask(MainActivity.this, mOrigem, mDestino);
                }

                @Override
                public void onLoadFinished(final Loader<List<LatLng>> listLoader,
                                           final List<LatLng> latLngs) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mRota = new ArrayList<LatLng>(latLngs);
                            atualizarMapa();
                            ocultarProgresso();
                        }
                    });
                }

                @Override
                public void onLoaderReset(Loader<List<LatLng>> listLoader) {
                }
            };

    private void iniciarDeteccaoDeLocal() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        mLocationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mMarkerLocalAtual.setPosition(
                new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mLocationClient.isConnected()) {
            PendingIntent pit = PendingIntent.getBroadcast(
                    this,
                    0,
                    new Intent(this, GeofenceReceiver.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mGeofenceInfo = new GeofenceInfo(
                    "1",
                    latLng.latitude, latLng.longitude,
                    200, // metros
                    Geofence.NEVER_EXPIRE,
                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

            List<Geofence> geofences = new ArrayList<Geofence>();
            geofences.add(mGeofenceInfo.getGeofence());
            mLocationClient.addGeofences(geofences, pit, this);
        }
    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {
        if (LocationStatusCodes.SUCCESS == i) {
            mGeofenceDB.salvarGeofence("1", mGeofenceInfo);
            mGeofenceInfo = null;
            atualizarMapa();
        }
    }
}