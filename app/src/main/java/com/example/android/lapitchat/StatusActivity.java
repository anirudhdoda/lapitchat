package com.example.android.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSaveBotton;

    //Firebase

    private DatabaseReference mDatabase;


    //progress dialog

    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mToolbar=(Toolbar)findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //,,progress



        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=currentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("status");

        String status_value=getIntent().getStringExtra("status_value");

        mStatus=(TextInputLayout)findViewById(R.id.status_input);
        mSaveBotton=(Button)findViewById(R.id.status_save_button);
        mStatus.getEditText().setText(status_value);

        mSaveBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress=new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Updating Your Status...");
                mProgress.setMessage("Please wait while updating your status...");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                String updateStatus=mStatus.getEditText().getText().toString();
                mDatabase.setValue(updateStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                            Toast.makeText(StatusActivity.this,"Status Updateed",Toast.LENGTH_LONG).show();
                            Intent backToAccoutnSettings=new Intent(StatusActivity.this,AccountSettingsActivity.class);
                            startActivity(backToAccoutnSettings);
                            finish();
                        }
                        else{
                            mProgress.hide();
                            Toast.makeText(StatusActivity.this,"Error occured",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


    }
}