package edu.pucmm.eict.Servicios;

import edu.pucmm.eict.Clases.Comentario;
import edu.pucmm.eict.Clases.Foto;
import edu.pucmm.eict.Clases.Usuario;
import edu.pucmm.eict.DataBase.DataBaseServices;

public class ComentarioServicio extends DataBaseServices<Comentario> {

    private static ComentarioServicio instancia;

    private ComentarioServicio() {
        super(Comentario.class);
    }

    public static ComentarioServicio getInstancia(){
        if(instancia==null){
            instancia = new ComentarioServicio();
        }
        return instancia;
    }


}