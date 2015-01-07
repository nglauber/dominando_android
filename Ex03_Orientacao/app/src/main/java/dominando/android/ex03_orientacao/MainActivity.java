package dominando.android.ex03_orientacao;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    EditText edt;
    ArrayList<String> nomes;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            nomes = savedInstanceState.getStringArrayList("nomes");
        } else {
            nomes = new ArrayList<String>();
        }

        edt = (EditText) findViewById(R.id.editText1);

        ListView listView = (ListView)
                findViewById(R.id.listView1);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, nomes);

        listView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("nomes", nomes);
    }


    public void meuBotaoClick(View v) {
        nomes.add(edt.getText().toString());
        edt.setText("");
        adapter.notifyDataSetChanged();
    }
}

