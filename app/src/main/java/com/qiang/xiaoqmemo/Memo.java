package com.qiang.xiaoqmemo;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 备忘录内容实体类
 * @author xiaoqiang
 * @date 19-3-18
 */
public class Memo implements Comparable<Memo> {
    /**
     * 创建日期 yyyy/MM/dd HH:mm
     */
    private String date;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(Memo o) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            Date date1 = format.parse(date);
            Date date2 = format.parse(o.getDate());
            if (date1.after(date2)) {
                return -1;
            } else if (date1.before(date2)) {
                return 1;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
