package com.noveogroup.evgeny.awersomeproject.db.api;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealTimeDBApi {

    public static final String USERS_NODE = "users";
    public static final String TASKS_NODE = "tasks";

    private static FirebaseDatabase database;

    public static RealTimeDBApi getInstance(){
        database = FirebaseDatabase.getInstance();
        return new RealTimeDBApi();
    }


}
