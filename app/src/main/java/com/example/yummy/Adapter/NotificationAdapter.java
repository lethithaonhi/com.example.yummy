package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.example.yummy.Model.Blog;
import com.example.yummy.Model.Menu;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private Context context;
    private List<Blog> blogList;
    private boolean isAdmin;

    public NotificationAdapter(Context context, List<Blog> blogList, boolean isAdmin){
        this.blogList = blogList;
        this.context = context;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_blog, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int i) {
        Blog blog = blogList.get(i);

        holder.tvTitle.setText(blog.getTitle());
        holder.tvContent.setText(blog.getContent());
        holder.tvTime.setText(blog.getTime());
        if(!blog.getImage().isEmpty())
            Picasso.get().load(blog.getImage()).into(holder.img);
        holder.viewRoot.setOnClickListener(v-> loadWeb(blog.getUrl()));
        if(isAdmin) {
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                }
            });
        }

        holder.btnDelete.setOnClickListener(v -> showDialogDelete(blog));
//        holder.btnEdit.setOnClickListener(v->showDialogEdit(menu));
    }

    @Override
    public int getItemCount() {
        return blogList != null ? blogList.size() : 0;
    }

    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        ImageView img;
        LinearLayout viewRoot;
        SwipeLayout swipeLayout;
        LinearLayout btnEdit, btnDelete;

        NotificationHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            img = itemView.findViewById(R.id.img_notifi);
            viewRoot = itemView.findViewById(R.id.view_root);
            swipeLayout = itemView.findViewById(R.id.swipe);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWeb(String url){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setTitle("");
        dialog.setContentView(R.layout.layout_webview);
        dialog.show();

        WebView webView = dialog.findViewById(R.id.webview);
        if (!url.isEmpty()) {
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url)
                {
                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementById('header')[0].style.display='none'; " +
                            "})()");
                }
            });
            webView.loadUrl(url);
        }else {
            Toast.makeText(context, R.string.error_change_pass, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogDelete(Blog blog) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(context.getResources().getString(R.string.mess_delete))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.delete), ((dialog, which) -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(Node.Blog).child(blog.getId()).setValue(blog);
                    dialog.dismiss();
                    notifyDataSetChanged();
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }))
                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
