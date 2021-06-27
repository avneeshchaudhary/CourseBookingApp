package com.courseapp.activities.instructor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.courseapp.R;
import com.courseapp.activities.admin.coursemanagement.CourseManagement;
import com.courseapp.models.Course;
import com.courseapp.viewholder.CourseViewHolder;
import com.courseapp.viewholder.CourseViewHolderI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourseList extends AppCompatActivity {

    private RecyclerView courseList;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        courseList = (RecyclerView)findViewById(R.id.courseList);
        mRef = FirebaseDatabase.getInstance().getReference().child("courses");
        RecyclerView.LayoutManager manager = new LinearLayoutManager(CourseList.this, RecyclerView.VERTICAL, false);
        courseList.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Course, CourseViewHolderI> adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolderI>(Course.class, R.layout.course_card, CourseViewHolderI.class, mRef) {
            @Override
            protected void populateViewHolder(CourseViewHolderI courseViewHolderI, Course course, int i) {
                courseViewHolderI.setLayout(course.getName(), course.getCode());
                courseViewHolderI.setOnclickListner(CourseList.this, getRef(i).getKey());
            }
        };
        courseList.setAdapter(adapter);
    }

    public void searchCourse(View view){
        startActivity(new Intent(CourseList.this, InstructorSearchActivity.class));
    }

}