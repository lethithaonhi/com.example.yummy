package com.example.yummy.Adapter;

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

import com.example.yummy.R;
import java.util.ArrayList;
import java.util.List;

public class DistinctAdapter extends RecyclerView.Adapter<DistinctAdapter.DistinctHolder> implements Filterable {
    private Context context;
    private List<String> data;
    private List<String> dataFilter;
    private String query = "";
    private int[] check;
    private int prePos = 0;
    private String myDistinct;


    public DistinctAdapter(Context context, List<String> data){
        this.context = context;
        this.data = data;
        dataFilter = data;
        check = new int[dataFilter.size()];
        myDistinct = data.get(0);
    }

    @NonNull
    @Override
    public DistinctHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_city, parent, false);
        return new DistinctHolder(view);
    }

    public String getMyDistinct(){
        return myDistinct;
    }

    @Override
    public void onBindViewHolder(@NonNull DistinctHolder holder, int i) {
        if (dataFilter != null && i < dataFilter.size()) {
            String key = dataFilter.get(i);

            holder.tvDisName.setText(key);
            if (check[i] != 1) {
                holder.checkBox.setChecked(false);
            }else {
                holder.checkBox.setChecked(true);
            }

            holder.checkBox.setOnClickListener(v -> {
                    myDistinct = key;
                    check[prePos] = 0;
                    prePos = i;
                    check[i] = 1;
                    notifyDataSetChanged();
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataFilter != null? dataFilter.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                query = charSequence.toString();
                if (!query.isEmpty()) {
                    List<String> filteredList = new ArrayList<>();
                    for (String key : data) {
                        if (key.toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(key);
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
                dataFilter = (List<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class DistinctHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvDisName, tvCount;
        DistinctHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_city);
            tvDisName = itemView.findViewById(R.id.tv_nameCity);
            tvCount = itemView.findViewById(R.id.tv_countCity);
            tvCount.setVisibility(View.GONE);
        }
    }
}
