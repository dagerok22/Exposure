package com.noveogroup.evgeny.awersomeproject.db.model;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    private String name;
    private List<String> usersWhoDone;
    private List<String> tags;
    private String imageUrl;
    private double lat;
    private double lng;
    private float rating;
    private String authorName;
    private String taskId;
    private String  authorId;
    private String date;

    public Task() {
    }

    public List<String> getUsersWhoDone() {
        return usersWhoDone;
    }

    public void setUsersWhoDone(List<String> usersWhoDone) {
        this.usersWhoDone = usersWhoDone;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String  getAuthorId() {
        return authorId;
    }

    public boolean isUserDone(String user){
        if (usersWhoDone == null) {
            return false;
        } else if (!usersWhoDone.contains(user)) {
            return false;
        }
        return true;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
