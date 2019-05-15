package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.Model.Addresses;
import com.example.yummy.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {
    private Context context;
    private List<Addresses> addressesList;

    public AddressAdapter(Context context, List<Addresses> addressesList){
        this.context = context;
        this.addressesList = addressesList;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_account_address, parent, false);
        return new AddressHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, int i) {
        Addresses addresses = addressesList.get(i);

        holder.tvAddress.setText(addresses.getName());
        holder.btnDelete.setOnClickListener(v->{

        });
    }

    @Override
    public int getItemCount() {
        return addressesList != null? addressesList.size():0;
    }

    class AddressHolder extends RecyclerView.ViewHolder {
        TextView tvAddress;
        ImageView btnDelete;

        AddressHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
