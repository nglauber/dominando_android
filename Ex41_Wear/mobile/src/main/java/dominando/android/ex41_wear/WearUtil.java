package dominando.android.ex41_wear;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class WearUtil implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private String mNodeId;
    private Uri mUriDados;
    private ComunicacaoComRelogio mOuvinte;
    private AsyncTask<Void, Void, PutDataMapRequest> mTask;

    public WearUtil(Context context, ComunicacaoComRelogio ouvinte) {
        mContext = context;
        mOuvinte = ouvinte;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void conectar() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void desconectar(){
        if (mGoogleApiClient.isConnected()) {
            enviarMensagemSair();
            apagarDados();
            mGoogleApiClient.disconnect();
        }
    }

    public void atualizarDados(final int position) {
        if (mTask != null) {
            mTask.cancel(true);
        }

        if (!mGoogleApiClient.isConnected()) return; // Se não estiver conectado, saia...

        mTask = new AsyncTask<Void, Void, PutDataMapRequest>() {
            @Override
            protected PutDataMapRequest doInBackground(Void... voids) {
                PutDataMapRequest putDataMapRequest =
                        PutDataMapRequest.create(Constantes.CAMINHO_DADOS);
                mUriDados = putDataMapRequest.getUri();
                DataMap map = putDataMapRequest.getDataMap();
                map.putInt(Constantes.EXTRA_POSICAO_ATUAL, position);
                map.putInt(Constantes.EXTRA_TOTAL_IMAGENS, Constantes.TOTAL_IMAGENS);

                Bitmap miniatura = ImagemUtil.imagem(
                        mContext, "foto" + (position + 1) + ".jpg", 160, 160);

                if (miniatura != null) {
                    final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    miniatura.compress(Bitmap.CompressFormat.JPEG, 80, byteStream);
                    Asset asset = Asset.createFromBytes(byteStream.toByteArray());
                    map.putAsset(Constantes.EXTRA_IMAGEM, asset);
                }
                return putDataMapRequest;
            }

            @Override
            protected void onPostExecute(PutDataMapRequest dataMapRequest) {
                super.onPostExecute(dataMapRequest);
                Wearable.DataApi.putDataItem(
                        mGoogleApiClient,
                        dataMapRequest.asPutDataRequest());
            }
        }.execute();
    }

    @Override
    public void onConnected(Bundle bundle) {
        descobrirNoDestino();
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        if (mOuvinte != null){
            mOuvinte.aoConectar();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }











    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        final byte navegacao = messageEvent.getData()[0];
        if (Constantes.MSG_NAVEGACAO.equals(messageEvent.getPath())
                || Constantes.MSG_POSICAO.equals(messageEvent.getPath())) {
            if (mOuvinte != null){
                mOuvinte.aoMudarDePagina(navegacao);
            }
        }
    }

    private void apagarDados(){
        if (mUriDados!= null) {
            Wearable.DataApi.deleteDataItems(mGoogleApiClient, mUriDados);
        }
        mNodeId = null;
    }

    private void enviarMensagemSair() {
        if (mNodeId != null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNodeId, Constantes.MSG_SAIR, null);
        }
    }

    private void descobrirNoDestino() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        List<Node> nodes = getConnectedNodesResult.getNodes();
                        if (nodes != null && nodes.size() > 0) {
                            Node node = nodes.get(0);
                            mNodeId = node.getId();
                        }
                    }
                });
    }

    public interface ComunicacaoComRelogio {
        void aoConectar();
        void aoMudarDePagina(int pagina);
    }
}
