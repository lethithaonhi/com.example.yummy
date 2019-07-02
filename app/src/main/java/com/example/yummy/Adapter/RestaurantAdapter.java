package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.example.yummy.Activity.RestaurantDetailActivity;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder> implements Filterable, AddResMenuAdapter.OnChangeListMenu {
    private List<Restaurant> restaurantList;
    private List<Restaurant> dataFilter;
    private String query = "";
    private Context context;
    private Branch branch;
    private double min;
    private TextView tvOpen, tvClose;
    private Calendar myCalender;
    private int type; //1:cus, 0: admin, 3: nearby
    private List<String> checkList;
    private OnSawMapChangeListener onSawMapChangeListener;

    public void setOnSawMapChangeListener(OnSawMapChangeListener onSawMapChangeListener) {
        this.onSawMapChangeListener = onSawMapChangeListener;
    }

    public interface OnSawMapChangeListener {
        void onSawMap(Branch branch);
    }

    public RestaurantAdapter(List<Restaurant> restaurantList, Context context, int type){
        this.restaurantList = restaurantList;
        this.context = context;
        dataFilter = restaurantList;
        this.type = type;
        checkList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantHolder holder, int i) {
        Restaurant restaurant = dataFilter.get(i);
        
        branch = getBranch(restaurant);
        if(branch != null) {
            holder.tvName.setText(restaurant.getName());
            holder.tvMark.setText(new DecimalFormat("##.##").format(restaurant.getMark()));
            holder.tvDistance.setText(new DecimalFormat("##.##").format(min / 1000) + " km");
            holder.tvAddress.setText(branch.getAddress());
            if (branch.getAvatar() != null && !branch.getAvatar().isEmpty())
                Picasso.get().load(branch.getAvatar()).into(holder.imRes);
            if (restaurant.getFreeship() == 0) {
                holder.viewFreeship.setVisibility(View.VISIBLE);
            }

            if (restaurant.getDiscounts() != null && restaurant.getDiscounts().getDiscount() != 0) {
                holder.viewDiscount.setVisibility(View.VISIBLE);
                holder.tvDiscount.setText(restaurant.getDiscounts().getDiscount() + "%");
            }
            holder.viewRoot.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("restaurant", restaurant);
                branch = getBranch(restaurant);
                intent.putExtra("branch", branch);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            if (restaurant.getIsClose() == 0) {
                holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.red));
                holder.imDelete.setImageResource(R.drawable.ic_lock);
            } else {
                holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.green));
                holder.imDelete.setImageResource(R.drawable.unlock);
            }

            if (type == 0) {
                if (restaurant.getIsClose() == 0) {
                    holder.imClose.setVisibility(View.GONE);
                } else {
                    holder.imClose.setVisibility(View.VISIBLE);
                }
            }

            if (type == 0) {
                holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
                        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                    }
                });
            }

            holder.btnClose.setOnClickListener(v -> showDialogClose(restaurant));
            holder.btnEdit.setOnClickListener(v -> showDialogEdit(restaurant));

            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();

            if (!checkTimeCloses(restaurant.getOpen_time(), today.format("%k:%M"), restaurant.getClose_open())) {
                holder.tvClosed.setVisibility(View.VISIBLE);
            } else {
                holder.tvClosed.setVisibility(View.GONE);
            }

            if(type == 0){
                if(i == 0){
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(restaurant.getCity());
                }else {
                    if(!restaurant.getCity().equals(restaurantList.get(i-1).getCity())){
                        holder.tvTitle.setVisibility(View.VISIBLE);
                        holder.tvTitle.setText(restaurant.getCity());
                    }else {
                        holder.tvTitle.setVisibility(View.GONE);
                    }
                }
            }

            holder.imSeeMap.setOnClickListener(v-> onSawMapChangeListener.onSawMap(branch));
        }
    }

    @Override
    public int getItemCount() {
        return dataFilter != null ? dataFilter.size() : 0;
    }

    private Branch getBranch(Restaurant restaurant){
        String distinct;
        if(Common.myDistinct == null || Common.myDistinct.equals(context.getString(R.string.all))){
            distinct = "";
        }else {
            distinct = Common.myDistinct;
        }
        if(restaurant.getBranchList() != null && restaurant.getBranchList().size() > 0) {
            branch = new Branch();
            min = 1000000000;
            for (Branch branchNew : restaurant.getBranchList()) {
                if (min > branchNew.getDistance() && branchNew.getDistrict().contains(distinct)) {
                    min = branchNew.getDistance();
                    branch = branchNew;
                }
            }
            return branch;
        }
        return null;
    }

    @Override
    public void OnChangeListMenu(List<String> checkList) {
        this.checkList = checkList;
    }

    class RestaurantHolder extends RecyclerView.ViewHolder {
        ImageView imRes, imClose, imDelete, imSeeMap;
        TextView tvName, tvAddress, tvMark, tvDistance, tvDiscount, tvClosed, tvTitle;
        LinearLayout viewFreeship, viewRoot, viewDiscount;
        SwipeLayout swipeLayout;
        LinearLayout btnEdit, btnClose, vTrash;
        RestaurantHolder(@NonNull View itemView) {
            super(itemView);

            imRes = itemView.findViewById(R.id.im_restaurant);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvMark = itemView.findViewById(R.id.tv_mark);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            viewFreeship = itemView.findViewById(R.id.view_freeship);
            viewRoot = itemView.findViewById(R.id.view_rooot);
            viewDiscount = itemView.findViewById(R.id.view_discount);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            imDelete = itemView.findViewById(R.id.im_delete);
            swipeLayout = itemView.findViewById(R.id.swipe);
            btnClose = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            imClose = itemView.findViewById(R.id.im_close);
            vTrash = itemView.findViewById(R.id.trash);
            tvClosed = itemView.findViewById(R.id.close_res);
            tvTitle = itemView.findViewById(R.id.tv_title);
            imSeeMap = itemView.findViewById(R.id.im_seeMap);
            if(type == 0){
                tvDistance.setVisibility(View.GONE);
                tvMark.setVisibility(View.GONE);
                imClose.setVisibility(View.VISIBLE);
            }else if(type == 3){
                imSeeMap.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                query = charSequence.toString();
                if (!query.isEmpty()) {
                    List<Restaurant> filteredList = new ArrayList<>();
                    for (Restaurant restaurant : restaurantList) {
                        if (restaurant.getName().toLowerCase().contains(query.toLowerCase()) || (branch != null && branch.getAddress().toLowerCase().contains(query.toLowerCase()))) {
                            filteredList.add(restaurant);
                        }
                    }

                    dataFilter = filteredList;

                } else {
                    dataFilter = restaurantList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataFilter = (List<Restaurant>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void showDialogClose(Restaurant restaurant) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(restaurant.getIsClose() == 1 ? context.getResources().getString(R.string.mess_open) : context.getResources().getString(R.string.mess_close))
                .setCancelable(false)
                .setPositiveButton(restaurant.getIsClose()== 1 ? context.getResources().getString(R.string.yes) : context.getResources().getString(R.string.close), ((dialog, which) -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    int isClose = restaurant.getIsClose();
                    if(isClose == 0)
                        isClose = 1;
                    else
                        isClose = 0;

                    mDatabase.child(Node.QuanAn).child(restaurant.getRes_id()).child(Node.isClose).setValue(isClose);

                    restaurant.setIsClose(isClose);
                    dialog.dismiss();
                    notifyDataSetChanged();
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }))
                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogEdit(Restaurant restaurant){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_add_restaurant);
        dialog.show();

        EditText edName = dialog.findViewById(R.id.edt_name);
        edName.setText(restaurant.getName());
        tvClose = dialog.findViewById(R.id.tv_close);
        tvOpen = dialog.findViewById(R.id.tv_open);
        tvClose.setText(restaurant.getClose_open());
        tvOpen.setText(restaurant.getOpen_time());
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.edit_res);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGrp);
        RadioButton rdYes = dialog.findViewById(R.id.rdb_no);
        RadioButton rdNo = dialog.findViewById(R.id.rdb_yes);
        EditText edFeeShip = dialog.findViewById(R.id.ed_ship);
        EditText edVideo = dialog.findViewById(R.id.edt_video);
        edVideo.setText(restaurant.getVideo());
        RecyclerView rcvType = dialog.findViewById(R.id.rcv_type);
        Button btnCreate = dialog.findViewById(R.id.btn_create);
        btnCreate.setText(R.string.edit);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rcvType.setLayoutManager(layoutManager);
        rcvType.setNestedScrollingEnabled(false);
        AddResMenuAdapter adapter = new AddResMenuAdapter(context);
        adapter.setCheckList(restaurant.getMenuIdList());
        rcvType.setAdapter(adapter);
        adapter.onChangeListMenu(this);

        if(restaurant.getFreeship() == 0){
            edFeeShip.setVisibility(View.GONE);
            rdYes.setChecked(true);
        }else {
            edFeeShip.setVisibility(View.VISIBLE);
            rdNo.setChecked(true);
            edFeeShip.setText(String.valueOf(restaurant.getFreeship()));
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdb_no) {
                edFeeShip.setVisibility(View.GONE);
            } else {
                edFeeShip.setVisibility(View.VISIBLE);
            }
        });
        myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        RecyclerView rcvCity = dialog.findViewById(R.id.rcv_city);
        rcvCity.setVisibility(View.GONE);

        tvOpen.setOnClickListener(v -> openTimeDialog(tvOpen, hour, minute));
        tvClose.setOnClickListener(v -> openTimeDialog(tvClose, hour, minute));

        btnCreate.setOnClickListener(v -> {
            String name = edName.getText().toString().trim();
            String video = edVideo.getText().toString().trim();
            String openTime = tvOpen.getText().toString();
            String closeTime = tvClose.getText().toString();
            boolean isFreeShip = edFeeShip.getVisibility() == View.GONE;
            int freeShip = isFreeShip ? 0 : Integer.parseInt(edFeeShip.getText().toString().trim());

            if(!name.isEmpty() && !video.isEmpty() && checkTime(openTime, closeTime) && checkList.size() > 0){
                restaurant.setName(name);
                restaurant.setOpen_time(openTime);
                restaurant.setClose_open(closeTime);
                restaurant.setFreeship(freeShip);
                restaurant.setVideo(video);

                Restaurant newRes = new Restaurant();
                newRes.setClose_open(restaurant.getClose_open());
                newRes.setIsClose(restaurant.getIsClose());
                newRes.setFreeship(restaurant.getFreeship());
                newRes.setMark(restaurant.getMark());
                newRes.setMenuIdList(restaurant.getMenuIdList());
                newRes.setName(restaurant.getName());
                newRes.setOpen_time(restaurant.getOpen_time());
                newRes.setVideo(restaurant.getVideo());
                newRes.setMenuIdList(checkList);

                DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
                mData.child(Node.QuanAn).child(restaurant.getRes_id()).setValue(newRes);
                Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                notifyDataSetChanged();
            }else {
                Toast.makeText(context, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private boolean checkTime(String open, String close){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        try {
            Date dateOpen = inputParser.parse(open);
            Date dateClose = inputParser.parse(close);
            if ( dateOpen.before(dateClose)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkTimeCloses(String open, String timeRes, String close){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        try {
            Date dateOpen = inputParser.parse(open);
            Date dateClose = inputParser.parse(close);
            Date time = inputParser.parse(timeRes);
            if ( dateOpen.before(time) && dateClose.after(time)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openTimeDialog(TextView txtTime, int hour, int minute) {
        @SuppressLint("SetTextI18n") TimePickerDialog.OnTimeSetListener myTimeListener = (view, hourOfDay, minute1) -> {
            if (view.isShown()) {
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute1);
                txtTime.setText(hourOfDay + ":" + minute1);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose hour:");
        if (timePickerDialog.getWindow() != null)
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }
}
