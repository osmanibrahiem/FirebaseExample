package com.osman.firebaseexample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private Context context;
    private List<Data> dataList;
    private LayoutInflater inflater;

    public DataAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dataList = new ArrayList<>();
    }

    public void addData(Data item) {
        dataList.add(item);
        notifyItemInserted(dataList.size());
    }

    public void updateData(Data item) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(item.getId())) {
                dataList.set(i, item);
                notifyItemChanged(i);
            }
        }
    }

    public void removeData(Data item) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(item.getId())) {
                dataList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, getItemCount());
            }
        }
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_data_item, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Data data = dataList.get(position);
        holder.setData(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView desc;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
        }

        void setData(Data data) {
            title.setText(data.getTitle());
            desc.setText(data.getDesc());
        }
    }
}
