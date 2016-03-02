package com.ldceconnect.ldcecommunity.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nevil on 12/15/2015.
 */
public class Discussion {
    public String id;
    public String title;
    public String description;
    public String owner;
    public String parentgroup;
    public String parentgroupname;
    public Date idate;
    public String numposts;
    public String numstars;
    public String hashtags;
    public String ispublic;
    public String iby;
    public Date udate;
    public String uby;
    @Override
    public String toString(){
        return title;
    }
}
