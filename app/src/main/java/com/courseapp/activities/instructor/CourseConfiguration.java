package com.courseapp.activities.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.courseapp.R;
import com.courseapp.models.ClassTiming;
import com.courseapp.models.CourseConfig;
import com.courseapp.models.Weekday;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.courseapp.AppCredentials.COURSE_CODE;

public class CourseConfiguration extends AppCompatActivity {

    private DatabaseReference mRef;
    private ProgressDialog mDialog;
    private TextView mondaySlotView;
    private TextView tuesdaySlotView;
    private TextView wednesdaySlotView;
    private TextView thursdaySlotView;
    private TextView fridaySlotView;
    private TextView[] slotViews;
    private ImageButton mondaySlotEdit;
    private ImageButton tuesdaySlotEdit;
    private ImageButton wednesdaySlotEdit;
    private ImageButton thursdaySlotEdit;
    private ImageButton fridaySlotEdit;
    private ImageButton[] slotEdits;
    private EditText courseDescription;
    private EditText courseCapacity;
    private CourseConfig courseConfig;
    private Weekday[] weekdays = new Weekday[]{Weekday.MON, Weekday.TUE, Weekday.WED, Weekday.THU, Weekday.FRI};
    private TextView courseName;
    private TextView courseCode;
    private TextView courseInstructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_configuration);

        mDialog = new ProgressDialog(CourseConfiguration.this);
        mDialog.setTitle("Collecting information");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);

        mondaySlotView = (TextView)findViewById(R.id.slot_monday);
        tuesdaySlotView = (TextView)findViewById(R.id.slot_tuesday);
        wednesdaySlotView = (TextView)findViewById(R.id.slot_wednesday);
        thursdaySlotView = (TextView)findViewById(R.id.slot_thursday);
        fridaySlotView = (TextView)findViewById(R.id.slot_friday);
        slotViews = new TextView[] {mondaySlotView, tuesdaySlotView, wednesdaySlotView, thursdaySlotView, fridaySlotView};
        
        mondaySlotEdit = (ImageButton)findViewById(R.id.edit_monday);
        tuesdaySlotEdit = (ImageButton)findViewById(R.id.edit_tuesday);
        wednesdaySlotEdit = (ImageButton)findViewById(R.id.edit_wednesday);
        thursdaySlotEdit = (ImageButton)findViewById(R.id.edit_thursday);
        fridaySlotEdit = (ImageButton)findViewById(R.id.edit_friday);
        slotEdits = new ImageButton[]{mondaySlotEdit, tuesdaySlotEdit, wednesdaySlotEdit, thursdaySlotEdit, fridaySlotEdit};
        
        courseDescription = (EditText)findViewById(R.id.course_description);
        courseCapacity = (EditText)findViewById(R.id.course_capacity);

        courseName = (TextView)findViewById(R.id.course_name);
        courseCode = (TextView)findViewById(R.id.course_code);
        courseInstructor = (TextView)findViewById(R.id.course_instructor);

        Intent intent = getIntent();
        String id = intent.getStringExtra(COURSE_CODE);
        mRef = FirebaseDatabase.getInstance().getReference().child("courses").child(id);
        
        mDialog.show();
        getCourseConfig();
        setEditors();
    }

    private void updateScheduleView(){
        for (int i = 0; i < 5; i++){
            ClassTiming timing = ClassTiming.valueOf(courseConfig.getClassTimingOn(weekdays[i]));
            String time;
            switch (timing){
                case SLOT_1PM_TO_2PM:
                    time = "1 PM to 2 PM ";
                    break;

                case SLOT_2PM_TO_3PM:
                    time = "2 PM to 3 PM ";
                    break;

                case SLOT_3PM_TO_4PM:
                    time = "3 PM to 4 PM ";
                    break;

                case SLOT_4PM_TO_5PM:
                    time = "4 PM to 5 PM ";
                    break;

                case SLOT_9AM_TO_10AM:
                    time = "9 AM to 10 AM ";
                    break;

                case SLOT_12PM_TO_1PM:
                    time = "12 PM to 1 PM ";
                    break;

                case SLOT_10AM_TO_11AM:
                    time = "10 AM to 11 AM ";
                    break;

                case SLOT_11AM_TO_12PM:
                    time = "11 AM to 12 PM ";
                    break;

                case OFF:
                    time = "OFF";
                    break;

                default:
                    time="OFF";
                    break;
            }
            slotViews[i].setText(time);
        }
    }

    private void setEditors() {
        for (int i = 0; i < 5; i++){
            int finalI = i;
            slotEdits[i].setOnClickListener(v -> showSelectDialog(weekdays[finalI]));
        }
    }

    private void showSelectDialog(Weekday weekday){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseConfiguration.this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.slot_select_dialog, null);
        builder.setView(DialogLayout);

        Button slotF = (Button) DialogLayout.findViewById(R.id.slotf);
        Button slot0 = (Button) DialogLayout.findViewById(R.id.slot0);
        Button slot1 = (Button) DialogLayout.findViewById(R.id.slot1);
        Button slot2 = (Button) DialogLayout.findViewById(R.id.slot2);
        Button slot3 = (Button) DialogLayout.findViewById(R.id.slot3);
        Button slot4 = (Button) DialogLayout.findViewById(R.id.slot4);
        Button slot5 = (Button) DialogLayout.findViewById(R.id.slot5);
        Button slot6 = (Button) DialogLayout.findViewById(R.id.slot6);
        Button slot7 = (Button) DialogLayout.findViewById(R.id.slot7);

        final android.app.AlertDialog select_dialog = builder.create();

        View.OnClickListener listener = v -> {
            int id = v.getId();
            switch (id){
                case R.id.slotf:
                    courseConfig.updateSchedule(weekday, ClassTiming.OFF);
                    break;

                case R.id.slot0:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_9AM_TO_10AM);
                    break;

                case R.id.slot1:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_10AM_TO_11AM);
                    break;

                case R.id.slot2:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_11AM_TO_12PM);
                    break;

                case R.id.slot3:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_12PM_TO_1PM);
                    break;

                case R.id.slot4:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_1PM_TO_2PM);
                    break;

                case R.id.slot5:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_2PM_TO_3PM);
                    break;

                case R.id.slot6:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_3PM_TO_4PM);
                    break;

                case R.id.slot7:
                    courseConfig.updateSchedule(weekday, ClassTiming.SLOT_4PM_TO_5PM);
                    break;
            }
            updateScheduleView();
            select_dialog.dismiss();
        };

        slotF.setOnClickListener(listener);
        slot0.setOnClickListener(listener);
        slot1.setOnClickListener(listener);
        slot2.setOnClickListener(listener);
        slot3.setOnClickListener(listener);
        slot4.setOnClickListener(listener);
        slot5.setOnClickListener(listener);
        slot6.setOnClickListener(listener);
        slot7.setOnClickListener(listener);

        select_dialog.show();
    }

    private void updateViews() {
        courseDescription.setText(courseConfig.getDescription());
        courseCapacity.setText(courseConfig.getCapacity() + "");
        updateScheduleView();
        mDialog.dismiss();
    }

    private void getCourseConfig() {
        courseConfig = new CourseConfig();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null) {
                    if (snapshot.child("name").getValue() != null) {
                        courseName.setText(snapshot.child("name").getValue().toString());
                    }
                    if (snapshot.child("code").getValue() != null) {
                        courseCode.setText(snapshot.child("code").getValue().toString());
                    }
                    if (snapshot.child("instructor").getValue() != null) {
                        courseInstructor.setText(snapshot.child("instructor").getValue().toString());
                    }
                    if (snapshot.child("config").getValue() != null) {
                        if (snapshot.child("config").child("description").getValue() != null) {
                            courseConfig.setDescription(snapshot.child("config").child("description").getValue().toString());
                        }
                        if (snapshot.child("config").child("capacity").getValue() != null) {
                            courseConfig.setCapacity((int) (long) snapshot.child("config").child("capacity").getValue());
                        }
                        if (snapshot.child("config").child("schedule").getValue() != null) {
                            HashMap<String, String> map = (HashMap<String, String>) snapshot.child("config").child("schedule").getValue();
                            courseConfig.setSchedule(map);
                        }
                        updateViews();
                    } else {
                        mDialog.dismiss();
                    }
                }else {
                    Toast.makeText(CourseConfiguration.this, "Error in loading data!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveConfigurations(View view){
        String description = courseDescription.getText().toString().trim();
        String capacity = courseCapacity.getText().toString().trim();
        try {
            int cap = Integer.parseInt(capacity);
            if (cap > 100){
                Toast.makeText(this, "Maximum allowed capacity is 100", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (cap < 1){
                Toast.makeText(this, "Minimum allowed capacity is 1", Toast.LENGTH_SHORT).show();
                return;
            }
            courseConfig.setCapacity(cap);
        }catch (Exception e){
            Toast.makeText(this, "Enter valid integer course capacity", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!description.isEmpty()) courseConfig.setDescription(description);

        CourseConfig.setCourseConfiguration(courseConfig, mRef);
        finish();
    }

}