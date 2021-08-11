package com.example.bandungzoochatbot;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private final Context context;
    List<Brainfile> brainfileList;
    LayoutInflater inflter;

    public ListViewAdapter(Context mContext, List<Brainfile> brainfileList) {
        this.context = mContext;
        this.brainfileList = brainfileList;
        inflter = (LayoutInflater.from(mContext));
    }

    @Override
    public int getCount() {
        return brainfileList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = inflter.inflate(R.layout.list_items, null);

        TextView pertanyaan = convertView.findViewById(R.id.pertanyaan);
        TextView jawaban = convertView.findViewById(R.id.jawaban);

        Brainfile brainfile = brainfileList.get(position);

        Log.d("message", String.valueOf(brainfile));

        pertanyaan.setText(brainfile.getPertanyaan());
        jawaban.setText(brainfile.getJawaban());

        return convertView;
    }
}
