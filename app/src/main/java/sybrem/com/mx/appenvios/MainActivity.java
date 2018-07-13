package sybrem.com.mx.appenvios;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int SOLICITUD_PERMISO_WRITE_CALL_LOG = 0;
    //int[] Permisos=new int[7];
    String permisoInternet = Manifest.permission.INTERNET;
    String permisoAccesoInternet = Manifest.permission.ACCESS_NETWORK_STATE;
    String permisoAccesoWifi = Manifest.permission.ACCESS_WIFI_STATE;
    String permisoUbicacion1 = Manifest.permission.ACCESS_FINE_LOCATION;
    String permisoUbicacion2 = Manifest.permission.ACCESS_COARSE_LOCATION;
    String permisoUbicacion3 = Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS;
    String permisoMemoria = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String permisoCamaga = Manifest.permission.CAMERA;
    //Permisos[0]=permisoInternet;
    String[] Permisos = new String[]{permisoMemoria, permisoUbicacion1, permisoCamaga};

    int bandera1 = 0, bandera2 = 0, sumaBanderas = 0;
    String banderaPermiso = "";

    TextView version_app;
    String currentVersionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();

        currentVersionName = BuildConfig.VERSION_NAME;

        ImageView img = (ImageView) findViewById(R.id.inicio);

        img.setClickable(true);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText usuario = (EditText)findViewById(R.id.txtUsuario);
                //EditText password = (EditText)findViewById(R.id.txtPass);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    for (int i = 0; i < Permisos.length; i++) {
                        sumaBanderas += entrarApp(Permisos[i]);
                        Log.e("@@$$ Permiso =>",""+sumaBanderas);
                    }
                    if (sumaBanderas < 3) {
                        /*String usuario = "";

                        Intent explicit_intent;
                        explicit_intent = new Intent(MainActivity.this,MainActivity.class);
                        explicit_intent.putExtra("usuario",usuario);
                        startActivity(explicit_intent);
                        return;*/
                    } else {
                        String usuario = "";

                        Intent explicit_intent;
                        explicit_intent = new Intent(MainActivity.this, Login.class);
                        explicit_intent.putExtra("usuario", usuario);
                        startActivity(explicit_intent);
                        return;
                    }
                } else {
                    String usuario = "";

                    Intent explicit_intent;
                    explicit_intent = new Intent(MainActivity.this, Login.class);
                    explicit_intent.putExtra("usuario", usuario);
                    startActivity(explicit_intent);
                    return;
                }


            }
        });

    }

    int entrarApp(String permiso) {
        int bandera_ubicacion = 0;
        if (ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED) {
            bandera_ubicacion = 1;
        } else {
            banderaPermiso = permiso;
            solicitarPermiso(permiso, "Necesita otorgar los permisos necesarios a la aplicación para poder acceder", SOLICITUD_PERMISO_WRITE_CALL_LOG, this);
        }

        return bandera_ubicacion;
    }

    public static void solicitarPermiso(final String permiso, String justificacion,
                                        final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)) {
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_WRITE_CALL_LOG) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                entrarApp(banderaPermiso);
            } else {
                Toast.makeText(this, "Sin el permiso, no podrá acceder a la aplicación", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
