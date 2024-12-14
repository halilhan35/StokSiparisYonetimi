package org.example.classes;

public class Log {

    private int logID;
    private int customerID;
    private int orderID;
    private String logDate;
    private String logType;
    private String logDetails;

    public Log(){}

    public Log(int logID, int customerID, int orderID, String logDate, String logType, String logDetails){
        this.logID = logID;
        this.customerID = customerID;
        this.orderID = orderID;
        this.logDate = logDate;
        this.logType = logType;
        this.logDetails = logDetails;
    }

    public int getLogID() {
        return this.logID;
    }

    public int getCustomerID() {
        return this.customerID;
    }

    public int getOrderID() {
        return this.orderID;
    }

    public String getLogDate() {
        return this.logDate;
    }

    public String getLogType() {
        return this.logType;
    }

    public String getLogDetails() {
        return this.logDetails;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public void setLogDetails(String logDetails) {
        this.logDetails = logDetails;
    }

}
