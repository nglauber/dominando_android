package dominando.android.ex42_lollipop;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetalheActivity extends ActionBarActivity {

    public static final String EXTRA_LIVRO = "livro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        ImageView imgCapa = (ImageView)findViewById(R.id.imgCapa);
        TextView txtTitulo = (TextView)findViewById(R.id.txtTitulo);
        TextView txtAutores = (TextView)findViewById(R.id.txtAutores);
        TextView txtPaginas = (TextView)findViewById(R.id.txtPaginas);
        TextView txtAno = (TextView)findViewById(R.id.txtAno);
        ImageButton btnAnim = (ImageButton) findViewById(R.id.btnAnim);

        Livro livro = (Livro)getIntent().getSerializableExtra(EXTRA_LIVRO);

        Picasso.with(this).load(livro.capa).into(imgCapa);
        txtTitulo.setText(livro.titulo);
        txtAutores.setText(livro.autor);
        txtAno.setText(String.valueOf(livro.ano));
        txtPaginas.setText(String.valueOf(livro.paginas));
        btnAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.finishAfterTransition(DetalheActivity.this);
            }
        });

        ViewCompat.setTransitionName(imgCapa, "capa");
        ViewCompat.setTransitionName(txtTitulo, "titulo");
        ViewCompat.setTransitionName(txtAutores, "autores");
        ViewCompat.setTransitionName(txtPaginas, "paginas");
        ViewCompat.setTransitionName(txtAno, "ano");
        ViewCompat.setTransitionName(btnAnim, "btn");

    }
}

