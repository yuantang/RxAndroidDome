package com.tom.rxanroiddome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tom on 2016/1/10.
 */
public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {

    private Context mContext;
    private List<AppInfo> mDatas;

    public AppInfoAdapter(Context context) {
        this.mContext = context;
//        this.mDatas = list;
    }

    public void addApplications(List<AppInfo> list) {
        this.mDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(mDatas.get(position).getmName());
        holder.itemPack.setText(mDatas.get(position).getmPack());
        holder.itemIcon.setImageBitmap(mDatas.get(position).getmIcon());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from activity_main file 'recycler_item.xml'
     * for easy to all activity_main elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_icon)
        ImageView itemIcon;
        @Bind(R.id.item_name)
        TextView itemName;
        @Bind(R.id.item_pack)
        TextView itemPack;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
