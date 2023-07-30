package encapsulaciones;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ProductosAux {

    @Id
    @GeneratedValue
    private int id;
    private int productId;
    @Id
    private int ventaID;
    private int cantidad;
    private float precio;
    private String nombre;
    private Float total;
    public ProductosAux(int productId, int ventaID, int cantidad, float precio, String nombre, float total) {
        this.productId = productId;
        this.ventaID = ventaID;
        this.cantidad = cantidad;
        this.precio = precio;
        this.nombre = nombre;
        this.total = total;
    }

    public ProductosAux() {

    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getVentaID() {
        return ventaID;
    }

    public void setVentaID(int ventaID) {
        this.ventaID = ventaID;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
}

