package com.example.shree.beams_final;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    RadioGroup rgRating;
    RadioButton selectdRating;
    TextView textView6;
    Button btnReviewSubmit, btnCamera, btnUpload, btnChoose;
    EditText etDescription;
    StorageReference mStorage;
    ProgressDialog uploadPic;
    RatingBar ratingBar;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQ = 10;
    String server_url="http://06ec3c3e.ngrok.io/update_info.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        textView6=findViewById(R.id.textView6);
        btnReviewSubmit = findViewById(R.id.btnReviewSubmit);
        btnCamera = findViewById(R.id.btnCamera);
        btnUpload = findViewById(R.id.btnUpload);
        //btnChoose = findViewById(R.id.btnChoose);
        etDescription = findViewById(R.id.etDescription);
        ratingBar=findViewById(R.id.ratingBar);

        final String receivedPosition=getIntent().getExtras().getString("position");
       // textView6.append(receivedPosition);
        Toast.makeText(getApplicationContext(),"Review for project -"+receivedPosition,Toast.LENGTH_LONG).show();

        mStorage = FirebaseStorage.getInstance().getReference();
        uploadPic = new ProgressDialog(this);

        int orientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(orientation);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        TextView tvTimeStamp= (TextView)findViewById(R.id.tvTimeStamp) ;
        final String details=formattedDate.toString();
        tvTimeStamp.setText(details);

        btnReviewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Rating
                final String rating=String.valueOf(ratingBar.getRating());
                //Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
                //Timestamp
                final String UploadTimestamp=details;
                //User id
                final String cId="9769480886";
                //Project reviewing on
                final String uploadProject=receivedPosition;
                //Description
                final String description=etDescription.getText().toString();

                StringRequest stringRequest=new StringRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*
                        builder.setTitle("Server response");
                        builder.setMessage("Response :"+response);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                             etDescription.setText("");

                            }
                        });
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                        */
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ReviewActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params =new HashMap<String, String>();
                        params.put("uId",cId);
                        params.put("project",uploadProject);
                        params.put("Rating",rating);
                        params.put("Description",description);
                        params.put("Details",details);
                        return params;
                    }
                };
                MySingleton.getInstance(ReviewActivity.this).addToRequestQueue(stringRequest);




            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                //After user caputers image it will call onActivity result method
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseIntent = new Intent();
                chooseIntent.setType("image/*");
                chooseIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(chooseIntent, "Select an image"), PICK_IMAGE_REQ);

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            uploadPic.setMessage("Uploading Pic");
            uploadPic.show();
            Uri Resulturi = (Uri) data.getData();
            if (Resulturi == null) {
                Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
            } else {
                StorageReference filepath = mStorage.child("Photos").child("1.jpg");
                filepath.putFile(Resulturi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadPic.dismiss();
                        Toast.makeText(getApplicationContext(), "FINISHED", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();

                    }
                });

            }
        }
        if (requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK) {

            uploadPic.setMessage("Uploading Pic");
            uploadPic.show();
            Uri fileuri = data.getData();
            if (fileuri == null) {
                Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
            } else {
                StorageReference filepath1 = mStorage.child("Uploaded_Pics").child("4.jpg");
                filepath1.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadPic.dismiss();
                        Toast.makeText(getApplicationContext(), "Finished", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    }
}
