package edu.pucmm.eict;

import edu.pucmm.eict.Clases.*;
import edu.pucmm.eict.DataBase.DataBase;
import edu.pucmm.eict.DataBase.DataBaseServices;
import edu.pucmm.eict.Servicios.*;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinFreemarker;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Main {
    static String tempURI = "";
    private static String modoConexion = "";

    public static void main(String[] args) throws SQLException {

        DataBase.getInstancia().startDb();

////////////////////////BDD////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

        Controladora control = new Controladora(); //Instancia controladora
        //  control.agregarProducto();

        //  ProductoServicios.getInstancia().findAll();
        //  VentasProductosServicios.getInstancia().findAll();


        ///////////////////////////////////////////////////////////////////////////////////////////////////
        JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");//Instancia motor de plantilla Thymeleaf.

        //Creando la instancia del servidor.
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/publico"); //desde la carpeta de resources --> Ruta estatica.
        }).start(1234);

        System.out.println("Javalin");

        /**
         * Metodo para indicar el puerto en Heroku
         * @return
         */

        /*
        app.before("/", ctx -> {
           ctx.redirect("/1");
        });
*/

        app.get("/", ctx -> {
           ctx.redirect("/1");
        });



        app.get("/:paginacion", ctx -> {
            int pagina = ctx.pathParam("paginacion", Integer.class).get();

            int cantidad = 0;

           // List<Producto> producto = ProductoServicios.getInstancia().findAll();
            List<Foto> foto = FotoServicios.getInstancia().findAll();
            EntityManager ent = ProductoServicios.getInstancia().getEntityManager();
            Query q = ent.createQuery("Select count (p.id) from Producto p");
            Long cantP = (Long) q.getSingleResult();
            float totalPagina = (float) (Math.ceil(cantP / 10));
            System.out.println("=========> "+ cantP);
            System.out.println("=========> "+ totalPagina);
            List<Producto> producto;

            if (cantP >= 11){
                producto =  ProductoServicios.getInstancia().paginacion(pagina);
                System.out.println("MAYOR A 11");
            }

            else {
                Query query = ent.createQuery("SELECT p from Producto p");
                query.setFirstResult(0);
                query.setMaxResults(10);
                producto = query.getResultList();
                System.out.println("MENORR A 11");
            }

            Map<String,Object> modelo = new HashMap<>();
            modelo.put("foto", foto);
            modelo.put("listaP", producto);
            modelo.put("totalPagina", totalPagina);

            if(ctx.sessionAttribute("SesionCarrito")!= null){
                CarroCompra c = ctx.sessionAttribute("SesionCarrito");
                cantidad= c.buscarCantCarrito(c);
               // control.getListaProductos().removeAll(control.getListaProductos());

                ProductoServicios.getInstancia().findAll();
                // control.mostrarProducto();
                System.out.println(" PPPP--SIZE P CARRITOOOOO--->" + cantidad);
            }
            ctx.sessionAttribute("cantidad", cantidad);
            modelo.put("cantidad",cantidad);
            ctx.render("/Templates/comprar.html", modelo);
        });


        app.post("/addCarrito", ctx -> {
            int id= Integer.parseInt(ctx.formParam("id"));
            int cant = Integer.parseInt(ctx.formParam("cant"));
           // control.agregarProducto();
            Producto aux= control.buscarProducto(id);
            Producto pCarrito = new Producto(aux.getNombre(), aux.getPrecio(), cant,aux.getDescripcion()); //cant de la ventas
            pCarrito.setId(id);

            System.out.println("*********************************************************");
            System.out.println("ID a agregar----->"+aux.getId() +"CANT:::" +cant );
            System.out.println("*********************************************************");

            //Crar sesion para carrito
            if (ctx.sessionAttribute("SesionCarrito")== null) { //comprobación de sesión carrito
                CarroCompra carrito = new CarroCompra();
                carrito.addCarrito(pCarrito);
                ctx.sessionAttribute("SesionCarrito", carrito); //crear sesión
                System.out.println("SESION CARRITO CREADA");
                System.out.println("SIZE: " + carrito.getListaProductos().size());
                ctx.redirect("/");
            }
            else if (ctx.sessionAttribute("SesionCarrito") != null){ //ya está creada
                CarroCompra carrito = ctx.sessionAttribute("SesionCarrito"); //añadir a carrito en la sesion creada
                carrito.addCarrito(pCarrito);
                System.out.println("Sesion carrito estuvo creada");
                System.out.println("Carrito--> " + carrito.getListaProductos().size());
                ctx.redirect("/");
            }
        });

        app.get("/1/limpiarC", ctx -> {
            ctx.sessionAttribute("SesionCarrito",null);
            ctx.redirect("/");
        });
