package com.ldceconnect.ldcecommunity.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nevil on 1/7/2016.
 */
public class GroupCategory {

    public String title;
    public String id;
    public Date idate;
    public String iby;
    public Date udate;
    public String uby;

    @Override
    public String toString(){
        return title;
    }
}
