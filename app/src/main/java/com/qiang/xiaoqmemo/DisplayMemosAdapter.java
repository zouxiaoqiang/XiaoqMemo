package com.qiang.xiaoqmemo;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * @author xiaoqiang
 * @date 19-3-18
 */
public class DisplayMemosAdapter extends RecyclerView.Adapter<DisplayMemosAdapter.ViewHolder> {
    private Context mContext;
    private List<Memo> mData;

    DisplayMemosAdapter(Context context, List<Memo> data) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_display_memo, viewGroup, false);
        view.setOnClickListener(v -> {
            View vv = View.inflate(mContext, R.layout.item_detail_memo, null);
            int pos = (int) view.getTag();
            TextView tvTitle = vv.findViewById(R.id.tv_title);
            TextView tvDate = vv.findViewById(R.id.tv_date);
            TextView tvContent = vv.findViewById(R.id.tv_content);
            Memo memo = mData.get(pos);
            tvTitle.setText(memo.getTitle());
            tvDate.setText(memo.getDate());
            tvContent.setText(memo.getContent());
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setView(vv)
                    .setCancelable(true)
                    .show();
            ImageButton imgBtnDelete = vv.findViewById(R.id.img_btn_delete);
            imgBtnDelete.setOnClickListener(v1 -> {
                String selection = "date=? and title=?";
                String[] selectionArgs =
                        new String[]{memo.getDate(), memo.getTitle()};
                MainActivity mainActivity = (MainActivity) mContext;
                int row = mainActivity.getContentResolver()
                        .delete(MemoContentProvider.CONTENT_URI, selection, selectionArgs);
                Log.d(MainActivity.TAG, "" + row);
                mainActivity.notifyDataSetChanged();
                dialog.cancel();
            });
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvTitle.setText(mData.get(i).getTitle());
        viewHolder.tvDate.setText(mData.get(i).getDate());
        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
