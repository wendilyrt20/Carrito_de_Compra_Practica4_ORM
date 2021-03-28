package edu.pucmm.eict.Servicios;

import edu.pucmm.eict.Clases.Foto;
import edu.pucmm.eict.Clases.Usuario;
import edu.pucmm.eict.DataBase.DataBaseServices;

public class FotoServicios extends DataBaseServices<Foto> {

    private static FotoServicios instancia;

    private FotoServicios() {
        super(Foto.class);
    }

    public static FotoServicios getInstancia(){
        if(instancia==null){
            instancia = new FotoServicios();
        }
        return instancia;
    }


}
