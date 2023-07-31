package eitc.pucmm;

import eitc.pucmm.ApiServices.RestService;
import eitc.pucmm.Server.GrpcServer;
import eitc.pucmm.controladores.ApiControlador;
import eitc.pucmm.controladores.RestControlador;
import eitc.pucmm.controladores.SoapControladora;
import eitc.pucmm.entidades.Usuario;
import eitc.pucmm.servicios.BootStrapServices;
import eitc.pucmm.servicios.UsuarioService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;


import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    private static String modoConexion = "";

    public static void main(String[] args) throws InterruptedException, IOException {

        String mensaje = "Software ORM - JPA";
        System.out.println(mensaje);

        if (args.length >= 1) {
            modoConexion = args[0];
            System.out.println("Modo de Operacion: " + modoConexion);
        }

        //Iniciando la base de datos.
        if (modoConexion.isEmpty()) {
            BootStrapServices.getInstancia().init();
            EntrarDatos();
        }

        //Creando la instancia del servidor.
        Javalin app = Javalin.create(config ->{
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/templates";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress=false;
                staticFileConfig.aliasCheck=null;
            });

            //Habilitando el CORS. Ver: https://javalin.io/plugins/cors#getting-started para más opciones.
            config.plugins.enableCors(corsContainer -> {
                corsContainer.add(corsPluginConfig -> {
                    corsPluginConfig.anyHost();
                });
            });
        });

        app.start(8080);

        //creando los endpoint de las rutas.
        new ApiControlador(app).aplicarRutas();
        //new SoapControladora(app).aplicarRutas();
        new RestService(app).aplicarRutas();

        GrpcServer server = new GrpcServer();
        server.start();
        server.blockunitlShutdown();
    }

    public static String codeGenerator() {
        int[] arr = {58, 59, 60, 61, 62, 63, 64};
        int leftLimit = 48; // letter 'a'
        int rightLimit = 90; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            if (!IntStream.of(arr).anyMatch(n -> n == randomLimitedInt)) {
                buffer.append((char) randomLimitedInt);
            } else {
                i--;
            }
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

    private static void EntrarDatos() {

        if (UsuarioService.getInstancia().autenticarUsuario("admin", "admin") == null) {
            //anadiendo usuarios de prueba
            Usuario usuario1 = new Usuario();
            usuario1.setUsuario("admin");
            usuario1.setNombre("admin");
            usuario1.setRol(Usuario.RoleasAPP.ROLE_ADMIN);
            usuario1.setPassword("admin");
            UsuarioService.getInstancia().crear(usuario1);
        }
    }

    public static String getModoConexion() {
        return modoConexion;
    }


}