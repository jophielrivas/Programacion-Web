import encapsulaciones.*;
import io.javalin.Javalin;
import servicios.*;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args){
        //Inicializacion del servidor
        var app = Javalin.create().start(7073);
        app.before(ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra();
            }
            ctx.sessionAttribute("carrito",carrito);
        });


        //Agregando Productos Iniciales
        crearProductos();

        ServicioUsuario.getInstancia().create(new Usuario("admin","admin","Usuario"));


        app.get("/", ctx -> ctx.redirect("/productos"));
        app.get("", ctx -> ctx.redirect("/productos"));


        /*Si el carrito no existe dentro de la sesion entonces se crea y se agrega como un atributo*/
        app.before(ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra();
            }
            ctx.sessionAttribute("carrito",carrito);

        });

        //Método del Login
        app.get("/login/{direc}", ctx -> {
            Map<String, Object> model = new HashMap<>();
            String direc = ctx.pathParam("direc");
            model.put("direc",direc);
            ctx.render("templates/login.html",model);
        });

        /*Dentro del POST del login se chequea si el usuario es el administrador o is está dentro de los usuarios
        agregados, si no lo está, entonces se redirge al login para que pase sin autenticar o para que entre con
        la cuenta de administrador.
         */
        app.post("/login/{direc}", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String temp = ctx.pathParam("direc");
            if(temp.equalsIgnoreCase("{}")){
                temp = "productos";
            }

            if (username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
                ctx.cookie("username", username);
                ctx.cookie("password",password);
                ctx.redirect("/" + temp);
            }else{
                ctx.cookie("username", username);
                ctx.cookie("password",password);
                ctx.redirect("/login/{direc}");
            }

        });

        //Método para ver la lista de productos
        app.get("/productos", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ServicioProducto.getInstance().findProd(0,10);

            Map<String, Object> model = new HashMap<>();
            model.put("productos", productos);
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            List<String> paginas = getPaginas();
            model.put("paginas",paginas);
            ctx.render("templates/productos.html",model);
        });

        app.get("/comprar/{id}", ctx -> {
            int pos =(Integer.parseInt(ctx.pathParam("id")) - 1) * 10;
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ServicioProducto.getInstance().findProd(pos+0, pos+10);
            Map<String, Object> model = new HashMap<>();
            model.put("productos",productos);
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            List<String> paginas = getPaginas();
            model.put("paginas",paginas);
            ctx.render("templates/productos.html", model);
        });

        //Método para agregar un producto al carrito
        app.post("/comprar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            int idProducto = Integer.parseInt(ctx.formParam("id"));
            Integer cantidad = 0;
            if(ctx.formParam("cantidad") != null) {
                cantidad = Integer.parseInt(ctx.formParam("cantidad"));
            }

            if(cantidad > 0){
               Producto temp = ServicioProducto.getInstance().find(idProducto);
               temp.setCantidad(cantidad);

               if(carrito.getProductosPorID(temp.getId()) != null){
                   int id = carrito.getProductosPorID(temp.getId()).getId();
                   temp.setCantidad(temp.getCantidad()+carrito.getProductosPorID(temp.getId()).getCantidad());
                   carrito.cambiarProducto(temp, carrito.getPos(id));
                   carrito.getCarritoTotal();
                   temp.setTotal(temp.getCantidad() * temp.getPrecio());
               } else {
                   temp.setTotal(temp.getCantidad() * temp.getPrecio());
                   carrito.addProducto(temp);
               }


            }
            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/productos");
        });

        //Método para visualizar producto
        app.get("/ver/{id}",ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            int id = Integer.parseInt(ctx.pathParam("id"));
            Producto producto = ServicioProducto.getInstance().find(id);
            List<Comentario> comments = ServicioComentario.getInstancia().findComments(id);
            String user = ctx.cookie("usuario");
            model.put("producto",producto);
            model.put("comments",comments);
            if(ctx.cookie("username") == null || ctx.cookie("password") == null || !ctx.cookie("username").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                model.put("usuario", "v");
            }else {
                model.put("usuario", 'a');
            }
            ctx.render("/templates/ver.html",model);
        });

        //Método para agregar comentario
        app.post("/addComment/{id}", ctx->{
            String comment = ctx.formParam("coment");
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            Comentario temp = new Comentario(comment,id);
            ServicioComentario.getInstancia().create(temp);
            ctx.redirect("/ver/"+id);
        });

        app.get("/delComent/{id}/{comment}", ctx ->{
            int id =  ctx.pathParamAsClass("id", Integer.class).get();
            int comment = ctx.pathParamAsClass("comment", Integer.class).get();
            ServicioComentario.getInstancia().deleteComent(comment);
            ctx.redirect("/ver/"+id);
        });


        //Método para ver la página del CRUD de los productos
        app.get("/crud-productos", ctx -> {
            if( ctx.cookie("username") == null || ctx.cookie("password") == null || !ctx.cookie("username").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                ctx.redirect("/login/crud-productos");
            }

            Map<String, Object> model = new HashMap<>();
            model.put("productos", ServicioProducto.getInstance().findAll());
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());

            ctx.render("templates/crud-productos.html",model);
        });

        //Método para eliminar un producto que existe en la tienda
        app.get("/eliminar/{id}", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            ServicioProducto.getInstance().delete(Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/crud-productos");
        });

        //Método para editar un producto que existe en la tienda
        app.get("/editar/{id}", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            model.put("producto", ServicioProducto.getInstance().find(Integer.parseInt(ctx.pathParam("id"))));
            model.put("titulo", ServicioProducto.getInstance().find(Integer.parseInt(ctx.pathParam("id"))).getNombre());
            System.out.println(ServicioProducto.getInstance().find(Integer.parseInt(ctx.pathParam("id"))).getDescripcion());

            ctx.render("templates/editar-producto.html",model);
        });

        //Método para guardar la edición del producto
        app.post("/editar/{id}", ctx -> {
            String nombre = ctx.formParam("product-name");
            float precio = Float.parseFloat(ctx.formParam("product-price"));
            String desc = ctx.formParam("desc");

            Producto temp = new Producto(nombre,precio,desc);
            temp.setId(Integer.parseInt(ctx.formParam("id")));
            ServicioProducto.getInstance().edit(temp);
            ctx.redirect("/crud-productos");
        });

        //Método para cargar la vista de crear un nuevo producto
        app.get("/crear", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());

            ctx.render("templates/crear-producto.html",model);
        });

        //Método para crear un nuevo producto
        app.post("/crear", ctx -> {
            String nombre = ctx.formParam("product-name");
            float precio = Float.parseFloat(ctx.formParam("product-price"));
            String desc = ctx.formParam("desc");
            List<Foto> fotos = new ArrayList<>();
            ctx.uploadedFiles("img").forEach(uploadedFile -> {
                try {
                    byte[] bytes = uploadedFile.getContent().readAllBytes();
                    String encodedString = Base64.getEncoder().encodeToString(bytes);
                    Foto foto = new Foto(uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString);
                    ServicioFoto.getInstancia().create(foto);
                    fotos.add(foto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Producto temp = new Producto(nombre,precio,desc, fotos);
            ServicioProducto.getInstance().create(temp);
            ctx.redirect("/crud-productos");
        });

        //Método para ver la vista del carrito de compras
        app.get("/carrito", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            model.put("productos", carrito.getListaProductos());

            model.put("totalCarrito", carrito.getCarritoTotal());

            ctx.render("templates/carrito.html",model);
        });

        app.post("/carrito", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());

            String nombreCliente = ctx.formParam("nombreCliente");
            Date date = new Date();
            VentasProductos venta = new VentasProductos();
            List<ProductosAux> nuevaListaProductos = ServicioProductosAux.getInstance().convertProd(carrito.getListaProductos(),venta.getId());
            float totalProduct = 0;
            for(Producto producto :carrito.getListaProductos()){
                ProductosAux temp = new ProductosAux(producto.getId(), venta.getId(),producto.getCantidad(),producto.getPrecio(), producto.getNombre(), producto.getPrecio() * producto.getCantidad());
                totalProduct += temp.getPrecio() * temp.getCantidad();
            }
            venta.setFechaCompra(date);
            venta.setListaProductos(nuevaListaProductos);
            venta.setNombreCliente(nombreCliente);
            venta.setTotal(totalProduct);
            if(carrito.getCarritoTotal()>0) {
                ServicioVentasProductos.getInstance().create(venta);
                carrito.limpiarCarrito();
            }

            ctx.redirect("/carrito");
        });

        //Método para eliminar un producto que existe en el carrito
        app.get("/eliminar-carrito/{id}", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            carrito.eliminarProductoPorId(Integer.parseInt(ctx.pathParam("id")));

            ctx.redirect("/carrito");
        });

        //Método para eliminar todos los productos del carrito
        app.get("/limpiar-carrito", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            carrito.limpiarCarrito();

            ctx.redirect("/carrito");
        });


        //Método para visualizar las ventas
        app.get("/ventas", ctx -> {
            if( ctx.cookie("username") == null || ctx.cookie("password") == null || !ctx.cookie("username").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                ctx.redirect("/login/ventas");
            }
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            model.put("ventas", ServicioVentasProductos.getInstance().getVentas());

            ctx.render("templates/ventas.html",model);
        });

    }

    private static void crearProductos(){
        String nombre;
        float precio;
        String desc;
        List<Foto> fotos = new ArrayList<Foto>();
        for(int i = 1 ; i < 31; i++){
            nombre = "producto "+ i;
            precio = 10 * i;
            desc = "Este es el producto "+i;
            Producto temp = new Producto(nombre,precio,desc);
            temp.setFotos(fotos);
            ServicioProducto.getInstance().create(temp);
        }
    }

    private static List<String> getPaginas() {
        int pag = ServicioProducto.getInstance().pag();
        List<String> list = new ArrayList<String>();
        for(int i = 0; i <= pag; i++){
            String aux = String.valueOf((i+1));
            list.add(aux);
        }
        return list;
    }
}
