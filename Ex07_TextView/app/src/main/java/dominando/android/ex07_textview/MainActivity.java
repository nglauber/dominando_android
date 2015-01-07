package dominando.android.ex07_textview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface typefaceLight = Typeface.createFromAsset(
                getAssets(), "Roboto-Light.ttf");

        Typeface typefaceCondensed = Typeface.createFromAsset(
                getAssets(), "RobotoCondensed-Regular.ttf");

        TextView txt1 = (TextView)findViewById(R.id.txtFonte1);
        txt1.setTypeface(typefaceLight);

        TextView txt2 = (TextView)findViewById(R.id.txtFonte2);
        txt2.setTypeface(typefaceCondensed);

        TextView txt3 = (TextView)findViewById(R.id.txtStike);
        txt3.setPaintFlags(txt3.getPaintFlags() |
                Paint.STRIKE_THRU_TEXT_FLAG);

        TextView txtHtml = (TextView) findViewById(R.id.txtHtml);
        final String textoEmHtml =
                "<html><body>Html em "
                        + "<b>Negrito</b>, <i>Itálico</i>"
                        + "e <u>Sublinhado</u>.<br>"
                        + "Mario: <img src='mario.png' /><br>"
                        + "Luigi: <img src='yoshi.png' /><br>"
                        + "Um texto depois da imagem</body></html>";

        Html.ImageGetter imgGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                BitmapDrawable drawable = null;
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(
                            getAssets().open(source));

                    drawable = new BitmapDrawable(
                            getResources(), bmp);

                    drawable.setBounds(
                            0, 0, bmp.getWidth(), bmp.getHeight());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return drawable;
            }
        };
        txtHtml.setText(
                Html.fromHtml(textoEmHtml, imgGetter, null));
    }
}
