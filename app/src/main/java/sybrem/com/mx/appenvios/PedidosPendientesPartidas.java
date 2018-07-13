package sybrem.com.mx.appenvios;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PedidosPendientesPartidas extends AppCompatActivity {

    ArrayList<FacturaPartidaClass> arrListPedPart;
    AdapterFacturaPartida adapter;
    AdapterFacturaPartidaSimple adapter_simple;
    ListView lvDetalleFactura, lvFactura;

    String jason_facturas = "", usuario = "";
    private static final int DATABASE_VERSION = 2;

    String num_pedido="", num_factura="", cliente="";
    Button btnRecibe, btnSimple, btnDetallada;

    TextView tvNPed, tvNFac;

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
        setContentView(R.layout.activity_pedidos_pendientes_partidas);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.setTitle("Regresar");

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, DATABASE_VERSION);
        dbHandler.checkDBStatus();
        usuario = dbHandler.ultimoUsuarioRegistrado();

        Intent intent=getIntent();
        Bundle extras = intent.getExtras();
        num_pedido=(String)extras.get("num_pedido");
        num_factura=(String)extras.get("num_factura");
        cliente = (String)extras.get("cliente");

        tvNPed = findViewById(R.id.tvNPed);
        tvNPed.setText("Pedido: "+num_pedido);

        tvNFac = findViewById(R.id.tvNFac);
        tvNFac.setText("Factura: "+num_factura);

        jason_facturas = dbHandler.damePartidasFacturasPerra(num_factura);
        arrListPedPart = new ArrayList<FacturaPartidaClass>();
        try {
            JSONArray jsonReporte = new JSONArray(jason_facturas);
            for (int i = 0; i < jsonReporte.length(); i++) {
                JSONObject obj = jsonReporte.getJSONObject(i);
                //num_factura, num_pedido, cliente, agente, almacen
                String nom_producto = obj.getString("nom_producto").toString();
                String cantidad = obj.getString("cantidad").toString();
                String img_producto = obj.getString("img_producto").toString();

                arrListPedPart.add(new FacturaPartidaClass(nom_producto,cantidad,img_producto));
            }

            lvDetalleFactura = findViewById(R.id.lvDetalleFactura);
            adapter = new AdapterFacturaPartida(PedidosPendientesPartidas.this, arrListPedPart);
            lvDetalleFactura.setAdapter(adapter);

            lvDetalleFactura.setDividerHeight(0);
            lvDetalleFactura.setDivider(null);

            lvFactura = findViewById(R.id.lvFactura);
            adapter_simple = new AdapterFacturaPartidaSimple(PedidosPendientesPartidas.this,arrListPedPart);
            lvFactura.setAdapter(adapter_simple);

            lvFactura.setDividerHeight(0);
            lvFactura.setDivider(null);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        btnRecibe = findViewById(R.id.btnRecibe);
        btnRecibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicit_intent;
                explicit_intent = new Intent(PedidosPendientesPartidas.this, FirmaEntrega.class);
                explicit_intent.putExtra("num_factura", num_factura);
                explicit_intent.putExtra("num_pedido", num_pedido);
                explicit_intent.putExtra("cliente", cliente);
                startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
                //Toast.makeText(PedidosPendientesPartidas.this, "TO DO BIEN POR AHORA PERRO", Toast.LENGTH_SHORT).show();
            }
        });

        btnSimple = findViewById(R.id.btnSimple);
        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvFactura.setVisibility(View.VISIBLE);
                lvDetalleFactura.setVisibility(View.GONE);
            }
        });

        btnDetallada = findViewById(R.id.btnDetallada);
        btnDetallada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvFactura.setVisibility(View.GONE);
                lvDetalleFactura.setVisibility(View.VISIBLE);
            }
        });
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
        explicit_intent = new Intent(PedidosPendientesPartidas.this, PedidosPendientes.class);
        explicit_intent.putExtra("usuario", usuario);
        //startActivity(explicit_intent);
        startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        // do nothing
        Intent explicit_intent;
        explicit_intent = new Intent(PedidosPendientesPartidas.this, PedidosPendientes.class);
        explicit_intent.putExtra("usuario", usuario);
        //startActivity(explicit_intent);
        startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}
