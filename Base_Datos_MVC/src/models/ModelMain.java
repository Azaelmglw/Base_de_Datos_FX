package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

/**
 *
 * @author Azaelmglw
 */

public class ModelMain {
    /*  Parents array list position:
    [0] -> main    |
    */
    
    /*  Alerts array list position:
    [0] -> confirmation_alert  |    [1] -> error_alert  |
    */
    
    /* Text Formatters array list position:
    [0] -> name_formatter
    */
    
    /*  App tools array list position:
    [0] -> id  |   [1] -> name  |   [2] -> email
    */
    
    private final Stage primaryStage;
    private List<Parent> parents = new ArrayList<>(5);
    private List<TextFormatter> text_formatters = new ArrayList<>(5);
    private List<Alert> alerts = new ArrayList<>(5);
    private List<String> app_tools = new ArrayList<>(5);
    
    private Optional <ButtonType> result;
    
    private String psql_query;
    private Connection psql_connection;
    private PreparedStatement psql_prepared_statement;
    private ResultSet psql_result_set;
    
    public ModelMain(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void ObtainUserData(){
        try {
            psql_query = "SELECT id_contacto AS ID, nombre AS Name, email AS Email FROM contactos ORDER BY id_contacto;";
            PSQLPrepareStatement();
            PSQLExecuteQueryPS();
            psql_result_set.first();
            SetValues(psql_result_set.getString("ID"), psql_result_set.getString("Name"), psql_result_set.getString("Email"));
        } 
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 005: An error has ocurred while obtaining the users data. " + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void InsertUser(){
        try{
            if(VerifyUserInput()){
                psql_query = "INSERT INTO contactos(nombre, email) VALUES(?,?);";
                PSQLPrepareStatement();
                psql_prepared_statement.setString(1, getName());
                psql_prepared_statement.setString(2, getEmail());
                PSQLExecuteUpdatePS();
                ObtainUserData();
                MoveToFirst();
            }
        }
        catch(SQLException e){
            getAlert(1).setHeaderText("Error 006: An error has ocurred while inserting the user. " + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void ModifyUser(){
        try{
            if(VerifyUserInput()){
                psql_query = "UPDATE contactos SET nombre = ?, email = ? WHERE id_contacto = ?;";
                PSQLPrepareStatement();
                psql_prepared_statement.setString(1, getName());
                psql_prepared_statement.setString(2, getEmail());
                psql_prepared_statement.setInt(3, getID());
                PSQLExecuteUpdatePS();
                ObtainUserData();
                MoveToFirst();
            }
        }
        catch(SQLException e){
            getAlert(1).setHeaderText("Error 007: An error has ocurred while modifying the user. " + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void DeleteUser(){
        try{
            psql_query = "DELETE FROM contactos WHERE id_contacto = ?";
            PSQLPrepareStatement();
            psql_prepared_statement.setInt(1, getID());
            PSQLExecuteUpdatePS();
            ObtainUserData();
            MoveToFirst();
        }
        catch(SQLException e){
            getAlert(1).setHeaderText("Error 008: An error has ocurred while deleting the user. " + e);
            getAlert(1).showAndWait();
        }
    }
    
    public boolean VerifyUserInput(){
        if(getName().isEmpty() || getEmail().isEmpty()){
            getAlert(1).setHeaderText("No value to be added.");
            getAlert(1).showAndWait();
            return false;
        }
        else if(!getEmail().contains("@")){
            getAlert(1).setHeaderText("Incorrect email.");
            getAlert(1).showAndWait();
            return false;
        }
        else{
            return true;
        }
    }
    
    public void SetValues(String id, String name, String email) {
        app_tools.add(0, id);
        app_tools.add(1, name);
        app_tools.add(2, email);
    }
    
    public void DeleteConfirmation(){
        getAlert(0).setTitle("Confirmation Required");
        getAlert(0).setHeaderText("Are you sure you want to delete this user?");
        getAlert(0).setContentText("Choose one of the following options.");
        result = getAlert(0).showAndWait();
    }

    public void MoveToFirst(){
        try {
            if (psql_result_set.isFirst()){

            } 
            else{
                psql_result_set.first();
                SetValues(psql_result_set.getString("ID"), psql_result_set.getString("Name"), psql_result_set.getString("Email"));
            }
        }
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 009: A problem has ocurred while moving to the first value." + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void MoveToPrevious(){
        try {
            if (psql_result_set.isFirst()){

            } 
            else{
                psql_result_set.previous();
                SetValues(psql_result_set.getString("ID"), psql_result_set.getString("Name"), psql_result_set.getString("Email"));
            }
        }
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 010: A problem has ocurred while moving to the previous value." + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void MoveToNext(){
        try {
            if (psql_result_set.isLast()){

            } 
            else{
                psql_result_set.next();
                SetValues(psql_result_set.getString("ID"), psql_result_set.getString("Name"), psql_result_set.getString("Email"));
            }
        }
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 011: A problem has ocurred while moving to the next value." + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void MoveToLast(){
        try {
            if (psql_result_set.isLast()){

            } 
            else{
                psql_result_set.last();
                SetValues(psql_result_set.getString("ID"), psql_result_set.getString("Name"), psql_result_set.getString("Email"));
            }
        }
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 012: A problem has ocurred while moving to the last value." + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void PSQLConnect() {
        try{
            Class.forName("org.postgresql.Driver");
            psql_connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/agenda_mvc", "postgres", "postgres");
        } 
        catch(SQLException e){
            getAlert(1).setHeaderText("Error 000: A problem has ocurred connecting to the database. " + e);
            getAlert(1).showAndWait();
        }
        catch(ClassNotFoundException e){
            getAlert(1).setHeaderText("I don't even know man" + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void PSQLPrepareStatement(){
        try{
            PSQLConnect();
            psql_prepared_statement = psql_connection.prepareStatement(psql_query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } 
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 002: A problem has ocurred while Preparing the Statement." + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void PSQLExecuteQueryPS(){
        try{
            PSQLConnect();
            psql_result_set = psql_prepared_statement.executeQuery();
        } 
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 003: A problem has ocurred while Executing the Query. " + e);
            getAlert(1).showAndWait();
        }
    }
    
    public void PSQLExecuteUpdatePS(){
        try{
            psql_prepared_statement.executeUpdate();
            psql_connection.close();
        } 
        catch (SQLException e){
            getAlert(1).setHeaderText("Error 013: A problem has ocurred while updating the database. " + e);
            getAlert(1).showAndWait();
        }
    }
    
    public Optional getResult(){
        return result;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Parent getParent(int parent_position) {
        return parents.get(parent_position);
    }

    public void setParent(int parent_position, Parent parent) {
        this.parents.add(parent_position, parent);
    }    
    
    public TextFormatter getTextFormatter(int text_formatter_position){
        return text_formatters.get(text_formatter_position);
    }

    public void setTextFormatter(int text_formatter_position, TextFormatter text_formatter){
        this.text_formatters.add(text_formatter_position, text_formatter);
    }
    
    public Alert getAlert(int alert_position) {
        return alerts.get(alert_position);
    }

    public void setAlert(int alert_position, Alert alert) {
        this.alerts.add(alert_position, alert);
    }
    
    public int getID(){
        return Integer.parseInt(app_tools.get(0));
    }
    
    public String getName(){
        return app_tools.get(1);
    }
    
    public void setName(String name){
        app_tools.add(1, name);
    }
    
    public String getEmail(){
        return app_tools.get(2);
    }
    
    public void setEmail(String email){
        this.app_tools.add(2, email);
    }
}