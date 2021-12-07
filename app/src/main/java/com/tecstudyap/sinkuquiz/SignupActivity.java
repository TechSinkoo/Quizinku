package com.tecstudyap.sinkuquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tecstudyap.sinkuquiz.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    ProgressDialog dialog;
    Uri selectedImageUri;
    DocumentReference documentReference;
    FirebaseStorage storage;
    String userId;
    DatabaseReference databaseReference;
  //  StorageTask uploadTask;
    StorageReference storageReference;
  //  FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating new account...");

//        documentReference = FirebaseFirestore.getInstance().document("users");
//        storageProfilePicsRef = storage.getReference().child("Profiles");

        setupHyperlink();
        binding.profileImageSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });

        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass, name, referCode;

                email = binding.emailBox.getText().toString();
                pass = binding.passwordBox.getText().toString();
                name = binding.nameBox.getText().toString();

                referCode = binding.referBox.getText().toString();
//
//                reference.putFile(selectedImageUri);
//               reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                   @Override
//                   public void onSuccess(Uri uri) {
//
//                   }
//               });



                final User user = new User(name, email,pass,null,referCode);

                dialog.show();
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();

                            fStore
                                    .collection("users")
                                    .document(uid)
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        dialog.dismiss();
                                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            dialog.dismiss();
                            Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finishAffinity();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11){
            if (data != null){
                selectedImageUri = data.getData();
               binding.profileImageSignUp.setImageURI(selectedImageUri);
            }
        }else{
            Toast.makeText(SignupActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }


       // uploadImageToFirebase(selectedImageUri);
    }

//    private void uploadImageToFirebase(Uri selectedImageUri){
//
//        //upload image to firebase storage.
//        StorageReference fileRef = storageReference.child("usrs/"+auth.getCurrentUser().getUid()+"/profilet.jpg");
//        fileRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//
//                        Picasso.get().load(uri).into(binding.profileImageSignUp);
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(SignupActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }



    private void setupHyperlink() {
        TextView linkTextView = findViewById(R.id.textView3);

        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());


        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignupActivity.this,PrivacyPolicyForQuizinku.class);
                startActivity(intent);


            }
        });



    }

}