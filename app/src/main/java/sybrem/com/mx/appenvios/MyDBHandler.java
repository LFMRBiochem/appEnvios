package sybrem.com.mx.appenvios;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "bladeTablet.db";

    //###### Tablas SQL
    public static final String TABLE_GL_ACCESOS = "gl_accesos";
    public static final String TABLE_GL_SYNC = "gl_sync";
    public static final String TABLE_GL_BITACORA_ACCESOS = "gl_bitacora_accesos";
    public static final String TABLE_VN_FACTURAS_PEDIDOS = "vn_facturas_pedidos";
    public static final String TABLE_VN_PEDIDOS_ENCABEZAO = "vn_pedidos_encabezao";
    public static final String TABLE_VN_PEDIDOS_PARTIDAX = "vn_pedidos_partidax";
    public static final String TABLE_VN_DOCUMENTOS_ENCABEZADO = "vn_documentos_encabezado";
    public static final String TABLE_VN_DOCUMENTOS_PARTIDAS = "vn_documentos_partidas";
    public static final String TABLE_VN_CAT_BANCOS_CLIENTES = "vn_cat_bancos_clientes";
    public static final String TABLE_TS_CAT_BANCOS = "ts_cat_bancos";
    public static final String TABLE_VN_ENTREGAS_TABLETA = "vn_entregas_tableta";

    //Columnas para gl_accesos

    // gl_accesos:
    public static final String COL_GLACCESOS_ID = "_id";
    public static final String COL_GLACCESOS_CVEUSUARIO = "cve_usuario";
    public static final String COL_GLACCESOS_PASSWORD = "password";
    public static final String COL_GLACCESOS_TIPOUSUARIO = "tipo_usuario";
    public static final String COL_GLACCESOS_ESTATUS = "estatus";
    public static final String COL_GLACCESOS_ACTUALIZO_PASSWORD = "actualizo_password";
    public static final String COL_GLACCESOS_ULTIMAACTUALIZACION = "ultima_actualizacion";
    public static final String COL_GLACCESOS_IMEI = "imei";

    // TAG JSON para gl_accesos:
    public static final String TAG_GLACCESOS_ID = "_id";
    public static final String TAG_GLACCESOS_CVEUSUARIO = "cve_usuario";
    public static final String TAG_GLACCESOS_PASSWORD = "password";
    public static final String TAG_GLACCESOS_TIPOUSUARIO = "tipo_usuario";
    public static final String TAG_GLACCESOS_ESTATUS = "estatus";
    public static final String TAG_GLACCESOS_ACTUALIZO_PASSWORD = "actualizo_password";
    public static final String TAG_GLACCESOS_ULTIMAACTUALIZACION = "ultima_actualizacion";
    public static final String TAG_GLACCESOS_IMEI = "imei";

    // gl_sync: == ESTA TABLA NO TIENE TAG DEBIDO A QUE NO HAY SINCRONIZACION DE LA MISMA
    public static final String COL_GLSYNC_ID = "_id";
    public static final String COL_GLSYNC_CVEUSUARIO = "cve_usuario";
    public static final String COL_GLSYNC_FECHASYNC = "fecha_sync";
    public static final String COL_GLSYNC_EXITOSYNC = "exito_sync";

    //gl_bitacora_accesos
    public static final String COL_GLBITACORAACCESOS_ID = "_id";
    public static final String COL_GLBITACORAACCESOS_CVEUSUARIO = "cve_usuario";
    public static final String COL_GLBITACORAACCESOS_FECHAREGISTRO = "fecha_registro";

    //Tabla VN_FACTURAS_PEDIDOS
    public static final String COL_VNFACTURASPEDIDOS_ID = "_id";
    public static final String COL_VNFACTURASPEDIDOS_CVE_COMPANIA = "cve_compania";
    public static final String COL_VNFACTURASPEDIDOS_NUM_DOCUMENTO = "num_documento";
    public static final String COL_VNFACTURASPEDIDOS_CVE_DOCUMENTO = "cve_documento";
    public static final String COL_VNFACTURASPEDIDOS_TOTAL_FACTURA = "total_factura";

    //Bloque para vn_pedidos_encabezao
    public static final String COL_VNPEDIDOSENCABEZAO_ID = "_id";
    public static final String COL_VNPEDIDOSENCABEZAO_CVE_COMPANIA = "cve_compania";
    public static final String COL_VNPEDIDOSENCABEZAO_NUM_PEDIDO = "num_pedido";
    public static final String COL_VNPEDIDOSENCABEZAO_NUM_FACTURA = "num_factura";
    public static final String COL_VNPEDIDOSENCABEZAO_ALMACEN = "almacen";
    public static final String COL_VNPEDIDOSENCABEZAO_CLIENTE = "cliente";
    public static final String COL_VNPEDIDOSENCABEZAO_AGENTE = "agente";
    public static final String COL_VNPEDIDOSENCABEZAO_FECHA_PEDIDO = "fecha_pedido";
    public static final String COL_VNPEDIDOSENCABEZAO_SINCRONIZADO = "sincronizado";


    //Bloque para vn_pedidos_partidax
    public static final String COL_VNPEDIDOSPARTIDAX_ID = "_id";
    public static final String COL_VNPEDIDOSPARTIDAX_CVE_COMPANIA = "cve_compania";
    public static final String COL_VNPEDIDOSPARTIDAX_NUM_PEDIDO = "num_pedido";
    public static final String COL_VNPEDIDOSPARTIDAX_NOM_PRODUCTO = "nom_producto";
    public static final String COL_VNPEDIDOSPARTIDAX_CANTIDAD = "cantidad";
    public static final String COL_VNPEDIDOSPARTIDAX_IMG_PRODUCTO = "img_producto";

    // Tabla vn_documentos_encabezado +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String COL_VNDOCUMENTOSENCABEZADO_ID = "_id";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVECOMPANIA = "cve_compania";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVEDOCUMENTO = "cve_documento";
    public static final String COL_VNDOCUMENTOSENCABEZADO_NUMDOCUMENTO = "num_documento";
    public static final String COL_VNDOCUMENTOSENCABEZADO_FECHADOCUMENTO = "fecha_documento";
    public static final String COL_VNDOCUMENTOSENCABEZADO_FECHAREGISTRO = "fecha_registro";
    public static final String COL_VNDOCUMENTOSENCABEZADO_TIPODOCUMENTO = "tipo_documento";
    public static final String COL_VNDOCUMENTOSENCABEZADO_SUMA = "suma";
    public static final String COL_VNDOCUMENTOSENCABEZADO_DESCUENTO = "descuento";
    public static final String COL_VNDOCUMENTOSENCABEZADO_SUBTOTAL = "subtotal";
    public static final String COL_VNDOCUMENTOSENCABEZADO_TOTAL = "total";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVECLIENTE = "cve_cliente";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVEAGENTE = "cve_agente";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIO = "cve_usuario";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVEMONEDA = "cve_moneda";
    public static final String COL_VNDOCUMENTOSENCABEZADO_TIPOCAMBIO = "tipo_cambio";
    public static final String COL_VNDOCUMENTOSENCABEZADO_COMENTARIOS = "comentarios";
    public static final String COL_VNDOCUMENTOSENCABEZADO_ESTATUS = "estatus";
    public static final String COL_VNDOCUMENTOSENCABEZADO_TOTALPAGADO = "total_pagado";
    public static final String COL_VNDOCUMENTOSENCABEZADO_RECIBOPAGO = "recibo_pago";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CONCILIADO = "conciliado";
    public static final String COL_VNDOCUMENTOSENCABEZADO_FECHACONCILIACION = "fecha_conciliacion";
    public static final String COL_VNDOCUMENTOSENCABEZADO_REFERENCIACONCILIACION = "referencia_conciliacion";
    public static final String COL_VNDOCUMENTOSENCABEZADO_EXISTEACLARACION = "existe_aclaracion";
    public static final String COL_VNDOCUMENTOSENCABEZADO_PERSONADEPOSITO = "persona_deposito";
    public static final String COL_VNDOCUMENTOSENCABEZADO_DOCTORESPALDO = "docto_respaldo";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIOCONCILIACION = "cve_usuario_conciliacion";
    public static final String COL_VNDOCUMENTOSENCABEZADO_AUDITORIA = "auditoria";
    public static final String COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSAUDITORIA = "comentarios_auditoria";
    public static final String COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSOTROS = "comentarios_otros";
    public static final String COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIODESCONCILIACION = "cve_usuario_desconciliacion";
    public static final String COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSDESCONCILIACION = "comentarios_desconciliacion";
    public static final String COL_VNDOCUMENTOSENCABEZADO_IEPS3 = "ieps_3";
    public static final String COL_VNDOCUMENTOSENCABEZADO_IEPS35 = "ieps_3_5";
    public static final String COL_VNDOCUMENTOSENCABEZADO_TOTALIEPS = "total_ieps";
    public static final String COL_VNDOCUMENTOSENCABEZADO_IEPS6 = "ieps_6";
    public static final String COL_VNDOCUMENTOSENCABEZADO_IEPS7 = "ieps_7";
    public static final String COL_VNDOCUMENTOSENCABEZADO_LATITUDE = "latitude";
    public static final String COL_VNDOCUMENTOSENCABEZADO_LONGITUDE = "longitude";
    public static final String COL_VNDOCUMENTOSENCABEZADO_TIPOCOBRANZA = "tipo_cobranza";

    // TAG JSON de vn_documentos_encabezado +++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVECOMPANIA = "cve_compania";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVEDOCUMENTO = "cve_documento";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_NUMDOCUMENTO = "num_documento";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_FECHADOCUMENTO = "fecha_documento";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_FECHAREGISTRO = "fecha_registro";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_TIPODOCUMENTO = "tipo_documento";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_SUMA = "suma";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_DESCUENTO = "descuento";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_SUBTOTAL = "subtotal";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_TOTAL = "total";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVECLIENTE = "cve_cliente";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVEAGENTE = "cve_agente";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVEUSUARIO = "cve_usuario";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVEMONEDA = "cve_moneda";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_TIPOCAMBIO = "tipo_cambio";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_COMENTARIOS = "comentarios";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_ESTATUS = "estatus";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_TOTALPAGADO = "total_pagado";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_RECIBOPAGO = "recibo_pago";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CONCILIADO = "conciliado";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_FECHACONCILIACION = "fecha_conciliacion";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_REFERENCIACONCILIACION = "referencia_conciliacion";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_EXISTEACLARACION = "existe_aclaracion";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_PERSONADEPOSITO = "persona_deposito";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_DOCTORESPALDO = "docto_respaldo";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVEUSUARIOCONCILIACION = "cve_usuario_conciliacion";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_AUDITORIA = "auditoria";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_COMENTARIOSAUDITORIA = "comentarios_auditoria";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_COMENTARIOSOTROS = "comentarios_otros";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_CVEUSUARIODESCONCILIACION = "cve_usuario_desconciliacion";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_COMENTARIOSDESCONCILIACION = "comentarios_desconciliacion";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_IEPS3 = "ieps_3";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_IEPS35 = "ieps_3_5";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_TOTALIEPS = "total_ieps";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_IEPS6 = "ieps_6";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_IEPS7 = "ieps_7";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_LATITUDE = "latitude";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_LONGITUDE = "longitude";
    public static final String TAG_VNDOCUMENTOSENCABEZADO_TIPOCOBRANZA = "tipo_cobranza";

    // Tabla vn_documentos_patidas ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String COL_VNDOCUMENTOSPARTIDAS_ID = "_id";
    public static final String COL_VNDOCUMENTOSPARTIDAS_CVECOMPANIA = "cve_compania";
    public static final String COL_VNDOCUMENTOSPARTIDAS_CVEDOCUMENTO = "cve_documento";
    public static final String COL_VNDOCUMENTOSPARTIDAS_NUMDOCUMENTO = "num_documento";
    public static final String COL_VNDOCUMENTOSPARTIDAS_NUMPARTIDA = "num_partida";
    public static final String COL_VNDOCUMENTOSPARTIDAS_CVETIPOPAGO = "cve_tipo_pago";
    public static final String COL_VNDOCUMENTOSPARTIDAS_CVEBANCOEMISOR = "cve_banco_emisor";
    public static final String COL_VNDOCUMENTOSPARTIDAS_CUENTACHEQUECLIENTE = "cuenta_cheque_cliente";
    public static final String COL_VNDOCUMENTOSPARTIDAS_NUMCHEQUE = "num_cheque";
    public static final String COL_VNDOCUMENTOSPARTIDAS_CVEBANCO = "cve_banco";
    public static final String COL_VNDOCUMENTOSPARTIDAS_REFERENCIA = "referencia";
    public static final String COL_VNDOCUMENTOSPARTIDAS_FECHA_BANCO = "fecha_banco";
    public static final String COL_VNDOCUMENTOSPARTIDAS_TOTAL = "total";
    public static final String COL_VNDOCUMENTOSPARTIDAS_IMGFIRMA = "firma_cliente";

    // TAG JSON vn_documentos_partidas ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String TAG_VNDOCUMENTOSPARTIDAS_CVECOMPANIA = "cve_compania";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_CVEDOCUMENTO = "cve_documento";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_NUMDOCUMENTO = "num_documento";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_NUMPARTIDA = "num_partida";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_CVETIPOPAGO = "cve_tipo_pago";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_CVEBANCOEMISOR = "cve_banco_emisor";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_CUENTACHEQUECLIENTE = "cuenta_cheque_cliente";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_NUMCHEQUE = "num_cheque";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_CVEBANCO = "cve_banco";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_REFERENCIA = "referencia";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_FECHA_BANCO = "fecha_banco";
    public static final String TAG_VNDOCUMENTOSPARTIDAS_TOTAL = "total";

    // Tabla vn_cat_bancos_clientes +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String COL_VNCATBANCOSCLIENTES_ID = "_id";
    public static final String COL_VNCATBANCOSCLIENTES_CVEBANCOEMISOR = "cve_banco_emisor";
    public static final String COL_VNCATBANCOSCLIENTES_NOMBREBANCO = "nombre_banco";

    // TAG JSON de vn_cat_bancos_clientes +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String TAG_VNCATBANCOSCLIENTES_CVEBANCOEMISOR = "cve_banco_emisor";
    public static final String TAG_VNCATBANCOSCLIENTES_NOMBREBANCO = "nombre_banco";

    // Tabla ts_cat_bancos ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String COL_TSCATBANCOS_ID = "_id";
    public static final String COL_TSCATBANCOS_CVECOMPANIA = "cve_compania";
    public static final String COL_TSCATBANCOS_CVEBANCO = "cve_banco";
    public static final String COL_TSCATBANCOS_NOMBRECORTO = "nombre_corto";
    public static final String COL_TSCATBANCOS_NOMBREBANCO = "nombre_banco";
    public static final String COL_TSCATBANCOS_MOSTRARVENTAS = "mostrar_ventas";
    public static final String COL_TSCATBANCOS_CVE = "cve";

    // TAG JSON ts_cat_bancos +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final String TAG_TSCATBANCOS_CVECOMPANIA = "cve_compania";
    public static final String TAG_TSCATBANCOS_CVEBANCO = "cve_banco";
    public static final String TAG_TSCATBANCOS_NOMBRECORTO = "nombre_corto";
    public static final String TAG_TSCATBANCOS_NOMBREBANCO = "nombre_banco";
    public static final String TAG_TSCATBANCOS_MOSTRARVENTAS = "mostrar_ventas";
    public static final String TAG_TSCATBANCOS_CVE = "cve";

    public static final String COL_VNENTREGASTABLETA_ID = "_id";
    public static final String COL_VNENTREGASTABLETA_NUM_FACTURA = "num_factura";
    public static final String COL_VNENTREGASTABLETA_CVE_COMPANIA = "cve_compania";
    public static final String COL_VNENTREGASTABLETA_FIRMA = "firma";
    public static final String COL_VNENTREGASTABLETA_CVE_USUARIO_CAPTURA = "cve_usuario_captura";
    public static final String COL_VNENTREGASTABLETA_SINCRONIZADO = "sincronizado";


    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bloque para la tabla local gl_bitacora_accesos
        String CREATE_GL_BITACORA_ACCESOS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GL_BITACORA_ACCESOS + "(" +
                COL_GLBITACORAACCESOS_ID + " INTEGER PRIMARY KEY, " +
                COL_GLBITACORAACCESOS_CVEUSUARIO + " TEXT, " +
                COL_GLBITACORAACCESOS_FECHAREGISTRO + " TEXT " +
                ")";
        db.execSQL(CREATE_GL_BITACORA_ACCESOS_TABLE);

        // Bloque: TABLE_GL_ACCESOS = "gl_accesos"
        String CREATE_GL_ACCESOS_TABLE = "CREATE TABLE  IF NOT EXISTS " + TABLE_GL_ACCESOS + "(" +
                COL_GLACCESOS_ID + " INTEGER PRIMARY KEY, " +
                COL_GLACCESOS_CVEUSUARIO + " TEXT, " +
                COL_GLACCESOS_PASSWORD + " BLOB, " +
                COL_GLACCESOS_TIPOUSUARIO + " TEXT, " +
                COL_GLACCESOS_ESTATUS + " INTEGER, " +
                COL_GLACCESOS_ACTUALIZO_PASSWORD + " INTEGER, " +
                COL_GLACCESOS_ULTIMAACTUALIZACION + " TEXT, " +
                COL_GLACCESOS_IMEI + " TEXT " +
                ")";
        db.execSQL(CREATE_GL_ACCESOS_TABLE);

        // Bloque para la tabla local GL_SYNC
        String CREATE_GL_SYNC_TABLE = "CREATE TABLE  IF NOT EXISTS " + TABLE_GL_SYNC + "(" +
                COL_GLSYNC_ID + " INTEGER PRIMARY KEY, " +
                COL_GLSYNC_CVEUSUARIO + " TEXT, " +
                COL_GLSYNC_FECHASYNC + " TEXT, " +
                COL_GLSYNC_EXITOSYNC + " INTEGER " +
                ")";
        db.execSQL(CREATE_GL_SYNC_TABLE);

        //Bloque para vn_facturas_pedidos
        String CREATE_VN_FACTURAS_PEDIDOS = "CREATE TABLE IF NOT EXISTS " + TABLE_VN_FACTURAS_PEDIDOS + "(" +
                COL_VNFACTURASPEDIDOS_ID + " INTEGER PRIMARY KEY, " +
                COL_VNFACTURASPEDIDOS_CVE_COMPANIA + " TEXT, " +
                COL_VNFACTURASPEDIDOS_NUM_DOCUMENTO + " TEXT, " +
                COL_VNFACTURASPEDIDOS_CVE_DOCUMENTO + " TEXT, " +
                COL_VNFACTURASPEDIDOS_TOTAL_FACTURA + " TEXT " +
                ")";
        db.execSQL(CREATE_VN_FACTURAS_PEDIDOS);

        //Bloque para vn_facturas_pedidos
        String CREATE_VN_PEDIDOS_ENCABEZAO = "CREATE TABLE IF NOT EXISTS " + TABLE_VN_PEDIDOS_ENCABEZAO + "(" +
                COL_VNPEDIDOSENCABEZAO_ID + " INTEGER PRIMARY KEY, " +
                COL_VNPEDIDOSENCABEZAO_CVE_COMPANIA + " TEXT, " +
                COL_VNPEDIDOSENCABEZAO_NUM_PEDIDO + " TEXT, " +
                COL_VNPEDIDOSENCABEZAO_NUM_FACTURA + " TEXT, " +
                COL_VNPEDIDOSENCABEZAO_ALMACEN + " TEXT, " +
                COL_VNPEDIDOSENCABEZAO_CLIENTE + " TEXT, " +
                COL_VNPEDIDOSENCABEZAO_AGENTE + " TEXT, " +
                COL_VNPEDIDOSENCABEZAO_FECHA_PEDIDO + " TEXT, " +
                COL_VNPEDIDOSENCABEZAO_SINCRONIZADO + " TEXT " +
                ")";
        db.execSQL(CREATE_VN_PEDIDOS_ENCABEZAO);

        //Bloque para vn_facturas_pedidos
        String CREATE_VN_PEDIDOS_PARTIDAX = "CREATE TABLE IF NOT EXISTS " + TABLE_VN_PEDIDOS_PARTIDAX + "(" +
                COL_VNPEDIDOSPARTIDAX_ID + " INTEGER PRIMARY KEY, " +
                COL_VNPEDIDOSPARTIDAX_CVE_COMPANIA + " TEXT, " +
                COL_VNPEDIDOSPARTIDAX_NUM_PEDIDO + " TEXT, " +
                COL_VNPEDIDOSPARTIDAX_NOM_PRODUCTO + " TEXT, " +
                COL_VNPEDIDOSPARTIDAX_CANTIDAD + " TEXT, " +
                COL_VNPEDIDOSPARTIDAX_IMG_PRODUCTO + " TEXT " +
                ")";
        db.execSQL(CREATE_VN_PEDIDOS_PARTIDAX);

        // Tabla vn_documentos_encabezado +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        String CREATE_VN_DOCUMENTOS_ENCABEZADO = "CREATE TABLE IF NOT EXISTS " + TABLE_VN_DOCUMENTOS_ENCABEZADO + "(" +
                COL_VNDOCUMENTOSENCABEZADO_ID + " INTEGER PRIMARY KEY, " +
                COL_VNDOCUMENTOSENCABEZADO_CVECOMPANIA + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_CVEDOCUMENTO + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_NUMDOCUMENTO + " INTEGER, " +
                COL_VNDOCUMENTOSENCABEZADO_FECHADOCUMENTO + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_FECHAREGISTRO + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_TIPODOCUMENTO + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_SUMA + " DECIMAL, " +
                COL_VNDOCUMENTOSENCABEZADO_DESCUENTO + " DECIMAL, " +
                COL_VNDOCUMENTOSENCABEZADO_SUBTOTAL + " DECIMAL, " +
                COL_VNDOCUMENTOSENCABEZADO_TOTAL + " DECIMAL, " +
                COL_VNDOCUMENTOSENCABEZADO_CVECLIENTE + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_CVEAGENTE + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIO + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_CVEMONEDA + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_TIPOCAMBIO + " DECIMAL, " +
                COL_VNDOCUMENTOSENCABEZADO_COMENTARIOS + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_ESTATUS + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_TOTALPAGADO + " DECIMAL, " +
                COL_VNDOCUMENTOSENCABEZADO_RECIBOPAGO + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_CONCILIADO + " INTEGER, " +
                COL_VNDOCUMENTOSENCABEZADO_FECHACONCILIACION + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_REFERENCIACONCILIACION + " INTEGER, " +
                COL_VNDOCUMENTOSENCABEZADO_EXISTEACLARACION + " INTEGER, " +
                COL_VNDOCUMENTOSENCABEZADO_PERSONADEPOSITO + " INTEGER, " +
                COL_VNDOCUMENTOSENCABEZADO_DOCTORESPALDO + " INTEGER, " +
                COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIOCONCILIACION + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_AUDITORIA + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSAUDITORIA + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSOTROS + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_CVEUSUARIODESCONCILIACION + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_COMENTARIOSDESCONCILIACION + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_IEPS3 + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_IEPS35 + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_TOTALIEPS + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_IEPS6 + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_IEPS7 + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_LATITUDE + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_LONGITUDE + " TEXT, " +
                COL_VNDOCUMENTOSENCABEZADO_TIPOCOBRANZA + " TEXT " +
                ")";
        db.execSQL(CREATE_VN_DOCUMENTOS_ENCABEZADO);

        // Tabla vn_documentos_partidas +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        String CREATE_VN_DOCUMENTOS_PARTIDAS = "CREATE TABLE IF NOT EXISTS " + TABLE_VN_DOCUMENTOS_PARTIDAS + "(" +
                COL_VNDOCUMENTOSPARTIDAS_ID + " INTEGER PRIMARY KEY, " +
                COL_VNDOCUMENTOSPARTIDAS_CVECOMPANIA + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_CVEDOCUMENTO + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_NUMDOCUMENTO + " INTEGER, " +
                COL_VNDOCUMENTOSPARTIDAS_NUMPARTIDA + " INTEGER, " +
                COL_VNDOCUMENTOSPARTIDAS_CVETIPOPAGO + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_CVEBANCOEMISOR + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_CUENTACHEQUECLIENTE + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_NUMCHEQUE + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_CVEBANCO + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_REFERENCIA + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_FECHA_BANCO + " TEXT, " +
                COL_VNDOCUMENTOSPARTIDAS_TOTAL + " DECIMAL, " +
                COL_VNDOCUMENTOSPARTIDAS_IMGFIRMA + " TEXT " +
                ")";
        db.execSQL(CREATE_VN_DOCUMENTOS_PARTIDAS);

        // Tabla vn_cat_bancos_clientes +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        String CREATE_VN_CAT_BANCOS_CLIENTES = "CREATE TABLE IF NOT EXISTS " + TABLE_VN_CAT_BANCOS_CLIENTES + "(" +
                COL_VNCATBANCOSCLIENTES_ID + " INTEGER PRIMARY KEY, " +
                COL_VNCATBANCOSCLIENTES_CVEBANCOEMISOR + " TEXT, " +
                COL_VNCATBANCOSCLIENTES_NOMBREBANCO + " TEXT " +
                ")";
        db.execSQL(CREATE_VN_CAT_BANCOS_CLIENTES);

        // Tabla ts_cat_bancos ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        String CREATE_TS_CAT_BANCOS = "CREATE TABLE IF NOT EXISTS " + TABLE_TS_CAT_BANCOS + "(" +
                COL_TSCATBANCOS_ID + " INTEGER PRIMARY KEY, " +
                COL_TSCATBANCOS_CVECOMPANIA + " TEXT, " +
                COL_TSCATBANCOS_CVEBANCO + " TEXT, " +
                COL_TSCATBANCOS_NOMBRECORTO + " TEXT, " +
                COL_TSCATBANCOS_NOMBREBANCO + " TEXT, " +
                COL_TSCATBANCOS_MOSTRARVENTAS + " INTEGER, " +
                COL_TSCATBANCOS_CVE + " TEXT " +
                ")";
        db.execSQL(CREATE_TS_CAT_BANCOS);

        String CREATE_TABLE_VN_ENTREGAS_TABLETA = "CREATE TABLE IF NOT EXISTS " + TABLE_VN_ENTREGAS_TABLETA + "(" +
                COL_VNENTREGASTABLETA_ID + " INTEGER PRIMARY KEY, " +
                COL_VNENTREGASTABLETA_NUM_FACTURA + " TEXT, " +
                COL_VNENTREGASTABLETA_CVE_COMPANIA + " TEXT, " +
                COL_VNENTREGASTABLETA_FIRMA + " TEXT, " +
                COL_VNENTREGASTABLETA_CVE_USUARIO_CAPTURA + " TEXT, " +
                COL_VNENTREGASTABLETA_SINCRONIZADO + " TEXT " +
                ")";
        db.execSQL(CREATE_TABLE_VN_ENTREGAS_TABLETA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GL_ACCESOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GL_SYNC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GL_BITACORA_ACCESOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VN_FACTURAS_PEDIDOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VN_PEDIDOS_ENCABEZAO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VN_PEDIDOS_PARTIDAX);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VN_DOCUMENTOS_ENCABEZADO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VN_DOCUMENTOS_PARTIDAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VN_CAT_BANCOS_CLIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TS_CAT_BANCOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VN_ENTREGAS_TABLETA);
    }

    public void resetCatalogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM  " + TABLE_GL_SYNC);
        db.execSQL("DELETE FROM  " + TABLE_VN_FACTURAS_PEDIDOS);
        db.execSQL("DELETE FROM  " + TABLE_VN_PEDIDOS_ENCABEZAO);
        db.execSQL("DELETE FROM  " + TABLE_VN_PEDIDOS_PARTIDAX);
        db.execSQL("DELETE FROM  " + TABLE_VN_DOCUMENTOS_ENCABEZADO);
        db.execSQL("DELETE FROM  " + TABLE_VN_DOCUMENTOS_PARTIDAS);
        db.execSQL("DELETE FROM  " + TABLE_VN_CAT_BANCOS_CLIENTES);
        db.execSQL("DELETE FROM  " + TABLE_TS_CAT_BANCOS);
        db.execSQL("DELETE FROM  " + TABLE_VN_ENTREGAS_TABLETA);
    }

    public void resetGlAccesos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  " + TABLE_GL_ACCESOS);
    }

    // Al colocarse al inicio del c—digo provoca y forza la creaci—n f’sica de la BD as’ como sus tablas.
    public void checkDBStatus() {
        String query = "SELECT * FROM " + TABLE_GL_SYNC;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        db.close();
    }

    // Inserta el usuario entrante en la bitacora interna del sistema
    public void registraBitacora(String cveUsuario) {
        ContentValues values = new ContentValues();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaRegistro = sdf.format(cal.getTime());
        String Query = "";

        values.put(COL_GLBITACORAACCESOS_CVEUSUARIO, cveUsuario.toString());
        values.put(COL_GLBITACORAACCESOS_FECHAREGISTRO, fechaRegistro.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        // Medida de depuración: antes de registrar el usuario se borran los accesos que tengan mas de dos meses de la bitacora
        Query = "DELETE FROM  " + TABLE_GL_BITACORA_ACCESOS + " where datetime(fecha_registro) < datetime(fecha_registro, '-2 month')";
        db.execSQL(Query);
        db.insert(TABLE_GL_BITACORA_ACCESOS, null, values);
        db.close();
    }

    // Metodo para validar el usuario y contrasena en el acceso
    public boolean checkAccesos() {

        Boolean info = false;

        Cursor cursor = getReadableDatabase().rawQuery("select cve_usuario from gl_accesos order by cve_usuario limit 1", null);
        cursor.moveToFirst();
        String usuarioCheck = new String();
        while (!cursor.isAfterLast()) {
            usuarioCheck = cursor.getString(cursor.getColumnIndex("cve_usuario"));
            cursor.moveToNext();
        }
        cursor.close();

        if (usuarioCheck.toString().length() >= 1) {
            info = true;
        }
        return info;
    }

    // Metodo para validar el usuario y contrasena en el acceso
    public boolean validacion(String campoUsuario, String campoPassword) {

        String usuario = campoUsuario;
        String password = campoPassword;
        Boolean valida = false;

        Cursor cursor = getReadableDatabase().rawQuery("select cve_usuario, password from gl_accesos where cve_usuario = '" + usuario + "' and password = '" + password + "' and estatus = '1';", null);
        cursor.moveToFirst();
        String usuarioCheck = new String();
        String passwordCheck = new String();
        while (!cursor.isAfterLast()) {
            usuarioCheck = cursor.getString(cursor.getColumnIndex("cve_usuario"));
            passwordCheck = cursor.getString(cursor.getColumnIndex("password"));
            cursor.moveToNext();
        }
        cursor.close();

        if (usuario.toString().equalsIgnoreCase(usuarioCheck) && password.toString().equalsIgnoreCase(passwordCheck)) {
            valida = true;
        }

        return valida;

    }

    // Inserta un registro individual en la tabla movil  gl_accesos. Recibe un objeto de tipo JSON con estructura del catalogo de accesos
    public void insertaGlAccesos(JSONObject jsonObj) {
        ContentValues values = new ContentValues();
        try {
            values.put(COL_GLACCESOS_CVEUSUARIO, jsonObj.getString(TAG_GLACCESOS_CVEUSUARIO));
            values.put(COL_GLACCESOS_PASSWORD, jsonObj.getString(TAG_GLACCESOS_PASSWORD));
            values.put(COL_GLACCESOS_TIPOUSUARIO, jsonObj.getString(TAG_GLACCESOS_TIPOUSUARIO));
            values.put(COL_GLACCESOS_ESTATUS, jsonObj.getString(TAG_GLACCESOS_ESTATUS));
            values.put(COL_GLACCESOS_ACTUALIZO_PASSWORD, jsonObj.getString(TAG_GLACCESOS_ACTUALIZO_PASSWORD));
            values.put(COL_GLACCESOS_ULTIMAACTUALIZACION, jsonObj.getString(TAG_GLACCESOS_ULTIMAACTUALIZACION));
            values.put(COL_GLACCESOS_IMEI, jsonObj.getString(TAG_GLACCESOS_IMEI));

        } catch (JSONException e) {
            e.printStackTrace();
        } // fin del catch

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_GL_ACCESOS, null, values);
        db.close();

    } // FIN DE insertaGlAccesos

    public void insertaFacturasPedidos(JSONObject jsonObj) {
        ContentValues values = new ContentValues();
        try {
            values.put(COL_VNFACTURASPEDIDOS_CVE_COMPANIA, jsonObj.getString(COL_VNFACTURASPEDIDOS_CVE_COMPANIA));
            values.put(COL_VNFACTURASPEDIDOS_NUM_DOCUMENTO, jsonObj.getString(COL_VNFACTURASPEDIDOS_NUM_DOCUMENTO));
            values.put(COL_VNFACTURASPEDIDOS_CVE_DOCUMENTO, jsonObj.getString(COL_VNFACTURASPEDIDOS_CVE_DOCUMENTO));
            values.put(COL_VNFACTURASPEDIDOS_TOTAL_FACTURA, jsonObj.getString(COL_VNFACTURASPEDIDOS_TOTAL_FACTURA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_VN_FACTURAS_PEDIDOS, null, values);
        db.close();
    }

    public void insertaPedidosEncabezao(JSONObject jsonObj) {
        ContentValues values = new ContentValues();
        try {
            values.put(COL_VNPEDIDOSENCABEZAO_CVE_COMPANIA, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_CVE_COMPANIA));
            values.put(COL_VNPEDIDOSENCABEZAO_NUM_PEDIDO, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_NUM_PEDIDO));
            values.put(COL_VNPEDIDOSENCABEZAO_NUM_FACTURA, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_NUM_FACTURA));
            values.put(COL_VNPEDIDOSENCABEZAO_ALMACEN, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_ALMACEN));
            values.put(COL_VNPEDIDOSENCABEZAO_CLIENTE, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_CLIENTE));
            values.put(COL_VNPEDIDOSENCABEZAO_AGENTE, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_AGENTE));
            values.put(COL_VNPEDIDOSENCABEZAO_FECHA_PEDIDO, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_FECHA_PEDIDO));
            values.put(COL_VNPEDIDOSENCABEZAO_SINCRONIZADO, jsonObj.getString(COL_VNPEDIDOSENCABEZAO_SINCRONIZADO));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_VN_PEDIDOS_ENCABEZAO, null, values);
        db.close();
    }

    public void insertaPedidosPartidax(JSONObject jsonObj) {
        ContentValues values = new ContentValues();
        try {
            values.put(COL_VNPEDIDOSPARTIDAX_CVE_COMPANIA, jsonObj.getString(COL_VNPEDIDOSPARTIDAX_CVE_COMPANIA));
            values.put(COL_VNPEDIDOSPARTIDAX_NUM_PEDIDO, jsonObj.getString(COL_VNPEDIDOSPARTIDAX_NUM_PEDIDO));
            values.put(COL_VNPEDIDOSPARTIDAX_NOM_PRODUCTO, jsonObj.getString(COL_VNPEDIDOSPARTIDAX_NOM_PRODUCTO));
            values.put(COL_VNPEDIDOSPARTIDAX_CANTIDAD, jsonObj.getString(COL_VNPEDIDOSPARTIDAX_CANTIDAD));
            values.put(COL_VNPEDIDOSPARTIDAX_IMG_PRODUCTO, jsonObj.getString(COL_VNPEDIDOSPARTIDAX_IMG_PRODUCTO));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_VN_PEDIDOS_PARTIDAX, null, values);
        db.close();
    }

    public void insertabancosClientes(JSONObject jsonObj) {
        ContentValues values = new ContentValues();
        try {
            values.put(COL_VNCATBANCOSCLIENTES_CVEBANCOEMISOR, jsonObj.getString(TAG_VNCATBANCOSCLIENTES_CVEBANCOEMISOR));
            values.put(COL_VNCATBANCOSCLIENTES_NOMBREBANCO, jsonObj.getString(TAG_VNCATBANCOSCLIENTES_NOMBREBANCO));
        } catch (JSONException e) {
            e.printStackTrace();
        } // fin del catch

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_VN_CAT_BANCOS_CLIENTES, null, values);
        db.close();
    } // ++++ Fin del insertabancosClientes

    // ++++ Inserta los registros de la BD a la tabla ts_cat_bancos de la BD de Sybrem
    public void insertacatBancos(JSONObject jsonObj) {
        ContentValues values = new ContentValues();
        try {
            values.put(COL_TSCATBANCOS_CVECOMPANIA, jsonObj.getString(TAG_TSCATBANCOS_CVECOMPANIA));
            values.put(COL_TSCATBANCOS_CVEBANCO, jsonObj.getString(TAG_TSCATBANCOS_CVEBANCO));
            values.put(COL_TSCATBANCOS_NOMBRECORTO, jsonObj.getString(TAG_TSCATBANCOS_NOMBRECORTO));
            values.put(COL_TSCATBANCOS_NOMBREBANCO, jsonObj.getString(TAG_TSCATBANCOS_NOMBREBANCO));
            values.put(COL_TSCATBANCOS_MOSTRARVENTAS, jsonObj.getString(TAG_TSCATBANCOS_MOSTRARVENTAS));
            values.put(COL_TSCATBANCOS_CVE, jsonObj.getString(TAG_TSCATBANCOS_CVE));
        } catch (JSONException e) {
            e.printStackTrace();
        } // fin del catch

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TS_CAT_BANCOS, null, values);
        db.close();
    } // ++++ Fin de insertacatBancos

    // Metodo que regresa la clave del ultimo usuario que fue registrado en la bitácora interna del sistema móvil en este dispositivo
    public String ultimoUsuarioRegistrado() {
        String cveUsuario = "";
        String Query = "select cve_usuario from gl_bitacora_accesos order by datetime(fecha_registro) desc limit 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()) {
            cveUsuario = cursor.getString(0);
        }

        return cveUsuario;
    }

    public boolean checkInformacionAgente() {
        Boolean info = false;
        String tipo_usuario = "";
        String dato_cliente = "";
        Cursor cursor = getReadableDatabase().rawQuery("select num_factura from vn_pedidos_encabezao where num_factura != '' limit 1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dato_cliente = cursor.getString(cursor.getColumnIndex("num_factura"));
            cursor.moveToNext();
        }
        cursor.close();
        if (dato_cliente.length() == 0) {
            info = true;
        }
        return info;
    }

    public String dameFacturasPerra() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT num_factura, num_pedido, cliente, agente, almacen FROM vn_pedidos_encabezao WHERE sincronizado = '0' ORDER BY num_factura", null);
        String JSON_armado = "[", num_factura = "", num_pedido="", cliente="", agente="", almacen="", json = "";
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                num_factura = cursor.getString(cursor.getColumnIndex("num_factura"));
                num_pedido = cursor.getString(cursor.getColumnIndex("num_pedido"));
                cliente = cursor.getString(cursor.getColumnIndex("cliente"));
                agente = cursor.getString(cursor.getColumnIndex("agente"));
                almacen = cursor.getString(cursor.getColumnIndex("almacen"));
                json = "{\"num_factura\":\"" + num_factura + "\", \"num_pedido\":\""+num_pedido+"\", \"cliente\":\""+cliente+"\", \"agente\":\""+agente+"\", \"almacen\":\""+almacen+"\"}, ";
                JSON_armado += json;
                cursor.moveToNext();
            }
        }
        if (JSON_armado.length() > 1) {
            JSON_armado = JSON_armado.substring(0, JSON_armado.length() - 2);
        }
        JSON_armado += "]";
        cursor.close();
        //Log.d("Analisis de generacion de codigo total ======>", JSonString);
        cursor.close();
        return JSON_armado;
    }

    public String damePartidasFacturasPerra(String num_fac) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT nom_producto, cantidad, img_producto FROM vn_pedidos_partidax WHERE num_pedido = " + num_fac, null);
        String JSON_armado = "[", nom_producto = "", cantidad="", img_producto="", json = "";
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                nom_producto = cursor.getString(cursor.getColumnIndex("nom_producto"));
                cantidad = cursor.getString(cursor.getColumnIndex("cantidad"));
                img_producto = cursor.getString(cursor.getColumnIndex("img_producto"));
                json = "{\"nom_producto\":\"" + nom_producto + "\", \"cantidad\":\""+cantidad+"\", \"img_producto\":\""+img_producto+"\"}, ";
                JSON_armado += json;
                cursor.moveToNext();
            }
        }
        if (JSON_armado.length() > 1) {
            JSON_armado = JSON_armado.substring(0, JSON_armado.length() - 2);
        }
        JSON_armado += "]";
        cursor.close();
        //Log.d("Analisis de generacion de codigo total ======>", JSonString);
        cursor.close();
        return JSON_armado;
    }

    public long getSiguientePago() {
        Cursor cursor = getReadableDatabase().rawQuery("select case when max(num_documento) is null then 1 else max(num_documento) + 1 end as proximo_pago from vn_documentos_encabezado  where  cve_compania = '019' and cve_documento = 'PAG'", null);
        cursor.moveToFirst();
        long proximoPago = 0;
        while (!cursor.isAfterLast()) {
            proximoPago = Long.parseLong(cursor.getString(cursor.getColumnIndex("proximo_pago")));
            cursor.moveToNext();
        }
        cursor.close();

        return proximoPago;
    }

    // ++++ Metodo para obtener en un array el catalogo de Bancos de los clientes  de la tabla vn_cat_bancos clientes
    public String[] getBancosClientes() {
        Cursor cursor = getReadableDatabase().rawQuery("select '' || cve_banco_emisor || '- ' || nombre_banco as banco from vn_cat_bancos_clientes order by nombre_banco", null);
        cursor.moveToFirst();
        ArrayList<String> bancosClientes = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            bancosClientes.add(cursor.getString(cursor.getColumnIndex("banco")));
            cursor.moveToNext();
        }
        cursor.close();
        return bancosClientes.toArray(new String[bancosClientes.size()]);
    }

    // ++++ Metodo para obtener en un array el catalogo de Bancos de deposito a Cuentas Biochem  de la tabla ts_cat_bancos
    public String[] getBancoDeposito() {
        Cursor cursor = getReadableDatabase().rawQuery("select '' || cve_banco || '- ' || nombre_corto as banco from ts_cat_bancos where mostrar_ventas = 1  order by nombre_corto", null);
        cursor.moveToFirst();
        ArrayList<String> bancoDeposito = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            bancoDeposito.add(cursor.getString(cursor.getColumnIndex("banco")));
            cursor.moveToNext();
        }
        cursor.close();
        return bancoDeposito.toArray(new String[bancoDeposito.size()]);
    }
    // ++++ Inserta el Pago capturado en la BD local
    public boolean registraPago(ContentValues documentosEncabezado, ContentValues documentosPartidas) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_VN_DOCUMENTOS_ENCABEZADO, null, documentosEncabezado);
        db.insert(TABLE_VN_DOCUMENTOS_PARTIDAS, null, documentosPartidas);
        db.close();

        return true;
    }

    public boolean registraEntrega(ContentValues entregax) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_VN_ENTREGAS_TABLETA, null, entregax);
        db.close();

        return true;
    }

    // ++++ Metodo para generar la cadena JSON para transmisión de pagos ++++
    public String transmitePagos() {
        String JSonString = "[";

        String query = "SELECT vde.cve_compania, vde.cve_documento, vde.num_documento, vde.fecha_documento, vde.fecha_registro, " +
                "vde.tipo_documento, vde.suma, vde.descuento, vde.subtotal, vde.total, " +
                "vde.cve_cliente, vde.cve_agente, vde.cve_usuario, vde.cve_moneda, vde.tipo_cambio, " +
                "vde.comentarios, vde.estatus, vde.total_pagado, vde.recibo_pago, vde.conciliado, " +
                "vde.fecha_conciliacion, vde.referencia_conciliacion, vde.existe_aclaracion, vde.persona_deposito, vde.docto_respaldo, " +
                "vde.cve_usuario_conciliacion, vde.auditoria, vde.comentarios_auditoria, vde.comentarios_otros, vde.cve_usuario_desconciliacion, " +
                "vde.comentarios_desconciliacion, vde.ieps_3, vde.ieps_3_5, vde.total_ieps, vde.ieps_6, vde.ieps_7, " +
                "vde.latitude, vde.longitude, vde.tipo_cobranza, " +
                "vdp.num_partida, vdp.cve_tipo_pago, vdp.cve_banco_emisor, vdp.cuenta_cheque_cliente, vdp.num_cheque, " +
                "vdp.cve_banco, vdp.referencia, vdp.fecha_banco, vdp.firma_cliente " +
                "from vn_documentos_encabezado vde " +
                "inner join vn_documentos_partidas vdp on " +
                "vde.cve_compania = vdp.cve_compania and " +
                "vde.cve_documento = vdp.cve_documento and " +
                "vde.num_documento = vdp.num_documento " +
                "where vde.cve_documento = 'PAG' and auditoria = 'SYNC'";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                JSonString += "{" + "\"" + "cve_compania" + "\":\"" + cursor.getString(0) + "\"," + // Inicia la lista del encabezado
                        "\"" + "cve_documento" + "\":\"" + cursor.getString(1) + "\"," +
                        "\"" + "num_documento" + "\":\"" + cursor.getString(2) + "\"," +
                        "\"" + "fecha_documento" + "\":\"" + cursor.getString(3) + "\"," +
                        "\"" + "fecha_registro" + "\":\"" + cursor.getString(4) + "\"," +
                        "\"" + "tipo_documento" + "\":\"" + cursor.getString(5) + "\"," +
                        "\"" + "suma" + "\":\"" + cursor.getString(6) + "\"," +
                        "\"" + "descuento" + "\":\"" + cursor.getString(7) + "\"," +
                        "\"" + "subtotal" + "\":\"" + cursor.getString(8) + "\"," +
                        "\"" + "total" + "\":\"" + cursor.getString(9) + "\"," +
                        "\"" + "cve_cliente" + "\":\"" + cursor.getString(10) + "\"," +
                        "\"" + "cve_agente" + "\":\"" + cursor.getString(11) + "\"," +
                        "\"" + "cve_usuario" + "\":\"" + cursor.getString(12) + "\"," +
                        "\"" + "cve_moneda" + "\":\"" + cursor.getString(13) + "\"," +
                        "\"" + "tipo_cambio" + "\":\"" + cursor.getString(14) + "\"," +
                        "\"" + "comentarios" + "\":\"" + cursor.getString(15) + "\"," +
                        "\"" + "estatus" + "\":\"" + cursor.getString(16) + "\"," +
                        "\"" + "total_pagado" + "\":\"" + cursor.getString(17) + "\"," +
                        "\"" + "recibo_pago" + "\":\"" + cursor.getString(18) + "\"," +
                        "\"" + "conciliado" + "\":\"" + cursor.getString(19) + "\"," +
                        "\"" + "fecha_conciliacion" + "\":\"" + cursor.getString(20) + "\"," +
                        "\"" + "referencia_conciliacion" + "\":\"" + cursor.getString(21) + "\"," +
                        "\"" + "existe_aclaracion" + "\":\"" + cursor.getString(22) + "\"," +
                        "\"" + "persona_deposito" + "\":\"" + cursor.getString(23) + "\"," +
                        "\"" + "docto_respaldo" + "\":\"" + cursor.getString(24) + "\"," +
                        "\"" + "cve_usuario_conciliacion" + "\":\"" + cursor.getString(25) + "\"," +
                        "\"" + "auditoria" + "\":\"" + cursor.getString(26) + "\"," +
                        "\"" + "comentarios_auditoria" + "\":\"" + cursor.getString(27) + "\"," +
                        "\"" + "comentarios_otros" + "\":\"" + cursor.getString(28) + "\"," +
                        "\"" + "cve_usuario_desconciliacion" + "\":\"" + cursor.getString(29) + "\"," +
                        "\"" + "comentarios_desconciliacion" + "\":\"" + cursor.getString(30) + "\"," +
                        "\"" + "ieps_3" + "\":\"" + cursor.getString(31) + "\"," +
                        "\"" + "ieps_3_5" + "\":\"" + cursor.getString(32) + "\"," +
                        "\"" + "total_ieps" + "\":\"" + cursor.getString(33) + "\"," +
                        "\"" + "ieps_6" + "\":\"" + cursor.getString(34) + "\"," +
                        "\"" + "ieps_7" + "\":\"" + cursor.getString(35) + "\"," +
                        "\"" + "latitude" + "\":\"" + cursor.getString(36) + "\"," +
                        "\"" + "longitude" + "\":\"" + cursor.getString(37) + "\"," +
                        "\"" + "tipo_cobranza" + "\":\"" + cursor.getString(38) + "\"," + // Aqui terina el encabezado del pago
                        "\"" + "num_partida" + "\":\"" + cursor.getString(39) + "\"," + // Aqui inicia la partida del pago
                        "\"" + "cve_tipo_pago" + "\":\"" + cursor.getString(40) + "\"," +
                        "\"" + "cve_banco_emisor" + "\":\"" + cursor.getString(41) + "\"," +
                        "\"" + "cuenta_cheque_cliente" + "\":\"" + cursor.getString(42) + "\"," +
                        "\"" + "num_cheque" + "\":\"" + cursor.getString(43) + "\"," +
                        "\"" + "cve_banco" + "\":\"" + cursor.getString(44) + "\"," +
                        "\"" + "referencia" + "\":\"" + cursor.getString(45) + "\"," +
                        "\"" + "fecha_banco" + "\":\"" + cursor.getString(46) + "\", " +
                        "\"" + "firma_cliente" + "\":\"" + cursor.getString(47) + "\"},\n";// Aqui termina la partida del pago
            }
        } finally {
            if (JSonString.length() > 1) {
                JSonString = JSonString.substring(0, JSonString.length() - 2);
            }
            JSonString += "]";
            cursor.close();
        }
        return JSonString;
    }

    // ++++ Le quita a marca a los pagos ya sincronizados haciendo update al campo audotoria VARCHAR(10)
    public void resetPagos() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("update  " + TABLE_VN_DOCUMENTOS_ENCABEZADO + " set auditoria = 'TRN' where auditoria = 'SYNC'");
        db.close();
    }

    public String getCia(String num_fac, String num_ped){
        Cursor cursor = getReadableDatabase().rawQuery("SELECT cve_compania FROM vn_pedidos_encabezao WHERE num_pedido = '"+num_ped+"' AND num_factura = '"+num_fac+"'", null);
        cursor.moveToFirst();
        String cia = "";
        while (!cursor.isAfterLast()) {
            cia = cursor.getString(cursor.getColumnIndex("cve_compania"));
            cursor.moveToNext();
        }
        cursor.close();

        return cia;
    }

    public void actualizadEntrega(String cia, String num_pedido, String num_factura){
        Boolean ahi_va = false;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE vn_pedidos_encabezao SET sincronizado = '2' WHERE num_pedido = '"+num_pedido+"' AND num_factura = '"+num_factura+"' AND cve_compania = '"+cia+"' AND sincronizado = '0'");
        db.close();
    }

    public String transmiteEntregas() {

        String JSonString = "[";
        String query = "SELECT num_factura, cve_compania, firma, cve_usuario_captura FROM "+TABLE_VN_ENTREGAS_TABLETA+" WHERE sincronizado = '0'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                JSonString += "{" + "\"" + "num_factura" + "\":\"" + cursor.getString(0) + "\"," + // Inicia la lista del encabezado
                        "\"" + "cve_compania" + "\":\"" + cursor.getString(1) + "\"," +
                        "\"" + "firma" + "\":\"" + cursor.getString(2) + "\"," +
                        "\"" + "cve_usuario_captura" + "\":\"" + cursor.getString(3) + "\"},\n";
            }
        } finally {
            if (JSonString.length() > 1) {
                JSonString = JSonString.substring(0, JSonString.length() - 2);
            }
            JSonString += "]";
            cursor.close();
            //Log.d("Analisis de generacion de codigo total ======>", JSonString);
        }
        return JSonString;
    } // Fin de transmite entregas

    public void resetEntregas() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE  " + TABLE_VN_ENTREGAS_TABLETA + " SET sincronizado = '1' WHERE sincronizado IN ('0','2')");
        db.close();
    }
}//num_factura, cve_compania, firma
