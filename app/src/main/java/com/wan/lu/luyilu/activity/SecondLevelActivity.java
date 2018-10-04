package com.wan.lu.luyilu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.wan.lu.luyilu.AppConstrant;
import com.wan.lu.luyilu.LinkBean;
import com.wan.lu.luyilu.MainActivity;
import com.wan.lu.luyilu.R;
import com.wan.lu.luyilu.adapter.StringAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SecondLevelActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;

    private StringAdapter mAdapter;
    private List<LinkBean> mData;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_level);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initView() {
        mData = new ArrayList<>();
        mAdapter = new StringAdapter(R.layout.adapter_string_list_item, mData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });
        mLlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LinkBean linkBean = mData.get(position);
                Intent intent = new Intent(SecondLevelActivity.this, VideoListActivity.class);
                intent.putExtra(AppConstrant.URL, linkBean.getUrl());
                intent.putExtra(AppConstrant.TITLE, linkBean.getText());
                startActivity(intent);
            }
        });
    }

    private void getData() {
        Observable.just(mUrl)
                .map(new Function<String, Document>() {
                    @Override
                    public Document apply(String s) throws Exception {
                        return Jsoup.connect(s)
                                .timeout(5000) // 设置超时时间
                                .get();
                    }
                })
                .map(new Function<Document, List<LinkBean>>() {
                    @Override
                    public List<LinkBean> apply(Document document) throws Exception {
                        List<LinkBean> linkBeans = new ArrayList<>();
                        Elements elements = document.select("div.item");
                        for (Element element : elements) {
                            Elements elements1 = element.select("a");
                            for (Element element1 : elements1) {
                                linkBeans.add(new LinkBean(element1.select("a").text(), AppConstrant.BASE_URL1 + element1.select("a").attr("href")));
                            }
                            linkBeans.remove(1);
                        }
                        return linkBeans;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .as(AutoDispose.<List<LinkBean>>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new Observer<List<LinkBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<LinkBean> linkBeans) {
                        mData.clear();
                        mData.addAll(linkBeans);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRefreshLayout.finishRefresh(false);
                        Toast.makeText(SecondLevelActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        mRefreshLayout.finishRefresh(true);
                    }
                });
    }


    private void initData() {
        mTvTitle.setText(getIntent().getStringExtra(AppConstrant.TITLE));
        mUrl = getIntent().getStringExtra(AppConstrant.URL);
    }
}
