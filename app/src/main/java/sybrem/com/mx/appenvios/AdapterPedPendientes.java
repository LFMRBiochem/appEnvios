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

import java.util.ArrayList;

public class AdapterPedPendientes extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<PedidoPendienteClass> items;
    private int lastPosition = -1;

    public AdapterPedPendientes(Activity activity, ArrayList<PedidoPendienteClass> items) {
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
            v = inf.inflate(R.layout.item_factuta_pedido, null);
        }

        PedidoPendienteClass pp = items.get(position);
        ImageView img_almacen = v.findViewById(R.id.img_almacen);
        TextView tvFactura = v.findViewById(R.id.tvFactura);
        TextView tvPedido = v.findViewById(R.id.tvPedido);
        TextView tvCliente = v.findViewById(R.id.tvCliente);
        TextView tvAgente = v.findViewById(R.id.tvAgente);
        TextView tvAlmacen = v.findViewById(R.id.tvAlmacen);

        img_almacen.setImageResource(pp.getImg_almacen());
        tvFactura.setText("Factura: "+pp.getNum_factura());
        tvPedido.setText("Pedido: "+pp.getNum_pedido());
        tvCliente.setText("Cliente: "+pp.getCliente());
        tvAgente.setText("Agente: "+pp.getAgente());
        tvAlmacen.setText("Almacén de origen: "+pp.getAlmacen());

        Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), (position > lastPosition) ? R.anim.lista_deabajo_parriba : R.anim.lista_dearriba_pabajo);
        v.startAnimation(animation);
        lastPosition = position;

        return v;
    }
}