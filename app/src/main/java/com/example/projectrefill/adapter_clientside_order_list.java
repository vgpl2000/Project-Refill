package com.example.projectrefill;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.internal.ParcelableSparseArray;
import com.google.android.material.internal.ParcelableSparseBooleanArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class adapter_clientside_order_list extends FirebaseRecyclerAdapter<client_model_home_orders,adapter_clientside_order_list.myviewholder> implements Filterable
        {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getInstance().getReference();
            String o_state;
            ProgressBar progressBar;
            Date c = Calendar.getInstance().getTime();



            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
            String formattedDate1 = df.format(c);


            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            public adapter_clientside_order_list(@NonNull FirebaseRecyclerOptions<client_model_home_orders> options) {

                super(options);
            }


            @Override
            protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull client_model_home_orders model) {


                databaseReference.child("Client").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            o_state=snapshot.child("c_orders").child(model.getName()).child("order_state").getValue(String.class);
                            if(o_state.equals("accepted")){
                                holder.btnacp.setVisibility(View.GONE);
                                holder.btncan.setVisibility(View.GONE);
                                holder.btndel.setVisibility(View.VISIBLE);
                            }else if(o_state.equals("cancelled")){
                                holder.btncan.setVisibility(View.GONE);
                                holder.btnacp.setVisibility(View.GONE);
                                holder.btndel.setVisibility(View.GONE);
                            }else if(o_state.equals("delivered")){
                                holder.btndel.setVisibility(View.GONE);
                                holder.btncan.setVisibility(View.GONE);
                                holder.btnacp.setVisibility(View.GONE);
                            }else if(o_state.equals("")){
                                holder.btnacp.setVisibility(View.VISIBLE);
                                holder.btncan.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
                //check order state




                    holder.textView.setText(model.getName());
                    holder.btnchk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppCompatActivity appCompatActivity = (AppCompatActivity) view.getContext();
                            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper, new Checkordersbtn_client_Fragment(model.getName())).addToBackStack(null).commit();
                        }
                    });




                    holder.btnacp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            android.app.AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Accept!");
                            builder.setMessage("Are you sure you want to accept new order from "+holder.textView.getText().toString()+" ?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(view.getContext(), "accepting", Toast.LENGTH_SHORT).show();
                                            String retailername, itemname, quan;

                                            Date c = Calendar.getInstance().getTime();


                                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                            String formattedDate = df.format(c).toString();
                                            System.out.println("date to display for the system   " + formattedDate);

                                            holder.btndel.setVisibility(View.VISIBLE);
                                            holder.btnacp.setVisibility(View.GONE);
                                            holder.btncan.setVisibility(View.GONE);

                                            //To set accepted order state value accepted

                                            databaseReference.child("Client").child("c_orders").child(model.getName()).child("order_state").setValue("accepted");

                                            String rname=holder.textView.getText().toString();

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

                                                            NotificationCompat.Builder builder1=new NotificationCompat.Builder(view.getContext(),"MyNotification");
                                                            builder1.setContentTitle("Order Accepted");
                                                            builder1.setContentText("Dear"+rname+" your recent order is accepted ");
                                                            builder1.setSmallIcon(R.drawable.logo);
                                                            builder1.setAutoCancel(true);

                                                            NotificationManagerCompat managerCompat=NotificationManagerCompat.from(view.getContext());
                                                            managerCompat.notify(1, builder1.build());
                                                            Toast.makeText(view.getContext(), "Accepted", Toast.LENGTH_SHORT).show();
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


                    holder.btncan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            android.app.AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Cancel!");
                            builder.setMessage("Are you sure you want to cancel new order from "+holder.textView.getText().toString()+" ?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(view.getContext(), "cancelling", Toast.LENGTH_SHORT).show();
                                            holder.btncan.setVisibility(View.GONE);
                                            holder.btnacp.setVisibility(View.GONE);

                                            //To update that order is cancelled in database
                                            databaseReference.child("Client").child("c_orders").child(model.getName()).child("order_state").setValue("cancelled");


                                            String rname=holder.textView.getText().toString();

                                            DatabaseReference fromp = FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname).child("check_orders");


                                            DatabaseReference top = FirebaseDatabase.getInstance().getReference("Client").child("c_cancelled").child(rname).child(formattedDate1);

                                            fromp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    top.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                            Toast.makeText(view.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();

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



                        }
                    });


                    holder.btndel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.btndel.setVisibility(View.GONE);
                            databaseReference.child("Client").child("c_orders").child(model.getName()).child("order_state").setValue("delivered");
                            Toast.makeText(view.getContext(), "delivered", Toast.LENGTH_SHORT).show();
                            String rname=holder.textView.getText().toString();

                            DatabaseReference fromp = FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname).child("check_orders");


                            DatabaseReference top = FirebaseDatabase.getInstance().getReference("Client").child("c_delivered").child(rname).child(formattedDate1);

                            fromp.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    top.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Toast.makeText(view.getContext(), "Delivered", Toast.LENGTH_SHORT).show();

                                            DatabaseReference frmtode=FirebaseDatabase.getInstance().getReference("Client").child("c_orders").child(rname);
                                            frmtode.removeValue();

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

                    holder.modep.setText(model.getPmode());


            }



            @NonNull

            @Override
            public Filter getFilter() {
                return null;
            }

            @NonNull
            @Override
            public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_order_clientside_homepage,parent,false);

                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
                    NotificationChannel channel=new NotificationChannel("MyNotification","MyNotification", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager=view.getContext().getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                return new myviewholder(view);

            }


            public class myviewholder extends RecyclerView.ViewHolder
             {
                    Button btnchk,btnacp,btncan,btndel;
                    TextView textView,modep;


        public myviewholder(@NonNull View itemView) {
            super(itemView);

            textView=itemView.findViewById(R.id.retailer_name);
            btnchk=itemView.findViewById(R.id.btn_checkorders);
            btnacp=itemView.findViewById(R.id.btn_accept);
            btncan=itemView.findViewById(R.id.btn_cancl);
            btndel=itemView.findViewById(R.id.btn_deli);
            progressBar=itemView.findViewById(R.id.progressBar);
            modep=itemView.findViewById(R.id.pmodeforclient);
        }

    }
            /*private void c_moveFirebaseRecord(DatabaseReference fromp, final DatabaseReference top) {
                fromp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        top.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(, "accepted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }*/


        }
