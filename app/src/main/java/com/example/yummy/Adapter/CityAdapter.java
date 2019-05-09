package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.yummy.Activity.RestaurantActivity;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import java.util.HashMap;
import java.util.Objects;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityHolder> implements Filterable {
    private Context context;
    private HashMap<String, Integer> data;
    private HashMap<String, Integer> dataFilter;
    private String query = "";
    private int prevSelection = -1;

    public CityAdapter (Context context, HashMap<String, Integer> data){
        this.context = context;
        this.data = data;
        dataFilter = data;
    }

    @NonNull
    @Override
    public CityAdapter.CityHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_city, parent, false);
        return new CityHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CityAdapter.CityHolder holder, @SuppressLint("RecyclerView") int i) {
        if(dataFilter.keySet().toArray() != null) {
            String key = (String) Objects.requireNonNull(dataFilter.keySet().toArray())[i];

            if (Common.myAddress.equals(key)) {
                holder.checkBox.setChecked(true);
                prevSelection = i;
            } else {
                holder.checkBox.setChecked(false);
            }

            holder.tvCityName.setText(key);
            holder.tvCount.setText(dataFilter.get(key) + " places");

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    holder.checkBox.setChecked(true);
                    Common.myAddress = key;
                    if (prevSelection >= 0) {
                        notifyItemChanged(prevSelection);
                    }
                    prevSelection = i;
                } else {
                    holder.checkBox.setChecked(false);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataFilter != null? dataFilter.size():0;
    }

    class CityHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvCityName, tvCount;
        CityHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox_city);
            tvCityName = itemView.findViewById(R.id.tv_nameCity);
            tvCount = itemView.findViewById(R.id.tv_countCity);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                query = charSequence.toString();
                if (!query.isEmpty()) {
                    HashMap<String, Integer> filteredList = new HashMap<>();
                    for (String key : data.keySet()) {
                        if (key.toLowerCase().contains(query.toLowerCase())) {
                            filteredList.put(key, data.get(key));
                        }
                    }

                    dataFilter = filteredList;

                } else {
                    dataFilter = data;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataFilter = (HashMap<String, Integer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
