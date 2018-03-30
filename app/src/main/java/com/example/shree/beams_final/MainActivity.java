package com.example.shree.beams_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar main_page_toolbar;
    Button btnGetJson,btnParseJson;
    AlertDialog.Builder alertDialogBuilder ;
    String JSON_STRING,JSON_PARSE;
    private RadioGroup rgType,rgConst;
    private RadioButton radioTypeButton,radioConstButton;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        btnParseJson=findViewById(R.id.btnParseJson);
        btnGetJson=findViewById(R.id.btnGetJson);
        rgType=findViewById(R.id.rgType);
        rgConst=findViewById(R.id.rgConst);


        main_page_toolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_page_toolbar);
        getSupportActionBar().setTitle("BEAMS");

        int orientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(orientation);

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You want to Logout?");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });


        btnGetJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundTask().execute();
                Toast.makeText(getApplicationContext(),"GOT JSON",Toast.LENGTH_SHORT).show();
            }
        });

        btnParseJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           if(JSON_PARSE==null)
                {
                    Toast.makeText(getApplicationContext(),"Get JSON data ",Toast.LENGTH_LONG).show();

                }
            else{
               Intent i = new Intent(MainActivity.this, ListOfProjects.class);
               i.putExtra("data", JSON_PARSE);
               startActivity(i);
            }

            }
        });


    }



    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null)
        {
            sendToStart();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout){
            alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut();
                    sendToStart();
                    dialogInterface.cancel();
                }
            });
            alertDialogBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(),"Still logged in",Toast.LENGTH_LONG).show();

                }
            });

            alertDialogBuilder.show();
        }else if(item.getItemId()==R.id.main_setting){
            Intent settingIntent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingIntent);

        }else if(item.getItemId()==R.id.about){
            Toast.makeText(getApplicationContext(),"Developed by Shree",Toast.LENGTH_SHORT).show();

        }

        return true;
    }

    private void sendToStart() {
        Toast.makeText(getApplicationContext(),"Not logged in ",Toast.LENGTH_SHORT).show();
        Intent startIntent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(startIntent);
        finish();  //User cant comeback on pressing back button
    }

    @Override
    public void onBackPressed() {
        alertDialogBuilder.setMessage("You want to exit?");
        alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
               // System.exit(0);
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogBuilder.show();

    }

    class BackgroundTask extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {

            int selectedType=rgType.getCheckedRadioButtonId();
            radioTypeButton=findViewById(selectedType);

            int selectedConst=rgConst.getCheckedRadioButtonId();
            radioConstButton=findViewById(selectedConst);

            //Toast.makeText(MainActivity.this,radioTypeButton.getText(),Toast.LENGTH_SHORT).show();

            String selected_type=radioTypeButton.getText().toString();
            String selected_const=radioConstButton.getText().toString();
            String ngrok="http://06ec3c3e.ngrok.io";

            if(selected_const.equals("Nashik") && selected_type.equals("Road")){
                json_url=ngrok+"/Nashik_Road.php";
            }else if(selected_const.equals("Nashik") && selected_type.equals("Bridge")){
                json_url=ngrok+"/Nashik_Bridge.php";
            }else if(selected_const.equals("Nashik") && selected_type.equals("All")){
                json_url=ngrok+"/Nashik.php";
            }

            if(selected_type.equals("Road")&& selected_const.equals("Mumbai") ){
                json_url=ngrok+"/Mumbai_Road.php";
            }else if(selected_type.equals("Bridge") && selected_const.equals("Mumbai")){
                json_url=ngrok+"/Mumbai_Bridge.php";
            }else if(selected_type.equals("All") && selected_const.equals("Mumbai")) {
                json_url = ngrok+"/Mumbai.php";
            }

            if(selected_const.equals("Aurangabad") && selected_type.equals("Road")){
                json_url=ngrok+"/Aurangabad_Road.php";
            }else if(selected_const.equals("Aurangabad") && selected_type.equals("Bridge")){
                json_url=ngrok+"/Aurangabad_Bridge.php";
            }else if(selected_const.equals("Aurangabad") && selected_type.equals("All")){
                json_url=ngrok+"/Aurangabad.php";
            }


        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url=new URL(json_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder=new StringBuilder();
                while((JSON_STRING=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(JSON_STRING+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tvDisplay=findViewById(R.id.tvDisplay);
            tvDisplay.setText(result);
            JSON_PARSE=result;


        }
    }

    
}
