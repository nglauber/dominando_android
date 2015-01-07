package dominando.android.ex02_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
        implements View.OnClickListener {

    EditText edtTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTexto = (EditText) findViewById(R.id.editText);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        Button buttonTela2 = (Button) findViewById(R.id.button2);
        buttonTela2.setOnClickListener(this);
        Button buttonTela2Parcelable = (Button) findViewById(R.id.button3);
        buttonTela2Parcelable.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                String texto = edtTexto.getText().toString();
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
                break;
            case R.id.button2:
                Intent it = new Intent(this, Tela2Activity.class);
                it.putExtra("nome", "Glauber");
                it.putExtra("idade", 30);
                startActivity(it);
                break;
            case R.id.button3:
                Cliente cliente = new Cliente(1, "Glauber");
                Intent intent = new Intent(this, Tela2Activity.class);
                intent.putExtra("cliente", cliente);
                startActivity(intent);
                break;

        }

    }
}
