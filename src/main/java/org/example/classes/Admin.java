package org.example.classes;

public class Admin {

    private int adminID;
    private String adminName;

    public Admin(){}

    public Admin(int adminID, String adminName){
        this.adminID = adminID;
        this.adminName = adminName;
    }

    public int getAdminID() {
        return this.adminID;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

}
