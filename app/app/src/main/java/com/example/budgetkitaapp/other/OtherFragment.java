package com.example.budgetkitaapp.other;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.login.MainActivity;
import com.example.budgetkitaapp.map.listLocation.map;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class OtherFragment extends Fragment {

    private Button btnLogout, btnSavedLocation, btnProfile;
    private TextView usernameOther;
    private ImageView profileImage;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;

    public OtherFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_other, container, false);
        requireActivity().setTitle("Other");

        usernameOther = v.findViewById(R.id.usernameOther);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnSavedLocation = v.findViewById(R.id.btnSavedLocation);
        btnProfile = v.findViewById(R.id.btnEditProfile);
        profileImage = v.findViewById(R.id.profileImage);
        btnSavedLocation = v.findViewById(R.id.btnSavedLocation);

        //Firebase authentication to identify the current user
        mAuth = FirebaseAuth.getInstance();

        // Reference to an image file in Firebase Storage
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getInstance().getReference("Profile Images").child(mAuth.getCurrentUser().getUid()).child("Profile_Image.jpg");

        // Download the image from the Firebase Storage
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (isAdded()) {
                    Glide.with(OtherFragment.this).load(uri).into(profileImage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (isAdded()) {
                    profileImage.setImageResource(R.drawable.noprofile);
                }
            }
        });

        //Display user's username from Firebase
        //Firebase authentication to identify user so only logged-in user can see their information
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            //Retrieve username from Firebase
            DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference("Accounts").child(mAuth.getCurrentUser()
                    .getUid())
                    .child("UserDetail") // Add child node for UserDetail
                    .child("username"); // Point to the "username" field

            usernameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Retrieve username value from Firebase
                    String username = snapshot.getValue(String.class);
                    //Set the username with the data from Firebase
                    usernameOther.setText(username);
                    usernameOther.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //Handle error if needed
                }
            });
        }

        //To logout user account
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Logout logic here
                                FirebaseAuth.getInstance().signOut();
                                Intent logoutIntent = new Intent(getActivity(), MainActivity.class);
                                startActivity(logoutIntent);
                                getActivity().finish();

                                // Show Toast message
                                Toast.makeText(getActivity(), "User has been logged out", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Dismiss the dialog
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //When user click edit profile button
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getActivity(), UserProfile.class);
                startActivity(profileIntent);
            }
        });

        btnSavedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent savedLocation = new Intent(getActivity(), map.class);
                savedLocation.putExtra("source", "otherFragment");
                startActivity(savedLocation);
            }
        });
        return v;

    }
}

