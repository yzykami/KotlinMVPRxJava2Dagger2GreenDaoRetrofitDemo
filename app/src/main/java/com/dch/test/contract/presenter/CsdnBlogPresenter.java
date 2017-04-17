package com.dch.test.contract.presenter;

import com.dch.test.contract.CsdnBlogContract;
import com.dch.test.repository.ArticalDataSource;
import com.dch.test.repository.ArticalRepository;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 作者：dch on 2017/4/10 16:32
 * 描述：
 * 邮箱：daichuanhao@caijinquan.com
 */
public class CsdnBlogPresenter implements CsdnBlogContract.Presenter {
    private final CsdnBlogContract.CsdnBlogView homeView;
    private final ArticalRepository mArticalRepository;

    public CsdnBlogPresenter(CsdnBlogContract.CsdnBlogView view, ArticalRepository articalRepository) {
        homeView = checkNotNull(view, "view不能为空");
        mArticalRepository = checkNotNull(articalRepository, "articalRepository不能为空");
        homeView.setPresenter(this);
    }

    @Override
    public void start() {
        getArticalsData();
    }

    @Override
    public void getArticalsData() {
        mArticalRepository.getArticalsData(new ArticalDataSource.LoadArticalCallback() {
            @Override
            public void onArticalLoaded(ArrayList<String> list) {
                homeView.showArticalList(list);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
}