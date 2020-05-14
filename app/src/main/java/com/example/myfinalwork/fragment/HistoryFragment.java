package com.example.myfinalwork.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myfinalwork.R;
import com.example.myfinalwork.adapter.HistoryListAdapter;
import com.example.myfinalwork.dao.HistorySearchDao;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private HistorySearchDao historySearchDao;
    private ListView historyListView;
    private View view;
    private View mainView;

    private TextView searchText;
    private Button searchBtn;
    private ViewPager viewPage;
    private List<String> res;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        historySearchDao = new HistorySearchDao(view.getContext(), "history_word", null, 2);
        historyListView = view.findViewById(R.id.history_list_view);
        searchText = getActivity().findViewById(R.id.search_text);
        searchBtn = getActivity().findViewById(R.id.search_btn);
        viewPage = getActivity().findViewById(R.id.view_pager);
        listHistory();
        return view;
    }

    private void listHistory() {
        SQLiteDatabase db = historySearchDao.getReadableDatabase();
        res = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from history_word order by word", null);
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndex("word"));
                res.add(word);
            } while (cursor.moveToNext());
        }
        if (res.size() < 1) {
            res.add("暂无历史记录");
        }

        // 设置适配器
        ListAdapter listAdapter = new HistoryListAdapter(view.getContext(), R.layout.history, res);
        historyListView.setAdapter(listAdapter);

        historyListView.setOnItemClickListener((parent, view, position, id) -> {
            String word = res.get(position);
            searchText.setText(word);
            searchBtn.callOnClick();
            viewPage.setCurrentItem(0);
        });
        cursor.close();
        db.close();
    }


    @Override
    public void onResume() {
        super.onResume();
        listHistory();
    }
}
