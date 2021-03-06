package com.example.dar.systemdesign;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.LinkAddress;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.example.dar.systemdesign.NavBarActivity.roomId;
import static com.example.dar.systemdesign.NavBarActivity.roomStatus;
import static com.example.dar.systemdesign.NavBarActivity.sContext;

@SuppressLint("ValidFragment")
public class InsideRoomActivity extends Fragment implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseUser user;

    private TextView textViewLeader, textViewTravelTime, textViewFare;
    private Button button_change, buttonSend;
    private EditText editTextMessage;

    private LinearLayout linearLayoutUsers, linearLayoutMessage;
    private ScrollView scrollView;
    private ImageView imageLeader;

    private int z = 0;

    public InsideRoomActivity(String id, String status){
        ((NavBarActivity)this.getActivity()).roomId = id;
        ((NavBarActivity)this.getActivity()).roomStatus = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inside_room, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("travel").child(((NavBarActivity)this.getActivity()).roomId);
        ref = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile/");

        textViewLeader = (TextView) rootView.findViewById(R.id.textViewLeader);
        textViewTravelTime = (TextView) rootView.findViewById(R.id.textViewTravelTime);
        textViewFare = (TextView) rootView.findViewById(R.id.textViewFare);
        button_change = (Button) rootView.findViewById(R.id.button_change);
        buttonSend = (Button) rootView.findViewById(R.id.buttonSend);
        editTextMessage = (EditText) rootView.findViewById(R.id.editTextMessage);

        linearLayoutUsers = (LinearLayout) rootView.findViewById(R.id.linearLayoutUsers);
        linearLayoutMessage = (LinearLayout) rootView.findViewById(R.id.message);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView) ;
        imageLeader = (ImageView) rootView.findViewById(R.id.imageLeader);

        if(roomStatus != null){
            if(roomStatus.equals("Time")){
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("users").child("Leader").getValue().toString().equals(user.getUid().toString())){
                            button_change.setText("Start Travel");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        button_change.setOnClickListener(this);
        buttonSend.setOnClickListener(this);

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayoutUsers.removeAllViews();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getKey().equals("Leader")){
                        final long ONE_MEGABYTE = 1024 * 1024 * 5;
                        storageReference.child(data.getValue().toString()+".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                float aspectRatio = bm.getWidth() / (float) bm.getHeight();

                                int width = 90;
                                int height = Math.round(width / aspectRatio);

                                bm = Bitmap.createScaledBitmap(bm, width, height, false);

                                imageLeader.setImageBitmap(bm);
                            }
                        });
                        ref.child(data.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                textViewLeader.setText(dataSnapshot.child("Fname").getValue().toString() + " " +dataSnapshot.child("Lname").getValue().toString());
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }else{
                        ref.child(data.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                LinearLayout linearLayout = new LinearLayout(sContext);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                linearLayout.setLayoutParams(layoutParams);
                                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                linearLayout.setPadding(0, 0, 0, dp(10));

                                ImageView imageView = new ImageView(sContext);
                                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(dp(36), dp(36));
                                imageView.setLayoutParams(layoutParams2);
                                imageView.setPadding(dp(10), 0, 0, 0);

                                final long ONE_MEGABYTE = 1024 * 1024 * 5;
                                storageReference.child(data.getValue().toString()+".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        float aspectRatio = bm.getWidth() /(float) bm.getHeight();

                                        int width = 90;
                                        int height = Math.round(width / aspectRatio);

                                        bm = Bitmap.createScaledBitmap(bm, width, height, false);

                                        imageView.setImageBitmap(bm);
                                    }
                                });

                                TextView textView = new TextView(sContext);
                                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                textView.setLayoutParams(layoutParams1);
                                textView.setPadding(dp(10), dp(10), 0, 0);
                                textView.setText(dataSnapshot.child("Fname").getValue().toString() + " " +dataSnapshot.child("Lname").getValue().toString());

                                linearLayout.addView(imageView);
                                linearLayout.addView(textView);
                                linearLayoutUsers.addView(linearLayout);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }
                }
                databaseReference.child("Guests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            LinearLayout linearLayout;
                            linearLayout = new LinearLayout(sContext);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            linearLayout.setLayoutParams(layoutParams);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.setPadding(0, 0,0, dp(10));

                            ImageView imageView = new ImageView(sContext);
                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dp(36), LinearLayout.LayoutParams.MATCH_PARENT);
                            imageView.setLayoutParams(layoutParams1);
                            imageView.setPadding(dp(10), 0, 0, 0);
                            imageView.setImageDrawable(sContext.getResources().getDrawable(R.drawable.ic_user_icon));

                            LinearLayout linearLayout1 = new LinearLayout(sContext);
                            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            linearLayout1.setLayoutParams(layoutParams3);
                            linearLayout1.setPadding(dp(10), 0, 0, 0);
                            linearLayout1.setOrientation(LinearLayout.VERTICAL);

                            TextView textView = new TextView(sContext);
                            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            textView.setLayoutParams(layoutParams2);
                            textView.setText(data.child("Name").getValue().toString());

                            TextView textView2 = new TextView(sContext);
                            textView2.setLayoutParams(layoutParams2);

                            ref.child(data.child("CompanionId").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    textView2.setText("Guest (with: "+dataSnapshot.child("Fname").getValue().toString() + " " +dataSnapshot.child("Lname").getValue().toString()+")");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            linearLayout1.addView(textView);
                            linearLayout1.addView(textView2);

                            linearLayout.addView(imageView);
                            linearLayout.addView(linearLayout1);

                            linearLayoutUsers.addView(linearLayout);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        databaseReference.child("Guests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(z != 0){
                    linearLayoutUsers.removeAllViews();
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (data.getKey().equals("Leader")) {
                                    final long ONE_MEGABYTE = 1024 * 1024 * 5;
                                    storageReference.child(data.getValue().toString() + ".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            float aspectRatio = bm.getWidth() / (float) bm.getHeight();

                                            int width = 90;
                                            int height = Math.round(width / aspectRatio);

                                            bm = Bitmap.createScaledBitmap(bm, width, height, false);

                                            imageLeader.setImageBitmap(bm);
                                        }
                                    });
                                    ref.child(data.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            textViewLeader.setText(dataSnapshot.child("Fname").getValue().toString() + " " + dataSnapshot.child("Lname").getValue().toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                } else {
                                    ref.child(data.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            LinearLayout linearLayout = new LinearLayout(sContext);
                                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            linearLayout.setLayoutParams(layoutParams);
                                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                            linearLayout.setPadding(0, 0, 0, dp(10));

                                            ImageView imageView = new ImageView(sContext);
                                            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(dp(36), dp(36));
                                            imageView.setLayoutParams(layoutParams2);
                                            imageView.setPadding(dp(10), 0, 0, 0);

                                            final long ONE_MEGABYTE = 1024 * 1024 * 5;
                                            storageReference.child(data.getValue().toString()+".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    float aspectRatio = bm.getWidth() /(float) bm.getHeight();

                                                    int width = 90;
                                                    int height = Math.round(width / aspectRatio);

                                                    bm = Bitmap.createScaledBitmap(bm, width, height, false);

                                                    imageView.setImageBitmap(bm);
                                                }
                                            });

                                            TextView textView = new TextView(sContext);
                                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            textView.setLayoutParams(layoutParams1);
                                            textView.setPadding(dp(10), dp(10), 0, 0);
                                            textView.setText(dataSnapshot.child("Fname").getValue().toString() + " " +dataSnapshot.child("Lname").getValue().toString());

                                            linearLayout.addView(imageView);
                                            linearLayout.addView(textView);
                                            linearLayoutUsers.addView(linearLayout);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        LinearLayout linearLayout;
                        linearLayout = new LinearLayout(sContext);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        linearLayout.setLayoutParams(layoutParams);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setPadding(0, 0,0, dp(10));

                        ImageView imageView = new ImageView(sContext);
                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dp(36), LinearLayout.LayoutParams.MATCH_PARENT);
                        imageView.setLayoutParams(layoutParams1);
                        imageView.setPadding(dp(10), 0, 0, 0);
                        imageView.setImageDrawable(sContext.getResources().getDrawable(R.drawable.ic_user_icon));

                        LinearLayout linearLayout1 = new LinearLayout(sContext);
                        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        linearLayout1.setLayoutParams(layoutParams3);
                        linearLayout1.setPadding(dp(10), 0, 0, 0);
                        linearLayout1.setOrientation(LinearLayout.VERTICAL);

                        TextView textView = new TextView(sContext);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        textView.setLayoutParams(layoutParams2);
                        textView.setText(data.child("Name").getValue().toString());

                        TextView textView2 = new TextView(sContext);
                        textView2.setLayoutParams(layoutParams2);

                        ref.child(data.child("CompanionId").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                textView2.setText("Guest (with: "+dataSnapshot.child("Fname").getValue().toString() + " " +dataSnapshot.child("Lname").getValue().toString()+")");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        linearLayout1.addView(textView);
                        linearLayout1.addView(textView2);

                        linearLayout.addView(imageView);
                        linearLayout.addView(linearLayout1);

                        linearLayoutUsers.addView(linearLayout);
                    }
                }else{
                    z++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //show messages
        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayoutMessage.removeAllViews();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    LinearLayout samp = new LinearLayout(sContext);
                    samp.setBackgroundResource(R.drawable.customborder);

                    String message = data.child("MessageText").getValue().toString();
                    String user = data.child("MessageUser").getValue().toString();

                    TextView textView1 = new TextView(sContext);
                    textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    ref.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            textView1.setText("User: " + dataSnapshot.child("Fname").getValue().toString() +" "+dataSnapshot.child("Lname").getValue().toString()+ "\nMessage: " + message);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    samp.addView(textView1);
                    linearLayoutMessage.addView(samp);
                }
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alarm(Integer.valueOf(dataSnapshot.child("DepartureTime").child("DepartureHour").getValue().toString()), Integer.valueOf(dataSnapshot.child("DepartureTime").child("DepartureMinute").getValue().toString()));
                textViewTravelTime.setText(dataSnapshot.child("EstimatedTravelTime").getValue().toString() + " min(s)");
                textViewFare.setText("Php. " + dataSnapshot.child("MinimumFare").getValue().toString()+"-"+dataSnapshot.child("MaximumFare").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    //add guest
    public void guest(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Enter guest name");

        EditText input = new EditText(sContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        String roomId = ((NavBarActivity)this.getActivity()).roomId;

        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddMember guest = new AddMember();
                guest.add(roomId, input.getText().toString());
            }
        });

        alertDialog.show();
    }

    //alarm
    public void alarm(int departureHour, int departureMinute){
        ((NavBarActivity)this.getActivity()).alarmManager_time = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
        ((NavBarActivity)this.getActivity()).alarmManager_advance = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);

        Date date = new Date();

        Calendar alarm_time = Calendar.getInstance();
        Calendar alarm_advance = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        alarm_time.setTime(date);
        alarm_advance.setTime(date);
        cal_now.setTime(date);

        alarm_time.set(Calendar.HOUR_OF_DAY, departureHour);
        alarm_time.set(Calendar.MINUTE, departureMinute);
        alarm_time.set(Calendar.SECOND, 0);

        if(departureMinute >= 1){
            alarm_advance.set(Calendar.HOUR_OF_DAY, departureHour);
            alarm_advance.set(Calendar.MINUTE, departureMinute - 1);
            alarm_advance.set(Calendar.SECOND, 0);
        }else{
            int i = 1 - departureMinute;
            alarm_advance.set(Calendar.HOUR_OF_DAY, departureHour - 1);
            alarm_advance.set(Calendar.MINUTE, 60 - i);
            alarm_advance.set(Calendar.SECOND, 0);
        }

        if (alarm_time.before(cal_now)) {
            alarm_time.add(Calendar.DATE, 1);
        }

        Intent intent_time = new Intent(sContext, NotificationTime.class);
        intent_time.putExtra("id", ((NavBarActivity)this.getActivity()).roomId);
        PendingIntent pendingIntent_time = PendingIntent.getBroadcast(sContext, 1, intent_time, PendingIntent.FLAG_CANCEL_CURRENT);
        ((NavBarActivity)this.getActivity()).alarmManager_time.set(AlarmManager.RTC_WAKEUP, alarm_time.getTimeInMillis(), pendingIntent_time);

        if (alarm_advance.before(cal_now)) {
            alarm_advance.add(Calendar.DATE, 1);
        }

        Intent intent_advance = new Intent(sContext, NotificationAdvance.class);
        intent_advance.putExtra("id", ((NavBarActivity)this.getActivity()).roomId);
        PendingIntent pendingIntent_advance = PendingIntent.getBroadcast(sContext, 1, intent_advance, PendingIntent.FLAG_CANCEL_CURRENT);
        ((NavBarActivity)this.getActivity()).alarmManager_advance.set(AlarmManager.RTC_WAKEUP, alarm_advance.getTimeInMillis(), pendingIntent_advance);

    }

    //send message
    public void sendMessage(View v){
        String str = editTextMessage.getText().toString();
        Message m = new Message(str, user.getUid().toString());
        String mKey;
        mKey = UUID.randomUUID().toString();
        databaseReference.child("messages").child(mKey).setValue(m);
        editTextMessage.setText("");
        InputMethodManager imm = (InputMethodManager) sContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public int dp(int number){
        DisplayMetrics displayMetrics = sContext.getResources().getDisplayMetrics();
        return (int)((number * displayMetrics.density) + 0.5);
    }

    @Override
    public void onClick(View v) {
        if (v == button_change){
            if (button_change.getText().toString().equals("Start Travel")){
                Fragment fragment = new TakePicActivity(NavBarActivity.roomId);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }else{
                guest();
            }
        }else if(v == buttonSend){
            sendMessage(v);
        }
    }
}