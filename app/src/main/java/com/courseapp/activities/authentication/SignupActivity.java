package com.courseapp.activities.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.courseapp.R;
import com.courseapp.activities.DashboardActivity;
import com.courseapp.models.Role;
import com.courseapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.courseapp.AppCredentials.APP_DB;
import static com.courseapp.AppCredentials.AUTHENTICATED;
import static com.courseapp.AppCredentials.ROLE;
import static com.courseapp.AppCredentials.USERNAME;

public class SignupActivity extends AppCompatActivity {
    private EditText userName;
    private EditText userPassword;
    private EditText userPasswordConfirm;
    private int mode;
    private TextView studentOp;
    private TextView instructorOp;
    private TextView[] Ops;
    private Role role;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userName = (EditText)findViewById(R.id.signup_username);
        userPassword = (EditText)findViewById(R.id.signup_password);
        userPasswordConfirm = (EditText)findViewById(R.id.signup_password_confirm);
        mode = 0;
        role = Role.STUDENT;
        studentOp = (TextView)findViewById(R.id.option_student);
        instructorOp = (TextView)findViewById(R.id.option_instructor);
        mDialog = new ProgressDialog(SignupActivity.this);
        mDialog.setTitle("Creating Account");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);

        Ops = new TextView[]{studentOp, instructorOp};
    }

    public void selectOption(View view){
        switch (view.getId()){
            case R.id.option_student:
                mode = 0;
                role = Role.STUDENT;
                break;

            case R.id.option_instructor:
                mode = 1;
                role = Role.INSTRUCTOR;
                break;
        }

        TextView selected = Ops[mode];
        for (TextView textView : Ops){
            textView.setBackground(getResources().getDrawable(R.drawable.option_default));
            textView.setTextColor(getColor(R.color.colorAccent));
        }
        selected.setTextColor(getColor(android.R.color.black));
        selected.setBackground(getResources().getDrawable(R.drawable.option_selected));
    }

    public void SignUp(View view){
        String username = userName.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String password_confirm = userPasswordConfirm.getText().toString().trim();

        if (username.length() < 4){
            Toast.makeText(this, R.string.username_length, Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 4){
            Toast.makeText(this, R.string.password_length, Toast.LENGTH_SHORT).show();
        }
        else if (!password_confirm.equals(password)){
            Toast.makeText(this, R.string.password_confirmation, Toast.LENGTH_SHORT).show();
        }
        else {
            mDialog.show();
            SignUpwithCredentials(username, password, role);
        }
    }

    public void goToLogin(View view){
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    private void SignUpwithCredentials(final String username, String password, final Role role){

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(role.name());
        mRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    Toast.makeText(SignupActivity.this, "Username is already taken!", Toast.LENGTH_SHORT).show();
                }
                else {
                    User user = new User(username, password, role);
                    mRef.child(username).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SharedPreferences preferences = getSharedPreferences(APP_DB, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(USERNAME, username);
                                editor.putString(ROLE, role.name());
                                editor.putBoolean(AUTHENTICATED, true);
                                editor.apply();
                                editor.commit();
                                mDialog.dismiss();
                                startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                                finish();
                            }
                            else {
                                mDialog.dismiss();
                                Toast.makeText(SignupActivity.this, "SignUp failed! Try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mDialog.dismiss();
                Toast.makeText(SignupActivity.this, "SignUp failed! Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}