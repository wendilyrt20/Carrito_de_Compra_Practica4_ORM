package edu.pucmm.eict.Servicios;

import edu.pucmm.eict.Clases.Producto;
import edu.pucmm.eict.DataBase.DataBaseServices;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ProductoServicios extends DataBaseServices<Producto> {
    private static ProductoServicios instancia;

    private ProductoServicios() {
        super(Producto.class);
    }

    public static ProductoServicios getInstancia(){
        if(instancia==null){
            instancia = new ProductoServicios();
        }
        return instancia;
    }


    public List<Producto> paginacion( int pagina) {
        EntityManager emt = getEntityManager();
        Query q = emt.createQuery("Select count (p.id) from Producto p");
        Long countResults = (Long) q.getSingleResult();
        int lastPageNumber = (int) (Math.ceil(countResults / 10)); //ultima pagina

        Query selectQuery = emt.createQuery("From Producto ");
        selectQuery.setFirstResult((pagina - 1) * 10);
        selectQuery.setMaxResults(10);

        List<Producto> lastaP = selectQuery.getResultList();
        return lastaP;
    }




}
