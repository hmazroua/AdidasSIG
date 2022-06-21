package com.SIG.model;

import java.util.ArrayList;

//invoice table
public class Invoice {
    private int number;
    private String date;
    private String customer;
    private ArrayList<Line> lines;

    public Invoice() {
}
    
    public Invoice(int num, String date, String customer) {
        this.number= num;
        this.date=date;
        this.customer=customer;
}
//calling invoices total
    public double getInvoiceTotal() {
        double total=0.0;
        for (Line line : getLines()) {
            total +=line.getLineTotal();
        }
        return total;
    }
    public ArrayList<Line> getLines() {
        if (lines == null){
            lines = new ArrayList<>();
        }
        return lines;
    }

    public int getNum() {
        return number;
    }

    public void setNum(int num) {
        this.number = num;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Invoice{" + "num=" + number + ", date=" + date + ", customer=" + customer + '}';
    }
    
    public String getAsCSV() {
        return number + "," + date + "," + customer;
    }

    public int getIdNum() {
        return number;
    }

    public String getInvoiceDate() {
      return date;
    }

    public String getCustomerName() {
        return customer;
    }

   
}
