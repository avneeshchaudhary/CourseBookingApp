package com.courseapp.activities.admin.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.courseapp.R;
import com.courseapp.models.Role;
import com.courseapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewStudent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);
    }

    public void addStudent(View view){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(Role.STUDENT.name());
        String studentName = ((EditText)findViewById(R.id.student_name)).getText().toString().trim();
        String studentPassword = ((EditText)findViewById(R.id.student_password)).getText().toString().trim();

        if (studentName.isEmpty()){
            Toast.makeText(this, "Please enter the instructor name", Toast.LENGTH_SHORT).show();
        }
        else if (studentPassword.isEmpty()){
            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
        }
        else {
            User user = new User(studentName, studentPassword, Role.STUDENT);
            mRef.child(studentName).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(NewStudent.this, "New student account created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(NewStudent.this, "Failed to create student account! Try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}