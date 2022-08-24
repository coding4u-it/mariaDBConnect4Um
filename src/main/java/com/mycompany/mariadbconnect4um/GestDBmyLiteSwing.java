/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mariadbconnect4um;

import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author france
 */
public class GestDBmyLiteSwing extends GestDBmyLite{
    
    public GestDBmyLiteSwing(String nomeDB, String userDB, String passDB) {
        super(nomeDB, userDB, passDB);
    }
    
    public GestDBmyLiteSwing(String nomeDB) {
        super(nomeDB);
    }
    
    /**
     * il metodo riceve una tabella di swing la rielabora e la restituisce 
     * carica di elementi presi dal db
     * @param model 
     * @return tabella swing
     * @throws SQLException 
     */
    public DefaultTableModel tabella(DefaultTableModel model) throws SQLException{
        int cMax = super.getRsmd().getColumnCount();
        model.setColumnCount(cMax); //visualizzazione risultato in tabella
        model.setColumnIdentifiers(etichette(cMax));
        int riga;Object valore; //dichiarazione variabile per righe ed oggetti tabella 
        while(super.getRs().next()) { 
            riga = model.getRowCount();
            for(int c=1; c<=cMax; c++) {
                model.setRowCount(riga + 1); //nuova riga
                valore = super.getRs().getObject(c);
                model.setValueAt(valore, riga, c-1);
            }
        }
        return model;
    } 
}
