package servicios;

import encapsulaciones.VentasProductos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class ServicioVentasProductos extends ServicioBD<VentasProductos> {

    private static ServicioVentasProductos servicioVentasProductos;

    public ServicioVentasProductos() {
        super(VentasProductos.class);
    }

    public static ServicioVentasProductos getInstance(){
        if(servicioVentasProductos == null){
            servicioVentasProductos = new ServicioVentasProductos();
        }
        return servicioVentasProductos;
    }

    public List<VentasProductos> getVentas(){
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from VENTASPRODUCTOS ", VentasProductos.class);
        List<VentasProductos> lista = query.getResultList();
        return lista;
    }

}
