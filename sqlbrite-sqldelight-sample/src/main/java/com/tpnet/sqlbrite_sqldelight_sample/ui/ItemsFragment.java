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

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.SqlDelightStatement;
import com.tpnet.sqlbrite_sqldelight_sample.BaseApplication;
import com.tpnet.sqlbrite_sqldelight_sample.R;
import com.tpnet.sqlbrite_sqldelight_sample.TodoItemModel;
import com.tpnet.sqlbrite_sqldelight_sample.model.TodoItem;
import com.tpnet.sqlbrite_sqldelight_sample.model.TodoList;
import com.tpnet.sqlbrite_sqldelight_sample.ui.adapter.ItemsAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.support.v4.view.MenuItemCompat.SHOW_AS_ACTION_IF_ROOM;
import static android.support.v4.view.MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT;
import static com.squareup.sqlbrite.SqlBrite.Query;

/**
 * item的Fragment
 */
public final class ItemsFragment extends Fragment {
    private static final String KEY_LIST_ID = "list_id";


    //菜单新建接口
    public interface Listener {
        void onNewItemClicked(long listId);
    }

    public static ItemsFragment newInstance(long listId) {
        Bundle arguments = new Bundle();
        arguments.putLong(KEY_LIST_ID, listId);

        ItemsFragment fragment = new ItemsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Inject
    BriteDatabase db;

    @BindView(android.R.id.list)
    ListView listView;
    @BindView(android.R.id.empty)
    View emptyView;

    private Listener listener;
    private ItemsAdapter adapter;                   //item的适配器
    private CompositeSubscription subscriptions;   //可以组合多个subscriptions，便于解除订阅


    TodoItemModel.Update_complete update_complete;


    private long getListId() {
        return getArguments().getLong(KEY_LIST_ID);
    }

    @Override
    public void onAttach(Context context) {
        if (!(context instanceof Listener)) {
            throw new IllegalStateException("Activity must implement fragment Listener.");
        }

        super.onAttach(context);
        BaseApplication.getAppComponent(context).inject(this);
        setHasOptionsMenu(true);

        listener = (Listener) context;
        adapter = new ItemsAdapter(context);

        //初始化更新数据库对象
        update_complete = new TodoItemModel.Update_complete(db.getWritableDatabase());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.add(R.string.new_item)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        listener.onNewItemClicked(getListId());
                        return true;
                    }
                });
        MenuItemCompat.setShowAsAction(item, SHOW_AS_ACTION_IF_ROOM | SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.items, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        listView.setEmptyView(emptyView);
        listView.setAdapter(adapter);

        //监听Listview的点击，更新todo_item表的状态
        RxAdapterView.itemClickEvents(listView) //
                .observeOn(Schedulers.io())
                .subscribe(new Action1<AdapterViewItemClickEvent>() {
                    @Override
                    public void call(AdapterViewItemClickEvent event) {
                        Log.e("@@", "call: item点击，更新数据" + event.id());


                        TodoItem item = adapter.getItem(event.position());
                        //取相反值
                        boolean newValue = !item.complete();

                        //更新数据库

                        update_complete.bind(newValue, item._id());
                        db.executeUpdateDelete(update_complete.table, update_complete.program);
                        //不能用下面这个方法，会导致Rxjava不能发送消息
                        //update_complete.program.executeUpdateDelete();

                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();

        //创建订阅者
        subscriptions = new CompositeSubscription();


        SqlDelightStatement countQuery = TodoItem.FACTORY.count_query(getListId());

        //查询todo_item表的对应todo_list_id的总数
        Observable<Long> itemCount = db.createQuery(TodoItem.TABLE_NAME, countQuery.statement, countQuery.args)
                .map(new Func1<Query, Long>() {
                    @SuppressLint("NewApi")
                    @Override
                    public Long call(Query query) {

                        Cursor cursor = query.run();
                        cursor.moveToFirst();
                        return TodoItem.ROW_COUNT_QUERY_MAPPER.map(cursor);
                    }
                });


        SqlDelightStatement titleQuery = TodoList.FACTORY.title_query(getListId());


        //根据_id查询todo_list表的数据的name
        Observable<String> listName =
                db.createQuery(TodoList.TABLE_NAME, titleQuery.statement, titleQuery.args)
                        .map(new Func1<Query, String>() {
                            @SuppressLint("NewApi")
                            @Override
                            public String call(Query query) {

                                Cursor cursor = query.run();

                                cursor.moveToFirst();

                                return TodoList.ROW_TITLE_MAPPER.map(cursor);
                            }
                        });


        //取得对应list的名字和todo_item表对应的数量数量作为标题。
        subscriptions.add(
                Observable.combineLatest(listName, itemCount, new Func2<String, Long, String>() {
                    @Override
                    public String call(String listName, Long itemCount) {
                        return listName + " (" + itemCount + ")";
                    }
                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String title) {
                                getActivity().setTitle(title);
                            }
                        }));


        SqlDelightStatement listQuery = TodoItem.FACTORY.list_query(getListId());

        //根据todo_list_id查询todo_item表的所有的数据，更新到适配器
        subscriptions.add(db.createQuery(TodoItem.TABLE_NAME, listQuery.statement, listQuery.args)
                .mapToList(TodoItem.MAPPER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter));


    }

    @Override
    public void onPause() {
        super.onPause();
        //解除订阅多个subscriptions
        subscriptions.unsubscribe();
    }
}
