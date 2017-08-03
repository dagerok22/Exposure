package com.noveogroup.evgeny.awersomeproject.db.api;


import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.db.model.User;
import com.noveogroup.evgeny.awersomeproject.util.DateTransformerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RealTimeDBApi {

    private static final String USERS_NODE = "users";
    private static final String TASKS_NODE = "tasks";
    private static final String IMAGES_NODE = "images";

    private static final String REAL_TIME_DB_LOG_TAG = "realtimeDB";

    private static FirebaseDatabase database;
    private static FirebaseStorage storage;
    private static DatabaseReference usersRef;
    private static DatabaseReference tasksRef;
    private static List<Task> tasksDataset;

    public static RealTimeDBApi getInstance() {
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        if (tasksDataset == null) {
            tasksDataset = new ArrayList<>();
        }
        usersRef = database.getReference(USERS_NODE);
        tasksRef = database.getReference(TASKS_NODE);
        return new RealTimeDBApi();
    }

    public void getAllTasks(OnTestResultCallBack callback) {
        final List<Task> tasksData = new ArrayList<>();
        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    tasksData.add(child.getValue(Task.class));
                }
                callback.onDataReceived(tasksData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void writeTask(String name,
                          List<String> tags,
                          String imageUrl,
                          LatLng coords,
                          float rating,
                          String authorId,
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
        newTask.setTaskId(newTaskRef.getKey());
        newTaskRef.setValue(newTask);
    }

    public void getAllUsers(OnUserResultCallBack callBack) {
        final List<User> userData = new ArrayList<>();
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    userData.add(child.getValue(User.class));
                }
                callBack.onDataReceived(userData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getUserById(String id, OnUserByIdResultCallBack callBack) {
        usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callBack.onDataReceived(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getTaskById(String id, OnTaskByIdResultCallBack callBack) {
        tasksRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task value = dataSnapshot.getValue(Task.class);
                callBack.onDataReceived(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void writeUser(String id,
                          String name,
                          int rating,
                          Date dateOfReg) {
        User newUser = new User();
        newUser.setName(name);
        newUser.setId(id);
        newUser.setRating(rating);
        newUser.setDateOfRegistration(DateTransformerUtil.getDateAsString(dateOfReg));

        DatabaseReference newUserRef = usersRef.child(id);
        newUserRef.setValue(newUser);
        newUserRef.child("name").setValue(name);
    }

    public void updateTask(Task task) {
        tasksRef.child(task.getTaskId()).setValue(task);
    }

    public void addRatingToUser(String id, float ratingToAdd) {
        usersRef.child(id).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersRef.child(id).child("rating").setValue(dataSnapshot.getValue(Integer.class) + ratingToAdd);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void writeImageAndGetUrl(File image, HandleImageFileCallback handleUri) {
        StorageReference storageRef = storage.getReference(IMAGES_NODE);
        StorageReference imagesRef = storageRef.child(image.getName());

        Uri file = Uri.fromFile(image);
        UploadTask uploadTask = imagesRef.putFile(file);
        uploadTask.addOnFailureListener(handleUri::onFailure)
                .addOnSuccessListener(taskSnapshot -> handleUri.onSuccess(taskSnapshot.getDownloadUrl()));
    }

    public void writeImageAndGetUrl(Uri image, HandleImageUriCallback handleUri) {
        StorageReference storageRef = storage.getReference(IMAGES_NODE);
        StorageReference imagesRef = storageRef.child(image.getLastPathSegment());

        UploadTask uploadTask = imagesRef.putFile(image);
        uploadTask.addOnFailureListener(handleUri::onFailure)
                .addOnSuccessListener(taskSnapshot -> handleUri.onSuccess(taskSnapshot.getDownloadUrl()));
    }

    public interface OnTestResultCallBack {
        void onDataReceived(List<Task> data);
    }

    public interface OnUserResultCallBack {
        void onDataReceived(List<User> data);
    }

    public interface OnUserByIdResultCallBack {
        void onDataReceived(User data);
    }

    public interface OnTaskByIdResultCallBack {
        void onDataReceived(Task data);
    }

    public interface HandleImageUriCallback {
        void onSuccess(Uri imageRef);

        void onFailure(Exception e);
    }

    public interface HandleImageFileCallback {
        void onSuccess(Uri imageRef);

        void onFailure(Exception e);
    }
}
