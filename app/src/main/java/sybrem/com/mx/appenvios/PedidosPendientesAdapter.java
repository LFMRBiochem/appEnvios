package sybrem.com.mx.appenvios;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PedidosPendientesAdapter extends RecyclerView.Adapter<PedidosPendientesAdapter.PedidosPendientesViewHolder> {

    private List<PedidoPendienteClass> items;

    public static class PedidosPendientesViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_almacenx;
        public TextView facturax;
        public TextView pedidox;
        public TextView clientex;
        public TextView agentex;
        public TextView almacenx;

        public PedidosPendientesViewHolder(View v) {
            super(v);
            img_almacenx = v.findViewById(R.id.img_almacen);
            facturax = v.findViewById(R.id.tvFactura);
            pedidox = v.findViewById(R.id.tvPedido);
            clientex = v.findViewById(R.id.tvCliente);
            agentex = v.findViewById(R.id.tvAgente);
            almacenx = v.findViewById(R.id.tvAlmacen);
        }
    }

    public PedidosPendientesAdapter(List<PedidoPendienteClass> items) {
        this.items = items;
    }

    @Override
    public PedidosPendientesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_pedidos_pendientes, viewGroup, false);
        return new PedidosPendientesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PedidosPendientesViewHolder holder, int position) {
        holder.img_almacenx.setImageResource(items.get(position).getImg_almacen());
        holder.facturax.setText("Factura: " + items.get(position).getNum_factura());
        holder.pedidox.setText("Pedido: " + items.get(position).getNum_pedido());
        holder.clientex.setText("Cliente: " + items.get(position).getCliente());
        holder.agentex.setText("Agente: " + items.get(position).getAgente());
        holder.almacenx.setText("Almacén de origen: " + items.get(position).getAlmacen());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
