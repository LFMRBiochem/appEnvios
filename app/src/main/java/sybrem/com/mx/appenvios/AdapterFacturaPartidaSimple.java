package sybrem.com.mx.appenvios;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterFacturaPartidaSimple extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<FacturaPartidaClass> items;
    private int lastPosition = -1;

    public AdapterFacturaPartidaSimple(Activity activity, ArrayList<FacturaPartidaClass> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_partida_pedido_simple, null);
        }

        FacturaPartidaClass fp = (FacturaPartidaClass) items.get(position);
        TextView tvNomProducto = v.findViewById(R.id.tvProdx);
        TextView tvPedido = v.findViewById(R.id.tvCantx);

        tvNomProducto.setText("Producto: " + fp.getNom_producto());
        tvPedido.setText("Cantidad: " + fp.getCantidad());

        if (getCount() > 3) {
            Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), (position > lastPosition) ? R.anim.lista_deabajo_parriba : R.anim.lista_dearriba_pabajo);
            v.startAnimation(animation);
            lastPosition = position;
        }

        return v;
    }
}
