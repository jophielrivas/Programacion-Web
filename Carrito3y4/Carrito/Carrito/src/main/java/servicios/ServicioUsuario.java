package servicios;

import encapsulaciones.Usuario;

public class ServicioUsuario extends ServicioBD<Usuario>{

    private static ServicioUsuario servicioUsuario;

    public ServicioUsuario() {
        super(Usuario.class);
    }

    public static ServicioUsuario getInstancia(){
        if(servicioUsuario==null){
            servicioUsuario = new ServicioUsuario();
        }
        return servicioUsuario;
    }

    public static String autentificarUsuario(Usuario aux) {
        return "ADM";
    }

}
