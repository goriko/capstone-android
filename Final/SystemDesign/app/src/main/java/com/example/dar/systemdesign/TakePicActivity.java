package com.example.dar.systemdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import static com.example.dar.systemdesign.NavBarActivity.sContext;

@SuppressLint("ValidFragment")
public class TakePicActivity extends Fragment {

    private String Id;

    private Button buttonCamera;
    private Button buttonProceed;
    private ImageView imageView;
    private Bitmap bitmap;
    private StorageReference storageReference;
    private Fragment fragment = null;

    public TakePicActivity(String id){
        this.Id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_take_pic, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();

        buttonCamera = (Button) rootView.findViewById(R.id.buttonCamera);
        buttonProceed = (Button) rootView.findViewById(R.id.buttonProceed);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
    }

    private void uploadFile(){
        StorageReference riversRef = storageReference.child("travel/"+Id+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        riversRef.putBytes(data);

        fragment = new TaxiDetailsActivity(Id);
        replaceFragment(fragment);
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
