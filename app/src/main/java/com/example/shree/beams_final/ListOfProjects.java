package com.example.shree.beams_final;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListOfProjects extends AppCompatActivity {

    Toolbar list_toolbar;
    ListView lvProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_projects);

        lvProjects=findViewById(R.id.lvProjects);
        list_toolbar=findViewById(R.id.list_toolbar);
        setSupportActionBar(list_toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int orientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(orientation);

        ArrayList<String> projects=new ArrayList<>();
        projects.add("Andheri");
        projects.add("Parle");
        projects.add("Bandra");
        projects.add("Andheri-west");
        projects.add("vile Parle");
        projects.add("Khar");

        ArrayAdapter projectAdapter=new ArrayAdapter(this,R.layout.mylist_view,projects);
        lvProjects.setAdapter(projectAdapter);

        lvProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent selectedIntent=new Intent(ListOfProjects.this,ReviewActivity.class);
                startActivity(selectedIntent);
               // Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
            }
        });

    }
}
