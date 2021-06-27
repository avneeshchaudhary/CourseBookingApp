package com.courseapp.activities.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.courseapp.R;
import com.courseapp.models.Course;
import com.courseapp.viewholder.CourseViewHolderS;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewStudentCourses extends AppCompatActivity {

    private RecyclerView courseList;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_courses);
        courseList = (RecyclerView)findViewById(R.id.courseList);
        mRef = FirebaseDatabase.getInstance().getReference().child("courses");
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ViewStudentCourses.this, RecyclerView.VERTICAL, false);
        courseList.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Course, CourseViewHolderS> adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolderS>(Course.class, R.layout.course_card, CourseViewHolderS.class, mRef) {
            @Override
            protected void populateViewHolder(CourseViewHolderS courseViewHolderS, Course course, int i) {
                courseViewHolderS.setLayout(course.getName(), course.getCode());
                courseViewHolderS.setOnclickListner(ViewStudentCourses.this, getRef(i).getKey());
            }
        };
        courseList.setAdapter(adapter);
    }

    public void searchCourseBar(View view){
        startActivity(new Intent(ViewStudentCourses.this, SearchCourseActivity.class));
    }
}