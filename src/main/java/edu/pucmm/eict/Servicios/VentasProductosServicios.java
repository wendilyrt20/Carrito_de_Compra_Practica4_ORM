package edu.pucmm.eict.Servicios;

import edu.pucmm.eict.Clases.VentasProductos;
import edu.pucmm.eict.DataBase.DataBaseServices;

public class VentasProductosServicios extends DataBaseServices<VentasProductos> {

    private static VentasProductosServicios instancia;

    private VentasProductosServicios() {
        super(VentasProductos.class);
    }

    public static VentasProductosServicios getInstancia(){
        if(instancia==null){
            instancia = new VentasProductosServicios();
        }
        return instancia;
    }



}
