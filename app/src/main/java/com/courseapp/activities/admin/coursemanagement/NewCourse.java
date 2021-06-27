package com.courseapp.activities.admin.coursemanagement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.courseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NewCourse extends AppCompatActivity {

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        mDialog = new ProgressDialog(NewCourse.this);
        mDialog.setTitle("Updating Data");
        mDialog.setCanceledOnTouchOutside(false);
    }

    public void addCourse(View view){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("courses").push();
        String courseName = ((EditText)findViewById(R.id.course_name)).getText().toString().trim();
        String courseCode = ((EditText)findViewById(R.id.course_code)).getText().toString().trim();

        if (courseName.isEmpty()){
            Toast.makeText(this, "Please enter the course name", Toast.LENGTH_SHORT).show();
        }
        else if (courseCode.isEmpty()){
            Toast.makeText(this, "Please enter a valid course code", Toast.LENGTH_SHORT).show();
        }
        else {
            mDialog.show();
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", courseName);
            map.put("code", courseCode);
            mRef.setValue(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(NewCourse.this, "Course added successfully!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    finish();
                }
                else {
                    Toast.makeText(NewCourse.this, "Error in adding new course! Try again later.", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            });
        }
    }
}