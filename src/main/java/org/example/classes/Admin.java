package org.example.classes;

public class Admin {

    private int adminID;
    private String adminName;
    private String adminPassword;

    public Admin(){}

    public Admin(int adminID, String adminName, String adminPassword){
        this.adminID = adminID;
        this.adminName = adminName;
        this.adminPassword = adminPassword;
    }

    public int getAdminID() {
        return this.adminID;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

}
