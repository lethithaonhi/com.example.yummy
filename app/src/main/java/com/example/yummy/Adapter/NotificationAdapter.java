package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
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

import com.example.yummy.Model.Blog;
import com.example.yummy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private Context context;
    private List<Blog> blogList;

    public NotificationAdapter(Context context, List<Blog> blogList){
        this.blogList = blogList;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return blogList != null ? blogList.size() : 0;
    }

    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        ImageView img;
        LinearLayout viewRoot;

        NotificationHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            img = itemView.findViewById(R.id.img_notifi);
            viewRoot = itemView.findViewById(R.id.view_root);
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
}
