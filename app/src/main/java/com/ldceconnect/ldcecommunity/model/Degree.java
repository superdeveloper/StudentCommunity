package com.ldceconnect.ldcecommunity.model;

import java.util.Date;

/**
 * Created by Nevil on 12/22/2015.
 */
public class Degree {
    public String id;
    public String title;
    public Date idate ;

    @Override
    public String toString(){
        return title;
    }

}
