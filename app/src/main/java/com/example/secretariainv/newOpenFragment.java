package com.example.secretariainv;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.BuildConfig;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class newOpenFragment extends Fragment {

    String img1;
    ImageView imageCartaz;
    Button facebookButton, instagramButton, whatsappButton;
    ImageButton facebook, instagram, whatsapp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_open, container, false);
        imageCartaz = view.findViewById(R.id.imagem1);
        imageCartaz.setImageBitmap(SendEditProfileFragment.decodeBase64(img1));
        facebook = view.findViewById(R.id.facebookShareButton);
        instagram = view.findViewById(R.id.imageButton);
        whatsapp = view.findViewById(R.id.imageButton2);
        //Saving image to share

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContent(imageCartaz);
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        return view;
    }
    public newOpenFragment(String img1){
        this.img1 = img1;
    }

   private void shareContent(ImageView imageCartaz){
        Drawable drawable= imageCartaz.getDrawable();
        Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();

        try {

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri imageUri = saveImageFromBase64(img1, "img_share.png");
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Uri saveImageFromBase64(String base64Image, String fileName) {
        try {
            // Convert base64 to bitmap
            byte[] decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
            Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Save bitmap to MediaStore
            Uri imageUri = saveImageToMediaStore(bitmap, fileName);
            if (imageUri != null) {
                return imageUri;
                // Image saved successfully
            } else {
                // Image save failed
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Uri saveImageToMediaStore(Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png"); // Change to the appropriate MIME type
        Uri imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri != null) {
            try {
                android.os.ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(imageUri, "w");
                if (pfd != null) {
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(pfd.getFileDescriptor());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    pfd.close();
                    return imageUri;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}