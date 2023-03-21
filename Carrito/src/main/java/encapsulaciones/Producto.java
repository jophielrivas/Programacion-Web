package encapsulaciones;

import java.math.BigDecimal;

public class Producto {
    private int id;
    private String nombre;
    private float precio;

    private int cantidad;

    private float total;


    public Producto( String nombre, float precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Producto(int id, String nombre, float precio, int cantidad, float total){
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public void actualizar(Producto producto) {
        this.nombre = producto.nombre;
        this.precio = producto.precio;
    }
}
