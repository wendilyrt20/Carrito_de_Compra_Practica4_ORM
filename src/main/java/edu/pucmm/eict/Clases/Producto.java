package edu.pucmm.eict.Clases;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity
public class Producto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private Double precio;
    private int cant; //cantidad de producto en stock
    private String descripcion;
//////////////////////////////////////////////////////////////////////////////////////////////
/*
    @ManyToOne(optional = true)
    private VentasProductos venta;
*/
    @ManyToMany(mappedBy = "listaProducto")
    private Set<VentasProductos> listaVentas;

    @OneToMany(mappedBy = "producto",fetch = FetchType.EAGER)
    private Set<Foto> listaFotos;

    @OneToMany(mappedBy = "producto",fetch = FetchType.EAGER)
    private Set<Comentario> listaComentario;

    public Producto(String nombre, Double precio, int cant, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cant = cant;
        this.descripcion = descripcion;
    }

    public Producto() {
    }


    public Set<Foto> getListaFotos() {
        return listaFotos;
    }

    public void setListaFotos(Set<Foto> listaFotos) {
        this.listaFotos = listaFotos;
    }

    public Set<Comentario> getListaComentario() {
        return listaComentario;
    }

    public void setListaComentario(Set<Comentario> listaComentario) {
        this.listaComentario = listaComentario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getCant() {
        return cant;
    }

    public void setCant(int cant) {
        this.cant = cant;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<VentasProductos> getListaVentas() {
        return listaVentas;
    }

    public void setListaVentas(Set<VentasProductos> listaVentas) {
        this.listaVentas = listaVentas;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

}
