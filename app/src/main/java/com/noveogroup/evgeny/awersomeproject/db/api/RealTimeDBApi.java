package com.noveogroup.evgeny.awersomeproject.db.api;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.DateTransformerUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RealTimeDBApi {

    private static final String USERS_NODE = "users";
    private static final String TASKS_NODE = "tasks";

    private static final String REAL_TIME_DB_LOG_TAG = "realtimeDB";

    private static FirebaseDatabase database;
    private static DatabaseReference usersRef;
    private static DatabaseReference tasksRef;
    private static List<Task> tasksDataset;

    public static RealTimeDBApi getInstance() {
        database = FirebaseDatabase.getInstance();
        if (tasksDataset == null) {
            tasksDataset = new ArrayList<>();
        }
        usersRef = database.getReference(USERS_NODE);
        tasksRef = database.getReference(TASKS_NODE);
        return new RealTimeDBApi();
    }

    public List<Task> getAllTasks() {
        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasksDataset.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    tasksDataset.add(child.getValue(Task.class));
                }
                System.out.print("asd");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tasksDataset = null;
            }
        });
        return tasksDataset;
    }

    public void writeTask(String name,
                          List<String> tags,
                          String imageUrl,
                          LatLng coords,
                          float rating,
                          int authorId,
                          String authorName,
                          Date date) {
        Task newTask = new Task();
        newTask.setAuthorId(authorId);
        newTask.setAuthorName(authorName);
        newTask.setDate(DateTransformerUtil.getDateAsString(date));
        newTask.setImageUrl(imageUrl);
        newTask.setLat(coords.latitude);
        newTask.setLng(coords.longitude);
        newTask.setRating(rating);
        newTask.setTags(tags);
        newTask.setName(name);

        DatabaseReference newTaskRef = tasksRef.push();
        newTaskRef.setValue(newTask);
    }
}
