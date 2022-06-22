package com.example.projectrefill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Random;

public class adapter_clientside_itemdisplaying extends FirebaseRecyclerAdapter<client_model_todisplayitemsavailable,adapter_clientside_itemdisplaying.myviewholderfordisplaying>{

    public adapter_clientside_itemdisplaying(@NonNull FirebaseRecyclerOptions<client_model_todisplayitemsavailable> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholderfordisplaying holder, int position, @NonNull client_model_todisplayitemsavailable model) {
        holder.nm.setText(model.getName());
    holder.pri.setText(model.getPrice());
    Glide.with(holder.img1.getContext()).load(model.getUrl()).into(holder.img1);
    holder.weight.setText(model.getWeight());


        holder.btnedititems.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppCompatActivity appCompatActivity=(AppCompatActivity) view.getContext();
            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper4,new client_itemeditbuttonpressed_fragment(model.name)).addToBackStack(null).commit();
        }
    });

    }



    @NonNull
    @Override
    public myviewholderfordisplaying onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_itemdisplayingforclient,parent,false);
        return new myviewholderfordisplaying(view);
    }


    public class myviewholderfordisplaying extends RecyclerView.ViewHolder{
        ImageView img1;
        TextView nm,pri,weight;
        Button btnedititems;
        //CardView cardView;


        public myviewholderfordisplaying(@NonNull View itemView) {
            super(itemView);
            img1=itemView.findViewById(R.id.imageViewfordisplayingitems);
            nm=itemView.findViewById(R.id.namevalueforitem);
            pri=itemView.findViewById(R.id.pricevalueforitem);
            weight=itemView.findViewById(R.id.weightvalueforitem);
            btnedititems=itemView.findViewById(R.id.btneditforitemdisplay);
           // cardView=itemView.findViewById(R.id.cardviewtodispitem);

        }
    }

}
