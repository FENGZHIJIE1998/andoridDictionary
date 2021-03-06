package com.example.myfinalwork.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfinalwork.R;
import com.example.myfinalwork.adapter.AutoAssociationsAdapter;
import com.example.myfinalwork.dao.HistorySearchDao;
import com.example.myfinalwork.glide.GlideImageLoader;
import com.example.myfinalwork.response.TranslationTextResponse;
import com.example.myfinalwork.response.child.Web;
import com.example.myfinalwork.retrofit.PostRequestInterface;

import com.example.myfinalwork.retrofit.RetrofitFactory;
import com.example.myfinalwork.utils.SHA256Util;
import com.example.myfinalwork.utils.TimeUtil;
import com.google.gson.Gson;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 在线翻译Fragment
 */

public class SearchFragment extends Fragment {


    private EditText searchText;
    private ListView autoAssociationsListView;
    private Button button;
    private TextView ukPhonetic;
    private TextView usPhonetic;
    private TextView explains;
    private TextView translation;
    private TextView web;
    private LinearLayout content;
    private ImageButton ukIb;
    private ImageButton usIb;
    private HistorySearchDao historySearchDao;
    private View view;
    private String from = "auto";
    private String to = "auto";
    private Spinner fromSpinner;
    private Spinner toSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchText = view.findViewById(R.id.search_text);
        autoAssociationsListView = view.findViewById(R.id.auto_associations_list_view);
        translation = view.findViewById(R.id.translation);
        ukPhonetic = view.findViewById(R.id.uk_phonetic);
        usPhonetic = view.findViewById(R.id.us_phonetic);
        explains = view.findViewById(R.id.explains);
        web = view.findViewById(R.id.web);
        content = view.findViewById(R.id.content);
        ukIb = view.findViewById(R.id.uk_ib);
        usIb = view.findViewById(R.id.us_ib);
        button = view.findViewById(R.id.search_btn);
        fromSpinner = view.findViewById(R.id.from);
        toSpinner = view.findViewById(R.id.to);
        historySearchDao = new HistorySearchDao(view.getContext(), "history_word", null, 2);


