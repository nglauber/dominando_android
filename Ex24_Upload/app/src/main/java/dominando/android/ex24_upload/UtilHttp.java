package dominando.android.ex24_upload;

import android.util.Log;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UtilHttp {

    public static void enviarFoto(String titulo, String path) throws Exception {

        String caminhoDoArquivoNoDispositivo = path;

        final String urlDoServidor = "http://192.168.2.101/upload_foto/upload_file.php";
        final String fimDeLinha = "\r\n";
        final String menosMenos = "--";
        final String delimitador = "*****";

        FileInputStream fileInputStream = null;
        DataOutputStream outputStream = null;
        HttpURLConnection conexao = null;

        try {
            URL url = new URL(urlDoServidor);
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setDoInput(true);
            conexao.setDoOutput(true);
            conexao.setUseCaches(false);
            conexao.setRequestMethod("POST");

            // Adicionando cabeçalhos
            conexao.setRequestProperty("Connection", "Keep-Alive");
            conexao.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + delimitador);
            conexao.connect();

            outputStream = new DataOutputStream(conexao.getOutputStream());

            // Adicionando campo titulo
            outputStream.writeBytes(menosMenos + delimitador + fimDeLinha);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"titulo\"");
            outputStream.writeBytes(fimDeLinha);
            outputStream.writeBytes(fimDeLinha);
            outputStream.writeBytes(titulo);
            outputStream.writeBytes(fimDeLinha);

            // Adicionando arquivo
            outputStream.writeBytes(menosMenos + delimitador + fimDeLinha);
            outputStream.writeBytes("Content-Disposition: form-data; "+
                    "name=\"arquivo\";filename=\""+
                    caminhoDoArquivoNoDispositivo + "\"" + fimDeLinha);
            outputStream.writeBytes(fimDeLinha);

            // Stream para ler o arquivo
            fileInputStream = new FileInputStream(
                    new File(caminhoDoArquivoNoDispositivo));

            byte[] buffer;
            int bytesRead, bytesAvailable, bufferSize;
            int maxBufferSize = 1 * 1024 * 1024; // 1MB

            // Preparando para escrever arquivo
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Lendo arquivo e escrevendo na conexão
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(fimDeLinha);
            outputStream.writeBytes(menosMenos + delimitador + menosMenos + fimDeLinha);

            int serverResponseCode = conexao.getResponseCode();
            String serverResponseMessage = conexao.getResponseMessage();

            if (serverResponseCode != HttpURLConnection.HTTP_OK){
                Log.d("NGVL", serverResponseCode + " = " + serverResponseMessage);
                throw new RuntimeException(
                        "Error "+ serverResponseCode +": "+ serverResponseMessage);
            }

        } finally {
            if (fileInputStream != null) fileInputStream.close();
            if (outputStream != null){
                outputStream.flush();
                outputStream.close();
            }
        }
    }
}

