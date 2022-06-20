package com.example.projectrefill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class adapter_clientside_retailerdetailesdisplying extends FirebaseRecyclerAdapter<client_model_fordisplayingretailerstoupdatedue,adapter_clientside_retailerdetailesdisplying.myviewholder> {


    public adapter_clientside_retailerdetailesdisplying(@NonNull FirebaseRecyclerOptions<client_model_fordisplayingretailerstoupdatedue> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull client_model_fordisplayingretailerstoupdatedue model) {
        holder.duefield.setText(model.getDue_amt());
        holder.name.setText(model.getName());
        holder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Submit", Toast.LENGTH_SHORT).show();

                String name1=holder.name.getText().toString();
                System.out.println(name1+"flsjfslfj;lsdfjlajsjfjsfjfjsjfjlsjflj;ljfljljfl");

                String dueamt=holder.duefield.getText().toString();
                System.out.println(dueamt+"flsjfslfj;lsdfjlajsjfjsfjfjsjfjlsjflj;ljfljljfl");


                HashMap user=new HashMap();
                user.put("due_amt",dueamt);


                //FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Retailer");
                databaseReference.child(name1).updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Toast.makeText(view.getContext(), "Done!!!!!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_design_fordisplayingretailerlistwithdueforclient,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder{
        TextView name;
        Button trans,submit;
        EditText duefield;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.retailer_name);
            trans=itemView.findViewById(R.id.buttontoopentransactions);
            submit=itemView.findViewById(R.id.buttontosubmitdue);
            duefield=itemView.findViewById(R.id.edittexttoeditdueamount);

        }
    }
}
