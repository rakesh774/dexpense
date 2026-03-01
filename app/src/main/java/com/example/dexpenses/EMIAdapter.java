package com.example.dexpenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EMIAdapter extends RecyclerView.Adapter<EMIAdapter.ViewHolder> {

    private List<EMI> emiList;
    private OnEMIClickListener listener;

    public interface OnEMIClickListener {
        void onEdit(EMI emi);
        void onDelete(EMI emi);
    }

    public EMIAdapter(List<EMI> emiList, OnEMIClickListener listener) {
        this.emiList = emiList;
        this.listener = listener;
    }

    public void setEMIs(List<EMI> emis) {
        this.emiList = emis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EMI emi = emiList.get(position);
        holder.tvName.setText(emi.getName());
        holder.tvAmount.setText("₹" + String.format("%.0f", emi.getAmount()));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(emi));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(emi));
    }

    @Override
    public int getItemCount() {
        return emiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEMIName);
            tvAmount = itemView.findViewById(R.id.tvEMIAmount);
            btnEdit = itemView.findViewById(R.id.btnEditEMI);
            btnDelete = itemView.findViewById(R.id.btnDeleteEMI);
        }
    }
}
