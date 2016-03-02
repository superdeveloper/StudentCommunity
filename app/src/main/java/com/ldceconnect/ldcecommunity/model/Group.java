package com.ldceconnect.ldcecommunity.model;

        import java.util.ArrayList;
        import java.util.Date;

/**
 * Created by Nevil on 12/15/2015.
 */
public class Group {
    public String name;
    public String descritpion;
    public String id;
    public String admin;
    public ArrayList<Integer> members;
    public Date idate;
    public String iby;
    public Date udate;
    public String uby;
    public String groupimageurl;
    public String numthreads;
    public String nummembers;
    public String category;
    public String adminname;
    public boolean doUpdate;
    public boolean groupImageUpdated;
    public int listIndex;
    @Override
    public String toString(){
        return name;
    }
}
