package dominando.android.ex10_adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CarrosAdapter extends BaseAdapter {

    Context ctx;
    List<Carro> carros;

    public CarrosAdapter(Context ctx, List<Carro> carros) {
        this.ctx = ctx;
        this.carros = carros;
    }

    @Override
    public int getCount() {
        return carros.size();
    }

    @Override
    public Object getItem(int position) {
        return carros.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1 passo
        Carro carro = carros.get(position);
        // 2 passo
        ViewHolder holder = null;
        if (convertView == null) {
            Log.d("NGVL", "View Nova => position: " + position);
            convertView = LayoutInflater.from(ctx)
                    .inflate(R.layout.item_carro, null);

            holder = new ViewHolder();
            holder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            holder.txtModelo = (TextView) convertView.findViewById(R.id.txtModelo);
            holder.txtAno = (TextView) convertView.findViewById(R.id.txtAno);
            holder.txtCombustivel = (TextView) convertView.findViewById(R.id.txtCombustivel);
            convertView.setTag(holder);
        } else {
            Log.d("NGVL", "View existente => position: "+ position);
            holder = (ViewHolder)convertView.getTag();
        }
        // 3 passo
        // 0=VW;1=GM;2=Fiat;3=Ford
        Resources res = ctx.getResources();
        TypedArray logos = res.obtainTypedArray(R.array.logos);

        holder.imgLogo.setImageDrawable(
                logos.getDrawable(carro.fabricante));
        holder.txtModelo.setText(carro.modelo);
        holder.txtAno.setText(String.valueOf(carro.ano));
        holder.txtCombustivel.setText(
                (carro.gasolina ? "G" : "") +
                        (carro.etanol ? "E" : ""));
        // 4 passo
        return convertView;
    }

    static class ViewHolder {
        ImageView imgLogo;
        TextView txtModelo;
        TextView txtAno;
        TextView txtCombustivel;
    }

}
