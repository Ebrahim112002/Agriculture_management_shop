/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agriculture_management_shop;

import java.sql.Date;

/**
 *
 * @author WINDOWS 10
 */
public class customersData {

    private Integer id;
    private Integer customerID;
    private Double total;
    private Date date;
    private String emUsername;

    public customersData(int id, int customerID, double total, Date date, String emUsername, String string1, String string2) {
        this.id = id;
        this.customerID = customerID;
        this.total = total;
        this.date = date;
        this.emUsername = emUsername;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public Double getTotal() {
        return total;
    }

    public Date getDate() {
        return date;
    }

    public String getEmUsername() {
        return emUsername;
    }

}
