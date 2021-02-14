package com.example.attendancesystem;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "StudentTable";
    Context contx;
    ArrayList<StudentClass> sal = new ArrayList<>();

    public RecyclerAdapter(Context contx, ArrayList<StudentClass> sal) {
        this.contx = contx;
        //get Data from firestore
        this.sal = sal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
        String userid;

        holder.sapid.setText(sal.get(position).getSapid());
        holder.name.setText(sal.get(position).getName());
        holder.verify.setText(sal.get(position).getIsVerified());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(contx, verifyStudent.class);
                i.putExtra("name", sal.get(position).getName());
                i.putExtra("sapid", sal.get(position).getSapid());
                i.putExtra("course", "MCA");
                contx.startActivity(i);
            }
        });
    }

    @Override
//    public int getItemCount() {
//        return 0;
//    }

    public int getItemCount() {
        return sal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sapid;
        TextView name;
        TextView course;
        TextView classs;
        TextView verify;
        TextView btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sapid = (TextView) itemView.findViewById(R.id.t1);
            name = (TextView) itemView.findViewById(R.id.t2);
            course = (TextView) itemView.findViewById(R.id.t3);
            classs = (TextView) itemView.findViewById(R.id.t4);
            verify = (TextView) itemView.findViewById(R.id.t5);
            btn = (TextView) itemView.findViewById(R.id.t6);

        }
    }
}