///////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////// BOTONES SUPERIORES ///////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////

        app.get("/1/carrito", ctx -> {
            try{
                CarroCompra carro = ctx.sessionAttribute("SesionCarrito");
                List<Producto> p =  carro.getListaProductos();
                Double total = carro.cantProducto(p);
               // ctx.sessionAttribute("total", total);
                Map<String,Object> modelo = new HashMap<>();
                modelo.put("lista",p);
                modelo.put("total", total);
                int cantidad = ctx.sessionAttribute("cantidad");
                modelo.put("cantidad",cantidad);
                ctx.render("/Templates/carrito.html", modelo);
            }catch(NullPointerException ex){
                ctx.redirect("/");
            }
        });


        app.get("/1/ventasR",ctx -> {
            if (ctx.sessionAttribute("admin") == null) {
                System.out.println(" SESION= NULL");
                tempURI= ctx.req.getRequestURI();
                ctx.render("/Templates/login.html");
            }
            else if (ctx.sessionAttribute("admin") != null){
                List<VentasProductos> venta = VentasProductosServicios.getInstancia().findAll();
                for (VentasProductos v: venta ) {
                    System.out.println("******************************************************************");
                    System.out.println(  v.getNombreCliente());
                    System.out.println("******************************************************************");
                }

                System.out.println("VENTA SESION BIEN");
                System.out.println("ADMIN PRODUCT");
                int cantidad = ctx.sessionAttribute("cantidad");
                //CarroCompra car = ctx.sessionAttribute("SesionCarrito");
                Map<String,Object> modelo = new HashMap<>();
                modelo.put("listaV", venta);
                modelo.put("cantidad",cantidad);
                ctx.render("/Templates/listadoVentasRealizadas.html",modelo);
            }
            else {
                ctx.redirect("/");
            }
        });


        app.get("/1/adminP",ctx -> {

            if (ctx.sessionAttribute("admin") == null) {
                System.out.println("NO SESION= NULL");
                tempURI= ctx.req.getRequestURI();
                ctx.path();
                ctx.render("/Templates/login.html");
            }
            else if (ctx.sessionAttribute("admin") != null){
                List<Producto> product = ProductoServicios.getInstancia().findAll();
                Map<String,Object> modelo = new HashMap<>();
                modelo.put("listaP", product);
                System.out.println("ADMIN PRODUCT");
                int cantidad = ctx.sessionAttribute("cantidad");
                modelo.put("cantidad",cantidad);
                ctx.render("/Templates/adminProducto.html",modelo);
            }

        });


        app.get("/1/comprar", ctx -> {
            ctx.redirect("/");

        });

