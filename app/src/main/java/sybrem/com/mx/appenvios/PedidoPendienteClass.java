package sybrem.com.mx.appenvios;

import android.graphics.drawable.Drawable;

public class PedidoPendienteClass {

    private String num_factura;
    private String num_pedido;
    private String cliente;
    private String agente;
    private String almacen;
    private int img_almacen;

    public PedidoPendienteClass(String num_factura, String num_pedido, String cliente, String agente, String almacen, int img_almacen) {
        super();
        this.num_factura = num_factura;
        this.num_pedido = num_pedido;
        this.cliente = cliente;
        this.agente = agente;
        this.almacen = almacen;
        this.img_almacen = img_almacen;
    }

    public String getNum_factura() {
        return num_factura;
    }

    public void setNum_factura(String num_factura) {
        this.num_factura = num_factura;
    }

    public String getNum_pedido() {
        return num_pedido;
    }

    public void setNum_pedido(String num_pedido) {
        this.num_pedido = num_pedido;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getAgente() {
        return agente;
    }

    public void setAgente(String agente) {
        this.agente = agente;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public int getImg_almacen() {
        return img_almacen;
    }

    public void setImg_almacen(int img_almacen) {
        this.img_almacen = img_almacen;
    }
}
