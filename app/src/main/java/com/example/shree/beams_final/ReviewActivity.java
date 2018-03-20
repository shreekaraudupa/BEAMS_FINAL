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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ReviewActivity extends AppCompatActivity {

    RadioGroup rgRating;
    RadioButton selectdRating;
    Button btnReviewSubmit, btnCamera, btnUpload, btnChoose;
    EditText etDescription;
    StorageReference mStorage;
    ProgressDialog uploadPic;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQ = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        rgRating = findViewById(R.id.rgRating);
        btnReviewSubmit = findViewById(R.id.btnReviewSubmit);
        btnCamera = findViewById(R.id.btnCamera);
        btnUpload = findViewById(R.id.btnUpload);
        //btnChoose = findViewById(R.id.btnChoose);
        etDescription = findViewById(R.id.etDescription);
        mStorage = FirebaseStorage.getInstance().getReference();
        uploadPic = new ProgressDialog(this);

        int orientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(orientation);

        btnReviewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedRat = rgRating.getCheckedRadioButtonId();
                selectdRating = findViewById(selectedRat);
                String finalRating = selectdRating.getText().toString();


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
