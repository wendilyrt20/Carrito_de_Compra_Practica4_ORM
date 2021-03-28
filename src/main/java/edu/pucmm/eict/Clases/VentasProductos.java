package edu.pucmm.eict.Clases;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity
public class VentasProductos implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date FechaCompra;
    private double total ;
    private String nombreCliente;


    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Producto> listaProducto;

    public VentasProductos(Date fechaCompra, double total, String nombreCliente,Set<Producto> listaProducto) {
        this.id = id;
        this.FechaCompra = fechaCompra;
        this.total = total;
        this.nombreCliente = nombreCliente;
        this.listaProducto = listaProducto;
    }


    public Set<Producto> getListaProducto() {
        return listaProducto;
    }

    public void setListaProducto(Set<Producto> listaProducto) {
        this.listaProducto = listaProducto;
    }

//////////////////////////////////////////////////////////////////////////////////////////////


    public VentasProductos() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaCompra() {
        return FechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        FechaCompra = fechaCompra;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
//////////////////////////////////////////////////////////////////////////////////////////////

/*
    public void addCarritoV(CarroCompra carro){
        listaCarrito.add(carro);
    }
*/


}
