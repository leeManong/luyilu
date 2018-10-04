package com.wan.lu.luyilu.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wan.lu.luyilu.App;
import com.wan.lu.luyilu.R;
import com.wan.lu.luyilu.activity.VideoPageBean;

import java.util.List;

public class VideoListAdapter extends BaseQuickAdapter<VideoPageBean,BaseViewHolder> {

    public VideoListAdapter(int resId, List<VideoPageBean> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoPageBean item) {
        helper.setText(R.id.tv_title, item.getText());
        Glide.with(App.getAppContext()).load(item.getPic()).into((ImageView) helper.getView(R.id.iv_pic));
    }
}
