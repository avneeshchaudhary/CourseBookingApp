package com.courseapp.activities.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.courseapp.R;
import com.courseapp.models.ClassTiming;
import com.courseapp.models.CourseConfig;
import com.courseapp.models.Role;
import com.courseapp.models.StudentSchedule;
import com.courseapp.models.Weekday;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.BooleanNode;
import com.google.firebase.database.snapshot.StringNode;

import java.util.HashMap;

import static com.courseapp.AppCredentials.APP_DB;
import static com.courseapp.AppCredentials.COURSE_CODE;
import static com.courseapp.AppCredentials.USERNAME;

public class ViewStudentCourseDetail extends AppCompatActivity {

    private DatabaseReference mRef;
    private ProgressDialog mDialog;
    private TextView[] slotViews;
    private TextView courseDescription;
    private TextView courseCapacity;
    private CourseConfig courseConfig;
    private Weekday[] weekdays = new Weekday[]{Weekday.MON, Weekday.TUE, Weekday.WED, Weekday.THU, Weekday.FRI};
    private TextView courseName;
    private TextView courseCode;
    private String courseId;
    private String studentName;
    private StudentSchedule studentSchedule;
    private TextView courseInstructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_course_detail);

        mDialog = new ProgressDialog(ViewStudentCourseDetail.this);
        mDialog.setTitle("Collecting information");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);

        TextView mondaySlotView = (TextView) findViewById(R.id.slot_monday);
        TextView tuesdaySlotView = (TextView) findViewById(R.id.slot_tuesday);
        TextView wednesdaySlotView = (TextView) findViewById(R.id.slot_wednesday);
        TextView thursdaySlotView = (TextView) findViewById(R.id.slot_thursday);
        TextView fridaySlotView = (TextView) findViewById(R.id.slot_friday);
        slotViews = new TextView[] {mondaySlotView, tuesdaySlotView, wednesdaySlotView, thursdaySlotView, fridaySlotView};
        studentSchedule = new StudentSchedule();

        SharedPreferences preferences = getSharedPreferences(APP_DB, MODE_PRIVATE);
        studentName = preferences.getString(USERNAME, "admin");

        courseDescription = (TextView)findViewById(R.id.course_description);
        courseCapacity = (TextView) findViewById(R.id.course_capacity);

        courseName = (TextView)findViewById(R.id.course_name);
        courseCode = (TextView)findViewById(R.id.course_code);
        courseInstructor = (TextView)findViewById(R.id.course_instructor);

        Intent intent = getIntent();
        courseId = intent.getStringExtra(COURSE_CODE);
        mRef = FirebaseDatabase.getInstance().getReference().child("courses").child(courseId);

        mDialog.show();
        getCourseConfig();
    }

    private void loadStudentSchedule() {
        FirebaseDatabase.getInstance().getReference().child("users").child(Role.STUDENT.name()).child(studentName).child("schedule").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    studentSchedule = snapshot.getValue(StudentSchedule.class);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("users").child(Role.STUDENT.name()).child(studentName).child("schedule").setValue(studentSchedule);
                }
                checkEnrollment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void updateViews() {
        courseDescription.setText(courseConfig.getDescription());
        courseCapacity.setText(courseConfig.getCapacity() + "");
        updateScheduleView();
        mDialog.dismiss();
    }

    private View.OnClickListener enrollCourse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDialog.setTitle("Checking eligibility");
            mDialog.show();
            boolean canEnroll = true;

            for (Weekday weekday : Weekday.values()){
                ClassTiming timing = ClassTiming.valueOf(courseConfig.getClassTimingOn(weekday));
                if (timing.equals(ClassTiming.OFF)) continue;
                if (!studentSchedule.isStudentAvailable(weekday, timing)){
                    canEnroll = false;
                    break;
                }
            }

            if (canEnroll){
                quickEnroll();
            }
            else {
                mDialog.dismiss();
                Toast.makeText(ViewStudentCourseDetail.this, "You cannot enroll in this course as it does not fit into your schedule", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void quickEnroll(){
        for (Weekday weekday : Weekday.values()){
            ClassTiming timing = ClassTiming.valueOf(courseConfig.getClassTimingOn(weekday));
            if (!timing.equals(ClassTiming.OFF)){
                studentSchedule.updateAddSchedule(weekday, timing);
            }
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(Role.STUDENT.name()).child(studentName).child("schedule").setValue(studentSchedule);
        mRef.child("enrollments").child(studentName).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("student_enrollments").child(studentName).child(courseId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ViewStudentCourseDetail.this, "Enrollment Successful!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ViewStudentCourseDetail.this, "Error in enrolling for the class! Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDialog.dismiss();
    }

    private View.OnClickListener unenrollCourse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRef.child("enrollments").child(studentName).removeValue();
            FirebaseDatabase.getInstance().getReference().child("student_enrollments").child(studentName).child(courseId).removeValue();
            for (Weekday weekday : Weekday.values()){
                ClassTiming timing = ClassTiming.valueOf(courseConfig.getClassTimingOn(weekday));
                if (!timing.equals(ClassTiming.OFF)){
                    studentSchedule.updateRemoveSchedule(weekday, timing);
                }
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(Role.STUDENT.name()).child(studentName).child("schedule").setValue(studentSchedule);
        }
    };

    private void checkEnrollment(){
        Button button = (Button)findViewById(R.id.enroll_button);
        mRef.child("enrollments").child(studentName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null){
                    button.setText("Enroll in course");
                    button.setOnClickListener(enrollCourse);
                }
                else{
                    button.setText("Unenroll from course");
                    button.setOnClickListener(unenrollCourse);
                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    }
                    loadStudentSchedule();
                }else {
                    Toast.makeText(ViewStudentCourseDetail.this, "Error in loading data!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}