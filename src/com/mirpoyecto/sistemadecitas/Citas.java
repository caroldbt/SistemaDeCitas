package com.mirpoyecto.sistemadecitas;

import javafx.beans.property.*;

public class Citas {

    private final IntegerProperty id;
    private final StringProperty cliente;
    private final StringProperty fecha;
    private final StringProperty especialidad;

    public Citas(int id, String cliente, String fecha, String especialidad) {
        this.id = new SimpleIntegerProperty(id);
        this.cliente = new SimpleStringProperty(cliente);
        this.fecha = new SimpleStringProperty(fecha);
        this.especialidad = new SimpleStringProperty(especialidad);
    }

    public String getEspecialidad() {
        return especialidad.get();
    }

    public StringProperty especialidadProperty() {
        return especialidad;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getCliente() {
        return cliente.get();
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public String getFecha() {
        return fecha.get();
    }

    public StringProperty fechaProperty() {
        return fecha;
    }
}
