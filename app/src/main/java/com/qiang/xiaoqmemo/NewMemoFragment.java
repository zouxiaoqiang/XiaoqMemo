package com.qiang.xiaoqmemo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xiaoqiang
 * @date 19-3-19
 */
public class NewMemoFragment extends Fragment implements View.OnClickListener {
    private EditText etTitle;
    private EditText etContent;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_memo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnStore = view.findViewById(R.id.btn_store);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnStore.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etTitle = view.findViewById(R.id.et_title);
        etContent = view.findViewById(R.id.et_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_store:
                storeNewMemo();
                break;
            case R.id.btn_cancel:
                destroy();
                break;
            default:
        }
    }

    private void storeNewMemo() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        Date d = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String date = format.format(d);
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("date", date);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getContentResolver().insert(MemoContentProvider.CONTENT_URI, values);
            mainActivity.notifyDataSetChanged();
        }
        destroy();
    }

    private void destroy() {
        FragmentTransaction ft;
        if (getFragmentManager() != null) {
            ft = getFragmentManager().beginTransaction();
            ft.remove(this);
            ft.commit();
        }
    }
}
