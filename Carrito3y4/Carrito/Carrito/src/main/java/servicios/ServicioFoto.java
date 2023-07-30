package servicios;

import encapsulaciones.Foto;

public class ServicioFoto extends ServicioBD<Foto>{

    private static ServicioFoto servicioFoto;
    public ServicioFoto() {
        super(Foto.class);
    }

    public static ServicioFoto getInstancia(){
        if(servicioFoto==null){
            servicioFoto = new ServicioFoto();
        }
        return servicioFoto;
    }

}
