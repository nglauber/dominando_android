package dominando.android.ex42_lollipop;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LivroHttp {

    public static final String LIVROS_URL_JSON =
            "https://raw.githubusercontent.com/nglauber/"+
                    "dominando_android/master/livros_novatec.json";

    private static HttpURLConnection connectar(String urlArquivo) throws IOException {
        final int SEGUNDOS = 1000;
        URL url = new URL(urlArquivo);
        HttpURLConnection conexao = (HttpURLConnection)url.openConnection();
        conexao.setReadTimeout(10 * SEGUNDOS);
        conexao.setConnectTimeout(15 * SEGUNDOS);
        conexao.setRequestMethod("GET");
        conexao.setDoInput(true);
        conexao.setDoOutput(false);
        conexao.connect();
        return conexao;
    }

    public static boolean temConexao(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static List<Livro> carregarLivrosJson() {
        try {
            HttpURLConnection conexao = connectar(LIVROS_URL_JSON);

            int resposta = conexao.getResponseCode();
            if (resposta ==  HttpURLConnection.HTTP_OK) {
                InputStream is = conexao.getInputStream();
                JSONObject json = new JSONObject(bytesParaString(is));
                return lerJsonLivros(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Livro> lerJsonLivros(JSONObject json) throws JSONException {
        List<Livro> listaDeLivros = new ArrayList<Livro>();

        String categoriaAtual;

        JSONArray jsonNovatec = json.getJSONArray("novatec");
        for (int i = 0; i < jsonNovatec.length(); i++) {
            JSONObject jsonCategoria = jsonNovatec.getJSONObject(i);
            categoriaAtual = jsonCategoria.getString("categoria");

            JSONArray jsonLivros = jsonCategoria.getJSONArray("livros");
            for (int j = 0; j < jsonLivros.length(); j++) {
                JSONObject jsonLivro = jsonLivros.getJSONObject(j);

                Livro livro = new Livro(
                        jsonLivro.getString("titulo"),
                        categoriaAtual,
                        jsonLivro.getString("autor"),
                        jsonLivro.getInt("ano"),
                        jsonLivro.getInt("paginas"),
                        jsonLivro.getString("capa")
                );

                listaDeLivros.add(livro);
            }
        }

        return listaDeLivros;
    }

    private static String bytesParaString(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        // O bufferzao vai armazenar todos os bytes lidos
        ByteArrayOutputStream bufferzao = new ByteArrayOutputStream();
        // precisamos saber quantos bytes foram lidos
        int bytesLidos;
        // Vamos lendo de 1KB por vez...
        while ((bytesLidos = is.read(buffer)) != -1) {
            // copiando a quantidade de bytes lidos do buffer para o bufferzão
            bufferzao.write(buffer, 0, bytesLidos);
        }
        return new String(bufferzao.toByteArray(), "UTF-8");
    }


    public static final String LIVROS_URL_XML =
            "https://raw.githubusercontent.com/nglauber/" +
                    "dominando_android/master/livros_novatec.xml";

    public static List<Livro> lerXmlLivros(InputStream is) throws Exception {
        List<Livro> listLivros = new ArrayList<Livro>();
        Livro livro = null;
        String tagAtual = null;
        String categoria = null;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, "UTF-8");

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                tagAtual = xpp.getName();
                if ("livro".equals(tagAtual)) {
                    livro = new Livro();
                    livro.categoria = categoria;
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("livro".equals(xpp.getName())) {
                    listLivros.add(livro);
                }
            } else if (eventType == XmlPullParser.TEXT && !xpp.isWhitespace()) {
                String text = xpp.getText();
                if ("titulo".equals(tagAtual)) {
                    livro.titulo = text;
                } else if ("paginas".equals(tagAtual)) {
                    livro.paginas = Integer.parseInt(text);
                } else if ("capa".equals(tagAtual)) {
                    livro.capa = text;
                } else if ("autor".equals(tagAtual)) {
                    livro.autor = text;
                } else if ("ano".equals(tagAtual)) {
                    livro.ano = Integer.parseInt(text);
                } else if ("categoria".equals(tagAtual)) {
                    categoria = text;
                }
            }
            eventType = xpp.next();
        }
        return  listLivros;
    }

    public static List<Livro> carregarLivrosXml() {
        try {
            HttpURLConnection conexao = connectar(LIVROS_URL_XML);

            int resposta = conexao.getResponseCode();
            if (resposta ==  HttpURLConnection.HTTP_OK) {
                InputStream is = conexao.getInputStream();

                return lerXmlLivros(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

