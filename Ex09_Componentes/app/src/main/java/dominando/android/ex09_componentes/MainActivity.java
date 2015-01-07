package dominando.android.ex09_componentes;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    CompoundButton checkBox;
    SeekBar seekBar;
    Spinner spinner;
    RadioGroup radioGroup;
    TextView txtValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox = (CompoundButton)findViewById(R.id.chkHabilitar);
        seekBar = (SeekBar)findViewById(R.id.skbValor);
        spinner = (Spinner)findViewById(R.id.spnNomes);
        radioGroup = (RadioGroup)findViewById(R.id.rgOpcoes);
        txtValor = (TextView)findViewById(R.id.txtValor);

        configuraSpinner();
        configuraSeekbar();

        // Atribuindo programaticamente os valores padrão
        checkBox.setChecked(true);
        seekBar.setProgress(20);
        spinner.setSelection(2);
        radioGroup.check(R.id.rbOpcao2);
    }

    private void configuraSeekbar() {
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar, int i, boolean b) {
                        txtValor.setText(String.valueOf(i));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
    }

    private void configuraSpinner() {
        String[] nomes = new String[]{
                "Eric", "Diana", "Presto", "Hank", "Sheila", "Bob"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, nomes);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void verValores(View v) {
        int idRadioSelecionado =
                radioGroup.getCheckedRadioButtonId();
        RadioButton radio =
                (RadioButton)findViewById(idRadioSelecionado);

        String habilitado = checkBox.isChecked() ? "Habilitado" : "Desabilitado";
        String valor = "valor: "+ seekBar.getProgress();
        String nome = "nome: "+ spinner.getSelectedItem().toString();
        String opcao = "opcao: "+ radio.getText();

        StringBuilder mensagem = new StringBuilder();
        mensagem.append(habilitado).append("\n")
                .append(valor).append("\n")
                .append(nome).append("\n")
                .append(opcao);
        Toast.makeText(this,
                mensagem.toString(), Toast.LENGTH_SHORT).show();
    }
}
