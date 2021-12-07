package com.tecstudyap.sinkuquiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tecstudyap.sinkuquiz.databinding.FragmentProfileBinding;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    Context context;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    String imgUrl;
    ProgressDialog dialog;
    StorageReference storageReference;
    Uri selectedImageUri;
    FirebaseStorage storage;
    CircleImageView circleImageView;
    FirebaseFirestore database;
    FragmentProfileBinding binding;
    FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Updating...Please Wait.");
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
try {

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle("Logout");
    builder.setMessage("Are you sure want to logout ?");
    builder.setCancelable(true);
    builder.setIcon(R.drawable.ic_baseline_exit_to_app_24);
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(),LoginActivity.class);
            startActivity(intent);
        }
    });
    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    });

    AlertDialog alertDialog = builder.create();
    alertDialog.show();



}catch (Exception e){}
            }
        });
     //   return inflater.inflate(R.layout.fragment_profile, container, false);

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });


        binding.tecstudyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tecstudyap.blogspot.com/"));
                startActivity(intent);
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {


                    uploadImageToFirebase(selectedImageUri);

                }catch (Exception e){}

            }
        });



        StorageReference profileRef = storageReference.child("usrs/"+auth.getCurrentUser().getUid()+"/profilet.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

              Picasso.get().load(uri).into(binding.profileImage);

        //String urlIm = uri.toString();
//                Glide.with(context)
//                        .load(uri)
//                        .into(binding.profileImage);
            }
        });





        binding.showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    binding.passBox.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else
                {
                    binding.passBox.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });



fetchData();


     return binding.getRoot();



    }

    public void fetchData(){

        DocumentReference documentReference = database.collection("users").document(auth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
           
                if (documentSnapshot.exists()){

                    binding.nameBox.setText(documentSnapshot.getString("name"));
                    binding.emailBox.setText(documentSnapshot.getString("email"));
                    binding.passBox.setText(documentSnapshot.getString("pass"));
                   // binding.profileImage.setImageResource(Integer.parseInt(imgUrl));
                }
                else{
                    Toast.makeText(getContext(), "Row now found.", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void uploadImageToFirebase(Uri selectedImageUri) {
        try {
            //upload image to firebase storage.
            StorageReference fileRef = storageReference.child("usrs/" + auth.getCurrentUser().getUid() + "/profilet.jpg");
           if (selectedImageUri!=null){
               dialog.show();
            fileRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            imgUrl = uri.toString();



                            String namebox = binding.nameBox.getText().toString();
                            String emailbox = binding.emailBox.getText().toString();
                            String passbox = binding.passBox.getText().toString();


                            if (namebox.isEmpty()) {
                                binding.nameBox.setError("Please type Name");
                                return;
                            }
                            if (emailbox.isEmpty()) {
                                binding.nameBox.setError("Please type email");
                                return;
                            }
                            if (passbox.isEmpty()) {
                                binding.nameBox.setError("Please type password");
                                return;
                            }
                            
                            // StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());


                            final User user = new User(namebox, emailbox, passbox, imgUrl, null);


                            String uid = FirebaseAuth.getInstance().getUid();
                            database.collection("users")
                                    .document(uid)
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                       dialog.dismiss();
                                        Toast.makeText(getContext(), "Successfully Changed...", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                            // Picasso.get().load(imgUrl).into(binding.profileImage);

//                        Glide.with(context)
//                                .load(uri)
//                                .into(binding.profileImage);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{


               StorageReference profileRef = storageReference.child("usrs/"+auth.getCurrentUser().getUid()+"/profilet.jpg");
               profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {

                       Picasso.get().load(uri).into(binding.profileImage);

                       //String urlIm = uri.toString();
//                Glide.with(context)
//                        .load(uri)
//                        .into(binding.profileImage);
                   }
               });


               Toast.makeText(getContext(), "Please select profile image.", Toast.LENGTH_SHORT).show();
           }
        }catch (Exception e){}
    }

        @Override
        public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 11) {
                if (data != null) {
                    selectedImageUri = data.getData();
                    binding.profileImage.setImageURI(selectedImageUri);
                }
            } else {
                Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
            }


            // uploadImageToFirebase(selectedImageUri);
        }




}

