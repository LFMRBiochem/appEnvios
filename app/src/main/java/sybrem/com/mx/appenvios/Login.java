package sybrem.com.mx.appenvios;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class Login extends AppCompatActivity {

    // Se declara por separado la variable de array de la asignaciÑn del contenido de la tabla.
    private String[] clientes;
    Button cmSyncAccesos, btnBorraDatos; // Con este botÑn descarga solo la tabla de accesos para poder firmarse en el sistema.
    ProgressDialog pd; // Mensaje de progreso en sincronizacion.

    private Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bladeTablet.db";

    String currentVersionName = "";

    Button borra_datos, btn;
    String usuario = "", password = "";
    TextView tvVerchon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final MyDBHandler dbHandler = new MyDBHandler(this, null, null, DATABASE_VERSION);
        dbHandler.checkDBStatus(); // Tiene como fin forzar la creacion de la base de datos.

        currentVersionName = BuildConfig.VERSION_NAME;
        tvVerchon = findViewById(R.id.tvVerchon);
        tvVerchon.setText("Versión "+currentVersionName);

        usuario = ((EditText) findViewById(R.id.txtUsuario)).getText().toString();
        password = ((EditText) findViewById(R.id.txtPass)).getText().toString();

        btnBorraDatos = (Button)findViewById(R.id.btnBorraDatos);
        btnBorraDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msj = "Se borrarán los datos de la aplicación de envíos Biochem\nDeseas continuar?";
                //Crea ventana de alerta.
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(Login.this);
                dialog1.setMessage(msj);
                dialog1.setTitle("Atención!!!");
                dialog1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                dialog1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String msj = "La aplicación se cerrará al terminar de borrar los datos";
                        //Crea ventana de alerta.
                        AlertDialog.Builder dialog11 = new AlertDialog.Builder(Login.this);
                        dialog11.setMessage(msj);
                        dialog11.setTitle("Atención!!!");
                        dialog11.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    // borra los datos de la aplicacion
                                    String packageName = getApplicationContext().getPackageName();
                                    Runtime runtime = Runtime.getRuntime();
                                    runtime.exec("pm clear " + packageName);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog11.show();
                    }
                });
                dialog1.show();
            }
        });

        btn = (Button) findViewById(R.id.btnLogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = ((EditText) findViewById(R.id.txtUsuario)).getText().toString();
                password = ((EditText) findViewById(R.id.txtPass)).getText().toString();

                Boolean valida = false;

                if (usuario.toString().length() <= 0 || password.toString().length() <= 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Proporcione su usuario y password", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Boolean info = dbHandler.checkAccesos();
                if (info == false) {
                    if (CheckNetwork.isInternetAvailable(Login.this)) //returns true if internet available
                    {
                        btn.setVisibility(View.GONE);
                        dbHandler.resetGlAccesos();
                        new JsonTaskAccess(dbHandler).execute();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } else {
                    valida = dbHandler.validacion(usuario, password);
                }

                if (valida == true) {
                    Intent explicit_intent;
                    explicit_intent = new Intent(Login.this, NavDrawerActivity.class);
                    explicit_intent.putExtra("usuario", usuario);
                    startActivity(explicit_intent);

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "El usuario no esta registrado o los datos son incorrectos", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

            }
        });
    }


    private class JsonTaskAccess extends AsyncTask<String, String, String> {
        String url5 = "http://www.sybrem.com.mx/adsnet/syncmovil/app_envios/SyncMovil.php?ruta=0&tabla=axesos";

        // Bloque para poder usar los metodos de la clase MyDBHandler en la clase JsonTask
        private MyDBHandler dbHandler;

        public JsonTaskAccess(MyDBHandler dbHandler) {
            this.dbHandler = dbHandler;
        }
        // Termina bloque de inclusion de los metodos de MyDBHandler (Se uso esta practica debido a que ya esta usado extends en la clase

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(Login.this);
            pd.setTitle("Conectando a servidor Biochem...");
            pd.setMessage("Descargando datos de usuario...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            /*************************************************************
             * Bloque para el catalogo de Accesos:                       *
             * ***********************************************************/
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String JsonUrl5 = "";

            try {
                URL url = new URL(url5);
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
                JsonUrl5 = buffer.toString();


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
                // Convierte el string con la informaciÑn de los clientes en un array JSON
                JSONArray jsonArr_accesos = new JSONArray(JsonUrl5);

                for (int i = 0; i < jsonArr_accesos.length(); i++) {
                    JSONObject jsonObj5 = jsonArr_accesos.getJSONObject(i);
                    dbHandler.insertaGlAccesos(jsonObj5);
                }
            } // Fin del Try
            catch (JSONException e) {
                e.printStackTrace();
            } // fin del catch

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pd.isShowing()) {
                pd.dismiss();
            }
            Boolean valida = dbHandler.validacion(usuario, password);
            if (valida == true) {
                Intent explicit_intent;
                explicit_intent = new Intent(Login.this, NavDrawerActivity.class);
                explicit_intent.putExtra("usuario", usuario);
                startActivity(explicit_intent);
            } else {
                btn.setVisibility(View.VISIBLE);
                Toast toast = Toast.makeText(getApplicationContext(), "El usuario no esta registrado o los datos son incorrectos", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
    } // --------------------------------------------------Fin de la clase JsonTaskAccess--------------------------------------------------

}
