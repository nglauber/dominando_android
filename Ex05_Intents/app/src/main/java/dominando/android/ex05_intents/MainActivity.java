package dominando.android.ex05_intents;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {
    private static final String[] OPCOES = {
            "Browser",
            "Realizando uma chamada",
            "Mapa",
            "Tocar música",
            "SMS",
            "Compartilhar",
            "Minha ação 1",
            "Minha ação 2",
            "Sair"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        OPCOES);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        Uri uri = null;
        Intent intent = null;

        switch (position) {
            // Abrindo uma URL
            case 0:
                uri = Uri.parse("http://www.nglauber.com.br");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            // Realiza uma chamada
            case 1:
                uri = Uri.parse("tel:99887766");
                intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;
            // Pesquisa uma posição do mapa
            // !Seu AVD deve estar usando Google APIs!
            case 2:
                uri = Uri.parse("geo:0,0?q=Rua+Amelia,Recife");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            // Executa uma música do SDcard
            case 3:
                uri = Uri.parse("file:///mnt/sdcard/musica.mp3");
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri,"audio/mp3");
                startActivity(intent);
                break;
            // Abrindo o editor de SMS
            case 4:
                uri = Uri.parse("sms:12345");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra("sms_body", "Corpo do SMS");
                startActivity(intent);
                break;
            // Compartilhar
            case 5:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Compartilhando via Intent.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            // Ação customizada 1
            case 6:
                intent = new Intent("dominando.android.ACAO_CUSTOMIZADA");
                startActivity(intent);
                break;
            // Ação customizada 2
            case 7:
                uri = Uri.parse("produto://Notebook/Slim");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            default:
                finish();
        }
    }
}

