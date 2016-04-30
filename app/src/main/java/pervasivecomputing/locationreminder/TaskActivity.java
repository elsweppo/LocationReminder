package pervasivecomputing.locationreminder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import Models.Task;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Marko Jeftic on 15.04.16.
 */
public class TaskActivity extends Activity {
    private DatabaseHelper mydb;
    // GPSTracker class
    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // create class object
        gps = new GPSTracker(TaskActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
// Get a Realm instance for this thread
        Realm realm = Realm.getInstance(realmConfig);


        RealmResults<Task> results  = realm.where(Task.class).findAll();

        for (Task t : results){
            Toast.makeText(getApplicationContext(),t.getTaskDetail(), Toast.LENGTH_LONG).show();
        }

//        mydb = new DatabaseHelper(this);
//        try{
//            Cursor rs = mydb.getData(0);
//            rs.moveToFirst();
//            Toast.makeText(getApplicationContext(),rs.getString(rs.getColumnIndex("task")), Toast.LENGTH_LONG).show();
//        }catch(Exception e){
//            e.printStackTrace();
//            Log.d("julian", e.getMessage());
//        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
                startActivity(intent);
            }

        });



    }

}
