package com.example.attendancesystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


public class LectureItemAdapter extends RecyclerView.Adapter<LectureItemAdapter.ViewHolder> {
    private LectureClass[] listdata;

    Context lcon;

    // RecyclerView recyclerView;
    public LectureItemAdapter(LectureClass[] listdata, Context lcon) {
        this.listdata = listdata;
        this.lcon = lcon;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.activity_lecture_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LectureClass myListData = listdata[position];
        holder.textView.setText(listdata[position].getCourse());
        String txt = "T:" + listdata[position].getStart_time() + " - " + listdata[position].getEnd_time();
        holder.textView2.setText(txt);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "click on item: " + myListData.getCourse(), Toast.LENGTH_LONG).show();
            }
        });
        final Intent i = new Intent(lcon, openCamera.class);
        holder.linearLayout.findViewById(R.id.schedule_giveAttend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lcon.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView textView2;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.schedule_course);
            this.textView2 = (TextView) itemView.findViewById(R.id.schedule_time);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.row_1);
        }
    }
}
