/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.gui;

/**
 *
 * @author ArcherL
 */
public class User {

    private int userId = 0;
    private String userName = null;
    private float amountSpent = 0;
    private String mobileNumber = null;
    private int groupId = 0;
    //private float finalDebt=0;

    User(int userId, String userName, int groupId, String mobileNumber, float amountSpent) {
        this.userId = userId;
        this.userName = userName;
        this.groupId = groupId;
        this.mobileNumber = mobileNumber;
        this.amountSpent = amountSpent;
        //this.finalDebt=finalDept;
    }

    User(String userName) {
        this.userName = userName;
    }

    User(float amountSpent) {
        this.amountSpent = amountSpent;
    }

    User(int userId,String userName, float amountSpent) {
        this.userId = userId;
        this.userName = userName;
        this.amountSpent = amountSpent;
    }


    //int RowCount=0;
    //int GhostVariable=0;
    public String getName() {
        return userName;
    }

    public int getId() {
        return userId;
    }

    public float getAmountSeeker() {
        return amountSpent;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public int getGroupId() {
        return groupId;
    }
}
