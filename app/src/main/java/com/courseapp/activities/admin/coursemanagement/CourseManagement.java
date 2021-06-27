package com.courseapp.activities.admin.coursemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.courseapp.R;
import com.courseapp.models.Course;
import com.courseapp.viewholder.CourseViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourseManagement extends AppCompatActivity {

    private RecyclerView courseList;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_management);
        courseList = (RecyclerView)findViewById(R.id.courseList);
        mRef = FirebaseDatabase.getInstance().getReference().child("courses");
        RecyclerView.LayoutManager manager = new LinearLayoutManager(CourseManagement.this, RecyclerView.VERTICAL, false);
        courseList.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(Course.class, R.layout.course_card, CourseViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(CourseViewHolder courseViewHolder, Course course, int i) {
                courseViewHolder.setLayout(course.getName(), course.getCode());
                courseViewHolder.setOnclickListner(CourseManagement.this, getRef(i).getKey());
            }
        };
        courseList.setAdapter(adapter);
    }

    public void addNewCourse(View view){
        startActivity(new Intent(CourseManagement.this, NewCourse.class));
    }

}