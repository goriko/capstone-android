package com.example.guanzon.share;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Boolean.FALSE;

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private EditText editTextfname, editTextlname, editTextnumber, editTextgnumber;
    private Spinner spinnerGender;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());

        editTextfname = (EditText) findViewById(R.id.editTextFName);
        editTextlname = (EditText) findViewById(R.id.editTextLName);
        editTextnumber = (EditText) findViewById(R.id.editTextNumber);
        editTextgnumber = (EditText) findViewById(R.id.editTextGuardianNumber);
        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        buttonSave = (Button) findViewById(R.id.buttonProceed);

        String[] items = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerGender.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editTextfname.setText(dataSnapshot.child("FName").getValue().toString());
                editTextlname.setText(dataSnapshot.child("LName").getValue().toString());
                editTextnumber.setText(dataSnapshot.child("Number").getValue().toString());
                editTextgnumber.setText(dataSnapshot.child("GuardianNumber").getValue().toString());
                if (dataSnapshot.child("Gender").getValue().toString().equals("Male")) {
                    spinnerGender.setSelection(0);
                } else {
                    spinnerGender.setSelection(1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        buttonSave.setOnClickListener(this);

    }

    public void save() {



        databaseReference.child("FName").setValue(editTextfname.getText().toString());
        databaseReference.child("LName").setValue(editTextlname.getText().toString());
        databaseReference.child("Number").setValue(editTextnumber.getText().toString());
        databaseReference.child("GuardianNumber").setValue(editTextgnumber.getText().toString());
        databaseReference.child("Gender").setValue(spinnerGender.getSelectedItem().toString());

        startActivity(new Intent(this, UserInfoActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSave) {
            save();
        }
    }
}