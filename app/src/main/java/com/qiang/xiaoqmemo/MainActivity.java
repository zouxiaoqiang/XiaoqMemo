package com.qiang.xiaoqmemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author xiaoqiang
 * @date 19-3-18
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 在当前apache服务器地址获取json数据
     * 需要自行创建web服务器和memo.json文件
     */
    private static final String URL_DOWNLOAD = "http://10.59.129.71/memo.json";

    private List<Memo> mMemoList = new ArrayList<>();

    private DisplayMemosAdapter mAdapter;

    private static final String FILE_PASSWD = "passwd";

    private static final String XML_MEMO = "memo.xml";

    public static final String TAG = "MainActivity";

    /**
     * 单线程线程池，执行耗时操作且保证mMemoList线程安全
     */
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         executorService = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10), Executors.defaultThreadFactory());
        logIn();
        initButtons();
        initRecyclerView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    private void initButtons() {
        Button btnCreateNew = findViewById(R.id.btn_create_new);
        btnCreateNew.setOnClickListener(this);
        Button btnDownload = findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);
        Button btnGenerateXML = findViewById(R.id.btn_generate_xml);
        btnGenerateXML.setOnClickListener(this);
        Button btnEditPasswd = findViewById(R.id.btn_edit_password);
        btnEditPasswd.setOnClickListener(this);
    }

    /**
     * 登录，初始密码为空
     */
    private void logIn() {
        EditText etPassword = new EditText(this);
        etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(this)
                .setTitle("输入登录密码")
                .setView(etPassword)
                .setCancelable(false)
                .setPositiveButton("登录", (dialog, which) -> {
                    SharedPreferences sp = getSharedPreferences(FILE_PASSWD, Context.MODE_PRIVATE);
                    String passwd = sp.getString("passwd", "");
                    String input = etPassword.getText().toString();
                    assert passwd != null;
                    if (!passwd.equals(input)) {
                        Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        MainActivity.this.finish();
                    }
                }).show();

    }

    private void initRecyclerView() {
        RecyclerView rvDisplayMemo = findViewById(R.id.rv_display_memo);
        rvDisplayMemo.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DisplayMemosAdapter(this, mMemoList);
        rvDisplayMemo.setAdapter(mAdapter);
    }

    /**
     * 将已经存储到磁盘的备忘录数据加载到内存
     */
    private void initData() {
        executorService.execute(() -> {
            String[] projection = new String[]{"date", "title", "content"};
            Cursor cursor = getContentResolver().query(MemoContentProvider.CONTENT_URI, projection, null, null);
            if (cursor != null) {
                mMemoList.clear();
                while (cursor.moveToNext()) {
                    Memo memo = new Memo();
                    memo.setDate(cursor.getString(0));
                    memo.setTitle(cursor.getString(1));
                    memo.setContent(cursor.getString(2));
                    mMemoList.add(memo);
                }
                cursor.close();
                runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                Collections.sort(mMemoList);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_new:
                createNew();
                break;
            case R.id.btn_download:
                downloadMemo();
                break;
            case R.id.btn_generate_xml:
                generateXml();
                break;
            case R.id.btn_edit_password:
                editPasswd();
                break;
            default:
        }
    }

    private void createNew() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        NewMemoFragment fragment = new NewMemoFragment();
        ft.replace(R.id.rl_content, fragment);
        ft.commit();
    }

    private void generateXml() {
        executorService.execute(() -> {
            try {
                XmlSerializer serializer = Xml.newSerializer();
                OutputStream os = openFileOutput(XML_MEMO, Context.MODE_PRIVATE);
                serializer.setOutput(os, "utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag("", "memos");
                for (Memo memo : mMemoList) {
                    serializer.startTag("", "memo");

                    serializer.startTag("", "date");
                    serializer.text(memo.getDate());
                    serializer.endTag("", "date");

                    serializer.startTag("", "title");
                    serializer.text(memo.getTitle());
                    serializer.endTag("", "title");

                    serializer.startTag("", "content");
                    serializer.text(memo.getContent());
                    serializer.endTag("", "content");

                    serializer.endTag("", "memo");
                }
                serializer.endTag("", "memos");
                serializer.endDocument();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void editPasswd() {
        View view = View.inflate(this, R.layout.item_edit_password, null);
        EditText etCurrentPasswd = view.findViewById(R.id.et_current_passwd);
        EditText etNewPasswd = view.findViewById(R.id.et_new_passwd);
        EditText etRepeatPasswd = view.findViewById(R.id.et_repeat_passwd);
        new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String currentPasswd = etCurrentPasswd.getText().toString();
                    String newPasswd = etNewPasswd.getText().toString();
                    String repeatPasswd = etRepeatPasswd.getText().toString();
                    SharedPreferences sp = getSharedPreferences(FILE_PASSWD, Context.MODE_PRIVATE);
                    String passwd = sp.getString("passwd", "");
                    if (!currentPasswd.equals(passwd)) {
                        Toast.makeText(this, "当前密码输入错误", Toast.LENGTH_SHORT).show();
                    } else if (!repeatPasswd.equals(newPasswd)) {
                        Toast.makeText(this, "重复密码输入错误", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("passwd", newPasswd);
                        editor.apply();
                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void downloadMemo() {
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL_DOWNLOAD)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.body() != null) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Memo memo = new Memo();
                            memo.setDate(obj.getString("date"));
                            memo.setTitle(obj.getString("title"));
                            memo.setContent(obj.getString("content"));
                            mMemoList.add(memo);
                        }
                        Collections.sort(mMemoList);
                        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void notifyDataSetChanged() {
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
