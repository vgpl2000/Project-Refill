package com.example.projectrefill;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Checkordersbtn_client_Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getInstance().getReference();
    String name;
    RecyclerView recyclerView2;
    adapter_clientside_checkbtn adapter2;
    Button btnacp,btncan,btndel;
    String o_state;
    String token2,token3,token4;
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
    String formattedDate1 = df.format(c);

    public Checkordersbtn_client_Fragment() {

    }
    public Checkordersbtn_client_Fragment(String name) {
        this.name=name;
    }
    public static Checkordersbtn_client_Fragment newInstance(String param1, String param2) {
        Checkordersbtn_client_Fragment fragment = new Checkordersbtn_client_Fragment();
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
        View view=inflater.inflate(R.layout.fragment_checkordersbtn_client_, container, false);

        TextView textView=view.findViewById(R.id.rname);
        textView.setText(name);
        String user=textView.getText().toString();

        btnacp=view.findViewById(R.id.btn_accept);
        btncan=view.findViewById(R.id.btn_cancl);
        btndel=view.findViewById(R.id.btn_deli);


        databaseReference.child("Client").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    o_state=snapshot.child("c_orders").child(name).child("order_state").getValue(String.class);
                    if(o_state.equals("accepted")){
                        btnacp.setVisibility(View.GONE);
                        btncan.setVisibility(View.GONE);
                        btndel.setVisibility(View.VISIBLE);
                    }else if(o_state.equals("cancelled")){
                        btncan.setVisibility(View.GONE);
                       btnacp.setVisibility(View.GONE);
                        btndel.setVisibility(View.GONE);
                    }else if(o_state.equals("delivered")){
                        btndel.setVisibility(View.GONE);
                        btncan.setVisibility(View.GONE);
                        btnacp.setVisibility(View.GONE);
                    }else if(o_state.equals("")){
                        btnacp.setVisibility(View.VISIBLE);
                        btncan.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        recyclerView2=(RecyclerView) view.findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        //for venu

        btnacp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
                builder.setTitle("Accept!");
                builder.setMessage("Are you sure you want to accept new order from "+name+" ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(view.getContext(), "accepting", Toast.LENGTH_SHORT).show();
                        String retailername, itemname, quan;

                        Date c = Calendar.getInstance().getTime();


                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String formattedDate = df.format(c).toString();
                        System.out.println("date to display for the system   " + formattedDate);

                        btndel.setVisibility(View.VISIBLE);
                        btnacp.setVisibility(View.GONE);
                        btncan.setVisibility(View.GONE);

                        //To set accepted order state value accepted

                        databaseReference.child("Client").child("c_orders").child(user).child("order_state").setValue("accepted");

                        String rname=name;

                        DatabaseReference fromp = FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname).child("check_orders");


                        DatabaseReference top = FirebaseDatabase.getInstance().getReference("Client").child("c_accepted").child(rname).child(formattedDate1);

                        DatabaseReference fromforretailer = FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname).child("check_orders");

                        DatabaseReference toretailer=FirebaseDatabase.getInstance().getReference("Retailer").child(rname).child("r_accepted").child(formattedDate1);

                        fromp.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                top.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {





                                        Toast.makeText(view.getContext(), "Accepted", Toast.LENGTH_SHORT).show();


                                        //notification testing

                                        SharedPreferences preferences;
                                        preferences = view.getContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
                                        String orderId=preferences.getString("username","");
                                        String owner="akashadeepa";


                                        FirebaseDatabase.getInstance().getReference("Retailer").child(rname).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                token2=snapshot.getValue(String.class);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        Handler handler=new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                System.out.println("inside postdelay");

                                                firebasenotificationsendertesting notificationsender2=new firebasenotificationsendertesting(token2,"Refill","Dear "+rname+" your order has been accepted!","accepted", getContext(),getActivity());
                                                notificationsender2.sendnotifications();


                                            }
                                        },1000);








                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(view.getContext(), "Error accepting", Toast.LENGTH_SHORT).show();
                            }
                        });

                        fromforretailer.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                toretailer.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });

        btncan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.app.AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
                builder.setTitle("Cancel!");
                builder.setMessage("Are you sure you want to cancel new order from "+name+" ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(view.getContext(), "cancelling", Toast.LENGTH_SHORT).show();

                        btncan.setVisibility(View.GONE);
                        btnacp.setVisibility(View.GONE);

                        //To update that order is cancelled in database
                        databaseReference.child("Client").child("c_orders").child(name).child("order_state").setValue("cancelled");


                        String rname=name;

                        DatabaseReference fromp = FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname).child("check_orders");


                        DatabaseReference top = FirebaseDatabase.getInstance().getReference("Client").child("c_cancelled").child(rname).child(formattedDate1);

                        fromp.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                top.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(view.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();

                                        SharedPreferences preferences;
                                        preferences = view.getContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
                                        String orderId=preferences.getString("username","");
                                        String owner="akashadeepa";


                                        FirebaseDatabase.getInstance().getReference("Retailer").child(rname).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                token3=snapshot.getValue(String.class);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        Handler handler=new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                System.out.println("inside postdelay");

                                                firebasenotificationsendertesting notificationsender2=new firebasenotificationsendertesting(token3,"Refill","Dear "+rname+" your order has been cancelled!","cancelled", getContext(),getActivity());
                                                notificationsender2.sendnotifications();


                                            }
                                        },1000);




                                        DatabaseReference frmtode=FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname);
                                        frmtode.removeValue();





                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(view.getContext(), "Error cancelling", Toast.LENGTH_SHORT).show();
                            }
                        });

                        DatabaseReference fromforretailer2 = FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname).child("check_orders");

                        DatabaseReference toretailer2=FirebaseDatabase.getInstance().getReference("Retailer").child(rname).child("r_cancelled").child(formattedDate1);

                        fromforretailer2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                toretailer2.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

                AppCompatActivity appCompatActivity = (AppCompatActivity) view.getContext();
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper2, new HomeFragment()).addToBackStack(null).commit();


            }
        });

        btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                btndel.setVisibility(View.GONE);
                databaseReference.child("Client").child("c_orders").child(name).child("order_state").setValue("delivered");
                Toast.makeText(view.getContext(), "delivered", Toast.LENGTH_SHORT).show();
                String rname=name;

                DatabaseReference fromp = FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname).child("check_orders");


                DatabaseReference top = FirebaseDatabase.getInstance().getReference("Client").child("c_delivered").child(rname).child(formattedDate1);

                fromp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        top.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(view.getContext(), "Delivered", Toast.LENGTH_SHORT).show();



                                FirebaseDatabase.getInstance().getReference("Retailer").child(rname).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        token4=snapshot.getValue(String.class);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        System.out.println("inside postdelay");

                                        firebasenotificationsendertesting notificationsender2=new firebasenotificationsendertesting(token4,"Refill","Dear "+rname+" your order has been delivered!","delivered", getContext(),getActivity());
                                        notificationsender2.sendnotifications();


                                    }
                                },1000);

                                DatabaseReference frmtode=FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname);
                                frmtode.removeValue();

                                AppCompatActivity appCompatActivity = (AppCompatActivity) view.getContext();
                                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper2, new HomeFragment()).addToBackStack(null).commit();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(view.getContext(), "Error delivery pressed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });




        final DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Client");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.child("c_orders").getChildren()){
                    final String rt=snapshot1.getKey().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String retailname=textView.getText().toString();



        FirebaseRecyclerOptions<client_model_btncheckorders> options2 =
                new FirebaseRecyclerOptions.Builder<client_model_btncheckorders>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Client").child("c_orders").child(retailname).child("check_orders").child("Items"), client_model_btncheckorders.class)
                        .build();
        adapter2=new adapter_clientside_checkbtn(options2);
        recyclerView2.setAdapter(adapter2);


        return view;

    }
    @Override
    public void onStart() {
        super.onStart();
        adapter2.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter2.stopListening();
    }


   public void onBackPressed()
    {
        AppCompatActivity appCompatActivity=(AppCompatActivity)getContext();
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper2,new HomeFragment()).addToBackStack(null).commit();

    }
}