package com.wan.lu.luyilu.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.wan.lu.luyilu.AppConstrant;
import com.wan.lu.luyilu.LinkBean;
import com.wan.lu.luyilu.MainActivity;
import com.wan.lu.luyilu.R;
import com.wan.lu.luyilu.adapter.VideoListAdapter;

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

public class VideoListActivity extends AppCompatActivity {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;

    private VideoListAdapter mAdapter;
    private List<VideoPageBean> mData;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initView() {
        mData = new ArrayList<>();
        mAdapter = new VideoListAdapter(R.layout.adapter_video_list_item, mData);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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
                .map(new Function<Document, List<VideoPageBean>>() {
                    @Override
                    public List<VideoPageBean> apply(Document document) throws Exception {
                        List<VideoPageBean> linkBeans = new ArrayList<>();
                        Elements elements = document.select("div.item");
                        for (Element element : elements) {
                            Elements elements1 = element.select("a");
                            for (int i = 0; i < elements1.size(); i++) {
                                if (i % 2 == 0) {
                                    linkBeans.add(new VideoPageBean(elements1.get(i).select("a").attr("title"), AppConstrant.BASE_URL1 + elements1.get(i).select("a").attr("href"), elements1.get(i).select("a").attr("data-original")));
                                }
                            }
                        }
                        return linkBeans;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .as(AutoDispose.<List<VideoPageBean>>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new Observer<List<VideoPageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<VideoPageBean> linkBeans) {
                        mData.clear();
                        mData.addAll(linkBeans);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRefreshLayout.finishRefresh(false);
                        Toast.makeText(VideoListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
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
