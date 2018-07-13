package sybrem.com.mx.appenvios;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;
import com.github.snowdream.android.widget.WebImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View view_Group;
    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    String usuarioLogeado = "", respue="";
    SmartImageView siv;
    ProgressDialog pd,pdSendPayment;

    private static final int DATABASE_VERSION = 1;

    static int[] icon = {R.drawable.ic_prod_nuevo, R.drawable.ic_salir, R.drawable.ic_listado,
            R.drawable.ic_reporte, R.drawable.ic_cobranza};
    static int[] icon_child = {R.drawable.ic_prod_nuevo, R.drawable.ic_promo_mes, R.drawable.ic_listado, R.drawable.ic_listado,
            R.drawable.ic_listado, R.drawable.ic_listado, R.drawable.ic_reporte, R.drawable.ic_reporte, R.drawable.ic_reporte,
            R.drawable.ic_reporte, R.drawable.ic_reporte, R.drawable.ic_venta, R.drawable.ic_cobranza, R.drawable.ic_visita,
            R.drawable.ic_prospecto, R.drawable.ic_comprobacion, R.drawable.ic_sincronizar, R.drawable.ic_salir};

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableList.setIndicatorBounds(expandableList.getRight() - 80, expandableList.getWidth());
        } else {
            expandableList.setIndicatorBoundsRelative(expandableList.getRight() - 80, expandableList.getWidth());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        final MyDBHandler dbHandler = new MyDBHandler(this, null, null, DATABASE_VERSION);
        dbHandler.checkDBStatus(); // Tiene como fin forzar la creacion de la base de datos.

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        usuarioLogeado = (String) extras.get("usuario");

        //Actuaizamos en la bitacora el usuario logeado
        dbHandler.registraBitacora(usuarioLogeado);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        expandableList = findViewById(R.id.menue);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        String url_img_inicio = "http://www.sybrem.com.mx/adsnet/syncmovil/img_inicio_app/inicio.png";


        siv = (SmartImageView) findViewById(R.id.img_inicio);
        Rect rect2 = new Rect(siv.getLeft(), siv.getTop(), siv.getRight(), siv.getBottom());
        siv.setImageUrl(url_img_inicio, rect2);

        //Se verifica si el usuario ya tiene informacion para poder operar si no es el caso sincroniza
        if (CheckNetwork.isInternetAvailable(NavDrawerActivity.this)) //returns true if internet available
        {
            //Se verifica si el agente ya tiene informacion para poder operar
            boolean infoAgente = dbHandler.checkInformacionAgente();

            //Si es true quiere decir que hay que sincronizar para obtener la informacion
            if (infoAgente == true) {
                dbHandler.resetCatalogs(); // Elimina la información de los catálogos existentes
                new JsonTask(dbHandler).execute(); // Descarga de la base de datos los catalogos.
            }
        }


        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        prepareListData();
        mMenuAdapter = new sybrem.com.mx.appenvios.ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                //Log.d("DEBUG", "submenu item clicked");
                view.setSelected(true);
                Intent explicit_intent;
                String nov_id = "";

                String usuarioLog = dbHandler.ultimoUsuarioRegistrado();
                String ruta = "";

                /*if (TipoUsuario.toString().equals("A")) {
                    ruta = dbHandler.num_ruta(usuarioLog);
                } else {
                    ruta = dbHandler.rutaSeleccionada();
                }*/
                switch (groupPosition) {

                    case 0:
                        switch (childPosition) {
                            case 0:
                                explicit_intent = new Intent(NavDrawerActivity.this, PedidosPendientes.class);
                                startActivity(explicit_intent);
                                break;
                        }
                        break;
                    case 1:
                        switch (childPosition) {
                            case 0:
                                new JsonTaskSendPayment(dbHandler).execute();
                                break;
                            case 1:
                                explicit_intent = new Intent(NavDrawerActivity.this, Login.class);
                                startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                finish();
                                break;
                        }
                        break;
                }
                if (view_Group != null) {
                    view_Group.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                view_Group = view;
                view_Group.setBackgroundColor(Color.parseColor("#DDDDDD"));
                mDrawerLayout.closeDrawers();
                return false;
            }
        });

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final MyDBHandler dbHandler = new MyDBHandler(this, null, null, DATABASE_VERSION);

        // Inflate the menu_backup; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.nav_drawer, menu);

        //Se pasan los datos del usuario que se logeo
        String usuarioLogeado = "";
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        usuarioLogeado = (String) extras.get("usuario");
        String tipo_usuario = dbHandler.tipoUsuario(usuarioLogeado);

        if (tipo_usuario.toString().equals("P") || tipo_usuario.toString().equals("U")) {
            super.onPrepareOptionsMenu(menu);
            menu.findItem(R.id.cambiar_agente).setVisible(true);
        }
        if (tipo_usuario.toString().equals("A")) {
            super.onPrepareOptionsMenu(menu);
            menu.findItem(R.id.cambiar_agente).setVisible(false);
        }

        //Se verifica si hay algo pendiente por sincronizar si es el caso mostramos icono de notificacion y opcion en el menu_backup
        Boolean pendiente = false;
        pendiente = dbHandler.checkPendienteEnvio();

        if (pendiente == true) {
            super.onPrepareOptionsMenu(menu);
            menu.findItem(R.id.pendientes).setVisible(true);
        } else {
            super.onPrepareOptionsMenu(menu);
            menu.findItem(R.id.pendientes).setVisible(false);
        }*/

        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding data header
        listDataHeader.add("Pedidos");
        listDataHeader.add("Salir");

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        //heading1.add("Entregados");
        heading1.add("Por entregar");
        //heading1.add("Rechazados");

        List<String> heading2 = new ArrayList<String>();
        heading2.add("Sincronizar");
        heading2.add("Salir");

        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(1), heading2);// Header, Child data
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        // Bloque para poder usar los metodos de la clase MyDBHandler en la clase JsonTask
        public MyDBHandler dbHandler;

        public JsonTask(MyDBHandler dbHandler) {
            this.dbHandler = dbHandler;
        }
        // Termina bloque de inclusion de los metodos de MyDBHandler (Se uso esta practica debido a que ya esta usado extends en la clase

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(NavDrawerActivity.this);
            pd.setTitle("Conectando a servidor Biochem...");
            pd.setMessage("Sincronizando base de datos...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            String usuarioLog = dbHandler.ultimoUsuarioRegistrado();
            String ruta = "0";


            String urlVnFacturasPedidos = "http://www.sybrem.com.mx/adsnet/syncmovil/app_envios/SyncMovil.php?ruta=" + ruta + "&tabla=facturas_pedidos";
            String urlVnPedidosEncabezao = "http://www.sybrem.com.mx/adsnet/syncmovil/app_envios/SyncMovil.php?ruta=" + ruta + "&tabla=pedidos_encabezao";
            String urlVnPedidosPartidax = "http://www.sybrem.com.mx/adsnet/syncmovil/app_envios/SyncMovil.php?ruta=" + ruta + "&tabla=pedidos_partidax";
            String urlBancosClientes = "http://www.sybrem.com.mx/adsnet/syncmovil/SyncMovil.php?ruta=0&tabla=bancosClientes";
            String urlCatalogoBancos = "http://www.sybrem.com.mx/adsnet/syncmovil/SyncMovil.php?ruta=0&tabla=catBancos";

            /*************************************************************
             * Bloque para vn_facturas_pedidos                           *
             * ***********************************************************/
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String JsonUrlVnFacturasPedidos = "";

            try {
                URL url = new URL(urlVnFacturasPedidos);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    // Log.d("Salida: ", "> " + line);   //Se hace una salida por monitoreo en la consola. ELIMINAR / COMENTAR
                }
                JsonUrlVnFacturasPedidos = buffer.toString();


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

            // Una vez obtenida la cadena JSon del primer catalogo se lee linea por lina para insertar en la tabla
            try {
                // Convierte el string con la informaciÑn de los clientes en un array JSON
                JSONArray jsonArr_facturas = new JSONArray(JsonUrlVnFacturasPedidos);

                for (int i = 0; i < jsonArr_facturas.length(); i++) {
                    JSONObject jsonObjVnFacturasPedidos = jsonArr_facturas.getJSONObject(i);
                    dbHandler.insertaFacturasPedidos(jsonObjVnFacturasPedidos);
                }
            } // Fin del Try
            catch (JSONException e) {
                e.printStackTrace();
            } // fin del catch

            /*************************************************************
             * Bloque para la tabla de vn_pedidos_encabezao              *
             * ***********************************************************/
            connection = null;
            reader = null;
            String JsonUrlVnPedidosEncabezao = "";
            try {
                URL url = new URL(urlVnPedidosEncabezao);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    // Log.d("Salida: ", "> " + line);   //Se hace una salida por monitoreo en la consola. ELIMINAR / COMENTAR
                }
                JsonUrlVnPedidosEncabezao = buffer.toString();


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

            // Una vez obtenida la cadena JSon del primer catalogo se lee linea por lina para insertar en la tabla
            try {
                // Convierte el string con la informaciÑn de los clientes en un array JSON
                JSONArray jsonArr_pedidos_encabezao = new JSONArray(JsonUrlVnPedidosEncabezao);

                for (int i = 0; i < jsonArr_pedidos_encabezao.length(); i++) {
                    JSONObject jsonObjVnPedidosEncabezao = jsonArr_pedidos_encabezao.getJSONObject(i);
                    dbHandler.insertaPedidosEncabezao(jsonObjVnPedidosEncabezao);
                }
            } // Fin del Try
            catch (JSONException e) {
                e.printStackTrace();
            } // fin del catch

            /*************************************************************
             * Bloque para la tabla de vn_pedidos_partidax            *
             * ***********************************************************/
            connection = null;
            reader = null;
            String JsonUrlVnPedidosPertidax = "";
            try {
                URL url = new URL(urlVnPedidosPartidax);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    // Log.d("Salida: ", "> " + line);   //Se hace una salida por monitoreo en la consola. ELIMINAR / COMENTAR
                }
                JsonUrlVnPedidosPertidax = buffer.toString();


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

            // Una vez obtenida la cadena JSon del primer catalogo se lee linea por lina para insertar en la tabla
            try {
                // Convierte el string con la informaciÑn de los clientes en un array JSON
                JSONArray jsonArr_pedidos_partidax = new JSONArray(JsonUrlVnPedidosPertidax);

                for (int i = 0; i < jsonArr_pedidos_partidax.length(); i++) {
                    JSONObject jsonObjVnPedidosPertidax = jsonArr_pedidos_partidax.getJSONObject(i);
                    dbHandler.insertaPedidosPartidax(jsonObjVnPedidosPertidax);
                }
            } // Fin del Try
            catch (JSONException e) {
                e.printStackTrace();
            } // fin del catch

            /*****************************************************************
             * Bloque para el catalogo de Bancos de Clientes (Listado)       *
             * ***************************************************************/
            connection = null;
            reader = null;
            String JsonUrlBancosClientes = "";


            try {
                URL url = new URL(urlBancosClientes);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                JsonUrlBancosClientes = buffer.toString();


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

            // Una vez obtenida la cadena JSon del segundo catalogo se lee linea por lina para insertar en la tabla
            try {
                // Convierte el string con la información de los clientes en un array JSON
                JSONArray jsonArr_bancosClientes = new JSONArray(JsonUrlBancosClientes);

                for (int i = 0; i < jsonArr_bancosClientes.length(); i++) {
                    JSONObject jsonObjBancosClientes = jsonArr_bancosClientes.getJSONObject(i);
                    dbHandler.insertabancosClientes(jsonObjBancosClientes);
                }
            } // Fin del Try
            catch (JSONException e) {
                e.printStackTrace();
            } // fin del catch

            /*****************************************************************************
             * Bloque para la importacion del catalogo ts_cat_bancos                     *
             * **************************************************************************/
            connection = null;
            reader = null;
            String JsonUrlCatalogoBancos = "";

            try {
                URL url = new URL(urlCatalogoBancos);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                JsonUrlCatalogoBancos = buffer.toString();


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

            // Una vez obtenida la cadena JSon del segundo catalogo se lee linea por lina para insertar en la tabla
            try {
                // Convierte el string con la información de los clientes en un array JSON
                JSONArray jsonArr_catBancos = new JSONArray(JsonUrlCatalogoBancos);

                for (int i = 0; i < jsonArr_catBancos.length(); i++) {
                    JSONObject jsonObjCatalogoBancos = jsonArr_catBancos.getJSONObject(i);
                    dbHandler.insertacatBancos(jsonObjCatalogoBancos);
                }
            } // Fin del Try
            catch (JSONException e) {
                e.printStackTrace();
            } // fin del catch


            return null;
        } // Fin del doInBackground

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pd.isShowing()) {
                pd.dismiss();
            }
            final MyDBHandler dbHandler = new MyDBHandler(NavDrawerActivity.this, null, null, DATABASE_VERSION);
            dbHandler.checkDBStatus();
            final String usuarioLog = dbHandler.ultimoUsuarioRegistrado();

        }
    } // Fin de la clase JsonTask


    private class JsonTaskSendPayment extends AsyncTask<String, String, String> {


        // Bloque para poder usar los metodos de la clase MyDBHandler en la clase JsonTask
        private MyDBHandler dbHandler;

        public JsonTaskSendPayment(MyDBHandler dbHandler) {
            this.dbHandler = dbHandler;
        }

        protected void onPreExecute() {
            super.onPreExecute();

            pdSendPayment = new ProgressDialog(NavDrawerActivity.this);
            pdSendPayment.setTitle("Conectando a servidor Biochem...");
            pdSendPayment.setMessage("Mandando pagos guardados");
            pdSendPayment.setCancelable(false);
            pdSendPayment.show();
        }

        protected String doInBackground(String... params) {

            /*************************************************************
             * Bloque para envio de los pagos :                        *
             * ***********************************************************/
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String JsonUrlEnvioPagos = "", respi = "";
            String urlSendpayment = "http://www.sybrem.com.mx/adsnet/syncmovil/app_envios/ReceptorMovilPagosN.php?jsonCad=" + URLEncoder.encode(dbHandler.transmitePagos());
            //String urlSendpayment = "http://www.sybrem.com.mx/adsnet/syncmovil/ReceptorImagenPagos_prueba.php?jsonCad="+ URLEncoder.encode(dbHandler.transmitePagos());
            //ReceptorImagenPagos_prueba.php
            Log.d("@@##=>", urlSendpayment);
            Log.e("@@#### ==>", urlSendpayment);
            Log.d("@@#### ==>", urlSendpayment);
            try {

                URL url = new URL(urlSendpayment);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                JsonUrlEnvioPagos = buffer.toString().substring(0, 11);
                respi = buffer.toString();
                if (JsonUrlEnvioPagos.toString().equals("SYNCPAGOSOK")) {
                    dbHandler.resetPagos();
                } else {
                    // Sincronizacion no exitosa. No se eliminan pagos
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

            return respi;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pdSendPayment.isShowing()) {
                pdSendPayment.dismiss();
            }
            //reload();
            String msj = result;
            //Crea ventana de alerta.
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(NavDrawerActivity.this);
            dialog1.setMessage((msj.trim().equals("SYNCPAGOSOK")) ? "PAGOS SINCRONIZADOS." : msj + ".");
            dialog1.setTitle("Respuesta del servidor BiOCHEM");
            dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    new JsonTaskSendEntregas(dbHandler).execute();
                }
            });
            dialog1.show();

        }
    } // Fin de la clase JsonTaskSendPayment


    private class JsonTaskSendEntregas extends AsyncTask<String, String, String> {


        // Bloque para poder usar los metodos de la clase MyDBHandler en la clase JsonTask
        private MyDBHandler dbHandler;

        public JsonTaskSendEntregas(MyDBHandler dbHandler) {
            this.dbHandler = dbHandler;
        }

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(NavDrawerActivity.this);
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
            android.support.v7.app.AlertDialog.Builder dialog1 = new android.support.v7.app.AlertDialog.Builder(NavDrawerActivity.this);
            dialog1.setMessage(respuesta_perrax.substring(0, 12).equals("SYNENTREGASOK") ? "Sincronización correcta" : respuesta_perrax);
            dialog1.setTitle("Entrega realizada");
            //Establece el boton de Aceptar y que hacer si se selecciona.
            dialog1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final MyDBHandler dbHandler = new MyDBHandler(NavDrawerActivity.this, null, null, 1);

                    String usuario = dbHandler.ultimoUsuarioRegistrado();

                    dbHandler.resetCatalogs();
                    new JsonTask(dbHandler).execute();
                    return;
                }
            });
            dialog1.show();
        }
    }


}
