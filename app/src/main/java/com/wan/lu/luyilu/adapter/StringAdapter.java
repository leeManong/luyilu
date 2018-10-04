package com.wan.lu.luyilu.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wan.lu.luyilu.LinkBean;
import com.wan.lu.luyilu.R;

import java.util.List;

public class StringAdapter extends BaseQuickAdapter<LinkBean,BaseViewHolder> {

    public StringAdapter(int resId, List<LinkBean> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LinkBean item) {
        helper.setText(R.id.tv_text, item.getText());
    }
}
