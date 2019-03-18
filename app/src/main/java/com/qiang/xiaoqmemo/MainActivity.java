package com.qiang.xiaoqmemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * @author xiaoqiang
 * @date 19-3-18
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String URL_DOWNLOAD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rvDisplayMemo = findViewById(R.id.rv_display_memo);
        TextView tvCreateNew = findViewById(R.id.tv_create_new);
        tvCreateNew.setOnClickListener(this);
        TextView tvDownload = findViewById(R.id.tv_download);
        tvDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_new:
            case R.id.tv_download:
                downloadMemo();
            default:
        }
    }

    private void downloadMemo() {

    }
}
