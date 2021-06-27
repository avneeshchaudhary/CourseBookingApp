package com.courseapp.activities.admin.instructormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.courseapp.R;
import com.courseapp.models.Role;
import com.courseapp.models.User;
import com.courseapp.viewholder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InstructorManagement extends AppCompatActivity {

    private RecyclerView courseList;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_management);
        courseList = (RecyclerView)findViewById(R.id.courseList);
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(Role.INSTRUCTOR.name());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(InstructorManagement.this, RecyclerView.VERTICAL, false);
        courseList.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User, UserViewHolder> adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class, R.layout.student_card, UserViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, User user, int i) {
                userViewHolder.setView(user.getUsername(), getRef(i));
            }
        };
        courseList.setAdapter(adapter);
    }

    public void addNewInstructor(View view){
        startActivity(new Intent(InstructorManagement.this, NewInstructor.class));
    }
}