/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mariadbconnect4um;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author france
 */
public class ExcelExport {

    public ExcelExport() { }
    
    public String exportTable(JTable table, File file) throws IOException {
        TableModel model = table.getModel();
        String tmp="";
        FileWriter out = new FileWriter(file);
        for(int i=0; i < model.getColumnCount(); i++) 
            out.write(model.getColumnName(i)+";");       
        out.write("\n");
        for(int i=0; i< model.getRowCount(); i++) {
            for(int j=0; j < model.getColumnCount();j++){
                if (model.getValueAt(i,j)==null)
                    out.write(""+";");
                else
                    out.write(model.getValueAt(i,j).toString()+";");  
            } 
            out.write("\n");
        }
        out.close();
        System.out.println("write out to: " + file);
        return file.toString();
    }
}