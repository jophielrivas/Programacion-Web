package encapsulaciones;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class VentasProductos {
    @Id
    @GeneratedValue
    private int id;
    @Temporal(TemporalType.DATE)
    private Date fechaCompra;
    private String nombreCliente;
    @OneToMany(fetch = FetchType.EAGER)
    private List<ProductosAux> listaProductos;

    private float total;

    public VentasProductos() {
    }

    public VentasProductos(Date fechaCompra, String nombreCliente, List<ProductosAux> listaProductos, float total) {
        this.id = id;
        this.fechaCompra = fechaCompra;
        this.nombreCliente = nombreCliente;
        this.listaProductos = listaProductos;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public List<ProductosAux> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<ProductosAux> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
