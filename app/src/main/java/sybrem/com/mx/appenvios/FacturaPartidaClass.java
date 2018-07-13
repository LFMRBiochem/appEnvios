package sybrem.com.mx.appenvios;

public class FacturaPartidaClass {

    private String nom_producto;
    private String cantidad;
    private String img_producto;

    public FacturaPartidaClass(String nom_producto, String cantidad, String img_producto) {
        this.nom_producto = nom_producto;
        this.cantidad = cantidad;
        this.img_producto = img_producto;
    }

    public String getNom_producto() {
        return nom_producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getImg_producto() {
        return img_producto;
    }
}