/////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////

        app.post("/login",ctx -> {
            String usuario= ctx.formParam("username");
            String pass = ctx.formParam("password");
            System.out.println("LOGIN");
            Usuario user = new Usuario(usuario,usuario,pass);
            UsuarioServicios.getInstancia().editar(user); //merge transaccion
            if (usuario.equalsIgnoreCase("admin")  && pass.equalsIgnoreCase("admin")){
                System.out.println("USER CORRECTO");
               ctx.sessionAttribute("admin", user);
                ctx.redirect(tempURI);

                if ( ctx.formParam("rememberMe")!= null){

                    ctx.attribute("user", user.getUsuario());
                    StrongTextEncryptor enc = new StrongTextEncryptor();
                    enc.setPassword("passEncryptation");
                    String adminEncryptor = enc.encrypt(user.getUsuario());
                    ctx.cookie("rememberMe", adminEncryptor,60480); //Coockie con duracion 1 semana
                    ctx.result("Cookie creada...");
                }

            } else {
                ctx.redirect("/1/adminP");
            }
        });


        app.post("/comprar/carrito", ctx -> {
            String user = ctx.formParam("user");
            Date date = new Date();
            java.sql.Date datesql = new java.sql.Date(date.getTime());
            CarroCompra carro = ctx.sessionAttribute("SesionCarrito");
            Double total = carro.cantProducto(carro.getListaProductos());
            Set<Producto> product = new HashSet<Producto>();
            for (Producto p:carro.getListaProductos() ) {
                product.add(p);
            }

            VentasProductos venta= new VentasProductos(datesql,total,user,product);
            VentasProductosServicios.getInstancia().crear(venta);
            System.out.println("CARRITOO size productos-> " + carro.getListaProductos().size());

             System.out.println("---->" + control.getListaVentasProductos().size());
            ctx.sessionAttribute("SesionCarrito",null);
            ctx.redirect("/");

        });





        app.get("/1/crearProd", ctx -> {
            System.out.println("Aqui en crear productos");
            Producto producto = new Producto(" ",0.0,0,"");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("producto", producto);
            ctx.render("/Templates/crearProducto.html",modelo);
        });

        app.post("/producto/new", ctx -> {
            String nombre= ctx.formParam("productname");
            Double precio = ctx.formParam("price", Double.class).get();
            Integer  cantidad = Integer.parseInt(ctx.formParam("cantidad"));
            String descripcion= ctx.formParam("descripcion");

            Producto producto =new Producto(nombre,precio,cantidad,descripcion);//cantidad de la venta
            ProductoServicios.getInstancia().crear(producto);
            System.out.println("NUEVO PRODUCTO");

            ctx.uploadedFiles("foto").forEach(uploadedFile -> {
                try {
                    byte[] bytes = uploadedFile.getContent().readAllBytes();
                    String encodedString = Base64.getEncoder().encodeToString(bytes);
                    Foto foto = new Foto(uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString, producto);
                    FotoServicios.getInstancia().crear(foto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            for (Foto foto:  FotoServicios.getInstancia().findAll()         ) {
                System.out.println("FOTOOOOOO--->"+ foto.getProducto().getId() +" ->  " +foto.getNombre());

            }
            ctx.redirect("/1/adminP");
        });

        app.post("/producto/editar",ctx -> {
            int id = ctx.formParam("idp",Integer.class).get();
            String nombre= ctx.formParam("productname");
            Double precio = ctx.formParam("price", Double.class).get();
            Integer  cantidad = Integer.parseInt(ctx.formParam("cantidad"));
            String descripcion= ctx.formParam("descripcion");

            System.out.println("------------------------->"+ id);

            System.out.println("MODIFICANDO PRODUCTO");
            Producto producto= control.buscarProducto(id);
            ///cantidad d ela venta
            producto.setPrecio(precio);
            producto.setNombre(nombre);
            ProductoServicios.getInstancia().editar(producto);
            ctx.redirect("/1/adminP");

        });


        app.post("/delete/product/compra", ctx -> {
            Integer id = Integer.parseInt(ctx.formParam("id"));
            String nombre = ctx.formParam("nombre");
            CarroCompra carrito = ctx.sessionAttribute("SesionCarrito");
            carrito.deleteProducto(id,nombre);
            ctx.redirect("/1/carrito");
        });

        app.get("/cancel", ctx -> {
            ctx.redirect(tempURI);
        });

        app.get("/productoEditar/:id", ctx -> {
            int id= ctx.pathParam("id",Integer.class).get();
           // List<Producto> producto =ProductoServicios.getInstancia().findAll();
            Map<String, Object> modelo = new HashMap<>();
            Producto pro =  control.buscarProducto(id);
            modelo.put("producto", pro);
           // ProductoServicios.getInstancia().editar(producto);
            System.out.println("ID PARA MODIFICARRRR" + id + modelo);
            ctx.render("/Templates/editarProducto.html",modelo);
        });




        app.get("/productoEliminar/:id",ctx -> {
            int id = ctx.pathParam("id",Integer.class).get();

            EntityManager ent = ProductoServicios.getInstancia().getEntityManager();
            ent.getTransaction().begin();
            Query qr = ent.createQuery("DELETE FROM Foto foto WHERE foto.producto.id= "+id);
            System.out.println("PRODUCTO--->"+id);
            ent.getTransaction().commit();
            ent.close();

            ctx.redirect("/1/adminP");
        });

        app.get("/comentario/:id", ctx -> {
           // tempURI= ctx.req.getRequestURI();
            int id = ctx.pathParam("id",Integer.class).get();
            Producto producto = control.buscarProducto(id);
            System.out.println("Para comentar--->" + producto.getId());
            Map<String,Object> modelo = new HashMap<>();
            modelo.put("producto", producto);
            ctx.render("/Templates/comentario.html",modelo);

        });

        app.post("/crear/comentario", ctx -> {
            int id = ctx.formParam("id",Integer.class).get();
            String comentario = ctx.formParam("comentario");
            Producto p = control.buscarProducto(id);
            System.out.println(p.getNombre());
            Comentario comm= new Comentario(comentario,p);
            ComentarioServicio.getInstancia().crear(comm);
            System.out.println("COMENTARIO BIEN------>");
            ctx.redirect("/1");

        });


        app.get("/listaComentario/:id", ctx -> {
          int id = ctx.pathParam("id",Integer.class).get();
           // int id = 1;
            System.out.println("------------------>" + id);

            EntityManager ent = ProductoServicios.getInstancia().getEntityManager();
            Query query = ent.createQuery("SELECT c from Comentario c where c.producto.id = " + id);
            List<Comentario> lista = (List<Comentario>)query.getResultList();
            System.out.println("Productos a listar comentario--->" + id);
            System.out.println("Sizeeee" + lista.size());

            Map<String,Object> modelo = new HashMap<>();
            modelo.put("comentario", lista);

            ctx.render("/Templates/verComentario.html",modelo);

        });



        app.get("/borrarComentario/:id", ctx -> {
            int id = ctx.pathParam("id",Integer.class).get();
            System.out.println("Comentario--->"+id);

            ComentarioServicio.getInstancia().eliminar(id);

             ctx.redirect("/1/adminP");
        });


        app.get("/1/cancelcoment", ctx -> {
           //ctx.redirect("/1/adminP");
            ctx.redirect(tempURI);

        });




    }

    private static int AsignarPuertoHeroku() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 7000; //Retorna el puerto por defecto en caso de no estar en Heroku.
    }
    /**
     * Nos
     * @return
     */
    public static String getModoConexion(){
        return modoConexion;
    }

}





