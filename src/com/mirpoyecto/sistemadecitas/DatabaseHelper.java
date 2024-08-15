package com.mirpoyecto.sistemadecitas;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:citas.db";

    //Metodo para conectar a la base de datos
    public static Connection connect(){
        Connection con = null;
        try{
            con = DriverManager.getConnection(URL);
            System.out.println("Conectado a la base de datos");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return con;
    }

    //Crear tabla de citas
    public static void crearTabla(){
        String sql = "CREATE TABLE IF NOT EXISTS citas (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                " cliente TEXT NOT NULL, \n" +
                " fecha TEXT NOT NULL," +
                "especialidad TEXT NOT NULL);";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println("Tabla de citas creada");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void eliminarTabla(){
        String sql = "DROP TABLE IF EXISTS citas";
        try(Connection con = connect();
        Statement stmt = con.createStatement()){
            stmt.execute(sql);
            System.out.println("Tabla de citas eliminada");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //Insertar cita en la base de datos
    public static void agregarCita(String cliente, String fecha, String especialidad){
        String sql = "INSERT INTO citas (cliente, fecha, especialidad) VALUES (?,?,?);";

        try(Connection con = connect();
        PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1,cliente);
            pstmt.setString(2,fecha);
            pstmt.setString(3,especialidad);
            pstmt.executeUpdate();
            System.out.println("Cita agregada");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //Obtener todas las citas
    public static ResultSet obtenerCitas(){
        String sql = "SELECT * FROM citas";
        try{
            Connection con = connect();
            Statement stmt = con.createStatement();
            return stmt.executeQuery(sql);
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    //Editar cita
    public static void editarCita(int id, String cliente, String fecha, String especialidad){
        String sql = "UPDATE citas SET cliente = ?, fecha = ?, especialidad =? WHERE id = ?";

        try(Connection con = connect();
        PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1,cliente);
            pstmt.setString(2,fecha);
            pstmt.setString(3,especialidad);
            pstmt.setInt(4,id);
            pstmt.executeUpdate();
            System.out.println("Cita actualizada");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //Eliminar cita
    public static void eliminarCita(int id){
        String sql = "DELETE FROM citas WHERE id = ?";
        try(Connection con = connect();
        PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1,id);
            pstmt.executeUpdate();
            System.out.println("Cita eliminada");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public static ResultSet obtenerCitaDuplicada(String cliente, String fecha, String especialidad){
        String sql = "SELECT * FROM citas WHERE cliente = ? AND fecha = ? AND especialidad = ?";
        try{
            Connection con = connect();
            PreparedStatement pstmt= con.prepareStatement(sql);
            pstmt.setString(1,cliente);
            pstmt.setString(2,fecha);
            pstmt.setString(3,especialidad);
            return pstmt.executeQuery();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static ResultSet obtenerCitaPorClienteYEspecialidad(String cliente,String especialidad){
        String sql = "SELECT * FROM citas WHERE cliente = ? AND especialidad = ?";
        try{
            Connection con = connect();
            PreparedStatement pstmt= con.prepareStatement(sql);
            pstmt.setString(1,cliente);
            pstmt.setString(2,especialidad);
            return pstmt.executeQuery();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
