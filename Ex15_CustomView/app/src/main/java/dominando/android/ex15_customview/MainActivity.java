package dominando.android.ex15_customview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
        implements JogoDaVelhaView.JogoDaVelhaListener,
        View.OnClickListener {

    JogoDaVelhaView jogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jogo = (JogoDaVelhaView) findViewById(R.id.jogoDaVelha);
        jogo.setListener(this);

        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void fimDeJogo(int vencedor) {
        String mensagem;
        switch (vencedor) {
            case JogoDaVelhaView.XIS:
                mensagem = "X venceu!";
                break;
            case JogoDaVelhaView.BOLA:
                mensagem = "O venceu!";
                break;
            default:
                mensagem = "Empatou!";
        }
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        jogo.reiniciarJogo();
    }
}

