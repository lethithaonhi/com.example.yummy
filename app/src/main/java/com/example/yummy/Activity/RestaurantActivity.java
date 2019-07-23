package com.example.yummy.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Adapter.CityAdapter;
import com.example.yummy.Adapter.DistinctAdapter;
import com.example.yummy.Adapter.RestaurantAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.UploadImage.ImageResponse;
import com.example.yummy.UploadImage.*;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.yummy.UploadImage.Constants.READ_WRITE_EXTERNAL;

public class RestaurantActivity extends AppCompatActivity {
    private List<Restaurant> restaurantList, restaurantAllList;
    private int type; //1: mark, 0: normal, 2: distance, 3:discount
    private String[] typeName;
    private CityAdapter cityAdapter;
    private RecyclerView rcvRes;
    private Dialog dialog;
    private RestaurantAdapter adapter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        typeName = new String[]{getResources().getString(R.string.Exp), getResources().getString(R.string.hot), getResources().getString(R.string.nearby), getResources().getString(R.string.discount)
                , getResources().getString(R.string.food), getResources().getString(R.string.drink), getResources().getString(R.string.cake), getResources().getString(R.string.fruits)
                , getResources().getString(R.string.snack), getResources().getString(R.string.vegetarian_food), getResources().getString(R.string.handmade), getResources().getString(R.string.more)};

