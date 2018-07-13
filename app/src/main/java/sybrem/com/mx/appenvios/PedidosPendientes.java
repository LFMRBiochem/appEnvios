package sybrem.com.mx.appenvios;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PedidosPendientes extends AppCompatActivity {

    /*private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;*/

    ArrayList<PedidoPendienteClass> arrListPedPend;
    AdapterPedPendientes adapter;
    ListView lvFacturas;

    String jason_facturas = "", usuario = "";
    private static final int DATABASE_VERSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {

            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {
                View v = tryInflate(name, context, attrs);
                if (v instanceof TextView) {
                    setTypeFace((TextView) v);
                }
                return v;
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_pendientes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.setTitle("Menú Principal");

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, DATABASE_VERSION);
        dbHandler.checkDBStatus();
        usuario = dbHandler.ultimoUsuarioRegistrado();

        jason_facturas = dbHandler.dameFacturasPerra();
        //List items = new ArrayList();
        arrListPedPend = new ArrayList<PedidoPendienteClass>();
        try {
            JSONArray jsonReporte = new JSONArray(jason_facturas);
            for (int i = 0; i < jsonReporte.length(); i++) {
                JSONObject obj = jsonReporte.getJSONObject(i);
                //num_factura, num_pedido, cliente, agente, almacen
                String num_factura = obj.getString("num_factura").toString();
                String num_pedido = obj.getString("num_pedido").toString();
                String cliente = obj.getString("cliente").toString();
                String agente = obj.getString("agente").toString();
                String almacen = obj.getString("almacen").toString();
                int img_alma = 0;
                if (!almacen.contains("BOR")) {
                    switch (almacen) {
                        case "QUERETARO":
                            img_alma = R.drawable.queretaro;
                            break;
                        case "MEXICO":
                            img_alma = R.drawable.distritofederal;
                            break;
                        case "VERACRUZ":
                            img_alma = R.drawable.veracruz;
                            break;
                        case "GUADALAJARA":
                            img_alma = R.drawable.jalisco;
                            break;
                        default:
                            img_alma = R.drawable.ic_loqueichon;
                            break;
                    }
                }else{
                    String[] split_almacen = almacen.split("-");
                    String alm_chido = split_almacen[1];
                    switch (alm_chido){
                        case "QRO":
                            almacen = "MANO - QUERETARO";
                            img_alma = R.drawable.queretaro;
                            break;
                        case "MEX":
                            almacen = "MANO - MEXICO";
                            img_alma = R.drawable.distritofederal;
                            break;
                        case "VER":
                            almacen = "MANO - VERACRUZ";
                            img_alma = R.drawable.veracruz;
                            break;
                        case "GDL":
                            almacen = "MANO - GUADALAJARA";
                            img_alma = R.drawable.jalisco;
                            break;
                    }
                }
                //items.add(new PedidoPendienteClass(num_factura, num_pedido, cliente, agente, almacen, img_alma));
                arrListPedPend.add(new PedidoPendienteClass(num_factura, num_pedido, cliente, agente, almacen, img_alma));
            }
            lvFacturas = findViewById(R.id.lvFacturas);
            adapter = new AdapterPedPendientes(PedidosPendientes.this, arrListPedPend);
            lvFacturas.setAdapter(adapter);

            lvFacturas.setDividerHeight(0);
            lvFacturas.setDivider(null);

            lvFacturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PedidoPendienteClass pp = (PedidoPendienteClass) adapter.getItem(position);
                    String num_pedido = pp.getNum_pedido();
                    String num_factura = pp.getNum_factura();
                    String cliente = pp.getCliente();
                    Intent explicit_intent;
                    explicit_intent = new Intent(PedidosPendientes.this, PedidosPendientesPartidas.class);
                    explicit_intent.putExtra("num_factura", num_factura);
                    explicit_intent.putExtra("num_pedido", num_pedido);
                    explicit_intent.putExtra("cliente", cliente);
                    startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    finish();
                }
            });
            /*
            // Obtener el Recycler
            recycler = (RecyclerView) findViewById(R.id.recicladorts1);
            recycler.setHasFixedSize(true);

            // Usar un administrador para LinearLayout
            lManager = new LinearLayoutManager(this);
            recycler.setLayoutManager(lManager);

            // Crear un nuevo adaptador
            adapter = new PedidosPendientesAdapter(items);
            recycler.setAdapter(adapter);*/

            /*recycler.addOnItemTouchListener(new RecyclerItemClickListener(this, recycler, new RecyclerItemClickListener
                    .OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //start new activity here
                    adapter.getItemId(position);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            }));
            */

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View tryInflate(String name, Context context, AttributeSet attrs) {
        LayoutInflater li = LayoutInflater.from(context);
        View v = null;
        try {
            v = li.createView(name, null, attrs);
        } catch (Exception e) {
            try {
                v = li.createView("android.widget." + name, null, attrs);
            } catch (Exception e1) {
            }
        }
        return v;
    }

    private void setTypeFace(TextView tv) {
        tv.setTypeface(FontUtils.getFonts(this, "ubuntux.ttf"));
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent explicit_intent;
        explicit_intent = new Intent(PedidosPendientes.this, NavDrawerActivity.class);
        explicit_intent.putExtra("usuario", usuario);
        //startActivity(explicit_intent);
        startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
        return true;
    }
}
