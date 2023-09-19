package com.example.secretariainv;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class HomeFragment extends Fragment {
    ImageView imageNoticia1, imageNoticia2, imageNoticia3, imageNoticia4, imageCartaz;
    ScrollView scrollView;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageNoticia1 = view.findViewById(R.id.imageNoticia1);
        imageNoticia2 = view.findViewById(R.id.imageNoticia2);
        imageNoticia3 = view.findViewById(R.id.imageNoticia3);
        imageNoticia4 = view.findViewById(R.id.imageNoticia4);
        imageCartaz = view.findViewById(R.id.imageCartaz);
        scrollView = view.findViewById(R.id.homeScrollView);
        swipeRefreshLayout = view.findViewById(R.id.homeSwipeRefreshLayout);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        SharedPreferences images = getActivity().getSharedPreferences("images", Context.MODE_PRIVATE);

        if (images.getString("read", "false").equals("false")){
            refreshData(images, swipeRefreshLayout);
        }
        else {
            imageCartaz.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Principal", null)));

            if(!images.getString("Card1", "None").equals("None")){
                imageNoticia1.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card1", null)));
            } else {
                imageNoticia1.setImageResource(R.drawable.embreve);
            }

            if(!images.getString("Card2", "None").equals("None")){
                imageNoticia2.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card2", null)));
            } else {
                imageNoticia2.setImageResource(R.drawable.embreve);
            }

            if(!images.getString("Card3", "None").equals("None")){
                imageNoticia3.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card3", null)));
            } else {
                imageNoticia3.setImageResource(R.drawable.embreve);
            }

            if(!images.getString("Card4", "None").equals("None")){
                imageNoticia4.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card4", null)));
            } else {
                imageNoticia4.setImageResource(R.drawable.embreve);
            }

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refresh here
                refreshData(images, swipeRefreshLayout);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        imageNoticia1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction  transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new newOpenFragment(images.getString("card1", "")));
                transaction.commit();
            }
        });

        imageNoticia2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction  transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new newOpenFragment(images.getString("card2", "")));
                transaction.commit();
            }
        });

        imageNoticia3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction  transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new newOpenFragment(images.getString("card3", "")));
                transaction.commit();
            }
        });

        imageNoticia4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction  transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new newOpenFragment(images.getString("card4", "")));
                transaction.commit();
            }
        });



        return view;
    }

    private void refreshData(SharedPreferences images, SwipeRefreshLayout swipeRefreshLayout){
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("ImagensLink");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        images.edit().putString("Principal", task.getResult().child("Principal").getValue().toString()).apply();
                        imageCartaz.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Principal", null)));

                        images.edit().putString("Card1", task.getResult().child("Card1").getValue().toString()).apply();
                        images.edit().putString("card1", task.getResult().child("card1").getValue().toString()).apply();
                        if(!images.getString("Card1", "None").equals("None")){
                            imageNoticia1.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card1", null)));
                        } else {
                            imageNoticia1.setImageResource(R.drawable.embreve);
                        }

                        images.edit().putString("Card2", task.getResult().child("Card2").getValue().toString()).apply();
                        images.edit().putString("card2", task.getResult().child("card2").getValue().toString()).apply();
                        if(!images.getString("Card2", "None").equals("None")){
                            imageNoticia2.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card2", null)));
                        } else {
                            imageNoticia2.setImageResource(R.drawable.embreve);
                        }

                        images.edit().putString("Card3", task.getResult().child("Card3").getValue().toString()).apply();
                        images.edit().putString("card3", task.getResult().child("card3").getValue().toString()).apply();
                        if(!images.getString("Card3", "None").equals("None")){
                            imageNoticia3.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card3", null)));
                        } else {
                            imageNoticia3.setImageResource(R.drawable.embreve);
                        }

                        images.edit().putString("Card4", task.getResult().child("Card4").getValue().toString()).apply();
                        images.edit().putString("card4", task.getResult().child("card4").getValue().toString()).apply();
                        if(!images.getString("Card4", "None").equals("None")){
                            imageNoticia4.setImageBitmap(SendEditProfileFragment.decodeBase64(images.getString("Card4", null)));
                        } else {
                            imageNoticia4.setImageResource(R.drawable.embreve);
                        }

                        images.edit().putString("read", "true").apply();
                        swipeRefreshLayout.setRefreshing(false);

                        /**FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                         FragmentTransaction  transaction = fragmentManager.beginTransaction();
                         transaction.replace(R.id.nav_host_fragment_1, new HomeFragment());
                         transaction.commit();**/
                    }
                }
            }
        });
    }
}