        // 设置 下拉框
        String[] ctype = new String[]{"自动", "中文", "英文", "日文", "韩文", "法文"};
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, ctype);
        fromSpinner.setAdapter(fromAdapter);

        fromSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String str = fromAdapter.getItem(position);
                        if (str == null) {
                            Toast.makeText(view.getContext(), "出错啦", Toast.LENGTH_LONG).show();
                        }
                        switch (str) {
                            case "自动":
                                from = "auto";
                                break;
                            case "中文":
                                from = "zh-CHS";
                                break;
                            case "英文":
                                from = "en";
                                break;
                            case "日文":
                                from = "ja";
                                break;
                            case "韩文":
                                from = "ko";
                                break;
                            case "法文":
                                from = "fr";
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + str);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        from = "auto";
                    }
                }
        );

        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, ctype);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String str = toAdapter.getItem(position);
                        if (str == null || "".equals(str)) {
                            Toast.makeText(view.getContext(), "出错啦", Toast.LENGTH_LONG).show();
                        }
                        switch (str) {
                            case "自动":
                                to = "auto";
                                break;
                            case "中文":
                                to = "zh-CHS";
                                break;
                            case "英文":
                                to = "en";
                                break;
                            case "日文":
                                to = "ja";
                                break;
                            case "韩文":
                                to = "ko";
                                break;
                            case "法文":
                                to = "fr";
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + str);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        to = "auto";
                    }
                }
        );


        //绑定点击事件
        button.setOnClickListener(v -> {
            Editable text = searchText.getText();
            if ("".contentEquals(text)) {
                Toast.makeText(view.getContext(), "请输入要查询的词", Toast.LENGTH_LONG).show();
                return;
            }
            SQLiteDatabase db = historySearchDao.getWritableDatabase();
            // 将搜索词插入历史记录库
            try {
                db.execSQL("insert into history_word(id, word) values(null, ?) ", new String[]{text.toString()});
            } catch (SQLiteConstraintException exception) {
                Log.d("warming", "重复插入");
            }
            db.close();
            // 将listView 设置为不可见同时清空item
            autoAssociationsListView.setVisibility(View.INVISIBLE);
            autoAssociationsListView.setAdapter(null);
            new SearchTask().execute();
        });
        // 绑定文本变化事件
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             *  文本变化后联想
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final List<String> res = new ArrayList<>();
                String search = s.toString();
                // 如果输入不为空
                if (!search.equals("")) {
                    SQLiteDatabase db = historySearchDao.getReadableDatabase();
                    // 模糊查询 五条数据
                    Cursor cursor = db.rawQuery("select * from history_word where word like ? order by word limit 5 ", new String[]{search + "%"});
                    if (cursor.moveToFirst()) {
                        do {
                            res.add(cursor.getString(cursor.getColumnIndex("word")));
                        } while (cursor.moveToNext());
                    }
                    // 设置适配器
                    ListAdapter listAdapter = new AutoAssociationsAdapter(view.getContext(), R.layout.auto_associations, res);
                    autoAssociationsListView.setAdapter(listAdapter);
                    autoAssociationsListView.setOnItemClickListener((parent, view, position, id) -> {
                        //设置 searchText
                        searchText.setText(res.get(position));
                    });
                    cursor.close();
                    db.close();
                }
                // 设置listView 可见
                if (res.size() > 0) {
                    autoAssociationsListView.setVisibility(View.VISIBLE);
                } else {
                    //清空listView
                    autoAssociationsListView.setAdapter(null);
                    autoAssociationsListView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //设置轮播图
        List<Integer> images = new ArrayList<>();
        images.add(R.mipmap.lunbo1);
        images.add(R.mipmap.lunbo2);
        images.add(R.mipmap.lunbo3);
        images.add(R.mipmap.lunbo4);
        Banner banner = view.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        return view;
    }


    /**
     * 异步任务
     */
    class SearchTask extends AsyncTask<Void, Integer, Boolean> {

        private TranslationTextResponse resp;

        /**
         * 发送请求
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            sendRequestWithOkHttp();
            return true;
        }

        /**
         * 更新UI
         */
        @Override
        protected void onPostExecute(Boolean result) {
            String empty = "";
            ukPhonetic.setText(empty);
            usPhonetic.setText(empty);
            translation.setText(empty);
            web.setText(empty);
            explains.setText(empty);

            // 判断错误
            if (resp != null && !resp.getErrorCode().equals("0")) {
                content.setVisibility(View.INVISIBLE);
                Toast.makeText(view.getContext(), "输入错误", Toast.LENGTH_LONG).show();
                return;
            }
            // 更新 translation
            StringBuilder tran = new StringBuilder();
            for (String str : resp.getTranslation()) {
                tran.append(str).append("\n");
            }
            // 除去最后一个 \n 换行
            tran.setLength(tran.length() - 1);
            translation.setText(tran.toString());

            if (resp.getBasic() != null) {
                if (resp.getBasic().getPhonetic() != null) {
                    ukPhonetic.setText(resp.getBasic().getPhonetic());
                    ukIb.setVisibility(View.VISIBLE);
                    ukIb.setOnClickListener(v -> speak(resp.getTSpeakUrl()));
                }
                if (resp.getBasic().getUkPhonetic() != null) {
                    ukPhonetic.setText(resp.getBasic().getUkPhonetic());
                    ukIb.setVisibility(View.VISIBLE);
                    ukIb.setOnClickListener(v -> speak(resp.getBasic().getUkSpeech()));
                }
                if (resp.getBasic().getUsPhonetic() != null) {
                    usPhonetic.setText(resp.getBasic().getUsPhonetic());
                    usIb.setVisibility(View.VISIBLE);
                    usIb.setOnClickListener(v -> speak(resp.getBasic().getUsSpeech()));
                }
                StringBuilder explain = new StringBuilder();
                for (String str : resp.getBasic().getExplains()) {
                    explain.append(str).append("\n");
                }
                explain.setLength(explain.length() - 1);
                explains.setText(explain.toString());
            }
            if (resp.getWeb() != null) {
                StringBuilder webs = new StringBuilder();
                for (Web web : resp.getWeb()) {
                    for (String value : web.getValue()) {
                        webs.append(value).append(",");
                    }
                    webs.setLength(webs.length() - 1);
                    webs.append("\n");
                }
                webs.setLength(webs.length() - 1);
                web.setText(webs.toString());
            }
            content.setVisibility(View.VISIBLE);
        }


        private final static String APP_ID = "0cc942aae8ef010c";
        private final static String APP_KEY = "sBaPGIUEoBJwXQbKero8qvpWeHtHIGqB";

        /**
         * 发送请求
         */
        private void sendRequestWithOkHttp() {
            try {
                PostRequestInterface request = RetrofitFactory.create(PostRequestInterface.class);
                Editable text = searchText.getText();
                String salt = UUID.randomUUID().toString();
                String curtime = TimeUtil.getUTCTimeStr();
                String sign = APP_ID + truncate(text.toString()) + salt + curtime + APP_KEY;
                Map<String, String> map = new HashMap<>();
                map.put("q", text.toString());
                map.put("from", from);
                map.put("to", to);
                map.put("appKey", APP_ID);
                map.put("salt", salt);
                map.put("sign", SHA256Util.encrypt(sign));
                map.put("signType", "v3");
                map.put("curtime", curtime);
                System.out.println(map);

                //对 发送请求 进行封装
                Call<TranslationTextResponse> call = request.translationText(map);
                Response<TranslationTextResponse> response = call.execute();

                resp = response.body();
                //String responseData = "{'tSpeakUrl':'http://openapi.youdao.com/ttsapi?q=%E7%88%B1&langType=zh-CHS&sign=9AD075ECDC579C44437BF625575FA840&salt=1588761973726&voice=4&format=mp3&appKey=0cc942aae8ef010c','returnPhrase':['love'],'RequestId':'887b40bb-193f-466f-af60-fa1f75cb8629','web':[{'value':['爱','爱情','爱心','恋爱'],'key':'love'},{'value':['浦岛景太郎','淳情房主俏佃农'],'key':'Love Hina'},{'value':['柏拉图式恋爱','柏拉图式爱情','精神恋爱','柏拉图式的爱情'],'key':'platonic love'}],'query':'love','translation':['爱'],'errorCode':'0','dict':{'url':'yddict://m.youdao.com/dict?le=eng&q=love'},'webdict':{'url':'http://m.youdao.com/dict?le=eng&q=love'},'basic':{'exam_type':['高中','初中'],'us-phonetic':'lʌv','phonetic':'lʌv','uk-phonetic':'lʌv','wfs':[{'wf':{'name':'过去式','value':'loved'}},{'wf':{'name':'过去分词','value':'loved'}},{'wf':{'name':'现在分词','value':'loving'}},{'wf':{'name':'复数','value':'loves'}},{'wf':{'name':'第三人称单数','value':'loves'}}],'uk-speech':'http://openapi.youdao.com/ttsapi?q=love&langType=en&sign=A210584A844999644572BD8E4D228A6D&salt=1588761973726&voice=5&format=mp3&appKey=0cc942aae8ef010c','explains':['n. 爱；爱情；喜好；（昵称）亲爱的；爱你的；心爱的人；钟爱之物；零分','v. 爱恋（某人）；关爱；喜欢（某物或某事）；忠于','n. (Love) （英、菲、瑞、美）洛夫（人名）'],'us-speech':'http://openapi.youdao.com/ttsapi?q=love&langType=en&sign=A210584A844999644572BD8E4D228A6D&salt=1588761973726&voice=6&format=mp3&appKey=0cc942aae8ef010c'},'l':'en2zh-CHS','speakUrl':'http://openapi.youdao.com/ttsapi?q=love&langType=en&sign=A210584A844999644572BD8E4D228A6D&salt=1588761973726&voice=4&format=mp3&appKey=0cc942aae8ef010c'}";

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private String truncate(String q) {
            if (q == null) {
                return null;
            }
            int len = q.length();
            return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
        }


    }

    /**
     * 播放发音
     *
     * @param tSpeakUrl
     */
    private void speak(String tSpeakUrl) {
        System.out.println(tSpeakUrl);
    }
}
