package com.example.shree.beams_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etName,etEmail,etPassword;
    Button btnRegister;
    Toolbar register_toolbar;
    private ProgressDialog regProgress;
    private FirebaseAuth mAuth;
    TextView tvPhoneNumber;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        regProgress=new ProgressDialog(this);
        mDatabase= FirebaseDatabase.getInstance().getReference();



        etName=findViewById(R.id.etName);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        tvPhoneNumber=findViewById(R.id.tvPhoneNumber);
        btnRegister=findViewById(R.id.btnRegister);
        register_toolbar=findViewById(R.id.register_toolbar);
        setSupportActionBar(register_toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int orientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(orientation);


        Intent RegIntent=getIntent();
        final String verifiedNumber=RegIntent.getStringExtra("number");
        tvPhoneNumber.setText(verifiedNumber);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dispName=etName.getText().toString();
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();
                String number=verifiedNumber;
                if(!TextUtils.isEmpty(dispName)&&!TextUtils.isEmpty(number) && !TextUtils.isEmpty(email)  && !TextUtils.isEmpty(password)  ){


                    regProgress.setTitle("Registering user..");
                    regProgress.setMessage("Please wait");
                    regProgress.setCanceledOnTouchOutside(false);
                    regProgress.show();
                    reg_user(dispName,email,password,number);
                }else{
                    Toast.makeText(getApplicationContext(),"Empty fields",Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    private void reg_user(final String dispName, final String email, String password, final String number) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                           /* try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                error="Weak password";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                error="Invalid email";
                            }catch (FirebaseAuthUserCollisionException e){
                                error="Existing account";
                            }catch (Exception e){
                                error="Unknown error";
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                            */
                            FirebaseUser curr_user=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=curr_user.getUid();

                            //mDatabase is the root directory
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            //To enter complex data we enter it in the form of hashmap
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",dispName);
                            userMap.put("status","Hi there i m using this cool app ");
                            userMap.put("image","http://www.eeferp.usp.br/sites/default/files/default_images/anon_user.png");
                            userMap.put("number",number);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    regProgress.dismiss();
                                    Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                    //on pressing back it shouldnt go to main activity so clear task
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish(); //shouldnt comeback
                                    Toast.makeText(getApplicationContext(),"Welcome "+dispName+"\n"+" Added details to databse ",Toast.LENGTH_LONG).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    regProgress.dismiss();
                                    Toast.makeText(getApplicationContext(),"Couldnt add to db",Toast.LENGTH_LONG).show();
                                    Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                    //on pressing back it shouldnt go to main activity so clear task
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            });




                            // Sign in success, update UI with the signed-in user's information

                        } else {
                            // If sign in fails, display a message to the user.
                            regProgress.hide();
                           Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

}
