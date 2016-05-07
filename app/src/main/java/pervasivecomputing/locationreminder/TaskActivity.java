package pervasivecomputing.locationreminder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import Models.Task;
import adapter.TaskAdapter;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by Marko Jeftic on 15.04.16.
 */
public class TaskActivity extends Activity implements TaskAdapter.deleteListener {
    private DatabaseHelper mydb;
    // GPSTracker class
    GPSTracker gps;

    ListView listView;
    double curLatitude, curLongitude;
    TaskAdapter adapter;
    Realm realm;
    RealmResults<Task> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        listView = (ListView) findViewById(R.id.list);
        gps = new GPSTracker(TaskActivity.this);

        if(gps.canGetLocation()){
            curLatitude = gps.getLatitude();
            curLongitude = gps.getLongitude();}else{
            gps.showSettingsAlert();
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        realm = Realm.getInstance(realmConfig);
        mResults  = realm.where(Task.class).findAll();
        ArrayList<Task> taskList = new ArrayList<>();
        for (Task t : mResults){
            taskList.add(t);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
                startActivity(intent);
            }

        });
        adapter = new TaskAdapter(this,taskList, curLatitude, curLongitude, realm, this);
        listView.setAdapter(adapter);

    }
    @Override
    protected void onResume(){
        super.onResume();
        for (Task t : mResults){
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            Log.d("julian", "systime" + System.currentTimeMillis());
            Log.d("julian", "caltime" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE));
            Calendar calendar2 = GregorianCalendar.getInstance();
            calendar2.setTimeInMillis(t.getTimestamp());
            Log.d("julian", "task time" + calendar2.get(Calendar.HOUR_OF_DAY) + calendar2.get(Calendar.MINUTE));
            if (t.getLongitude() != 0 && t.getLatitude()!= 0){
                float[] dist = new float[2];
                Location.distanceBetween(t.getLatitude(), t.getLongitude(), curLatitude, curLongitude, dist);
                if (t.getRadius() >= dist[0]){
                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    Toast.makeText(TaskActivity.this, "The task " + t.getTaskDetail() + " is due", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (t.getTimestamp() != 0){
                Log.d("julian", "time not null");
                if ((System.currentTimeMillis() + 60000 > t.getTimestamp()) && (System.currentTimeMillis() - 60000 < t.getTimestamp())){
                    Log.d("julian", "vibrate");
                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    Toast.makeText(TaskActivity.this, "The task " + t.getTaskDetail() + " is due", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    @Override
    public void onDelete(final String identifier) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_task_msg);
        builder.setPositiveButton(R.string.delete_task_positive, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Task task = realm.where(Task.class).equalTo("taskDetail", identifier).findFirst();
                adapter.remove(task);
                try{
                    realm.beginTransaction();
                    task.removeFromRealm();
                    realm.commitTransaction();
                }catch (RealmException re){
                    re.printStackTrace();
                    realm.cancelTransaction();
                }
            }
        });
        builder.setNegativeButton(R.string.delete_task_cancel, null);

        Dialog dialog = builder.create();
        dialog.show();




    }
}
