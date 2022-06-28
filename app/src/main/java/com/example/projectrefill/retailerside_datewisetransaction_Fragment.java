package com.example.projectrefill;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class retailerside_datewisetransaction_Fragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    String date;
    TextView dateval;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getInstance().getReference();
    adapter_retailerside_datewise_dispoforder adapter;


    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences preferences;

   // DatabaseReference databaseReference = database.getInstance().getReference();




    public retailerside_datewisetransaction_Fragment() {
        // Required empty public constructor
    }
    public retailerside_datewisetransaction_Fragment(String date) {
        this.date=date;
    }


    public static retailerside_datewisetransaction_Fragment newInstance(String param1, String param2) {
        retailerside_datewisetransaction_Fragment fragment = new retailerside_datewisetransaction_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_retailerside_datewisetransaction_, container, false);

        //blocked or not
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = getActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor=preferences.edit();
        String name1=preferences.getString("username","");
        databaseReference.child("Retailer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String state=snapshot.child(name1).child("state").getValue(String.class);

                if(state.equals("blocked")){
                    editor.putString("state","blocked");
                    editor.commit();
                    Toast.makeText(getActivity(), "You don't have access!", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }else{
                    editor.putString("notblocked","");
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        dateval=view.findViewById(R.id.texttodispdateforref);
        dateval.setText(date);
        String  date=dateval.toString();
        recyclerView=view.findViewById(R.id.recyclerViewdatewisedipofdetails);
        progressBar=view.findViewById(R.id.progressBardatewise);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager1(getContext()));


        preferences = getActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String username=preferences.getString("username","");

        String datenew=dateval.getText().toString();
        System.out.println(datenew+" date value");

       /* FirebaseRecyclerOptions<retailer_model_datewise_detailsdisp> options =
                new FirebaseRecyclerOptions.Builder<retailer_model_datewise_detailsdisp>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Retailer").child(username).child("r_orders"),retailer_model_datewise_detailsdisp.class)
                        .build();*/

        FirebaseRecyclerOptions<retailer_model_datewise_detailsdisp> options =
                new FirebaseRecyclerOptions.Builder<retailer_model_datewise_detailsdisp>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Retailer").child(username).child("r_history").child(datenew),retailer_model_datewise_detailsdisp.class)
                        .build();

        adapter=new adapter_retailerside_datewise_dispoforder(options);
        adapter.startListening();

        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                progressBar.setVisibility(View.GONE);
                super.onScrolled(recyclerView, dx, dy);
            }
        });




        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public class CustomLinearLayoutManager1 extends LinearLayoutManager {

        public CustomLinearLayoutManager1(Context context) {
            super(context);
        }


        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}