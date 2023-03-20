
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String respuesta;
        do {
            System.out.println("Ingrese un URL: ");

            Scanner scanner = new Scanner(System.in);
            String url = scanner.nextLine();
            try {
                //Pedir el html del url proporcionado
                Document doc = Jsoup.connect(url).get();
                Response text = null;

                //1) Indica la cantidad de líneas del recurso retornado
                try {
                    text = Jsoup.connect(url).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long lines = text.body().split("\n").length;
                System.out.print("\nLa cantidad de lineas en el documento es de: " + lines + " lineas");

                //2) Indica la cantidad de párrafos
                Elements parrafos = doc.select("p");
                System.out.println("\nLa cantidad de parrafos es de: " + parrafos.size());

                //3) Indica la cantidad de imágenes dentro de los párrafos
                if (parrafos.size() != 0) {
                    Elements img = doc.select("p img");
                    System.out.println("La cantidad de imagenes dentro de parrafos es de: " + img.size());
                }

                //4)Busca los formularios tipo POST
                Elements postForms = doc.select("form[method$=post]");
                System.out.println("La cantidad de formularios con metodo POST es de: " + postForms.size());

                //4)Busca los formularios tipo GET
                Elements getForms = doc.select("form[method$=get]");
                System.out.println("La cantidad de formularios con metodo GET es de: " + getForms.size() + "\n");

                //5) Imprime los hijos de cada formulario
                inputs(postForms);
                inputs(getForms);

                //Solo entra a la función si existen formularios de tipo POST
                if (postForms.size() != 0) {
                    post(postForms, url);
                }

            } catch (UnknownHostException error) {
                System.out.println("La URL es invalida");
                System.out.println(error.toString());
            }
            do {
                System.out.println("Quiere volver a utlizar la aplicación? S - (SI) N - (NO) \n");
                respuesta = scanner.nextLine().toUpperCase();
            }while(!(respuesta).equals("S") && !(respuesta).equals("N"));
        }while(respuesta.equals("S"));
    }

    //5) Función para mostrar los campos de tipo INPUT
    public static void inputs(Elements forms) {
        int cont = 1;
        for (Element form : forms) {
            if (form.children().select("input").size() != 0) {
                System.out.println("Los inputs del formulario " + form.attr("method").toLowerCase() + " #" + cont + " son:");
                for (Element child : form.select("input")) {
                    System.out.println("\t" + child);
                }
            } else {
                System.out.println("El formulario " + form.attr("method").toLowerCase() + " #" + cont + " no tiene campos input como hijos");
            }
            cont++;
        }
    }


    public static void post(Elements forms,String url){
        int cont = 1;
        for (Element form: forms) {
            try {
                String postURL = form.absUrl("action");
                Response response;

                //6) Se envia la petición al servidor
                response = Jsoup.connect(postURL)
                        .method(Method.POST)
                        .data("asignatura","practica1")
                        .header("matricula","20191219")
                        .execute();

                System.out.println("\nRespuesta del formulario POST #"+cont+": \n Status code=" + response.statusCode() + " ,URL=" + response.url());
                System.out.println(" Headers: " + response.headers());

            } catch (IOException error) {
                System.out.println("\nRespuesta: " + error);
            }
        }
    }
}
