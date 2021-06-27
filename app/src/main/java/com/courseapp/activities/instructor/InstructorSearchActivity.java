package com.courseapp.activities.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.courseapp.R;
import com.courseapp.activities.student.SearchCourseActivity;
import com.courseapp.models.Course;
import com.courseapp.viewholder.CourseViewHolderI;
import com.courseapp.viewholder.CourseViewHolderS;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class InstructorSearchActivity extends AppCompatActivity {

    private RecyclerView courseList;
    private TextView notFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_search);
        notFound = (TextView)findViewById(R.id.not_found);
        courseList = (RecyclerView)findViewById(R.id.courseList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(InstructorSearchActivity.this, RecyclerView.VERTICAL, false);
        courseList.setLayoutManager(manager);
    }

    public void searchCourse(View view){
        String query = ((EditText)findViewById(R.id.searchbar)).getText().toString().trim();
        if (query.isEmpty()){
            notFound.setVisibility(View.VISIBLE);
        }else {
            Query searchCodeQuery = FirebaseDatabase.getInstance().getReference().child("courses").orderByChild("code").equalTo(query);
            Query searchNameQuery = FirebaseDatabase.getInstance().getReference().child("courses").orderByChild("name").equalTo(query);

            searchCodeQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null){
                        searchNameQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() == null){
                                    notFound.setVisibility(View.VISIBLE);
                                }
                                else {
                                    notFound.setVisibility(View.GONE);
                                    setAdapter(searchNameQuery);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else {
                        notFound.setVisibility(View.GONE);
                        setAdapter(searchCodeQuery);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void setAdapter(Query mRef){
        FirebaseRecyclerAdapter<Course, CourseViewHolderI> adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolderI>(Course.class, R.layout.course_card, CourseViewHolderI.class, mRef) {
            @Override
            protected void populateViewHolder(CourseViewHolderI courseViewHolder, Course course, int i) {
                courseViewHolder.setLayout(course.getName(), course.getCode());
                courseViewHolder.setOnclickListner(InstructorSearchActivity.this, getRef(i).getKey());
            }
        };
        courseList.setAdapter(adapter);
    }
}