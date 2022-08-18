package com.yele.hu.blesdk520demo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yele.hu.blesdk520demo.R;
import com.yele.hu.blesdk520demo.bean.AutoCmdState;

import java.util.List;

public class AdapterCmdBtn extends RecyclerView.Adapter<AdapterCmdBtn.ViewHolder> {

    private Context context;
    private List<AutoCmdState> mList;

    public AdapterCmdBtn(Context context, List<AutoCmdState> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AutoCmdState autoCmdState = mList.get(position);
        holder.tvSelectItem.setText(autoCmdState.cmdName);

        if(listener != null){
            holder.llBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    listener.clickCmd(position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSelectItem;
        LinearLayout llBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSelectItem = itemView.findViewById(R.id.tv_select_item);
            llBg = itemView.findViewById(R.id.ll_bg);
        }
    }

    private OnCmdListener listener;
    public void setOnCmdListener(OnCmdListener l){
        this.listener = l;
    }
    public interface OnCmdListener{
        void clickCmd(int position);
    }
}
