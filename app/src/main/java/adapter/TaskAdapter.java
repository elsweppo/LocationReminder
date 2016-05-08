package adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import Models.Task;
import io.realm.Realm;
import io.realm.exceptions.RealmException;
import pervasivecomputing.locationreminder.R;

/**
 * Created by julianschweppe on 07.05.16.
 */
public class TaskAdapter extends ArrayAdapter<Task> {

    ArrayList<Task> taskList;
    Context context;
    double latitude, longitude;
    Realm realm;
    deleteListener listener;

    public interface deleteListener{
        void onDelete(String identifier);
    }



    public TaskAdapter(Context context, ArrayList<Task> objects, double latitude, double longitude, Realm realm, deleteListener listener) {
        super(context, -1, objects);
        this.taskList = objects;
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.realm = realm;
        this.listener = listener;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.cell_task, parent, false);
        TextView taskName = (TextView) v.findViewById(R.id.tasks_name);
        TextView taskDistance = (TextView) v.findViewById(R.id.tasks_distance);
        TextView taskDate = (TextView) v.findViewById(R.id.tasks_date);
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.tasks_checkbox);

        ImageButton deleteBtn = (ImageButton) v.findViewById(R.id.tasks_delete);
        final Task task = taskList.get(position);
        taskName.setText(task.getTaskDetail());
        if (task.getLatitude() != 0 && task.getLongitude() != 0){
            float[] distResults =  new float[2];
            Location.distanceBetween(latitude, longitude, task.getLatitude(), task.getLongitude(), distResults);
            taskDistance.setVisibility(View.VISIBLE);
            taskDistance.setText(distResults[0] + "m");
        }
        else{
            taskDistance.setVisibility(View.GONE);
        }
        if (task.getTimestamp() != 0){
            taskDate.setVisibility(View.VISIBLE);
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTimeInMillis(task.getTimestamp());
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
            taskDate.setText(format.format(calendar.getTime()));
        }else{
            taskDate.setVisibility(View.GONE);
        }

        checkBox.setSelected(task.isDone());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try{
                    realm.beginTransaction();
                    task.setDone(b);
                    realm.commitTransaction();
                }catch (RealmException re){
                    realm.cancelTransaction();
                }

            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(task.getTaskDetail());
            }
        });

        return v;
    }



}
