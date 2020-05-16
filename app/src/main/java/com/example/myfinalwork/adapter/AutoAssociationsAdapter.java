package com.example.myfinalwork.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfinalwork.R;
import com.example.myfinalwork.entity.HistorySearch;

import java.util.List;

/**
 * 自动联想适配器
 */
public class AutoAssociationsAdapter extends ArrayAdapter<String> {
    private int resourceId;

    public AutoAssociationsAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String word = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            TextView textView = convertView.findViewById(R.id.auto_associations);
            viewHolder.textView = textView;
            convertView.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            viewHolder = (ViewHolder) convertView.getTag(); // 重新获取ViewHolder
        }
        viewHolder.textView.setText(word);
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}
