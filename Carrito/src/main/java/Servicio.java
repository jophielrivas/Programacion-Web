import encapsulaciones.VentasProductos;
import encapsulaciones.Producto;
import encapsulaciones.Usuario;

import java.util.List;
import java.util.ArrayList;

public class Servicio {
    private static Servicio instancia;
    private List<Usuario> usuarios;
    private List<Producto> productos;
    private List<VentasProductos> ventas;
    private int cont;
    private long carrito;

    public Servicio() {
        usuarios = new ArrayList<>();
        productos = new ArrayList<>();
        ventas = new ArrayList<>();
        cont = 0;
        carrito = 0;
        usuarios.add(new Usuario("admin","admin","admin"));
    }

    public static Servicio getInstance(){
        if(instancia == null){
            instancia = new Servicio();
        }
        return instancia;
    }
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<VentasProductos> getVentas() {
        return ventas;
    }
    public Usuario autentificarUsuario(String usuario, String nombre, String password){
        return new Usuario(usuario,nombre,password);
    }

    public long getCarrito(){
        return carrito++;
    }

    public Producto registrarProducto(Producto producto){
        producto.setId(cont++);
        productos.add(producto);
        return producto;
    }


    public Producto getProductosPorID(int id) {
        return productos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public Producto actualizarProducto(Producto producto){
        Producto tmp = getProductosPorID(producto.getId());
        if(tmp == null){
            throw new RuntimeException("No Existe el producto: "+producto.getId());
        }
        tmp.actualizar(producto);
        return tmp;
    }

    public boolean eliminarProductoPorId(int id){
        Producto temp = getProductosPorID(id);
        return productos.remove(temp);
    }

    public void addVentas(VentasProductos venta) {
        ventas.add(venta);
    }
}
