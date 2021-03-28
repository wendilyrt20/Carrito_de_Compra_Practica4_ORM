package edu.pucmm.eict.Servicios;

import edu.pucmm.eict.Clases.Usuario;
import edu.pucmm.eict.DataBase.DataBaseServices;

public class UsuarioServicios extends DataBaseServices<Usuario> {

    private static UsuarioServicios instancia;


    private UsuarioServicios() {
        super(Usuario.class);
    }

    public static UsuarioServicios getInstancia(){
        if(instancia==null){
            instancia = new UsuarioServicios();
        }
        return instancia;
    }
}
