package com.ldceconnect.ldcecommunity.model;

import java.util.Date;

/**
 * Created by Nevil on 12/1/2015.
 */
public class Post {
    public String id;
    public String text;
    public String threadid;
    public String postowner;
    public String postownername;
    public Date idate;
    public String iby;
    public Date udate;
    public String uby;
    public User user;
}