package com.courseapp.activities.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.courseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.courseapp.AppCredentials.APP_DB;
import static com.courseapp.AppCredentials.COURSE_CODE;
import static com.courseapp.AppCredentials.USERNAME;

public class InstructorCourseEdit extends AppCompatActivity {

    private TextView courseName;
    private TextView courseCode;
    private TextView courseInstructor;
    private DatabaseReference mRef;
    private Button configureButton;
    private Button assignmentButton;
    private boolean isInstructor;
    private String courseId;
    private String instructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_course_edit);

        Intent intent = getIntent();
        courseId = intent.getStringExtra(COURSE_CODE);
        mRef = FirebaseDatabase.getInstance().getReference().child("courses").child(courseId);

        courseName = (TextView)findViewById(R.id.course_name);
        courseCode = (TextView)findViewById(R.id.course_code);
        courseInstructor = (TextView)findViewById(R.id.course_instructor);
        configureButton = (Button)findViewById(R.id.config_button);
        assignmentButton = (Button)findViewById(R.id.assign_button);
        isInstructor = false;
        checkAccess();
    }

    private void checkAccess(){
        SharedPreferences preferences = getSharedPreferences(APP_DB, MODE_PRIVATE);
        instructor = preferences.getString(USERNAME, "Deep Gupta");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("name").getValue() != null) {
                    courseName.setText(snapshot.child("name").getValue().toString());
                }
                if (snapshot.child("code").getValue() != null) {
                    courseCode.setText(snapshot.child("code").getValue().toString());
                }
                if (snapshot.child("instructor").getValue() != null) {
                    String course_instructor = snapshot.child("instructor").getValue().toString();
                    courseInstructor.setText(course_instructor);
                    isInstructor = course_instructor.equals(instructor);
                    if (!isInstructor){
                        assignmentButton.setEnabled(false);
                        assignmentButton.setBackground(getDrawable(R.drawable.button_disabled));
                        assignmentButton.setText("Assign as Instructor");
                    }
                }else {
                    courseInstructor.setText("NA");
                    assignmentButton.setText("Assign as Instructor");
                    isInstructor = false;
                    assignmentButton.setOnClickListener(v -> {
                        mRef.child("instructor").setValue(instructor).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(instructor).child("courses").child(courseId).setValue(true);
                                configureButton.setEnabled(true);
                                Toast.makeText(InstructorCourseEdit.this, "You are now instructor of this course", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(InstructorCourseEdit.this, "Failed in assigning as instructor. Try again!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }

                if (!isInstructor){
                    configureButton.setEnabled(false);
                    configureButton.setBackground(getDrawable(R.drawable.button_disabled));
                }else{
                    configureButton.setEnabled(true);
                    configureButton.setBackground(getDrawable(R.drawable.button_accent));
                    assignmentButton.setEnabled(true);
                    assignmentButton.setText("UnAssign as Instructor");
                    assignmentButton.setOnClickListener(v -> {
                        mRef.child("instructor").removeValue();
                        mRef.child("config").removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(instructor).child("courses").child(courseId).removeValue();
                    });

                    configureButton.setOnClickListener(v -> {
                        Intent intent = new Intent(InstructorCourseEdit.this, CourseConfiguration.class);
                        intent.putExtra(COURSE_CODE, courseId);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}