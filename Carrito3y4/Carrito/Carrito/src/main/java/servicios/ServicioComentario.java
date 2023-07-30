package servicios;

import encapsulaciones.Comentario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.util.List;

public class ServicioComentario extends ServicioBD<Comentario> {
    private static ServicioComentario servicioComentario;
    public ServicioComentario() {
        super(Comentario.class);
    }

    public static ServicioComentario getInstancia(){
        if(servicioComentario==null){
            servicioComentario = new ServicioComentario();
        }
        return servicioComentario;
    }

    public List<Comentario> findComments(int id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select e from Comentario e where e.productoId = :id");
        query.setParameter("id", id);
        List<Comentario> lista = query.getResultList();
        return lista;
    }
    public void deleteComent(int id) throws PersistenceException {
        Session session = getEntityManager().unwrap(org.hibernate.Session.class);;
        session.beginTransaction();
        Query query = session.createQuery("delete from Comentario where id =" + id);
        int row = query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
