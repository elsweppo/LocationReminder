package Models;


import android.location.*;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject{

    @PrimaryKey String taskDetail;
    private long timestamp;
    private RealmList<TaskLocation> locations;

    private boolean done;
    private int radius;

    public RealmList<TaskLocation> getLocations() {
        return locations;
    }

    public void setLocations(RealmList<TaskLocation> locations) {
        this.locations = locations;
    }







    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


    public String getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(String taskDetail) {
        this.taskDetail = taskDetail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


}