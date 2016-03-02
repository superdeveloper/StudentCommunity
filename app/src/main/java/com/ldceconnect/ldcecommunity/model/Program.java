package com.ldceconnect.ldcecommunity.model;

import java.util.Date;

/**
 * Created by Nevil on 12/22/2015.
 */
public class Program {
    public String id;
    public String name;
    public String info;
    public Date idate;

    @Override
    public String toString(){
        return name;
    }
}
