package com.tecstudyap.sinkuquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tecstudyap.sinkuquiz.databinding.FragmentHomeBinding;

import java.util.ArrayList;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentHomeBinding binding;
    FirebaseFirestore database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        database = FirebaseFirestore.getInstance();

        final ArrayList<CategoryModel> categories = new ArrayList<>();

        final CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);

        database.collection("categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            CategoryModel model = snapshot.toObject(CategoryModel.class);
                            model.setCategoryId(snapshot.getId());
                            categories.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.categoryList.setAdapter(adapter);

        binding.spinwheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(getContext(), SpinnerActivity.class));
            }
        });

        binding.textView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //method 1

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = "Play quiz and earn real cash, download Quizinku from Google Play Store";
                String subject = "Play and Earn Money";
                intent.putExtra(Intent.EXTRA_TEXT,body);
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
               // intent.putExtra(Intent.EXTRA_STREAM,"file:///android_asset/imagefile.png");
                startActivity(Intent.createChooser(intent,"Share with"));



                //method 2
//                String image = "file:///android_asset/termsandcondition.html";
//                Uri uri2 = Uri.fromFile(image);
//                        Intent intent1 = new Intent();
//                        intent1.setAction(Intent.ACTION_SEND);
//                        intent1.setType("image/*");
//                        intent1.putExtra(android.content.Intent.EXTRA_SUBJECT, "App Name");
//                        intent1.putExtra(Intent.EXTRA_TEXT, "Download the app from google play store now - ");
//                        intent1.putExtra(Intent.EXTRA_STREAM, uri2);
//                        try {
//                            startActivity(Intent.createChooser(intent1, "Share"));
//                        } catch (ActivityNotFoundException e) {
//                            Toast.makeText(getContext(), "please try again", Toast.LENGTH_SHORT).show();
//                        }

            }
        });


        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}