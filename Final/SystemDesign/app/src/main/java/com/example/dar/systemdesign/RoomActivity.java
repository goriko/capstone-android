package com.example.dar.systemdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.drm.DrmStore;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.dar.systemdesign.
        Activity.sContext;

public class RoomActivity extends Fragment implements View.OnClickListener {

    private GridLayout gridView;
    private Button buttonCreate;
    private DatabaseReference databaseReference;
    private LinearLayout linearLayout;
    private Integer x = 0;
    private String[] str = new String[100];
    private Fragment fragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_room, container, false);

        gridView = (GridLayout) rootView.findViewById(R.id.layout);
        buttonCreate = (Button) rootView.findViewById(R.id.buttonCreate);

        databaseReference = FirebaseDatabase.getInstance().getReference("travel");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gridView.removeAllViews();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    if(data.child("Available").getValue().toString().equals("1")){
                        x++;
                        layout(x,data);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new CreateRoomActivity();
                replaceFragment(fragment);
            }
        });

        return rootView;
    }

    @SuppressLint("ResourceAsColor")
    public void layout(int x, DataSnapshot data){
        CardView cardView = new CardView(sContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(layoutParams);
        cardView.setUseCompatPadding(true);

        RelativeLayout relativeLayout1 = new RelativeLayout(sContext);
        RelativeLayout.LayoutParams relParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout1.setLayoutParams(relParams1);
        relativeLayout1.setPadding(dp(20), 0, dp(20), 0);

        LinearLayout linearLayout1 = new LinearLayout(sContext);
        LinearLayout.LayoutParams linParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout1.setLayoutParams(linParams1);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout1.setPadding(0, dp(20), 0, 0);

        TextView textView1 = new TextView(sContext);
        LinearLayout.LayoutParams textParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams1.gravity = Gravity.RIGHT;
        textView1.setLayoutParams(textParams1);
        textView1.setText("Join Room");
        textView1.setTextColor(sContext.getResources().getColor(R.color.yellow));
        textView1.setTypeface(Typeface.DEFAULT_BOLD);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 15.5);
        textView1.setId(x);

        LinearLayout linearLayout2 = new LinearLayout(sContext);
        LinearLayout.LayoutParams linParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout2.setLayoutParams(linParams2);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        linearLayout2.setPadding(dp(20), dp(20), dp(20), dp(15));

        LinearLayout linearLayout3 = new LinearLayout(sContext);
        linearLayout3.setLayoutParams(linParams2);
        linearLayout3.setPadding(0, dp(20), 0, dp(20));

        TextView textView2 = new TextView(sContext);
        LinearLayout.LayoutParams textParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView2.setLayoutParams(textParams2);
        textView2.setText("Room Information");
        textView2.setTypeface(Typeface.DEFAULT_BOLD);

        RelativeLayout relativeLayout2 = new RelativeLayout(sContext);
        RelativeLayout.LayoutParams relParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dp(2));
        relativeLayout2.setLayoutParams(relParams2);
        relativeLayout2.setPadding(0, dp(18), 0, dp(15));
        relativeLayout2.setBackgroundColor(R.color.blue);

        LinearLayout linearLayout4 = new LinearLayout(sContext);
        LinearLayout.LayoutParams linParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout4.setLayoutParams(linParams4);
        linearLayout4.setPadding(0, dp(20), 0, 0);

        TextView textView3 = new TextView(sContext);
        LinearLayout.LayoutParams textParams3 = new LinearLayout.LayoutParams(dp(120), LinearLayout.LayoutParams.WRAP_CONTENT);
        textView3.setLayoutParams(textParams3);
        textView3.setText("Starting Address:");

        TextView textView4 = new TextView(sContext);
        LinearLayout.LayoutParams textParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(30));
        textView4.setLayoutParams(textParams4);
        textView4.setPadding(dp(20), 0, dp(20), 0);
        textView4.setText(data.child("OriginString").getValue().toString());

        LinearLayout linearLayout5 = new LinearLayout(sContext);
        LinearLayout.LayoutParams linParams5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout5.setLayoutParams(linParams5);

        TextView textView5 = new TextView(sContext);
        LinearLayout.LayoutParams textParams5 = new LinearLayout.LayoutParams(dp(120), LinearLayout.LayoutParams.WRAP_CONTENT);
        textView5.setLayoutParams(textParams5);
        textView5.setText("Destination:");

        TextView textView6 = new TextView(sContext);
        LinearLayout.LayoutParams textParams6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(30));
        textView6.setLayoutParams(textParams6);
        textView6.setPadding(dp(20), 0, dp(20), 0);
        textView6.setText(data.child("DestinationString").getValue().toString());

        LinearLayout linearLayout6 = new LinearLayout(sContext);
        LinearLayout.LayoutParams linParams6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout6.setLayoutParams(linParams6);

        TextView textView7 = new TextView(sContext);
        LinearLayout.LayoutParams textParams7 = new LinearLayout.LayoutParams(dp(120), LinearLayout.LayoutParams.WRAP_CONTENT);
        textView7.setLayoutParams(textParams7);
        textView7.setText("Departure Time");

        TextView textView8 = new TextView(sContext);
        LinearLayout.LayoutParams textParams8 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(30));
        textView8.setLayoutParams(textParams8);
        textView8.setPadding(dp(20), 0, dp(20), 0);
        if(data.child("DepartureTime").child("DepartureHour").getValue() != null) {
            if(Integer.valueOf(data.child("DepartureTime").child("DepartureHour").getValue().toString()) < 12){
                textView8.setText(data.child("DepartureTime").child("DepartureHour").getValue().toString()+":"+data.child("DepartureTime").child("DepartureMinute").getValue().toString()+" am");
            }else{
                int time = Integer.valueOf(data.child("DepartureTime").child("DepartureHour").getValue().toString()) - 12;
                textView8.setText(time+":"+data.child("DepartureTime").child("DepartureMinute").getValue().toString()+" pm");
            }
        }
        LinearLayout linearLayout7 = new LinearLayout(sContext);
        LinearLayout.LayoutParams linParams7 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout7.setLayoutParams(linParams7);

        TextView textView9 = new TextView(sContext);
        LinearLayout.LayoutParams textParams9 = new LinearLayout.LayoutParams(dp(120), LinearLayout.LayoutParams.WRAP_CONTENT);
        textView9.setLayoutParams(textParams9);
        textView9.setText("Population");

        TextView textView10 = new TextView(sContext);
        LinearLayout.LayoutParams textParams10 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(30));
        textView10.setLayoutParams(textParams10);
        textView10.setPadding(dp(20), 0, dp(20), 0);
        textView10.setText(data.child("NoOfUsers").getValue().toString()+"/4");

        LinearLayout linearLayout8 = new LinearLayout(sContext);
        LinearLayout.LayoutParams linParams8 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout8.setLayoutParams(linParams8);
        linearLayout8.setOrientation(LinearLayout.VERTICAL);
        linearLayout8.setPadding(0, dp(20), 0, 0);

        TextView textView11 = new TextView(sContext);
        LinearLayout.LayoutParams textParams11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams11.gravity = Gravity.RIGHT;
        textView11.setLayoutParams(textParams11);
        textView11.setText("Php. "+data.child("MinimumFare").getValue().toString()+"-"+data.child("MaximumFare").getValue().toString());
        textView11.setTypeface(Typeface.DEFAULT_BOLD);
        textView11.setTextColor(sContext.getResources().getColor(R.color.yellow));
        textView11.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 15.5);

        linearLayout1.addView(textView1);
        relativeLayout1.addView(linearLayout1);

        linearLayout3.addView(textView2);

        linearLayout4.addView(textView3);
        linearLayout4.addView(textView4);

        linearLayout5.addView(textView5);
        linearLayout5.addView(textView6);

        linearLayout6.addView(textView7);
        linearLayout6.addView(textView8);

        linearLayout7.addView(textView9);
        linearLayout7.addView(textView10);

        linearLayout8.addView(textView11);

        linearLayout2.addView(linearLayout3);
        linearLayout2.addView(relativeLayout2);
        linearLayout2.addView(linearLayout4);
        linearLayout2.addView(linearLayout5);
        linearLayout2.addView(linearLayout6);
        linearLayout2.addView(linearLayout7);
        linearLayout2.addView(linearLayout8);

        cardView.addView(relativeLayout1);
        cardView.addView(linearLayout2);
        gridView.addView(cardView);

        str[x] = data.getKey().toString();
        textView1.setOnClickListener(RoomActivity.this);
    }

    public int dp(int number){
        DisplayMetrics displayMetrics = sContext.getResources().getDisplayMetrics();
        return (int)((number * displayMetrics.density) + 0.5);
    }

    @Override
    public void onClick(View v) {
        Integer i = v.getId();
        AddMember addMember = new AddMember();
        addMember.add(str[i], null);
        fragment = new InsideRoomActivity(str[i], "no");
        replaceFragment(fragment);
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment, "InsideRoom");
        transaction.addToBackStack("InsideRoom");
        transaction.commit();
    }

}
