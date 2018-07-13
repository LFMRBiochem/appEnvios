package sybrem.com.mx.appenvios;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CapturaPagoEntrega extends AppCompatActivity implements View.OnClickListener {

    private String[] tiposPago = new String[]{"1- Efectivo", "2- Cheque"};
    private String[] bancosClientes;
    private String[] bancosDepositos;
    String[] companias = {"Biochem", "All4Pets"};
    TextView nomCliente, fechaCheque, mensaje1;
    String ubicacion = "0.0 0.0";

    TableLayout tl_firma, isEfectivo;
    LinearLayout isCheque, isTransfer;

    private SignaturePad miFirma;
    Button firmilla, btnCheque2;

    private static final int DATABASE_VERSION = 2;

    Spinner bancoEmisor_cheque, bancoEmisor_transfer, bancoReceptor_transfer;
    String bandera_forma_pago = "";
    int dia, mes, año;
    boolean GpsStatus, esta_activo;
    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    int bandera_firma;
    EditText edtcomentarios, email_cliente;
    EditText montoEfectivo, montoCheque;

    EditText CtaEmisorCheque, noCheque;
    String ciaPago = "";
    ProgressDialog pdSendPayment,pd;

    String num_pedido = "", num_factura = "", clientex = "", firmax_pagox = "", respue="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_pago_entrega);

        final MyDBHandler dbHandler = new MyDBHandler(this, null, null, DATABASE_VERSION);
        dbHandler.checkDBStatus(); // Tiene como fin forzar la creacion de la base de datos.

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        num_pedido = (String) extras.get("num_pedido");
        num_factura = (String) extras.get("num_factura");
        clientex = (String) extras.get("cliente");
        ciaPago = (String) extras.get("cia");
        firmax_pagox = (String) extras.get("firma_cliente");

        nomCliente = findViewById(R.id.nomCliente);
        nomCliente.setText(clientex);

        bancosClientes = dbHandler.getBancosClientes();
        bancosDepositos = dbHandler.getBancoDeposito();

        fechaCheque = (TextView) findViewById(R.id.textviewCheque2);

        mensaje1 = (TextView) findViewById(R.id.mensaje_idPagos);
        //mensaje1.setVisibility(View.GONE);
        mensaje1.setText(ubicacion);

        isEfectivo = (TableLayout) findViewById(R.id.isEfectivo);
        isEfectivo.setVisibility(View.GONE);

        isCheque = (LinearLayout) findViewById(R.id.isCheque);
        isCheque.setVisibility(View.GONE);

        isTransfer = (LinearLayout) findViewById(R.id.isTransfer);
        isTransfer.setVisibility(View.GONE);

        miFirma = (SignaturePad) findViewById(R.id.firma_pago);
        firmilla = (Button) findViewById(R.id.btnBorraFirma);

        tl_firma = (TableLayout) findViewById(R.id.tr_firma);
        tl_firma.setVisibility(View.GONE);
        miFirma.setVisibility(View.GONE);

        ///PARA PONER LOS VALORES DEL LISTADO DE FORMA DE PAGO
        final Spinner lstViewFormaPago = (Spinner) findViewById(R.id.lstViewFormaPago);
        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tiposPago);
        lstViewFormaPago.setAdapter(adaptador);

        bancoEmisor_cheque = (Spinner) findViewById(R.id.bancoEmisorCheque);
        ArrayAdapter adapter_bancoEmisors = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bancosClientes);
        bancoEmisor_cheque.setAdapter(adapter_bancoEmisors);

        bancoEmisor_transfer = (Spinner) findViewById(R.id.lstViewBancoEmisorts);
        bancoEmisor_transfer.setAdapter(adapter_bancoEmisors);

        bancoReceptor_transfer = (Spinner) findViewById(R.id.lstViewBancoReceptorts);
        ArrayAdapter adapter_bancoReceptors = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bancosDepositos);
        bancoReceptor_transfer.setAdapter(adapter_bancoReceptors);

        //DEFINE ACCIONES A TOMAR SEGUN EL TIPO DE PAGO
        lstViewFormaPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String seleccionado = "", tipo_pagox = "";
            String[] partida_de_madre;
            int tipo_pagox_int = 0;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seleccionado = parent.getItemAtPosition(position).toString();
                partida_de_madre = seleccionado.split("-");
                tipo_pagox = partida_de_madre[0];
                tipo_pagox_int = Integer.parseInt(tipo_pagox);
                switch (tipo_pagox_int) {
                    //Si es EFECTIVO
                    case 1:
                        bandera_forma_pago = "efectivo";
                        isEfectivo.setVisibility(View.VISIBLE);
                        isCheque.setVisibility(View.GONE);
                        isTransfer.setVisibility(View.GONE);
                        break;
                    //Si es CHEQUE
                    case 2:
                        bandera_forma_pago = "cheque";
                        isCheque.setVisibility(View.VISIBLE);
                        isEfectivo.setVisibility(View.GONE);
                        isTransfer.setVisibility(View.GONE);
                        break;
                    case 4:
                        bandera_forma_pago = "transfer";
                        isTransfer.setVisibility(View.VISIBLE);
                        isEfectivo.setVisibility(View.GONE);
                        isCheque.setVisibility(View.GONE);
                        break;
                    case 5:
                        bandera_forma_pago = "transfer";
                        isTransfer.setVisibility(View.VISIBLE);
                        isEfectivo.setVisibility(View.GONE);
                        isCheque.setVisibility(View.GONE);
                        break;
                    default:
                        bandera_forma_pago = "";
                        isTransfer.setVisibility(View.GONE);
                        isEfectivo.setVisibility(View.GONE);
                        isCheque.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCheque2 = (Button) findViewById(R.id.btnCheque2);
        btnCheque2.setOnClickListener(this);

        ///Para el boton de salir
        Button btnSalir = (Button) findViewById(R.id.btnPagosSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyDBHandler dbHandler = new MyDBHandler(CapturaPagoEntrega.this, null, null, 1);
                dbHandler.checkDBStatus(); // Tiene como fin forzar la creacion de la base de datos.

                String usuario = dbHandler.ultimoUsuarioRegistrado();

                Intent explicit_intent;
                explicit_intent = new Intent(CapturaPagoEntrega.this, NavDrawerActivity.class);
                explicit_intent.putExtra("usuario", usuario);
                startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
                return;
            }
        });

        esta_activo = CheckGpsStatus();
        toggleGPSUpdates();

        firmilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miFirma.clear();
            }
        });

       /* miFirma.setOnSignedListener(new SignaturePad.OnSignedListener() {
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
        });*/

        if (firmax_pagox.equals("")){
            bandera_firma = 0;
        }else{
            bandera_firma = 1;
        }

        ///Para el boton de guardar el pago
        Button btnSave = (Button) findViewById(R.id.btnSaved);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                esta_activo = CheckGpsStatus();
                if (!esta_activo) {
                    String msj = "Necesitas activar el GPS para poder continuar,\nSi eliges >> NO ACTIVAR << los cambios se perderan y regresaras al menú principal.";
                    //Crea ventana de alerta.
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(CapturaPagoEntrega.this);
                    dialog1.setMessage(msj);
                    //dialog1.setNegativeButton("",null);
                    dialog1.setTitle("GPS Desactivado");
                    //Establece el boton de Aceptar y que hacer si se selecciona.
                    dialog1.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent1;
                            intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            //startActivity(intent1);
                        }
                    });
                    dialog1.setNegativeButton("No Activar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    //Muestra la ventana esperando respuesta.
                    dialog1.show();
                } else {

                    //Obtenemos la localizacion
                    String datosLocalizacion = mensaje1.getText().toString();

                    if (!datosLocalizacion.equals("0.0 0.0")) {
                        if (!bandera_forma_pago.equals("")) {
                            final MyDBHandler dbHandler = new MyDBHandler(CapturaPagoEntrega.this, null, null, 1);
                            dbHandler.checkDBStatus(); // Tiene como fin forzar la creacion de la base de datos.

                            //Se asigna el valor de los objetos a una variable de los campos comentarios y monto, recibo, referencia, cuenta, cheque y fecha
                            edtcomentarios = (EditText) findViewById(R.id.edtComentarios);
                            //String emailClaient = email_cliente.getText().toString();

                            //Si el tipo de pago es EFECTIVO
                            montoEfectivo = (EditText) findViewById(R.id.montoEfectivo);
                            String monto_efectivo = (bandera_forma_pago.equals("efectivo")) ? montoEfectivo.getText().toString() : "";

                            //Si el tipo de pago es CHEQUE
                            CtaEmisorCheque = (EditText) findViewById(R.id.CtaEmisorCheque);
                            noCheque = (EditText) findViewById(R.id.edtCheque2);
                            montoCheque = (EditText) findViewById(R.id.montoCheque);
                            String bancoE_cheque = (bandera_forma_pago.equals("cheque")) ? bancoEmisor_cheque.getSelectedItem().toString() : "";
                            String monto_cheque = (bandera_forma_pago.equals("cheque")) ? montoCheque.getText().toString() : "";
                            String no_cheque = (bandera_forma_pago.equals("cheque")) ? noCheque.getText().toString() : "";
                            String fecha_cheque = (bandera_forma_pago.equals("cheque")) ? fechaCheque.getText().toString() : "";

                            ///Se extrae la informacion de los campos para ser guardados
                            String cliente = "";//textClientes.getText().toString();
                            String colon = "";//autoCompleteColos.getText().toString();
                            String formaPago = lstViewFormaPago.getSelectedItem().toString();

                            //Lo siguiente se evealua independientemente del tipo de pago
                            String comentarios = edtcomentarios.getText().toString();

                            int bandera_errores = 0;
                            String msj = "No podra guardar los cambios por lo siguiente:\n";

                            String bArray_miFirmilla = "";
                            /*if (bandera_firma != 0) {
                                //Se valida la parte de la imagen de la firma del cliente
                                Bitmap miFirmilla = miFirma.getTransparentSignatureBitmap(false);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                miFirmilla.compress(Bitmap.CompressFormat.PNG, 100, bos);
                                bArray_miFirmilla = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
                            } else {
                                bandera_errores++;
                                msj += "*El cliente debe firmar el pago\n";
                            }*/

                            ///Se valida que se haya seleccionado algun cliente
                            /*if (cliente.length() == 0) {
                                bandera_errores++;
                                msj += "*Debe elegir al cliente.\n";
                            }*/
                            ///Si no se escribieron algunos comentarios
                            if (comentarios.length() == 0) {
                                bandera_errores++;
                                msj += "*Debe escribir algunos comentarios.\n";
                            }
                            /*if (emailClaient.length() == 0) {
                                bandera_errores++;
                                msj += "*Debe escribir el email del cliente.\n";
                            }*/
                            //Se comienzan con las validaciones dependiendo el tipo de pago elegido
                            switch (bandera_forma_pago) {
                                case "efectivo":
                                    if (monto_efectivo.length() == 0) {
                                        bandera_errores++;
                                        msj += "*El monto del pago no fue definido\n";
                                    } else if (Double.parseDouble(monto_efectivo) <= 0) {
                                        bandera_errores++;
                                        msj += "*El monto del pago no puede ser menor o igual a cero\n";
                                    }
                                    break;
                                case "cheque":
                                    if (bancoE_cheque.length() == 0) {
                                        bandera_errores++;
                                        msj += "*El banco emisor del cheque no fue definido\n";
                                    }
                                    if (monto_cheque.length() == 0) {
                                        bandera_errores++;
                                        msj += "*El monto del cheque no fue definido\n";
                                    } else if (Double.parseDouble(monto_cheque) <= 0) {
                                        bandera_errores++;
                                        msj += "*El monto del cheque no puede ser menor o igual a cero\n";
                                    }
                                    if (no_cheque.length() == 0) {
                                        bandera_errores++;
                                        msj += "*El número de cheque no fue definido\n";
                                    }
                                    if (fecha_cheque.equals("_/_/_")) {
                                        bandera_errores++;
                                        msj += "*La fecha del cheque no fue definida\n";
                                    }
                                    break;
                                /*case "transfer":
                                    if (referencia.length() == 0) {
                                        bandera_errores++;
                                        msj += "La referencia del pago no fue definida\n";
                                    }
                                    if (banco_emisor_transfer.length() == 0) {
                                        bandera_errores++;
                                        msj += "El banco emisor no fue definido\n";
                                    }
                                    if (banco_receptor_transfer.length() == 0) {
                                        bandera_errores++;
                                        msj += "El banco receptor no fue definido\n";
                                    }
                                    if (fecha_transfer.equals("_/_/_")) {
                                        bandera_errores++;
                                        msj += "La fecha no fue definida\n";
                                    }
                                    if (monto_trasfer.length() == 0) {
                                        bandera_errores++;
                                        msj += "El monto no fue definido\n";
                                    } else if (Double.parseDouble(monto_trasfer) <= 0) {
                                        bandera_errores++;
                                        msj += "El monto no puede ser menor o igual a cero\n";
                                    }
                                    break;*/
                            }
                            if (bandera_errores > 0) {

                                //Crea ventana de alerta.
                                AlertDialog.Builder dialog1 = new AlertDialog.Builder(CapturaPagoEntrega.this);
                                dialog1.setMessage(msj);
                                dialog1.setTitle("Error al guardar");
                                dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                });
                                dialog1.show();
                                return;
                            } else {
                                //Partimos el string el primero es latitude y el segundo longitude
                                String[] separatedLoc = datosLocalizacion.split(" ");
                                String latitude = "";
                                String longitude = "";
                                latitude = separatedLoc[0];
                                longitude = separatedLoc[1];

                                ContentValues documentosEncabezado = new ContentValues();
                                ContentValues documentosPartidas = new ContentValues();

                                //Variables que faltan para el llenado
                                String dbCveCompania = "019";
                                String dbCveDocumento = "PAG";

                                //Se obtiene el siguiente numero de pago local
                                long dbNumDocumento = dbHandler.getSiguientePago();

                                //Se saca la fecha de registro
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String fechaRegistro = sdf.format(cal.getTime());

                                //Se divide el dato del cliente para obtener la clave del cliente
                                String[] separatedCliente = cliente.split("-");
                                String cve_cliente = separatedCliente[0];

                                //dbHandler.actualizaEmailcliente(emailClaient,cve_cliente);
                                String email_client = "";

                                //Se divide el dato forma de pago
                                String[] separatedFormaPago = formaPago.split("-");
                                String cve_forma_pago = separatedFormaPago[0];

                                //Se obtiene el agente al cual esta asociado el cliente
                                String usuario = dbHandler.ultimoUsuarioRegistrado();
                                String cve_agente = "";
                                //String tipoUsuario = dbHandler.tipoUsuario(usuario);
                                /*if (tipoUsuario.toString().equals("A")) {
                                    cve_agente = usuario;
                                } else {
                                    cve_agente = dbHandler.agenteSeleccionado();
                                }*/

                                String fecha = null;
                                String monto = null;
                                String cheque = "", cve_banco_emisor = "", cve_banco_deposito = "";
                                String[] separatedBancoEmisor = null;

                                switch (bandera_forma_pago) {

                                    case "efectivo":
                                        fecha = "";
                                        monto = monto_efectivo;
                                        break;
                                    case "cheque":
                                        fecha = fecha_cheque;
                                        monto = monto_cheque;
                                        cheque = no_cheque;
                                        separatedBancoEmisor = bancoE_cheque.split("-");
                                        cve_banco_emisor = separatedBancoEmisor[0];
                                        break;
                                    /*case "transfer":
                                        fecha = fecha_transfer;
                                        monto = monto_trasfer;
                                        separatedBancoEmisor = bancoE_cheque.split("-");
                                        cve_banco_emisor = separatedBancoEmisor[0];
                                        cve_banco_deposito = banco_receptor_transfer;
                                        break;*/
                                }

                                String cve_persona = "", cve_documento = "", cuenta = "";

                                // Asigna el encabezado de la tabla vn_documentos_encabezado
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVECOMPANIA, ciaPago);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVEDOCUMENTO, dbCveDocumento);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_NUMDOCUMENTO, dbNumDocumento);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_FECHADOCUMENTO, fecha);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_FECHAREGISTRO, fechaRegistro);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_TIPODOCUMENTO, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_SUMA, monto);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_DESCUENTO, "0");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_SUBTOTAL, monto);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_TOTAL, monto);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVECLIENTE, num_factura);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVEAGENTE, cve_agente);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIO, usuario);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVEMONEDA, "PES");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_TIPOCAMBIO, "1");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_COMENTARIOS, comentarios);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_ESTATUS, "G");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_TOTALPAGADO, monto);
                                //documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_RECIBOPAGO, recibo);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_RECIBOPAGO, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CONCILIADO, "0");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_FECHACONCILIACION, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_REFERENCIACONCILIACION, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_EXISTEACLARACION, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_PERSONADEPOSITO, cve_persona);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_DOCTORESPALDO, cve_documento);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIOCONCILIACION, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_AUDITORIA, "SYNC");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSAUDITORIA, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSOTROS, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIODESCONCILIACION, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSDESCONCILIACION, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_IEPS3, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_IEPS35, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_TOTALIEPS, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_IEPS6, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_IEPS7, "");
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_LATITUDE, latitude);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_LONGITUDE, longitude);
                                documentosEncabezado.put(dbHandler.COL_VNDOCUMENTOSENCABEZADO_TIPOCOBRANZA, "");

                                // Asigna las patrtidas de la tabla vn_documentos_partidas
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_CVECOMPANIA, ciaPago);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_CVEDOCUMENTO, dbCveDocumento);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_NUMDOCUMENTO, dbNumDocumento);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_NUMPARTIDA, "1");
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_CVETIPOPAGO, cve_forma_pago);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_CVEBANCOEMISOR, cve_banco_emisor);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_CUENTACHEQUECLIENTE, cuenta);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_NUMCHEQUE, cheque);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_CVEBANCO, cve_banco_deposito);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_REFERENCIA, "");
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_FECHA_BANCO, fecha);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_TOTAL, monto);
                                documentosPartidas.put(dbHandler.COL_VNDOCUMENTOSPARTIDAS_IMGFIRMA, firmax_pagox);

                                if (dbHandler.registraPago(documentosEncabezado, documentosPartidas)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "El pago se ha guardado correctamente con el numero: " + dbNumDocumento, Toast.LENGTH_SHORT);
                                    toast.show();

                                    if (CheckNetwork.isInternetAvailable(CapturaPagoEntrega.this)) //returns true if internet available
                                    {
                                        //Log.d("@@###json->",dbHandler.transmitePagos());
                                        new JsonTaskSendPayment(dbHandler).execute(); // Envia todos los pagos pendientes x transmitir al sevidor. No los borra local
                                    }

                                    //reload();
                                }
                            }

                        }//Fin si no se selecciono una forma de pago
                        else {
                            String msj = "Debe seleccionar una forma de pago para poder continuar";
                            //Crea ventana de alerta.
                            AlertDialog.Builder dialog1 = new AlertDialog.Builder(CapturaPagoEntrega.this);
                            dialog1.setMessage(msj);
                            dialog1.setTitle("Error al guardar");
                            dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            dialog1.show();
                        }
                    }// Fin del if que verifica que la latitud y longitud de la ubicacion no sea "0.0 0.0"
                    else {
                        String msj = "Tu ubicación no fue obtenida con éxito\nintenta de nuevo más tarde";
                        //Crea ventana de alerta.
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(CapturaPagoEntrega.this);
                        dialog1.setMessage(msj);
                        //dialog1.setNegativeButton("",null);
                        dialog1.setTitle("Error de GPS");
                        //Establece el boton de Aceptar y que hacer si se selecciona.
                        dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        });
                        dialog1.show();
                    }
                }//Fin del else que verifica que el GPS esté activo
            }

        }); ///Fin del metodo para guardar el pago
    }

    private class JsonTaskSendPayment extends AsyncTask<String, String, String> {


        // Bloque para poder usar los metodos de la clase MyDBHandler en la clase JsonTask
        private MyDBHandler dbHandler;

        public JsonTaskSendPayment(MyDBHandler dbHandler) {
            this.dbHandler = dbHandler;
        }

        protected void onPreExecute() {
            super.onPreExecute();

            pdSendPayment = new ProgressDialog(CapturaPagoEntrega.this);
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
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(CapturaPagoEntrega.this);
            dialog1.setMessage((msj.trim().equals("SYNCPAGOSOK")) ? "PAGO SINCRONIZADO." : msj + ".");
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

            pd = new ProgressDialog(CapturaPagoEntrega.this);
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
            android.support.v7.app.AlertDialog.Builder dialog1 = new android.support.v7.app.AlertDialog.Builder(CapturaPagoEntrega.this);
            dialog1.setMessage(respuesta_perrax.substring(0, 12).equals("SYNENTREGASOK") ? "Sincronización correcta" : respuesta_perrax);
            dialog1.setTitle("Entrega realizada");
            //Establece el boton de Aceptar y que hacer si se selecciona.
            dialog1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final MyDBHandler dbHandler = new MyDBHandler(CapturaPagoEntrega.this, null, null, 1);

                    String usuario = dbHandler.ultimoUsuarioRegistrado();

                    Intent explicit_intent;
                    explicit_intent = new Intent(CapturaPagoEntrega.this, NavDrawerActivity.class);
                    explicit_intent.putExtra("usuario", usuario);
                    startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    finish();
                    return;
                }
            });
            dialog1.show();
        }
    }

    //Metodo para refrescar la pantalla cuando se guarda el pago
    public void reload() {

        final MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.checkDBStatus(); // Tiene como fin forzar la creacion de la base de datos.

        String usuario = dbHandler.ultimoUsuarioRegistrado();

        Intent explicit_intent;
        explicit_intent = new Intent(CapturaPagoEntrega.this, NavDrawerActivity.class);
        explicit_intent.putExtra("usuario", usuario);
        startActivity(explicit_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
        return;
    }

    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        año = c.get(Calendar.YEAR);

        if (v == btnCheque2) {
            DatePickerDialog dpd = new DatePickerDialog(CapturaPagoEntrega.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int año, int mes, int dia) {
                    String fechacs = año + "/" + (mes + 1) + "/" + dia;
                    fechaCheque.setText(fechacs);
                }
            }, año, mes, dia);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis() - 2000);
            dpd.show();
        }
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Activar GPS")
                .setMessage("Necesitas activar el GPS para poder continuar.")
                .setPositiveButton("Activar ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        if (!CheckNetwork.isInternetAvailable(CapturaPagoEntrega.this)) //returns true if internet available
        {
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } else {
                if (locationManager.getAllProviders().contains(LocationManager.PASSIVE_PROVIDER)) {
                    return locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                } else {
                    return false;
                }
            }
        } else {
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
    }

    public void toggleGPSUpdates() {
        if (!checkLocation())
            return;
        locationManager.removeUpdates(locationListenerGPS);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        if (!CheckNetwork.isInternetAvailable(CapturaPagoEntrega.this)) //returns true if internet available
        {
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 1000, 5, locationListenerGPS);
            } else {
                Log.d("Criteria => ", "criteria");
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                locationManager.getBestProvider(criteria, true);
                String provider = locationManager.getBestProvider(criteria, true);

                Location location = locationManager.getLastKnownLocation(provider);

                updateWithNewLocation(location);

                locationManager.requestLocationUpdates(provider, 2000, 5, locationListenerGPS);
            }
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 2 * 1000, 5, locationListenerGPS);
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ubicacion = "" + latitudeGPS + " " + longitudeGPS;
                    mensaje1.setText(ubicacion);
                    Toast.makeText(CapturaPagoEntrega.this, "Localización correcta", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
    /* Fin de la clase localizacion */

    private void updateWithNewLocation(Location location) {

        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            ubicacion = "" + lat + " " + lng;
        } else {
            ubicacion = "0.0 0.0";
        }

        mensaje1.setText(ubicacion);
    }

    public boolean CheckGpsStatus() {

        locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if (!CheckNetwork.isInternetAvailable(CapturaPagoEntrega.this)) //returns true if internet available
        {
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } else {
                if (locationManager.getAllProviders().contains(LocationManager.PASSIVE_PROVIDER)) {
                    GpsStatus = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                } else {
                    GpsStatus = false;
                }
            }
        } else {
            GpsStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

        if (GpsStatus == true) {
            Toast toast = Toast.makeText(getApplicationContext(), "GPS activado", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "GPS desactivado", Toast.LENGTH_SHORT);
            toast.show();
        }

        return GpsStatus;
    }/* Fin de la clase localizacion */
}
