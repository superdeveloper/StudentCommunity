package com.ldceconnect.ldcecommunity.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nevil on 12/14/2015.
 */
public class Department {
    public String name;
    public String descritpion;
    public String id;
    public ArrayList<Integer> students;
    public String email;
    public String nummembers;
    public String contact;
    public String address;
    public Date idate;
    public String iby;
    public Date udate;
    public String uby;
    public String deptimageurl;

    @Override
    public String toString(){
        return name;
    }
}
