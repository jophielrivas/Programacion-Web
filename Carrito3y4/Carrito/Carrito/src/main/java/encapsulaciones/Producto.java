package encapsulaciones;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;


@Entity
public class Producto implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    private String nombre;
    private float precio;

    private String descripcion;


    @Transient
    private int cantidad;

    @Column(columnDefinition = "boolean default true")
    private boolean estado;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Foto> fotos;
    private float total;

    public Producto() {
    }

    public Producto(String nombre, float precio) {
        this.nombre = nombre;
        this.precio = precio;
        estado = true;
    }

    public Producto(String nombre, float precio, String descripcion) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        estado = true;
    }
    public Producto(String nombre, float precio, String descripcion, List<Foto> fotos) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.fotos = fotos;
        estado = true;
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

    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
