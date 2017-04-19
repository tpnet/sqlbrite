/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tpnet.sqlbrite_sqldelight_sample.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.SqlDelightStatement;
import com.tpnet.sqlbrite_sqldelight_sample.BaseApplication;
import com.tpnet.sqlbrite_sqldelight_sample.R;
import com.tpnet.sqlbrite_sqldelight_sample.model.TodoList;
import com.tpnet.sqlbrite_sqldelight_sample.ui.adapter.ListsAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static android.support.v4.view.MenuItemCompat.SHOW_AS_ACTION_IF_ROOM;
import static android.support.v4.view.MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT;

public final class ListsFragment extends Fragment {

    //回调到MainActivity的接口
    public interface Listener {
        //列表点击
        void onListClicked(long id);

        //新建列表点击
        void onNewListClicked();
    }

    //单例实现
    public static ListsFragment newInstance() {
        return new ListsFragment();
    }

    @Inject
    BriteDatabase db;   //注入获取BriteDatabase

    //使用ButterKnife获取View
    @BindView(android.R.id.list)
    ListView listView;
    @BindView(android.R.id.empty)
    View emptyView;

    //回调接口
    private Listener listener;
    //适配器
    private ListsAdapter adapter;
    //Rxjava的订阅者，在离开界面进行接触订阅
    private Subscription subscription;

    //初始化时候进行Dagger2的注入
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof Listener)) {
            throw new IllegalStateException("Activity must implement fragment Listener.");
        }

        super.onAttach(context);

        //Dagger2的注入
        BaseApplication.getAppComponent(context).inject(this);

        //设置菜单
        setHasOptionsMenu(true);

        listener = (Listener) context;
        //初始化适配器
        adapter = new ListsAdapter(context);
    }

    //添加菜单为 NEW LIST
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //点击菜单回调
        MenuItem item = menu.add(R.string.new_list)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        listener.onNewListClicked();
                        return true;
                    }
                });
        MenuItemCompat.setShowAsAction(item, SHOW_AS_ACTION_IF_ROOM | SHOW_AS_ACTION_WITH_TEXT);
    }

    //创建Fragment的布局
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lists, container, false);
    }

    //view创建完毕，开始绑定Butterknife、适配器
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        listView.setEmptyView(emptyView);
        listView.setAdapter(adapter);
    }


    @OnItemClick(android.R.id.list)
    void listClicked(long listId) {
        listener.onListClicked(listId);
    }

    //界面显示完毕设置标题、查询数据库
    @Override
    public void onResume() {
        super.onResume();

        //设置标题
        getActivity().setTitle(getResources().getString(R.string.app_name));

        //创建查询
        SqlDelightStatement sqLiteStatement = TodoList.FACTORY.list_query();

        subscription = db.createQuery(TodoList.TABLE_NAME, sqLiteStatement.statement, sqLiteStatement.args)
                .mapToList(TodoList.LIST_ITEM_FUNC)   //映射到ListItem的MAPPER
                .observeOn(AndroidSchedulers.mainThread()) //设置订阅者在主线程进行
                .subscribe(adapter);
    }
    

    @Override
    public void onPause() {
        super.onPause();
        //界面隐藏解除订阅
        subscription.unsubscribe();
    }
}
