package com.dch.test.contract;

import com.dch.test.base.BasePresenter;
import com.dch.test.base.BaseView;
import com.dch.test.repository.entity.GankEntity;

/**
 * 作者：Dch on 2017/4/20 19:01
 * 描述：
 * 邮箱：daichuanhao@caijinquan.com
 */
public interface HomeContract {

    interface View extends BaseView<HomePresenter> {
        void showDailyList(GankEntity entity);

        void showError(Throwable throwable);
    }

    interface HomePresenter extends BasePresenter {
        void getAndroidData(int pageNum, int pageSize);
        void getMeiziData(int pageNum, int pageSize);
    }
}
