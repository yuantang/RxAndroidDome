package com.tom.rxdome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tom.rxdome.R;
import com.tom.rxdome.model.Joker;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tom on 2016/1/26.
 */
public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG ="Adapter" ;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    private List<Joker> mData;

    public Adapter(Context context) {
        this.mContext = context;
    }

    public void addData(List<Joker> data){
        this.mData = data;
        notifyDataSetChanged();
    };
    public void addMore(List<Joker> data){
        for (int i = 0; i <data.size() ; i++) {
            this.mData.add(this.mData.size(),data.get(i));
        }
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_footer, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }else if(viewType==TYPE_ITEM){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_joker_item, null);
            return new itemViewHolder(view);
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof itemViewHolder){
            ((itemViewHolder) holder).itemAuthor.setText(mData.get(position).getAuthor());
            ((itemViewHolder) holder).itemContent.setText(mData.get(position).getContent());
            if (!mData.get(position).getPicUrl().isEmpty()){
                ((itemViewHolder) holder).itemPic.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(mData.get(position).getPicUrl()).into(  ((itemViewHolder) holder).itemPic);
            }else {
                ((itemViewHolder) holder).itemPic.setVisibility(View.GONE);
            }
        }else if(holder instanceof FooterViewHolder){
            ((FooterViewHolder) holder).loadProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void clearAll(){
        if (mData!=null){
            mData.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position+1==getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TYPE_ITEM;
        }
    }
    @Override
    public int getItemCount() {
        return mData==null?0:mData.size();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'layout_joker_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class itemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_pic)
        ImageView itemPic;
        @Bind(R.id.item_content)
        TextView itemContent;
        @Bind(R.id.item_author)
        TextView itemAuthor;
        public itemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    static class FooterViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.load_progress_bar)
        ProgressBar loadProgressBar;
        public FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
