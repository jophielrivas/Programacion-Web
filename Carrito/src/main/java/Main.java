import encapsulaciones.CarroCompra;
import encapsulaciones.Producto;
import encapsulaciones.VentasProductos;
import io.javalin.Javalin;

import java.time.LocalDate;
import java.util.*;

public class Main {

    public static void main(String[] args){
        //Inicializacion del servidor
        var app = Javalin.create().start(7071);

        //Creacion de la controladora
        Servicio servicio = Servicio.getInstance();

        //Agregando Productos Iniciales
        servicio.registrarProducto(new Producto("Vaso", 50.0F));
        servicio.registrarProducto(new Producto("Plato", 125.0F));
        servicio.registrarProducto(new Producto("Tenedor y Cuchillo", 85.0F));


        app.get("/", ctx -> ctx.redirect("/productos"));
        app.get("", ctx -> ctx.redirect("/productos"));


        /*Si el carrito no existe dentro de la sesion entonces se crea y se agrega como un atributo*/
        app.before(ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra(servicio.getCarrito());
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

            for (int i = 0; i < servicio.getUsuarios().size(); i++) {
                if (servicio.getUsuarios().get(i).getUsuario().equals(username) && servicio.getUsuarios().get(i).getPassword().equals(password)) {
                    ctx.cookie("username", username);
                    ctx.cookie("password",password);
                    ctx.redirect("/" + temp);
                }else{
                    ctx.cookie("username", username);
                    ctx.cookie("password",password);
                    ctx.redirect("/login/{direc}");
                }
            }
        });

        //Método para ver la lista de productos
        app.get("/productos", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            Map<String, Object> model = new HashMap<>();
            model.put("productos", servicio.getProductos());
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            ctx.render("templates/productos.html",model);
        });

        //Método para agregar un producto al carrito
        app.post("/productos", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            int idProducto = Integer.parseInt(ctx.formParam("id"));
            Integer cantidad = 0;
            if(ctx.formParam("cantidad") != null) {
                cantidad = Integer.parseInt(ctx.formParam("cantidad"));
            }

            if(cantidad > 0){
               Producto temp = servicio.getProductosPorID(idProducto);
               temp.setCantidad(cantidad);
               temp.setTotal(cantidad* temp.getPrecio());
               carrito.addProducto(temp);
            }
            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/productos");
        });

        //Método para ver la página del CRUD de los productos
        app.get("/crud-productos", ctx -> {
            if( ctx.cookie("username") == null || ctx.cookie("password") == null || !ctx.cookie("username").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                ctx.redirect("/login/crud-productos");
            }

            Map<String, Object> model = new HashMap<>();
            model.put("productos", servicio.getProductos());
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());

            ctx.render("templates/crud-productos.html",model);
        });

        //Método para eliminar un producto que existe en la tienda
        app.get("/eliminar/{id}", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            servicio.eliminarProductoPorId(Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/crud-productos");
        });

        //Método para editar un producto que existe en la tienda
        app.get("/editar/{id}", ctx -> {
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());
            model.put("producto",servicio.getProductosPorID(Integer.parseInt(ctx.pathParam("id"))));
            model.put("titulo", servicio.getProductosPorID(Integer.parseInt(ctx.pathParam("id"))).getNombre());

            ctx.render("templates/editar-producto.html",model);
        });

        //Método para guardar la edición del producto
        app.post("/editar/{id}", ctx -> {
            String nombre = ctx.formParam("product-name");
            float precio = Float.parseFloat(ctx.formParam("product-price"));

            Producto temp = new Producto(nombre,precio);
            temp.setId(Integer.parseInt(ctx.formParam("id")));
            servicio.actualizarProducto(temp);
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
            Map<String, Object> model = new HashMap<>();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            model.put("cantidadCarrito",carrito.getCantidadProductos());


            String nombre = ctx.formParam("product-name");
            float precio = Float.parseFloat(ctx.formParam("product-price"));
            Producto temp = new Producto(nombre,precio);
            servicio.registrarProducto(temp);
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
            List<Producto> nuevaListaProductos = new ArrayList<>();
            for(Producto producto :carrito.getListaProductos()){
                Producto temp = new Producto(producto.getId(), producto.getNombre(), producto.getPrecio(),producto.getCantidad(), producto.getTotal());
                nuevaListaProductos.add(temp);
            }
            VentasProductos temp = new VentasProductos(date,nombreCliente,nuevaListaProductos, carrito.getCarritoTotal());
            if(carrito.getCarritoTotal()>0) {
                servicio.addVentas(temp);
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
            model.put("ventas",servicio.getVentas());

            ctx.render("templates/ventas.html",model);
        });

    }
}
