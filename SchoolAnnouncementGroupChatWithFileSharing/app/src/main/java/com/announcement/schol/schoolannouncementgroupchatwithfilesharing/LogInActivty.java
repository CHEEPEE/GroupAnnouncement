package com.announcement.schol.schoolannouncementgroupchatwithfilesharing;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;



public class LogInActivty extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @BindView(R.id.input_email)
    EditText email;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.needaccount)
    TextView needAccount;
    @BindView(R.id.button_login)
    Button buttonlogin;
    @BindView(R.id.loginprogress)
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(),password.getText().toString());
            }
        });

       needAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i = new Intent(LogInActivty.this, SignUpActivity.class);
                startActivity(i);
           }
       });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            Intent i = new Intent(LogInActivty.this,GroupChatActivity.class);
            startActivity(i);
        }
    }

    private void signIn(String email, String password) {

        if (validateForm()){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent i = new Intent(LogInActivty.this, GroupChatActivity.class);
                                startActivity(i);
                                System.out.println("success");
                            } else {
                                // If sign in fails, display a message to the user.
                                progressBar.setVisibility(View.INVISIBLE);
                                System.out.println("failed");
                                Toast.makeText(LogInActivty.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
          }
    }


    private boolean validateForm() {
        boolean valid = true;

        String emailString = email.getText().toString();
        if (TextUtils.isEmpty(emailString)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordString = password.getText().toString();
        if (TextUtils.isEmpty(passwordString)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }



}
