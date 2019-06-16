package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddResMenuAdapter extends RecyclerView.Adapter<AddResMenuAdapter.AddResMenuHolder> {
    private Context context;
    private Map<String, String> data;
    private List<String> menuList;
    private OnChangeListMenu onChangeListMenu;
    private List<String> checkList;

    public interface OnChangeListMenu{
        void OnChangeListMenu(List<String> checkList);
    }

    public void onChangeListMenu(OnChangeListMenu onChangeListMenu){
        this.onChangeListMenu = onChangeListMenu;
    }

    public AddResMenuAdapter(Context context) {
        this.context = context;
        this.data = Common.menuList.get(0);
        menuList = new ArrayList<>(data.values());
        checkList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AddResMenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_city, viewGroup, false);
        return new AddResMenuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddResMenuHolder holder, int pos) {
        String name = menuList.get(pos);
        holder.tvName.setText(name);

        if(checkList != null && checkList.size() > 0 && checkList.contains(getKey(name))){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String key = getKey(name);
            if(isChecked){
                checkList.add(key);
            }else {
                checkList.remove(key);
            }
            onChangeListMenu.OnChangeListMenu(checkList);
        });
    }

    public void setCheckList(List<String> checkList){
        this.checkList = checkList;
    }

    @Override
    public int getItemCount() {
        return menuList != null ? menuList.size() : 0;
    }

    class AddResMenuHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvName, tvCount;

        AddResMenuHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox_city);
            tvName = itemView.findViewById(R.id.tv_nameCity);
            tvCount = itemView.findViewById(R.id.tv_countCity);
            tvCount.setVisibility(View.GONE);
        }
    }

    private String getKey(String menu) {
        for (Map.Entry<String,String> entry : data.entrySet()) {
            if (entry.getValue().contains(menu)) {
                return entry.getKey();
            }
        }
        return "";
    }
}