        type = getIntent().getIntExtra("type", 0);
        initView();
        registerReceiver();
    }

    private void registerReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }

    private void initView() {
        TextView tvType = findViewById(R.id.tv_type);
        tvType.setText(typeName[type]);
        EditText edSearch = findViewById(R.id.edt_search);
        ImageView btnSearch = findViewById(R.id.btn_search);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvCIty = findViewById(R.id.tv_city);
        tvCIty.setText(Common.myAddress);
        ImageView btnClose = findViewById(R.id.btn_close);
        TextView tvDistinct = findViewById(R.id.tv_district);
        tvDistinct.setText(Common.myDistinct);
        rcvRes = findViewById(R.id.rcv_restaurant);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvRes.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvRes.getContext(), layoutManager.getOrientation());
        rcvRes.addItemDecoration(dividerItemDecoration);
        RestaurantMainAsyncTask myAsyncTask = new RestaurantMainAsyncTask();
        myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        LinearLayout viewCity = findViewById(R.id.view_city);
        viewCity.setOnClickListener(v -> createDialogCity());

        LinearLayout viewDistinct = findViewById(R.id.v_distinct);
        viewDistinct.setOnClickListener(v -> createDialogDistinct());
        tvAddress.setText(Common.myLocation.getName());

        ImageView btnCamera = findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(v -> createDialogSearchByImg());

        btnSearch.setOnClickListener(v -> {
            edSearch.setVisibility(View.VISIBLE);
            viewCity.setVisibility(View.GONE);
            tvType.setVisibility(View.GONE);
            btnClose.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.GONE);
            btnCamera.setVisibility(View.VISIBLE);
        });

        btnClose.setOnClickListener(v -> {
            viewCity.setVisibility(View.VISIBLE);
            edSearch.setText("");
            edSearch.setVisibility(View.GONE);
            tvType.setVisibility(View.VISIBLE);
            btnClose.setVisibility(View.GONE);
            btnSearch.setVisibility(View.VISIBLE);
            btnCamera.setVisibility(View.GONE);
            UtilsBottomBar.hideKeyboard(this);
        });

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });

        LinearLayout viewAddress = findViewById(R.id.view_address);
        viewAddress.setOnClickListener(v -> {
            if (Common.accountCurrent != null) {
                Intent intent = new Intent(this, ChangeAddressActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.login_first, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    private void setRestaurantList() {
        if (type == 1) {
            Collections.sort(restaurantList, (obj1, obj2) -> Float.compare(obj1.getMark(), obj2.getMark()));
            Collections.reverse(restaurantList);
        } else if (type == 2) {
            for (Restaurant restaurant : restaurantList) {
                Collections.sort(restaurant.getBranchList(), (obj1, obj2) -> Float.compare(obj1.getDistance(), obj2.getDistance()));
            }

            Collections.sort(restaurantList, (obj1, obj2) -> Float.compare(obj1.getBranchList().get(0).getDistance(), obj2.getBranchList().get(0).getDistance()));
        } else if (type == 3) {
            List<Restaurant> restaurantListNew = new ArrayList<>();
            for (Restaurant restaurant : restaurantList) {
                if (restaurant.getDiscounts() != null && restaurant.getDiscounts().getDiscount() > 0) {
                    restaurantListNew.add(restaurant);
                }
            }
            restaurantList = restaurantListNew;
            Collections.sort(restaurantList, (ob1, ob2) -> ob2.getDiscounts().getDiscount() - ob1.getDiscounts().getDiscount());
        } else if (type == 4) {
            getResFromMenu("mathucdon1");
        } else if (type == 5) {
            getResFromMenu("mathucdon2");
        } else if (type == 6) {
            getResFromMenu("mathucdon3");
        } else if (type == 7) {
            getResFromMenu("mathucdon5");
        } else if (type == 8) {
            getResFromMenu("mathucdon6");
        } else if (type == 9) {
            getResFromMenu("mathucdon7");
        } else if (type == 10) {
            getResFromMenu("mathucdon8");
        } else if (type == 11) {
            getResFromMenu("mathucdon4");
        }
    }

    private void getResFromMenu(String maMenu) {
        List<Restaurant> restaurantNewList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            for (String menu : restaurant.getMenuIdList()) {
                if (menu.equals(maMenu)) {
                    restaurantNewList.add(restaurant);
                    break;
                }
            }
        }
        restaurantList = restaurantNewList;
        for (Restaurant restaurant : restaurantList) {
            Collections.sort(restaurant.getBranchList(), (obj1, obj2) -> Float.compare(obj1.getDistance(), obj2.getDistance()));
        }

        Collections.sort(restaurantList, (obj1, obj2) -> Float.compare(obj1.getBranchList().get(0).getDistance(), obj2.getBranchList().get(0).getDistance()));
    }

    private void createDialogCity() {
        dialog = new Dialog(RestaurantActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_city);
        dialog.show();

        EditText edSearchCity = dialog.findViewById(R.id.edt_search_city);
        ImageView close = dialog.findViewById(R.id.close_dialog);
        close.setOnClickListener(v -> dismissDialog());
        RecyclerView rcvCity = dialog.findViewById(R.id.rcv_city);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvCity.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvCity.getContext(), layoutManager.getOrientation());
        rcvCity.addItemDecoration(dividerItemDecoration);

        cityAdapter = new CityAdapter(this, Common.cityList, false);
        rcvCity.setAdapter(cityAdapter);

        TextView tvDone = dialog.findViewById(R.id.tv_done);
        tvDone.setOnClickListener(v -> {
            Common.myAddress = cityAdapter.getCity();
            Common.myDistinct = getString(R.string.all);
            UtilsBottomBar.getDistinct(Common.myAddress, true);
            finish();
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        });

        edSearchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cityAdapter.getFilter().filter(s.toString());
            }
        });
    }

    private void createDialogDistinct() {
        Dialog dialogDis = new Dialog(RestaurantActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialogDis.setTitle("");
        dialogDis.setContentView(R.layout.dialog_city);
        dialogDis.show();

        EditText edSearchCity = dialogDis.findViewById(R.id.edt_search_city);
        ImageView close = dialogDis.findViewById(R.id.close_dialog);
        TextView tvTitle = dialogDis.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.distinct);
        close.setOnClickListener(v -> dialogDis.dismiss());
        RecyclerView rcvCity = dialogDis.findViewById(R.id.rcv_city);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvCity.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvCity.getContext(), layoutManager.getOrientation());
        rcvCity.addItemDecoration(dividerItemDecoration);

        DistinctAdapter disAdapter = new DistinctAdapter(this, Common.distinctList);
        rcvCity.setAdapter(disAdapter);

        TextView tvDone = dialogDis.findViewById(R.id.tv_done);
        tvDone.setOnClickListener(v -> {
            Common.myDistinct = disAdapter.getMyDistinct();
            finish();
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        });

        edSearchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                disAdapter.getFilter().filter(s.toString());
            }
        });
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RestaurantMainAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            restaurantList = new ArrayList<>();
            restaurantAllList = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new RestaurantAdapter(restaurantList, getBaseContext(), 1);
            rcvRes.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            restaurantAllList = Common.db.getRestaurant(Common.listResId, Common.myAddress);
            for (Restaurant restaurant : restaurantAllList) {
                for (Branch branch : restaurant.getBranchList()) {
                    branch.setDistance(UtilsBottomBar.getDistanceBranch(branch, Common.myLocation.getLatitude(), Common.myLocation.getLongitude()));
                }
                if (Common.myDistinct != null && !Common.myDistinct.equals(getString(R.string.all))) {
                    if (checkRes(restaurant)) {
                        restaurantList.add(restaurant);
                    }
                }
            }
            if (Common.myDistinct != null && !Common.myDistinct.equals(getString(R.string.all))) {
                restaurantList = new ArrayList<>();
                for (Restaurant restaurant : restaurantAllList) {
                    if (checkRes(restaurant)) {
                        restaurantList.add(restaurant);
                    }
                }
            } else {
                restaurantList = restaurantAllList;
            }
            setRestaurantList();
            return null;
        }
    }

    private boolean checkRes(Restaurant restaurant) {
        if (restaurant.getBranchList().size() > 0 && Common.myDistinct != null) {
            for (Branch branch : restaurant.getBranchList()) {
                if (branch.getDistrict().contains(Common.myDistinct)) {
                    return true;
                }
            }
        }
        return false;
    }

    //search by image

    private int TAKE_PHOTO_CODE = 1;
    private int CHOOSE_PHOTO_CODE = 2;
    private File chosenFile;
    private Uri returnUri;
    private Dialog dialogLoadImg;
    private ProgressDialog progressDialog;

    private void createDialogSearchByImg() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_change_avatar);
        dialog.show();

        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnTake = dialog.findViewById(R.id.btn_takePho);
        TextView btnChoose = dialog.findViewById(R.id.btn_choosePho);

        btnTake.setOnClickListener(v -> {
            checkPermission(false);
            dialog.dismiss();
        });

        btnChoose.setOnClickListener(v -> {
            checkPermission(true);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void checkPermission(boolean isChoose) {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO_CODE);
        }else {
            if(isChoose) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_PHOTO_CODE);
            }else {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO_CODE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            returnUri = data.getData();
            Bitmap bitmap = null;
            if (requestCode == TAKE_PHOTO_CODE && data.getExtras() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == CHOOSE_PHOTO_CODE) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            createDialogLoadImg(bitmap);
            getFilePath();
        }
    }

    private void createDialogLoadImg(Bitmap bitmap) {
        dialogLoadImg = new Dialog(this);
        dialogLoadImg.setTitle("");
        dialogLoadImg.setContentView(R.layout.dialog_search_by_image);
        dialogLoadImg.show();

        Button btnSearch = dialogLoadImg.findViewById(R.id.btn_search);
        ImageView imSearch = dialogLoadImg.findViewById(R.id.img_search);
        TextView tvStatus = dialogLoadImg.findViewById(R.id.tv_status);

        if(bitmap != null){
            imSearch.setImageBitmap(bitmap);
            tvStatus.setText(getString(R.string.success));
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        }

        btnSearch.setOnClickListener(v->onUpload());
    }

    private void onUpload() {
        ImgurService imgurService = ImgurService.retrofit.create(ImgurService.class);
        final Call<ImageResponse> call =
                imgurService.postImage(
                       "search",
                        "search by image", "", "",
                        MultipartBody.Part.createFormData(
                                "image",
                                chosenFile.getName(),
                                RequestBody.create(MediaType.parse("image/*"), chosenFile)
                        ));

        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response == null) {
                    return;
                }
                if (response.isSuccessful()) {
                    if(dialogLoadImg != null && dialogLoadImg.isShowing()) {
                        dialogLoadImg.dismiss();
                    }
                    Toast.makeText(RestaurantActivity.this, "http://imgur.com/" + response.body().data.id , Toast.LENGTH_SHORT)
                            .show();
                    Log.d("URL Picture", "http://imgur.com/" + response.body().data.id);
                    progressDialog = new ProgressDialog(RestaurantActivity.this);
                    progressDialog.setMessage(getString(R.string.please_wait));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Toast.makeText(RestaurantActivity.this, "An unknown error has occured.", Toast.LENGTH_SHORT)
                        .show();
                t.printStackTrace();
            }
        });

    }

    private void getFilePath() {
        String filePath = DocumentHelper.getPath(this, this.returnUri);
        //Safety check to prevent null pointer exception
        if (filePath == null || filePath.isEmpty()) return;
        chosenFile = new File(filePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_WRITE_EXTERNAL) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getFilePath();
            } else {
                Toast.makeText(RestaurantActivity.this, R.string.error_pess, Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
