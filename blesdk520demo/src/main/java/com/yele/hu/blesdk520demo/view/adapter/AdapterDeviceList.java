package com.yele.hu.blesdk520demo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yele.blesdklibrary.bean.OkaiBleDevice;
import com.yele.hu.blesdk520demo.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterDeviceList extends RecyclerView.Adapter<AdapterDeviceList.ViewHolder> {

    private Context mContext;
    private List<OkaiBleDevice> mList = new ArrayList<>();

    public AdapterDeviceList(Context context, List<OkaiBleDevice> mList){
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
        holder.tvMaterialNumber.setText(device.sn);

        holder.llType.setVisibility(View.VISIBLE);
        holder.tvType.setText(mList.get(position).type);

        holder.llBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.clickDev(mList.get(position));
                }
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
        TextView tvSupplier;
        TextView tvMaterialNumber;
        LinearLayout llBg;
        LinearLayout llType;
        TextView tvType;
        public ViewHolder(View view) {
            super(view);
            tvSupplier = view.findViewById(R.id.tv_supplier);
            tvMaterialNumber = view.findViewById(R.id.tv_material_number);
            llBg = view.findViewById(R.id.ll_bg);
            llType = view.findViewById(R.id.ll_type);
            tvType = view.findViewById(R.id.tv_type);
        }
    }

    private OnDevListener listener;
    public void setOnDevListener(OnDevListener l){
        this.listener = l;
    }
    public interface OnDevListener{
        void clickDev(OkaiBleDevice bleDevice);
    }

}
