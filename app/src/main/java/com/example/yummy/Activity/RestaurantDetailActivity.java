package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Adapter.RestaurantDetailPaperAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.Model.Review;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RestaurantDetailActivity extends AppCompatActivity {
    private Restaurant restaurant;
    private Branch branch;
    private ViewPager viewPager;
    private FrameLayout viewReview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        restaurant = getIntent().getParcelableExtra("restaurant");
        branch = getIntent().getParcelableExtra("branch");
        setToolbar();
        setViewPaper();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilsBottomBar.getReview();
    }

    private void initView(){
        ImageView imRes = findViewById(R.id.imgQuanAnChiTiet);
        if(!branch.getAvatar().isEmpty())
        Picasso.get().load(branch.getAvatar()).into(imRes);
        TextView tvMark = findViewById(R.id.tv_mark);
        tvMark.setText(String.valueOf(restaurant.getMark()));
        viewReview = findViewById(R.id.view_review);

        viewReview.setOnClickListener(v -> {
            if(Common.accountCurrent != null) {
                showReviewCusView();
            }else {
                Toast.makeText(this, R.string.login_first, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(restaurant.getName());
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbarLayout);
        if(restaurant != null) {
            collapsingToolbar.setTitle(restaurant.getName());
        }
    }

    private void setViewPaper(){
        viewPager = findViewById(R.id.view_pager);
        RestaurantDetailPaperAdapter adapter = new RestaurantDetailPaperAdapter(getSupportFragmentManager(), restaurant, branch);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setHideReview(boolean isHide){
        viewReview.setVisibility(isHide ? View.GONE:View.VISIBLE);
    }

    private void showReviewCusView(){
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_review_cus);
        dialog.show();

        ImageView imBack = dialog.findViewById(R.id.im_back);
        TextView tvPost = dialog.findViewById(R.id.tv_post);
        TextView tvNameRes = dialog.findViewById(R.id.tv_nameRes);
        EditText edTitle = dialog.findViewById(R.id.ed_title);
        EditText edContent = dialog.findViewById(R.id.ed_content);
        TextView tvMark = dialog.findViewById(R.id.tv_mark);
        SeekBar seekBar = dialog.findViewById(R.id.sb_mark);

        imBack.setOnClickListener(v->dialog.dismiss());
        tvNameRes.setText(restaurant.getName());
        tvMark.setText("0");
        seekBar.setProgress(0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMark.setText(String.valueOf((float) progress/10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvPost.setOnClickListener(v->{
            String title = edTitle.getText().toString().trim();
            String content = edContent.getText().toString().trim();
            float mark = Float.parseFloat(tvMark.getText().toString());

            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformat = new SimpleDateFormat("dd/MMM/yyyy");
            String date = dateformat.format(c.getTime()) +" " +today.format("%k:%M:%S");

            if(content.length() > 50 && !date.isEmpty() && mark > 0){
                Review review = new Review();
                review.setId_user(Common.accountCurrent.getUserId());
                review.setContent(content);
                review.setAvatar(Common.accountCurrent.getAvatar());
                review.setMark(mark);
                review.setName(Common.accountCurrent.getName());
                review.setTime(date);
                review.setTitle(title);

                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                nodeRoot.child(Node.Review).child(restaurant.getRes_id()).push().setValue(review);
                restaurant.getReviewList().add(review);
                nodeRoot.child(Node.QuanAn).child(restaurant.getRes_id()).child(Node.mark).setValue(caculatorMark());
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float caculatorMark() {
        float mark = (float) 5.0;
        if (restaurant.getReviewList().size() > 0) {
            for (Review review : restaurant.getReviewList()) {
                mark += review.getMark();
            }
            mark = mark/(restaurant.getReviewList().size()+1);
            restaurant.setMark(mark);
        }
        return mark;
    }
}
