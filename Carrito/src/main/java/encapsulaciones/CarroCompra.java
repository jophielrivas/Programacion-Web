package encapsulaciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarroCompra {
    private long id;
    private List<Producto> listaProductos;

    public CarroCompra(long id) {
        this.id = id;
        this.listaProductos = new ArrayList<Producto>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public void addProducto(Producto nuevo){
        this.listaProductos.add(nuevo);
    }
    public Producto getProductosPorID(int id) {
        return listaProductos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public void cambiarProducto(Producto temp, int pos) {
        listaProductos.set(pos, temp);
    }

    public int getPos(Integer id) {
        int cont = 0;
        while(cont < listaProductos.size()){
            if(listaProductos.get(cont).getId() == id){
                return cont;
            }
            cont++;
        }
        return -1;
    }

    public void eliminarProductoPorId(int id) {
        int pos = getPos(id);

        listaProductos.remove(pos);
    }

    public int getCantidadProductos(){
        int cantidad = 0;
        for (Producto producto : listaProductos) {
            cantidad += producto.getCantidad();
        }
        return cantidad;
    }

    public float getCarritoTotal(){
        float carritoTotal = 0;

        for(Producto producto :  listaProductos){
            carritoTotal += producto.getTotal();
        }
        return carritoTotal;
    };

    public void limpiarCarrito() {
        this.listaProductos = new ArrayList<Producto>();
    }
}
