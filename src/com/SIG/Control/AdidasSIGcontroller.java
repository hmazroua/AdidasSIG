package com.SIG.Control;

import com.SIG.GUI.InvoiceDesign;
import com.SIG.GUI.InvoiceDialog;
import com.SIG.GUI.LineDialog;
import com.SIG.model.Invoice;
import com.SIG.model.InvoicesTableModel;
import com.SIG.model.Line;
import com.SIG.model.LinesTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Hassan Mazroua
 */
public class AdidasSIGcontroller implements ActionListener, ListSelectionListener {
    private InvoiceDesign designframe;
    private InvoiceDialog invoiceDialog;
    private LineDialog lineDialog;

    public AdidasSIGcontroller(InvoiceDesign frame){
    this.designframe = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand=e.getActionCommand();
        System.out.println("Action " + actionCommand);
        switch (actionCommand){
                case "Load File":
                    loadFile();
                break;
                case "Save File":
                    saveFile();
                break;
                case "Create New Invoice":
                    createNewInvoice();
                break;
                case "Delete Invoice":
                    deleteInvoice();
                break;
                case "Add New Item":
                    addNewItem();
                break;
                case "Delete Item":
                    deleteItem();
                break;
                case "createInvoiceConfirm":
                    createInvoiceconfirm();
                    break;
                case "createInvoiceCancel":
                    createInvoiceCancel();
                    break;
                case "createLineConfirm":
                    createLineConfirm();
                    break;
                case "createLineCancel":
                    createLineCancel();
                    break; 
    }
    }
    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = designframe.getInvoiceTable().getSelectedRow();
        if (selectedIndex != -1) {
        System.out.println("You have selection on raw No. "+ selectedIndex);
        Invoice currentInvoice = designframe.getInvoices().get(selectedIndex);
        designframe.getInvoiceNumLabel().setText(""+currentInvoice.getNum());
        designframe.getInvoiceDateLabel().setText(currentInvoice.getDate());
        designframe.getCustomerNameLabel().setText(currentInvoice.getCustomer());
        designframe.getInvoiceTotalLabel().setText(""+currentInvoice.getInvoiceTotal());
        LinesTableModel linesTableModel = new LinesTableModel(currentInvoice.getLines());
        designframe.getLineTable().setModel(linesTableModel);
        linesTableModel.fireTableDataChanged();
    }
    }
    //load CSV files into the project designframe
    private void loadFile(){
        JFileChooser fc = new JFileChooser();
        try {
        int result = fc.showOpenDialog(designframe);
        if (result == JFileChooser.APPROVE_OPTION) {
           File headerFile = fc.getSelectedFile();
           Path headerPath = Paths.get(headerFile.getAbsolutePath());
           List<String> headerLines = Files.readAllLines(headerPath);
           System.out.println("Invoices are successfully read");
           ArrayList<Invoice> invoicesArray = new ArrayList<> ();
           for (String headerLine : headerLines){
               try {
               String[] headerLineParts = headerLine.split(",");
               int invoiceNum = Integer.parseInt(headerLineParts[0]);
               String invoiceDate = (headerLineParts[1]);
               String customerName = (headerLineParts[2]);
               Invoice invoice= new Invoice(invoiceNum, invoiceDate, customerName);
               invoicesArray.add(invoice);
               } catch (Exception ex){
                   ex.printStackTrace();
                   JOptionPane.showMessageDialog(designframe, "Wrong File Format", "Error", JOptionPane.ERROR_MESSAGE);
                }
           }
           System.out.println("Invoices are comma separated successfully");
           result = fc.showOpenDialog(designframe);
           if (result == JFileChooser.APPROVE_OPTION) {
           File lineFile = fc.getSelectedFile();
           Path linePath = Paths.get(lineFile.getAbsolutePath());
           List<String> lineLines = Files.readAllLines(linePath);
           System.out.println("Lines are successfully read");
           for (String lineLine : lineLines){
               try {
               String[] lineLineParts = lineLine.split(",");
               int invoiceNum = Integer.parseInt(lineLineParts[0]);
               String itemName = (lineLineParts[1]);
               double itemPrice = Double.parseDouble(lineLineParts[2]);
               int count = Integer.parseInt(lineLineParts[3]);
               Invoice inv = null;
               for (Invoice invoice : invoicesArray){
                   if (invoice.getNum() == invoiceNum){
                       inv = invoice;
                       break;
                   }
               }
               Line line = new Line(invoiceNum, itemName, itemPrice, count, inv);
               inv.getLines().add(line);
               } catch (Exception ex){
                   ex.printStackTrace();
                   JOptionPane.showMessageDialog(designframe, "Wrong File Format", "Error", JOptionPane.ERROR_MESSAGE);
           }
           System.out.println("Lines are linked with invoices");
           }
           designframe.setInvoices(invoicesArray);
           InvoicesTableModel invoicesTableModel = new InvoicesTableModel(invoicesArray);
           designframe.setInvoicesTableModel(invoicesTableModel);
           designframe.getInvoiceTable().setModel(invoicesTableModel);
           designframe.getInvoicesTableModel().fireTableDataChanged();
        }
    } 
        }catch (Exception ex){
        ex.printStackTrace();
        JOptionPane.showMessageDialog(designframe, "Wrong File Format", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
//save new invoices table or save new item in line table 
    private void saveFile() {
        ArrayList<Invoice> invoices = designframe.getInvoices();
        String headers = "";
        String lines = "";
        for (Invoice invoice : invoices) {
            String invoiceCSV = invoice.getAsCSV();
            headers += invoiceCSV;
            headers += "\n";
            
            for (Line line : invoice.getLines()) {
            String lineCSV = line.getAsCSV();
            lines += lineCSV;
            lines += "\n";
            }
        }
        System.out.println("Changes Saved");
        try {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(designframe);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter hfWriter = new FileWriter(headerFile);
                hfWriter.write(headers);
                hfWriter.flush();
                hfWriter.close();
                result = fc.showSaveDialog(designframe);
                if (result == JFileChooser.APPROVE_OPTION) {
                File lineFile = fc.getSelectedFile();
                FileWriter lfWriter = new FileWriter(lineFile);
                lfWriter.write(lines);
                lfWriter.flush();
                lfWriter.close();
            }
            }
        } catch (Exception ex) {
                   }
    }

     //create new invoice to invoice table   
    private void createNewInvoice() { 
        invoiceDialog = new InvoiceDialog(designframe);
        invoiceDialog.setVisible(true);
    }
// delete existing invoice
    private void deleteInvoice(){
        int selectedRow = designframe.getInvoiceTable().getSelectedRow();
        if (selectedRow != -1) {
            designframe.getInvoices().remove(selectedRow);
            designframe.getInvoicesTableModel().fireTableDataChanged();
        }
            }
// add new item to line table
    private void addNewItem(){
             lineDialog = new LineDialog(designframe);
             lineDialog.setVisible(true);
    }
// delete item from line table
    private void deleteItem(){
        int selectedInv = designframe.getInvoiceTable().getSelectedRow();
        int selectedRow = designframe.getLineTable().getSelectedRow();
        if (selectedInv != -1 && selectedRow != -1) {
            Invoice invoice = designframe.getInvoices().get(selectedInv);
            invoice.getLines().remove(selectedRow);
            LinesTableModel linesTableModel = new LinesTableModel(invoice.getLines());
            designframe.getLineTable().setModel(linesTableModel);
            linesTableModel.fireTableDataChanged();
            designframe.getInvoicesTableModel().fireTableDataChanged();
        }
            }
// create new invoice confirmation dialog
    private void createInvoiceconfirm() {
        String date = invoiceDialog.getInvoiceDateField().getText();
        String customer = invoiceDialog.getCustomerNameField().getText();
        int num = designframe.getNextInvoiceNum();
        // to validate date formatting to be intger not string type
        try {
            String[] dateFormatSection = date.split("-");
            if (dateFormatSection.length < 3) {
                JOptionPane.showMessageDialog(designframe, "Wrong date formate, please enter correct date","Error",JOptionPane.ERROR_MESSAGE);
            } else {
                int day = Integer.parseInt(dateFormatSection[0]);
                int month = Integer.parseInt(dateFormatSection[1]);
                int year = Integer.parseInt(dateFormatSection[2]);
                if (day > 31 || month > 12) {
                   JOptionPane.showMessageDialog(designframe, "Wrong date formate, please enter correct date","Error",JOptionPane.ERROR_MESSAGE);
                }else{
            Invoice invoice = new Invoice(num, date, customer);
            designframe.getInvoices().add(invoice);
            designframe.getInvoicesTableModel().fireTableDataChanged();
            invoiceDialog.setVisible(false);
            invoiceDialog.dispose();
            invoiceDialog = null;
            }    
        }
        }catch (Exception ex){
        JOptionPane.showMessageDialog(designframe, "Wrong date formate, please enter correct date","Error",JOptionPane.ERROR_MESSAGE);
                }
    }
// cancel creating new invoice
    private void createInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
    }
// create new item in the line table
    private void createLineConfirm() {
        String item = lineDialog.getItemNameField().getText();
        String countStr = lineDialog.getItemCountField().getText();
        String priceStr = lineDialog.getItemPriceField().getText();
        int count = Integer.parseInt(countStr);
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = designframe.getInvoiceTable().getSelectedRow();
        if (selectedInvoice != -1) {
            Invoice invoice = designframe.getInvoices().get(selectedInvoice);
            Line line = new Line(count, item, price, count, invoice);
            invoice.getLines().add(line);
            LinesTableModel linesTableModel = (LinesTableModel) designframe.getLineTable().getModel();
            linesTableModel.fireTableDataChanged();
            designframe.getInvoicesTableModel().fireTableDataChanged();
            
        }
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }
// cancel adding new item
    private void createLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }
    }

// this file is the main controller of the project