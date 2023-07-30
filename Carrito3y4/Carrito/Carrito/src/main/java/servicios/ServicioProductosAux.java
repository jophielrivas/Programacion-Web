package servicios;

import encapsulaciones.Producto;
import encapsulaciones.ProductosAux;

import java.util.ArrayList;
import java.util.List;

public class ServicioProductosAux extends ServicioBD<ProductosAux> {
    private static ServicioProductosAux instance;

    public ServicioProductosAux() {
        super(ProductosAux.class);
    }

    public static ServicioProductosAux getInstance(){
        if(instance == null){
            instance = new ServicioProductosAux();
        }
        return instance;
    }

    public List<ProductosAux> convertProd(List<Producto> productos, int venta){
        List<ProductosAux> list = new ArrayList<ProductosAux>();
        for (Producto prod:productos) {
            ProductosAux temp = new ProductosAux(prod.getId(),venta,prod.getCantidad(),prod.getPrecio(),prod.getNombre(), prod.getCantidad() * prod.getPrecio());
            getInstance().create(temp);
            list.add(temp);
        }
        return list;
}

}
