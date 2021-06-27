package com.courseapp.activities.admin.instructormanagement;

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

public class NewInstructor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_instructor);
    }

    public void addInstructor(View view){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(Role.INSTRUCTOR.name());
        String instructorName = ((EditText)findViewById(R.id.instructor_name)).getText().toString().trim();
        String instructorPassword = ((EditText)findViewById(R.id.instructor_password)).getText().toString().trim();

        if (instructorName.isEmpty()){
            Toast.makeText(this, "Please enter the instructor name", Toast.LENGTH_SHORT).show();
        }
        else if (instructorPassword.isEmpty()){
            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
        }
        else {
            User user = new User(instructorName, instructorPassword, Role.INSTRUCTOR);
            mRef.child(instructorName).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(NewInstructor.this, "New Instructor account created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(NewInstructor.this, "Failed to create instructor account! Try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}