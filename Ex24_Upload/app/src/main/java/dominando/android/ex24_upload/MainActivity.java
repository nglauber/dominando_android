package dominando.android.ex24_upload;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int REQUEST_FOTO = 1;
    EditText mEdtTexto;
    TextView mTxtArquivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdtTexto = (EditText) findViewById(R.id.edtTexto);
        mTxtArquivo = (TextView) findViewById(R.id.txtArquivo);

        findViewById(R.id.btnSelFoto).setOnClickListener(this);
        findViewById(R.id.btnEnviarFoto).setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_FOTO) {
            Uri selectedImageUri = data.getData();
            mTxtArquivo.setText(getPath(selectedImageUri));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSelFoto:
                selecionarFoto();
                break;

            case R.id.btnEnviarFoto:
                enviarFoto();
                break;
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(
                uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(
                MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_FOTO);
    }

    private void enviarFoto() {
        new UploadArquivoTask().execute();
    }

    class UploadArquivoTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean sucesso = false;
            try {
                UtilHttp.enviarFoto(
                        mEdtTexto.getText().toString(),
                        mTxtArquivo.getText().toString()
                );
                sucesso = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return sucesso;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            super.onPostExecute(sucesso);
            int mensagem = sucesso ?
                    R.string.msg_sucesso :
                    R.string.msg_falha ;

            Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_SHORT).show();
        }
    }
}

