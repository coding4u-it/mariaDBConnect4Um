/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mariadbconnect4um;

import java.sql.*;
import java.util.ArrayList;

public class GestDBmyLite{
    private String urlDB;  //contiene la stringa di connessione al DB
    private String driver; //contiene il driver del DB
    private String userDB; // se DB MySQL contiene username per accedere al db
    private String passDB; // se DB MySQL contiene password per accedere al db
    private Connection conn;
    private Statement stm;
    private DatabaseMetaData dbmd;
    private ResultSet rs;
    private ResultSetMetaData rsmd;
    private String nomeDB; //nome del DB
    private boolean tipoDB; //tipologiaDB true=mysql, false=sqlite
    
    /**
     * Costruttore permette di caricare il driver per DB MySQL
     * e creare la stringa di connessione al DB 
     * @param nomeDB nome del DB
     * @param userDB username del DB MySQL di default root
     * @param passDB username del DB MySQL di default ""
     */
    public GestDBmyLite(String nomeDB, String userDB, String passDB){
        this.userDB=userDB;
        this.passDB=passDB;
        this.nomeDB=nomeDB;
        driver="com.mysql.cj.jdbc.Driver";
        urlDB="jdbc:mysql://80.211.81.8:3306/"+nomeDB+"?zeroDateTimeBehavior=ConvertToNull";//evita errore sulle date nulle
        try {  //carico il driver
            Class.forName(driver); 
        }
        catch(ClassNotFoundException e) {
          System.out.println("Driver non trovato!");
          System.exit(1);
        }        
    }
    /**
     * Costruttore permette di caricare il driver per DB SQLite
     * e creare la stringa di connessione al DB 
     * @param nomeDB nome del DB
     */
     public GestDBmyLite(String nomeDB){
        driver="org.sqlite.JDBC";
        urlDB="jdbc:sqlite:"+nomeDB;
        try {  //carico il driver
            Class.forName(driver); 
        }
        catch(ClassNotFoundException e) {
          System.out.println("Driver non trovato!");
          System.exit(1);
        }        
    }
     
     /**
      * metodo per il controllo del DB usato MySQL o SQLite
     * @return 
      */
     public String ctrlDB(){
        String tmp="ok";
        try {  //apre la connessione verso il database
            if (urlDB.contains("mysql")){
               setConn(DriverManager.getConnection(urlDB,userDB,passDB));
               this.tipoDB=true;
            }
            else{
               setConn(DriverManager.getConnection(urlDB));
               this.tipoDB=false;
            }
        }
        catch (SQLException e) {
            System.out.println("Errore nella connessione"+e);
            tmp="Credenziali errate!";
        }
        return tmp;
     }

    /**
     * @return the dbmd
     */
    public DatabaseMetaData getDbmd() {
        return dbmd;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public Statement getStm() {
        return stm;
    }

    public ResultSet getRs() {
        return rs;
    }

    /**
     * @param rs the rs to set
     */
    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public ResultSetMetaData getRsmd() {
        return rsmd;
    }
    /**
     * metodo per informazioni sui metadati
     */
    public void metadati() {
     ctrlDB();
        try {
            dbmd = conn.getMetaData();
        } catch (SQLException e) {
            System.err.println("There was an error getting the metadata: "+e.getMessage());
        }
    }

    /**
     * inizializzazione connessione, statement e resultset
     * @param query 
     */
    public void dirQuery(String query){  
        ctrlDB();
        //creazione statement mediante una query sql e ricevo il risultato nel resultset
       try { 
            stm=conn.createStatement();
            setRs(stm.executeQuery(query));
            rsmd=rs.getMetaData();
        }
        catch (SQLException e) {
            System.out.println("Errore nella query");
            System.exit(1);
        }
    }
    /**
     * inizializzazione connessione, statement 
     * @param query 
     */  
    public void dirExec(String query){    
        ctrlDB();
        try { //eseguo una query di inserimento o modifica o cancellazione al database
           stm=conn.createStatement();
           stm.executeUpdate(query);
           stm.close();
        }
        catch (SQLException e) {
            System.out.println("Errore nella query");
            System.exit(1);
        }
    }
    /**
     * chiude connessione al DB
     * @throws SQLException 
     */ 
    public void closeConn() throws SQLException{
        if(conn!=null)
            conn.close();
    }
    /**
     * chiude resultset
     * @throws SQLException 
     */
    public void closeRs() throws SQLException{
        rs.close();
    } 
    
     /**
     * @return ArrayList with the table's name
     * @throws SQLException
     */
    public ArrayList getTablesMetadata() throws SQLException {
        metadati();
       //System.out.println("pn "+dbmd.getDatabaseProductName()+" pv "+dbmd.getDatabaseProductVersion()+" dn "+dbmd.getDriverName()+" dv "+dbmd.getDriverVersion());
        ArrayList<String> tables = new ArrayList<>();
        //riceve gli object in String array.
        setRs(getDbmd().getTables(null, null, null, new String[]{"TABLE"}));
        while (getRs().next()) {
            //Seleziona il catalogo dal Database 
            if(getRs().getString("TABLE_CAT").equalsIgnoreCase(getNomeDB())){
                tables.add(getRs().getString("TABLE_NAME"));
            }
        } 
        rs.close();
      return tables;
    }
    
    /**
     * il metodo serve per fornire le etichette alle tabelle
     * @param n
     * @return array di stringhe 
     * @throws SQLException 
     */
    public String[] etichette(int n) throws SQLException{
        String [] mat=new String[n];
        for(int i=1;i<=mat.length;i++)
            mat[i-1]=getRsmd().getColumnLabel(i);
        return mat;
    }
    
     /**
     * @return Array with the catalog name
     * @throws SQLException
     */
    public String[] getCatalogMetadata() throws SQLException {
        metadati();
        int i=0;
        //riceve gli object in String array.
        setRs(getDbmd().getCatalogs());
        String cat="";
        while (getRs().next()) {
            cat+=(getRs().getString(1))+",";
            i++;
        }
        rs.close();
      return cat.split(",");
    }   

    /**
     * @return the nomeDB
     */
    public String getNomeDB() {
        return nomeDB;
    }

    /**
     * @param nomeDB the nomeDB to set
     */
    public void setNomeDB(String nomeDB) {
        this.nomeDB = nomeDB;
        if (tipoDB)
            urlDB="jdbc:mysql://80.211.81.8:3306/"+nomeDB+"?zeroDateTimeBehavior=ConvertToNull";//evita errore sulle date nulle
        else
            urlDB="jdbc:sqlite:"+nomeDB;
    }
}
