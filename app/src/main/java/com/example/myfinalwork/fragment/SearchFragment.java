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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfinalwork.R;
import com.example.myfinalwork.adapter.AutoAssociationsAdapter;
import com.example.myfinalwork.dao.HistorySearchDao;
import com.example.myfinalwork.glide.GlideImageLoader;
import com.example.myfinalwork.response.TranslationResponse;
import com.example.myfinalwork.response.child.Web;
import com.example.myfinalwork.utils.Sha256Util;
import com.example.myfinalwork.utils.TimeUtil;
import com.google.gson.Gson;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        historySearchDao = new HistorySearchDao(view.getContext(), "history_word", null, 2);

        //绑定点击事件
        button.setOnClickListener(v -> {
            Editable text = searchText.getText();

            SQLiteDatabase db = historySearchDao.getWritableDatabase();
            if ("".contentEquals(text)) {
                Toast.makeText(view.getContext(), "请输入要查询的词", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                db.execSQL("insert into history_word(id,word) values(null,?) ", new String[]{text.toString()});
            } catch (SQLiteConstraintException exception) {
                Log.d("warming", "重复插入");
            }
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

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SQLiteDatabase db = historySearchDao.getReadableDatabase();
                final List<String> res = new ArrayList<>();
                String search = s.toString();
                // 如果输入不为空
                if (!search.equals("")) {
                    // 模糊查询
                    Cursor cursor = db.rawQuery("select * from history_word where word like ? order by word limit 5 ", new String[]{search + "%"});
                    if (cursor.moveToFirst()) {
                        do {
                            String word = cursor.getString(cursor.getColumnIndex("word"));
                            res.add(word);
                        } while (cursor.moveToNext());
                    }
                    // 设置适配器
                    ListAdapter listAdapter = new AutoAssociationsAdapter(view.getContext(), R.layout.auto_associations, res);
                    autoAssociationsListView.setAdapter(listAdapter);
                    autoAssociationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String word = res.get(position);
                            searchText.setText(word);
                        }
                    });
                    cursor.close();
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
        List<Integer> images = new ArrayList<Integer>();
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

            private TranslationResponse resp;

            /**
             * 发送请求
             *
             * @param params
             * @return
             */
            @Override
            protected Boolean doInBackground(Void... params) {
                resp = sendRequestWithOkHttp();
                return true;
            }

            /**
             * 更新UI
             *
             * @param result
             */
            @Override
            protected void onPostExecute(Boolean result) {
                if (resp != null && !resp.getErrorCode().equals("0")) {
                    content.setVisibility(View.INVISIBLE);
                    Toast.makeText(view.getContext(), "输入错误", Toast.LENGTH_LONG).show();
                    return;
                }

                StringBuilder tran = new StringBuilder();
                for (String str : resp.getTranslation()) {
                    tran.append(str).append("\n");
                }
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
        private final static String APP_KEY = "HYZ8DP9glQPtOOhfoONYZdc4luEO9DU7";

        private TranslationResponse sendRequestWithOkHttp() {

            try {
                OkHttpClient client = new OkHttpClient();
                Editable text = searchText.getText();
                String url = "https://openapi.youdao.com/api";
                String salt = UUID.randomUUID().toString();

                String curtime = TimeUtil.getUTCTimeStr();
                String sign = APP_ID + text.toString() + salt + curtime + APP_KEY;
                RequestBody requestBody = new FormBody.Builder()
                        .add("q", text.toString())
                        .add("form", "auto")
                        .add("to", "auto")
                        .add("appKey", "0cc942aae8ef010c")
                        .add("salt", salt)
                        .add("sign", Sha256Util.encrypt(sign))
                        .add("signType", "v3")
                        .add("curtime", curtime)
                        .build();

                Request request = new Request.Builder().url(url).post(requestBody).build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                //String responseData = "{'tSpeakUrl':'http://openapi.youdao.com/ttsapi?q=%E7%88%B1&langType=zh-CHS&sign=9AD075ECDC579C44437BF625575FA840&salt=1588761973726&voice=4&format=mp3&appKey=0cc942aae8ef010c','returnPhrase':['love'],'RequestId':'887b40bb-193f-466f-af60-fa1f75cb8629','web':[{'value':['爱','爱情','爱心','恋爱'],'key':'love'},{'value':['浦岛景太郎','淳情房主俏佃农'],'key':'Love Hina'},{'value':['柏拉图式恋爱','柏拉图式爱情','精神恋爱','柏拉图式的爱情'],'key':'platonic love'}],'query':'love','translation':['爱'],'errorCode':'0','dict':{'url':'yddict://m.youdao.com/dict?le=eng&q=love'},'webdict':{'url':'http://m.youdao.com/dict?le=eng&q=love'},'basic':{'exam_type':['高中','初中'],'us-phonetic':'lʌv','phonetic':'lʌv','uk-phonetic':'lʌv','wfs':[{'wf':{'name':'过去式','value':'loved'}},{'wf':{'name':'过去分词','value':'loved'}},{'wf':{'name':'现在分词','value':'loving'}},{'wf':{'name':'复数','value':'loves'}},{'wf':{'name':'第三人称单数','value':'loves'}}],'uk-speech':'http://openapi.youdao.com/ttsapi?q=love&langType=en&sign=A210584A844999644572BD8E4D228A6D&salt=1588761973726&voice=5&format=mp3&appKey=0cc942aae8ef010c','explains':['n. 爱；爱情；喜好；（昵称）亲爱的；爱你的；心爱的人；钟爱之物；零分','v. 爱恋（某人）；关爱；喜欢（某物或某事）；忠于','n. (Love) （英、菲、瑞、美）洛夫（人名）'],'us-speech':'http://openapi.youdao.com/ttsapi?q=love&langType=en&sign=A210584A844999644572BD8E4D228A6D&salt=1588761973726&voice=6&format=mp3&appKey=0cc942aae8ef010c'},'l':'en2zh-CHS','speakUrl':'http://openapi.youdao.com/ttsapi?q=love&langType=en&sign=A210584A844999644572BD8E4D228A6D&salt=1588761973726&voice=4&format=mp3&appKey=0cc942aae8ef010c'}";
                System.out.println(responseData);
                TranslationResponse resp = parseJSON(responseData);
                return resp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 解析JSON
         */
        private TranslationResponse parseJSON(String json) {
            Gson gson = new Gson();
            TranslationResponse resp = gson.fromJson(json, TranslationResponse.class);
            return resp;
        }

    }

    /**
     * 播放发音
     *
     * @param tSpeakUrl
     */
    private void speak(String tSpeakUrl) {
        System.out.println(tSpeakUrl);
//        if (tSpeakUrl != null) {
//
//            Uri uri1 = Uri.parse(tSpeakUrl);
//            try {
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                mediaPlayer.setDataSource(view, uri1);
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.prepareAsync();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        Log.e("MediaPlayer ", "开始播放");
//                        mp.start();
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}
