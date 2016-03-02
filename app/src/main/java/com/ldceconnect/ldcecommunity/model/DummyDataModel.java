package com.ldceconnect.ldcecommunity.model;

/**
 * Created by Suleiman on 14-04-2015.
 */
public class DummyDataModel {
    public String name;

    public static final String[] departmentData = {"Computer Science", "Mechanical Engineering", "Electrical Engineering",
            "Environmental Engineering", "Textile Engineering", "Automobile Engineering",
            "Biotechnology", "Electronics & Communications", "Information Technology", "Applied Mechanics"};

    public static final String[] discussionData = {"Help needed for social cause.", "Teqnix 2016 discussion", "Clean LD",
            "Has anybody worked on adaptive algorithms ?", "Final BE Computer project suggestions required.", "Jobs Discussions",
            "Job postings 2016", "Admin help", "", ""};

    public static final String[] groupData = {"Campus help group", "Wake up LDites", "Android lovers group",
            "Cultural Festival 2016", "Jobs & Trainings group", "Social Discussions",
            "BE EC Project 2016 ", "Environmentalists", "Teatime wisewords", "Alumni 2016"};

    public static final String[] studentData = {"Joel Christian","Tanvi Patel", "Asif Muhammad", "Aravind Shah", "Nignesh Patel", "Jagadish Prajapati",
            "Bhavesh Prajapati", "Tanmay Vyas", "Gandhi Alay", "Rushabh Parikh"};

    public static final String[] samplePostMessage = {
            "This is a Sample message 1.\\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 2.\\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 3.\\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 4.\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 5.\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 6.\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 7.\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 8.\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 9.\nThis is a Sample message. \nThis is a Sample message.",
            "This is a Sample message 10.\nThis is a Sample message. \nThis is a Sample message.",
    };

    public static final String samplePostMessageLong = "This is a Sample long message 1.\nThis is a Sample long message. \nThis is a Sample long message. \nThis is a Sample long message.\nThis is a Sample long message.";

    DummyDataModel(String name){
        this.name=name;
    }
}

