package com.jtanuki.go.thaichang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private Button regBtn;
    private TextInputLayout mName, mEmail, mPassword;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private ProgressDialog mRegProgress;

    //Databases

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mName = (TextInputLayout) findViewById(R.id.displayName);
        mEmail = (TextInputLayout) findViewById(R.id.logEmail);
        mPassword = (TextInputLayout) findViewById(R.id.logPassword);
        regBtn = (Button) findViewById(R.id.CreateAccount);
        mToolbar = (Toolbar) findViewById(R.id.regPageToolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_name = mName.getEditText().getText().toString();
                String email_name = mEmail.getEditText().getText().toString();
                String password_name = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email_name) && !TextUtils.isEmpty(password_name)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account.");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    registerUser(display_name, email_name, password_name);

                }



            }
        });
    }


    // REGISTER USER METHOD #4 In Firebase Assistant Auth
    private void registerUser(final String display_name, String email_name, String password_name) {
        mAuth.createUserWithEmailAndPassword(email_name, password_name)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();

           //CauTION HIGH RISK OF ERROR OCCURANCE
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name",display_name);
                            userMap.put("status","Hello ThaiChang!");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                            mRegProgress.dismiss();
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();


                                    }

                                }
                            });




                        } else {  Toast.makeText(RegisterActivity.this, "Please correct information and try again.",
                                Toast.LENGTH_SHORT).show();

                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, "Password should contain a letter, a number, and 6 digits.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent startIntent = new Intent(RegisterActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
