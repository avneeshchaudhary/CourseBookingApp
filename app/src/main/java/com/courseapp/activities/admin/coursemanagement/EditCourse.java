package com.courseapp.activities.admin.coursemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.courseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.courseapp.AppCredentials.COURSE_CODE;

public class EditCourse extends AppCompatActivity {

    private DatabaseReference mRef;
    private EditText courseName;
    private EditText courseCode;
    private HashMap<String, Object> data;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        courseCode = (EditText)findViewById(R.id.course_code);
        courseName = (EditText)findViewById(R.id.course_name);

        mDialog = new ProgressDialog(EditCourse.this);
        mDialog.setTitle("Updating Data");
        mDialog.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        String courseId = intent.getStringExtra(COURSE_CODE);
        mRef = FirebaseDatabase.getInstance().getReference().child("courses").child(courseId);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data = (HashMap<String, Object>)snapshot.getValue();
                courseName.setText((String)data.getOrDefault("name", ""));
                courseCode.setText((String)data.getOrDefault("code", ""));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveCourseDetails(View view){
        String code = courseCode.getText().toString();
        String name = courseName.getText().toString();

        if (code.length() < 4){
            Toast.makeText(this, "Please enter a valid course code minimum 4 character long", Toast.LENGTH_SHORT).show();
        }
        else if (name.length() < 4){
            Toast.makeText(this, "Please enter a valid course name minimum 4 character long", Toast.LENGTH_SHORT).show();
        }
        else {
            mDialog.show();
            data.put("name", name);
            data.put("code", code);
            mRef.setValue(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(EditCourse.this, "Course Details updated successfully!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    finish();
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditCourse.this, "Failed to update course details. Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}