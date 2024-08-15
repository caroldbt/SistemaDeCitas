package com.mirpoyecto.sistemadecitas;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainApp extends Application {
    //Lista observable para gestionar las citas
    private ObservableList<Citas> citas = FXCollections.observableArrayList();
    private Citas citasSeleccionada = null;

    private TextField clienteField;
    private DatePicker fechaField;
    private Spinner<Integer> horaSpinner;
    private Spinner<Integer> minutoSpinner;
    private ComboBox<String> especialidadComboBox;
    private Stage ventanaEspecialidades;

    @Override
    public void start(Stage primaryStage){

        //Crear tabla
        TableView<Citas> tableView = new TableView<>();
        TableColumn<Citas, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Citas, String> colCliente = new TableColumn<>("Paciente");
        colCliente.setCellValueFactory(cellData -> cellData.getValue().clienteProperty());

        TableColumn<Citas, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());

        TableColumn<Citas, String> colEspecialidad = new TableColumn<>("Especialidad");
        colEspecialidad.setCellValueFactory(cellData -> cellData.getValue().especialidadProperty());

        //Agregar columna de acciones (editar y eliminar)
        TableColumn<Citas, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>(){
            private final Button editButton = new Button("Editar");
            private final Button deleteButton = new Button("Eliminar");
            private final HBox hbox = new HBox(10, editButton, deleteButton);
            {
                editButton.setOnAction(event -> {
                   citasSeleccionada= getTableView().getItems().get(getIndex());
                   cargarDatosParaEditar();
                });

                deleteButton.setOnAction(event -> {
                    Citas citaAEliminar = getTableView().getItems().get(getIndex());
                    eliminarCita(citaAEliminar);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(empty){
                    setGraphic(null);
                }else {
                    setGraphic(hbox);
                }
            }
        });

        tableView.getColumns().addAll(colId, colCliente, colFecha,colEspecialidad, colAcciones);
        tableView.setItems(citas);

        //Crear campos de texto
        clienteField = new TextField();
        clienteField.setPromptText("Paciente");

        //Crear DatePicker para la fecha
        fechaField = new DatePicker();
        fechaField.setPromptText("Seleccionar Fecha");

        //Crear Spinner para la hora y minutos
        horaSpinner = new Spinner<>(0,23,12);
        minutoSpinner = new Spinner<>(0,59,0);

        //Combobox de especialidades
        especialidadComboBox = new ComboBox<>();
        especialidadComboBox.setItems(FXCollections.observableArrayList("Cardiología","Pediatría","Dermatología","Neurología"));
        especialidadComboBox.setPromptText("Seleccione Especialidad");

        //Crear boton para añadir cita
        Button addButton = new Button("Agregar cita");
        addButton.setOnAction(e -> {
            agregarCita();

        });

        //Boton especialidad
        Button especialidadesButton = new Button("Ver Especialidades");
        especialidadesButton.setOnAction(e -> mostrarVentanaEspecialidades());

        //Boton para cerrar la ventana
        Button cerrarButton = new Button("Cerrar");
        cerrarButton.setOnAction(e ->{
            primaryStage.close();
            if(ventanaEspecialidades != null){
                ventanaEspecialidades.close();
            }
        } );

        //Crear un layout para el formulario
        GridPane formLayout = new GridPane();
        formLayout.setHgap(10);
        formLayout.setVgap(10);
        formLayout.setPadding(new Insets(10, 10, 10, 10));

        formLayout.add(new Label("Paciente"), 0, 0);
        formLayout.add(clienteField, 1, 0);

        formLayout.add(new Label("Fecha"), 0, 1);
        formLayout.add(fechaField, 1, 1);

        formLayout.add(new Label("Hora:"),0,2);
        formLayout.add(horaSpinner, 1, 2);

        formLayout.add(new Label("Minutos:"),0,3);
        formLayout.add(minutoSpinner, 1, 3);

        formLayout.add(new Label("Especialidad:"),0,4);
        formLayout.add(especialidadComboBox, 1, 4);

        formLayout.add(addButton, 1, 5);

        //Layout principal
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new  Insets(10));
        mainLayout.getChildren().addAll(formLayout,tableView, especialidadesButton, cerrarButton);

        //Crear scena
        Scene scene = new Scene(mainLayout,700,700);

        //Configurar la ventana principal
        primaryStage.setTitle("Sistema de Citas");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Cargar citas desde la base de datos
        cargarCitas();
    }
    private void agregarCita() {
        String cliente = clienteField.getText();
        LocalDate fechaseleccionada = fechaField.getValue();
        int hora = horaSpinner.getValue();
        int minuto = minutoSpinner.getValue();
        String especialidad = especialidadComboBox.getValue();

        if (cliente == null || cliente.isEmpty() || fechaseleccionada == null || especialidad == null) {
            mostrarAlerta("Error", "Debe completar todos los campos.");
            return;
        }

        // Crear LocalDateTime combinando la fecha y hora seleccionadas
        LocalDateTime fechaHora = LocalDateTime.of(fechaseleccionada, LocalTime.of(hora, minuto));
        String fechaFormateada = fechaHora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Verificar si la cita ya existe
        if (!citaDisponible(cliente, fechaFormateada, especialidad)) {
            mostrarAlerta("Error", "Ya existe una cita en esa fecha, hora y especialidad para este paciente.");
            return;
        }

        DatabaseHelper.agregarCita(cliente, fechaFormateada, especialidad);
        mostrarAlerta("Éxito", "Cita creada exitosamente.");
        cargarCitas();
        limpiarFormulario();
    }


    private boolean citaYaExiste(String cliente, String especialidad){
        try(ResultSet rs = DatabaseHelper.obtenerCitaPorClienteYEspecialidad(cliente, especialidad)) {
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    private void mostrarVentanaEspecialidades(){
        if (ventanaEspecialidades == null){
            ventanaEspecialidades = new Stage();
            VBox especialidadesLayout = new VBox(10);
            especialidadesLayout.setPadding(new Insets(10));

            for(String especialidad : especialidadComboBox.getItems()){
                Button especialidadButton = new Button(especialidad);
                especialidadButton.setOnAction(e -> mostrarCitasPorEspecialidad(especialidad));
                especialidadesLayout.getChildren().add(especialidadButton);
            }
            Button volverButton = new Button("Volver");
            volverButton.setOnAction(e -> ventanaEspecialidades.close());
            Button cerrarButton = new Button("Cerrar");
            cerrarButton.setOnAction(e -> {
                ventanaEspecialidades.close();
                Stage primaryStage = (Stage) volverButton.getScene().getWindow();
                primaryStage.close();
            });
            Scene especialidadesScene = new Scene(especialidadesLayout, 700, 700);
            ventanaEspecialidades.setTitle("Especialidades");
            ventanaEspecialidades.setScene(especialidadesScene);
        }
        ventanaEspecialidades.show();
    }

    private void cargarCitas(){
        citas.clear();
        try(ResultSet rs =DatabaseHelper.obtenerCitas()){
            while(rs.next()){
                citas.add(new Citas(rs.getInt("id"), rs.getString("cliente"),rs.getString("fecha"), rs.getString("especialidad")));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private boolean citaDisponible(String cliente, String fecha, String especialidad) {
        try (ResultSet rs = DatabaseHelper.obtenerCitaDuplicada(cliente, fecha, especialidad)) {
            return !rs.next();  // Devuelve verdadero solo si no hay citas duplicadas
        } catch (SQLException e) {
            System.out.println("Error al verificar la disponibilidad de la cita: " + e.getMessage());
            return false;
        }
    }


    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void mostrarCitasPorEspecialidad(String especialidad){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Citas para "+especialidad);
        alert.setHeaderText("Listado de Citas");

        StringBuilder sb = new StringBuilder();
        for (Citas cita : citas){
            if(cita.getEspecialidad().equals(especialidad)){
                sb.append("Paciente: ").append(cita.getCliente())
                        .append(" | Fecha y hora: ").append(cita.getFecha())
                        .append("\n");
            }
        }
        if (sb.length() == 0){
            sb.append("Nno hay citas para esa especialidad");
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }
    //Metodo para cargar datos al formulario para editar
    private void cargarDatosParaEditar(){
        if(citasSeleccionada !=null){
            //Cargar los datos de la cita seleccionada en los campos
            clienteField.setText(citasSeleccionada.getCliente());
            LocalDateTime fechaHora = LocalDateTime.parse(citasSeleccionada.getFecha(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            fechaField.setValue(fechaHora.toLocalDate());
            horaSpinner.getValueFactory().setValue(fechaHora.getHour());
            minutoSpinner.getValueFactory().setValue(fechaHora.getMinute());
        }
    }
    //Metodo para limpiar el formulario
    private void limpiarFormulario(){
        clienteField.clear();
        fechaField.setValue(null);
        horaSpinner.getValueFactory().setValue(12);
        minutoSpinner.getValueFactory().setValue(0);
        especialidadComboBox.setValue(null);
    }
    //Metodo para eliminar cita
    private void eliminarCita(Citas cita){
        DatabaseHelper.eliminarCita(cita.getId());
        cargarCitas();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
