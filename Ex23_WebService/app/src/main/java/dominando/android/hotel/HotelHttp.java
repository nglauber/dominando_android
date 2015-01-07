package dominando.android.hotel;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HotelHttp {
    private static String BASE_URL =
            "http://192.168.56.1/hotel_service/webservice.php";
    private Context mContext;
    private HotelRepositorio mRepositorio;

    public HotelHttp(Context ctx) {
        mContext = ctx;
        mRepositorio = new HotelRepositorio(mContext);
    }

    public void sincronizar() throws Exception {
        enviarDadosPendentes();
        List<Hotel> hoteis = getHoteis();
        ContentResolver cr = mContext.getContentResolver();
        for (Hotel hotel : hoteis) {
            hotel.status = Hotel.Status.OK;
            mRepositorio.inserirLocal(hotel, cr);
        }
    }

    private void enviarDadosPendentes() throws Exception{
        Cursor cursor = mContext.getContentResolver().query(
                HotelProvider.CONTENT_URI, null,
                HotelSQLHelper.COLUNA_STATUS +" != "+
                        Hotel.Status.OK.ordinal(), null,
                HotelSQLHelper.COLUNA_ID_SERVIDOR +" DESC");

        while (cursor.moveToNext()) {
            Hotel hotel = HotelRepositorio.hotelFromCursor(cursor);

            if (hotel.status == Hotel.Status.INSERIR) {
                inserir(hotel);

            } else if (hotel.status == Hotel.Status.EXCLUIR) {
                excluir(hotel);

            } else if (hotel.status == Hotel.Status.ATUALIZAR) {
                if (hotel.idServidor == 0) {
                    inserir(hotel);
                } else {
                    atualizar(hotel);
                }
            }
        }
    }

    private void inserir(Hotel hotel) {
        try {
            if (enviarHotel("POST", hotel)) {
                hotel.status = Hotel.Status.OK;
                mRepositorio.atualizarLocal(hotel, mContext.getContentResolver());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizar(Hotel hotel) {
        try {
            if (enviarHotel("PUT", hotel)) {
                hotel.status = Hotel.Status.OK;
                mRepositorio.atualizarLocal(hotel, mContext.getContentResolver());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void excluir(Hotel hotel) {
        boolean podeExcluir = true;
        if (hotel.idServidor != 0) {
            try {
                podeExcluir = enviarHotel("DELETE", hotel);
            } catch (Exception e) {
                podeExcluir = false;
                e.printStackTrace();
            }
        }
        if (podeExcluir) {
            mRepositorio.excluirLocal(hotel, mContext.getContentResolver());
        }
    }

    private boolean enviarHotel(String metodoHttp, Hotel hotel) throws Exception {
        boolean sucesso = false;
        boolean doOutput = !"DELETE".equals(metodoHttp);
        String url = BASE_URL;
        if (!doOutput) {
            url += "/"+ hotel.idServidor;
        }
        HttpURLConnection conexao = abrirConexao(url, metodoHttp, doOutput);

        if (doOutput) {
            OutputStream os = conexao.getOutputStream();
            os.write(hotelToJsonBytes(hotel));
            os.flush();
            os.close();
        }
        int responseCode = conexao.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = conexao.getInputStream();
            String s = streamToString(is);
            is.close();

            JSONObject json = new JSONObject(s);
            int idServidor = json.getInt("id");
            hotel.idServidor = idServidor;
            sucesso = true;

        } else {
            throw new RuntimeException("Erro ao realizar operação");
        }
        conexao.disconnect();
        return sucesso;
    }

    private HttpURLConnection abrirConexao(String url,
                                           String metodo, boolean doOutput) throws Exception{
        URL urlCon = new URL(url);
        HttpURLConnection conexao = (HttpURLConnection) urlCon.openConnection();
        conexao.setReadTimeout(15000);
        conexao.setConnectTimeout(15000);
        conexao.setRequestMethod(metodo);
        conexao.setDoInput(true);
        conexao.setDoOutput(doOutput);
        if (doOutput) {
            conexao.addRequestProperty("Content-Type", "application/json");
        }
        conexao.connect();
        return conexao;
    }

    private List<Hotel> getHoteis() throws Exception {
        HttpURLConnection conexao = abrirConexao(BASE_URL, "GET", false);

        List<Hotel> list = new ArrayList<Hotel>();
        if (conexao.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String jsonString = streamToString(conexao.getInputStream());
            JSONArray json = new JSONArray(jsonString);

            for (int i = 0; i < json.length(); i++) {
                JSONObject hotelJSON = json.getJSONObject(i);

                Hotel p = new Hotel(
                        0,
                        hotelJSON.getString("nome"),
                        hotelJSON.getString("endereco"),
                        (float)hotelJSON.getDouble("estrelas"),
                        hotelJSON.getInt("id"),
                        Hotel.Status.OK);
                list.add(p);
            }
        }
        return list;
    }

    private String streamToString(InputStream is) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes)) > 0) {
            baos.write(bytes, 0, lidos);
        }
        return new String(baos.toByteArray());
    }

    private byte[] hotelToJsonBytes(Hotel hotel) {
        try {
            JSONObject jsonPessoa = new JSONObject();
            jsonPessoa.put("id", hotel.idServidor);
            jsonPessoa.put("nome", hotel.nome);
            jsonPessoa.put("endereco", hotel.endereco);
            jsonPessoa.put("estrelas", hotel.estrelas);
            String json = jsonPessoa.toString();
            return json.getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

