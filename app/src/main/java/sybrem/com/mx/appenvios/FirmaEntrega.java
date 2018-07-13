package sybrem.com.mx.appenvios;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class FirmaEntrega extends AppCompatActivity {

    SignaturePad firmaEntrega;
    Button btnTermina, btnTerminaPaga, btnRegresa, btnBorraFirma;
    String jason_facturas = "", usuario = "";
    private static final int DATABASE_VERSION = 2;
    TextView tvNCliente;
    String num_pedido = "", num_factura = "", cliente = "";
    int bandera_firma = 0;
    String cia = "", respue ="";
    ProgressDialog pd;

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
        setContentView(R.layout.activity_firma_entrega);

        this.setTitle("Firmar entrega");

        final MyDBHandler dbHandler = new MyDBHandler(this, null, null, DATABASE_VERSION);
        dbHandler.checkDBStatus();
        usuario = dbHandler.ultimoUsuarioRegistrado();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        num_pedido = (String) extras.get("num_pedido");
        num_factura = (String) extras.get("num_factura");
        cliente = (String) extras.get("cliente");

        tvNCliente = findViewById(R.id.tvNCliente);
        tvNCliente.setText("Cliente: " + cliente);

        cia = dbHandler.getCia(num_factura, num_pedido);

        firmaEntrega = findViewById(R.id.firmaEntrega);

        firmaEntrega.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                bandera_firma = 1;
            }

            @Override
            public void onSigned() {
                bandera_firma = 1;
            }

            @Override
            public void onClear() {
                bandera_firma = 0;
            }
        });

        btnBorraFirma = findViewById(R.id.btnBorraFirma);
        btnBorraFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firmaEntrega.clear();
            }
        });

        btnTermina = findViewById(R.id.btnTermina);
        btnTerminaPaga = findViewById(R.id.btnTerminaPaga);
        btnRegresa = findViewById(R.id.btnRegresa);

        btnTermina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bArray_miFirmilla = "";
                Bitmap miFirmilla = firmaEntrega.getTransparentSignatureBitmap(false);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                miFirmilla.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bArray_miFirmilla = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);

                ContentValues entregax = new ContentValues();

                entregax.put(dbHandler.COL_VNENTREGASTABLETA_NUM_FACTURA, num_factura);
                entregax.put(dbHandler.COL_VNENTREGASTABLETA_CVE_COMPANIA, cia);
                entregax.put(dbHandler.COL_VNENTREGASTABLETA_FIRMA, bArray_miFirmilla);
                entregax.put(dbHandler.COL_VNENTREGASTABLETA_CVE_USUARIO_CAPTURA, usuario);
                entregax.put(dbHandler.COL_VNENTREGASTABLETA_SINCRONIZADO, "0");

                if (dbHandler.registraEntrega(entregax)) {
                    dbHandler.actualizadEntrega(cia, num_pedido, num_factura);
                    if (CheckNetwork.isInternetAvailable(FirmaEntrega.this)) {
                        new JsonTaskSendEntregas(dbHandler).execute();
                    } else {
                        String respuesta_perrax = "Se guardaron los cambios.\nSincroniza la aplicación cuando tengas una conexión a internet.\n";
                        //Crea ventana de alerta.
                        android.support.v7.app.AlertDialog.Builder dialog1 = new android.support.v7.app.AlertDialog.Builder(FirmaEntrega.this);
                        dialog1.setMessage(respuesta_perrax);
                        dialog1.setTitle("Cambios guardados");
                        //Establece el boton de Aceptar y que hacer si se selecciona.
                        dialog1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final MyDBHandler dbHandler = new MyDBHandler(FirmaEntrega.this, null, null, 1);

                                String usuario = dbHandler.ultimoUsuarioRegistrado();

                                Intent explicit_intent;
                                explicit_intent = new Intent(FirmaEntrega.this, NavDrawerActivity.class);
                                explicit_intent.putExtra("usuario", usuario);
                                startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                finish();
                                return;
                            }
                        });
                        dialog1.show();
                    }
                } else {
                    String respuesta_perrax = "Error al guardar en la base de datos local.\nIntenta más tarde.";
                    //Crea ventana de alerta.
                    android.support.v7.app.AlertDialog.Builder dialog1 = new android.support.v7.app.AlertDialog.Builder(FirmaEntrega.this);
                    dialog1.setMessage(respuesta_perrax);
                    dialog1.setTitle("Error");
                    //Establece el boton de Aceptar y que hacer si se selecciona.
                    dialog1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final MyDBHandler dbHandler = new MyDBHandler(FirmaEntrega.this, null, null, 1);

                            String usuario = dbHandler.ultimoUsuarioRegistrado();

                            /*Intent explicit_intent;
                            explicit_intent = new Intent(NavDrawerActivity.this, NavDrawerActivity.class);
                            explicit_intent.putExtra("usuario", usuario);
                            startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            finish();*/
                            return;
                        }
                    });
                    dialog1.show();
                }
            }
        });

        btnTerminaPaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bArray_miFirmilla = "";
                Bitmap miFirmilla = firmaEntrega.getTransparentSignatureBitmap(false);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                miFirmilla.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bArray_miFirmilla = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);

                Intent explicit_intent;
                explicit_intent = new Intent(FirmaEntrega.this, CapturaPagoEntrega.class);
                explicit_intent.putExtra("num_factura", num_factura);
                explicit_intent.putExtra("num_pedido", num_pedido);
                explicit_intent.putExtra("cliente", cliente);
                explicit_intent.putExtra("firma_cliente", bArray_miFirmilla);
                explicit_intent.putExtra("cia",cia);
                startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });

        btnRegresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent explicit_intent;
                explicit_intent = new Intent(FirmaEntrega.this, PedidosPendientesPartidas.class);
                explicit_intent.putExtra("num_factura", num_factura);
                explicit_intent.putExtra("num_pedido", num_pedido);
                explicit_intent.putExtra("cliente", cliente);
                startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
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
    public void onBackPressed() {
        // do nothing
    }

    private class JsonTaskSendEntregas extends AsyncTask<String, String, String> {


        // Bloque para poder usar los metodos de la clase MyDBHandler en la clase JsonTask
        private MyDBHandler dbHandler;

        public JsonTaskSendEntregas(MyDBHandler dbHandler) {
            this.dbHandler = dbHandler;
        }

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(FirmaEntrega.this);
            pd.setTitle("Conectando a servidor Biochem...");
            pd.setMessage("Sincronizando información");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            /*************************************************************
             * Bloque para envio de los prospectos :                        *
             * ***********************************************************/
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String JsonUrlEnvioPromesas = "";
            String urlSendPromesas = "http://www.sybrem.com.mx/adsnet/syncmovil/app_envios/ReceptorMovilEntregas.php?jsonCad=" + URLEncoder.encode(dbHandler.transmiteEntregas());

            try {

                URL url = new URL(urlSendPromesas);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                respue = buffer.toString();
                JsonUrlEnvioPromesas = buffer.toString().substring(0, 13);
                if (JsonUrlEnvioPromesas.toString().equals("SYNENTREGASOK")) {
                    dbHandler.resetEntregas();
                } else {
                    // Sincronizacion no exitosa. No se eliminan las visitas
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return respue;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pd.isShowing()) {
                pd.dismiss();
            }
            String respuesta_perrax = respue;
            //Crea ventana de alerta.
            android.support.v7.app.AlertDialog.Builder dialog1 = new android.support.v7.app.AlertDialog.Builder(FirmaEntrega.this);
            dialog1.setMessage(respuesta_perrax.substring(0, 12).equals("SYNENTREGASOK") ? "Sincronización correcta" : respuesta_perrax);
            dialog1.setTitle("Entrega realizada");
            //Establece el boton de Aceptar y que hacer si se selecciona.
            dialog1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final MyDBHandler dbHandler = new MyDBHandler(FirmaEntrega.this, null, null, 1);

                    String usuario = dbHandler.ultimoUsuarioRegistrado();

                    Intent explicit_intent;
                    explicit_intent = new Intent(FirmaEntrega.this, NavDrawerActivity.class);
                    explicit_intent.putExtra("usuario", usuario);
                    startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    finish();
                    return;
                }
            });
            dialog1.show();
        }
    }
}
