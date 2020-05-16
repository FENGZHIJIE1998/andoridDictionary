package com.example.myfinalwork.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfinalwork.R;

import java.util.List;

/**
 * 历史记录适配器
 */
public class HistoryListAdapter extends ArrayAdapter<String> {
    private int resourceId;

    public HistoryListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String word = getItem(position);

        HistoryListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new HistoryListAdapter.ViewHolder();
            TextView textView = convertView.findViewById(R.id.history);
            viewHolder.textView = textView;
            convertView.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            viewHolder = (HistoryListAdapter.ViewHolder) convertView.getTag(); // 重新获取ViewHolder
        }
        viewHolder.textView.setText(word);
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}

