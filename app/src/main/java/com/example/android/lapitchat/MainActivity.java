package com.example.android.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    //ViewPager
    private ViewPager mViewpager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            sendToStart();
        } else
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            Toolbar mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Lapit Chat");

            mViewpager = (ViewPager) findViewById(R.id.main_tabPager);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            mViewpager.setAdapter(mSectionsPagerAdapter);

            mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
            mTabLayout.setupWithViewPager(mViewpager);
    }
    @Override
    public void onStart() {
         super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
       if(currentUser==null)
       {
           sendToStart();
       }
       else{


                       mUserRef.child("online").setValue("true");
       }
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(currentUser!=null)
//            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
//    }

    private void sendToStart()
    {
        Intent startIntent=new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_button){
            FirebaseAuth.getInstance().signOut();

            //method to intent on start page
            sendToStart();
        }
        if(item.getItemId()==R.id.main_settings_button)
        {
            Intent settingsIntent=new Intent(MainActivity.this,AccountSettingsActivity.class);
            startActivity(settingsIntent);
        }
        if(item.getItemId()==R.id.main_all_users)
        {
            Intent allUsersIntent=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(allUsersIntent);
        }
        return true;
    }
}