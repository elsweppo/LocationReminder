package pervasivecomputing.locationreminder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by julianschweppe on 15.04.16.
 */
public class TaskDetailActivity extends Activity {
    private Button storeBtn;
    private EditText taskDetailsEdit;
    private EditText locationEdit;
    private EditText dateEdit;
    private SeekBar seekbar;
    private TimePicker timepicker;
    private TextView result;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        storeBtn = (Button) findViewById(R.id.storeBtn);
        result = (TextView) findViewById(R.id.textView2);
        taskDetailsEdit = (EditText) findViewById(R.id.editText);
        locationEdit = (EditText) findViewById(R.id.editText2);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        timepicker = (TimePicker) findViewById(R.id.time_picker);
        timepicker.setIs24HourView(true);
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




        storeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (taskDetailsEdit.getText().toString().matches("")) {
                    Toast.makeText(TaskDetailActivity.this, "You did not enter a task", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }


}
