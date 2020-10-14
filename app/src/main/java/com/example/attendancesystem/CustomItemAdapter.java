package com.example.attendancesystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomItemAdapter extends ArrayAdapter<SubjectsClass> {
    private static final String TAG = "SubjectArrayAdapter";
    private List<SubjectsClass> subjList = new ArrayList<>();

    public CustomItemAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(SubjectsClass object) {
        subjList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.subjList.size();
    }

    @Override
    public SubjectsClass getItem(int index) {
        return this.subjList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FruitViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_row_layout, parent, false);
            viewHolder = new FruitViewHolder();
            viewHolder.sapid = (TextView) row.findViewById(R.id.tv1);
            viewHolder.course = (TextView) row.findViewById(R.id.tv2);
            viewHolder.subject = (TextView) row.findViewById(R.id.tv3);
            viewHolder.teacher = (TextView) row.findViewById(R.id.tv4);
            row.setTag(viewHolder);
        } else {
            viewHolder = (FruitViewHolder) row.getTag();
        }
        SubjectsClass subj = getItem(position);
        viewHolder.sapid.setText(subj.getSapid());
        viewHolder.course.setText(subj.getCourse());
        viewHolder.subject.setText(subj.getSubject());
        viewHolder.teacher.setText(subj.getTeacher());
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    static class FruitViewHolder {
        TextView sapid;
        TextView course;
        TextView subject;
        TextView teacher;
    }
}