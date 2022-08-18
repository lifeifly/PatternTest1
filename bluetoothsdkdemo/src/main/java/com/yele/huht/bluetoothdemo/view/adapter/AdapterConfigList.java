package com.yele.huht.bluetoothdemo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yele.baseapp.utils.StringUtils;
import com.yele.huht.bluetoothdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterConfigList extends RecyclerView.Adapter<AdapterConfigList.ViewHolder> {

    private Context mContext;
    private List<String> mList = new ArrayList<>();

    public AdapterConfigList(Context context, List<String> mList){
        this.mContext = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_select_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String cmd = mList.get(position);
        if(StringUtils.isEmpty(cmd)){
            holder.llBg.setVisibility(View.GONE);
        }else {
            holder.llBg.setVisibility(View.VISIBLE);
        }
        holder.tvSelectItem.setText(mList.get(position));
        holder.tvResult.setText(String.format(Locale.US,"%d", position));
        holder.llBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clickDev(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSelectItem;
        TextView tvResult;
        LinearLayout llBg;
        public ViewHolder(View view) {
            super(view);
            tvSelectItem = view.findViewById(R.id.tv_select_item);
            tvResult = view.findViewById(R.id.tv_result);
            llBg = view.findViewById(R.id.ll_bg);
        }
    }

    private OnDevListener listener;
    public void setOnDevListener(OnDevListener l){
        this.listener = l;
    }
    public interface OnDevListener{
        void clickDev(int id);
    }

}
