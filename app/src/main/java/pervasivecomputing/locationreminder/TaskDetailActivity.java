package pervasivecomputing.locationreminder;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import Models.Task;
import Models.TaskLocation;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

/**
 * Created by Marko Jeftic on 15.04.16.
 */
public class TaskDetailActivity extends Activity {
    private Button storeBtn;
    private Button addLocationBtn;
    private Button addDateBtn;
    private Button addTimeBtn;

    private LinearLayout locationLayout;

    private ArrayList<Location> locations;


    private EditText taskDetailsEdit;
    private TextView timeTv;
    private TextView dateTv;

    private SeekBar seekbar;
    private TextView result;
    private int progress=0;
    public int year, month, day, hourOfDay , minute = 0;


    int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        locations = new ArrayList<>();
        storeBtn = (Button) findViewById(R.id.storeBtn);
        addLocationBtn = (Button) findViewById(R.id.addLocationBtn);
        addDateBtn = (Button) findViewById(R.id.addDateBtn);
        addTimeBtn = (Button) findViewById(R.id.addTimeBtn);
        result = (TextView) findViewById(R.id.textView2);
        taskDetailsEdit = (EditText) findViewById(R.id.editText);
        dateTv = (TextView) findViewById(R.id.dateTv);
        timeTv = (TextView) findViewById(R.id.timeTv);
        locationLayout = (LinearLayout) findViewById(R.id.locationLl);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        addDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlacePicker();
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                result.setText(progress + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        storeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                storeTask();
            }
        });
    }

    public void changeDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
        dateTv.setText(day+ "." + month + "." + year);
    }


    public void changeTime(int hourOfDay, int minute){
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        timeTv.setText(hourOfDay + ":" + minute);
    }

    public void showTimePicker(){
        DialogFragment newFragment = new TimepickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public void showDatePicker(){
        DialogFragment newFragment = new DatepickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }


    public void storeTask(){
        if (taskDetailsEdit.getText().toString().matches("")) {
            Toast.makeText(TaskDetailActivity.this, "You did not enter a task", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //DatabaseHelper dbHelper = new DatabaseHelper(TaskDetailActivity.this);
            if ((day == 0) && (locations.size() == 0)){
                Toast.makeText(TaskDetailActivity.this, "You must either provide a location or a date for the task", Toast.LENGTH_SHORT).show();
                return;
            }
            String taskValue = taskDetailsEdit.getText().toString();
            int radiusValue = progress;
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
// Get a Realm instance for this thread
            Realm realm = Realm.getInstance(realmConfig);
            realm.beginTransaction();
            Task task = realm.createObject(Task.class);
            task.setTaskDetail(taskValue);
            task.setRadius(radiusValue);

            if (day != 0 && month != 0 && year != 0){
                GregorianCalendar calendar = new GregorianCalendar(year, month, day, hourOfDay, minute);
                long timestamp = calendar.getTimeInMillis();
                task.setTimestamp(timestamp);
            }
            if (locations.size() > 0){
                RealmList<TaskLocation> taskLocations = new RealmList<TaskLocation>();
                for (int i = 0;i < locations.size(); i++){
                    TaskLocation taskLoc = realm.createObject(TaskLocation.class);
                    taskLoc.setId(Double.toString(System.currentTimeMillis()));
                    taskLoc.setLongitude(locations.get(i).getLongitude());
                    taskLoc.setLatitude(locations.get(i).getLatitude());
                    realm.copyToRealmOrUpdate(taskLoc);
                    taskLocations.add(taskLoc);
                }
                task.setLocations(taskLocations);
            }

            task.setDone(false);
            realm.commitTransaction();
//            dbHelper.createTask(taskValue, longitude, latitude, radiusValue, currentLocation, timestamp);

            Intent intent = new Intent(TaskDetailActivity.this, TaskActivity.class);
            startActivity(intent);
        }
    }

    public void startPlacePicker(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Location loc = new Location("dummyprovider");
                loc.setLatitude(place.getLatLng().latitude);
                loc.setLongitude(place.getLatLng().longitude);
                locations.add(loc);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView tv=new TextView(this);
                tv.setLayoutParams(lparams);
                tv.setText(place.getName().toString());
                locationLayout.addView(tv);
            }
        }
    }







}
