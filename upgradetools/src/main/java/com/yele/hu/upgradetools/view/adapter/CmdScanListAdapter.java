package com.yele.hu.upgradetools.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yele.hu.upgradetools.R;
import com.yele.hu.upgradetools.bean.info.car.OkaiBleDevice;

import java.util.ArrayList;
import java.util.List;

public class CmdScanListAdapter extends RecyclerView.Adapter<CmdScanListAdapter.ViewHolder> {

    private Context mContext;
    private List<OkaiBleDevice> mList = new ArrayList<>();

    public CmdScanListAdapter(Context context, List<OkaiBleDevice> mList){
        this.mContext = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_device_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final OkaiBleDevice device = mList.get(position);

        holder.tvSupplier.setText(device.device.getName());
        holder.tvMaterialNumber.setText(device.device.getAddress());
        holder.tv_type.setText(device.type);
        holder.tv_sn.setText(device.sn);
        holder.llBg.setOnClickListener(v -> listener.clickDev(position,device));
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
        TextView tvSupplier;
        TextView tvMaterialNumber;
        LinearLayout llBg;
        TextView tv_type,tv_sn;

        public ViewHolder(View view) {
            super(view);
            tvSupplier = view.findViewById(R.id.tv_supplier);
            tvMaterialNumber = view.findViewById(R.id.tv_material_number);
            llBg = view.findViewById(R.id.ll_bg);
            tv_type = view.findViewById(R.id.tv_type);
            tv_sn = view.findViewById(R.id.tv_sn);
        }
    }

    private OnDevListener listener;
    public void setOnDevListener(OnDevListener l){
        this.listener = l;
    }
    public interface OnDevListener{
        void clickDev(int id,OkaiBleDevice device);
    }

}
