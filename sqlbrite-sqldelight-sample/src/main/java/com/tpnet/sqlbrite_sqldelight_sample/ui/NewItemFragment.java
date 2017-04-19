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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.sqlbrite.BriteDatabase;
import com.tpnet.sqlbrite_sqldelight_sample.BaseApplication;
import com.tpnet.sqlbrite_sqldelight_sample.R;
import com.tpnet.sqlbrite_sqldelight_sample.TodoItemModel;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static butterknife.ButterKnife.findById;

/**
 * 新建item的Fragmnet Dialog，
 */
public final class NewItemFragment extends DialogFragment {
    private static final String KEY_LIST_ID = "list_id";


    //单例
    public static NewItemFragment newInstance(long listId) {
        Bundle arguments = new Bundle();
        arguments.putLong(KEY_LIST_ID, listId);

        NewItemFragment fragment = new NewItemFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    
    private final PublishSubject<String> createClicked = PublishSubject.create();

    @Inject
    BriteDatabase db;

    private long getListId() {
        return getArguments().getLong(KEY_LIST_ID);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BaseApplication.getAppComponent(context).inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        View view = LayoutInflater.from(context).inflate(R.layout.new_item, null);

        EditText name = findById(view, android.R.id.input);
        Observable.combineLatest(createClicked, RxTextView.textChanges(name),
                new Func2<String, CharSequence, String>() {
                    @Override
                    public String call(String ignored, CharSequence text) {
                        return text.toString();
                    }
                }) //
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String description) {

                        //初始化插入对象
                        TodoItemModel.Insert_item insertItem = new TodoItemModel.Insert_item(db.getWritableDatabase());

                        //填入数据
                        insertItem.bind(getListId(), description, false);
                        //开始插入，这里会Rxjava会异步回调，进行更新列表
                        db.executeInsert(insertItem.table, insertItem.program);

                        //注意这里不要使用下面的插入代码，会导致Rxjava不能异步回调发送数据
                        //insertItem.program.executeInsert();

                    }
                });


        return new AlertDialog.Builder(context) //
                .setTitle(R.string.new_item)
                .setView(view)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createClicked.onNext("clicked");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                    }
                })
                .create();
    }
}
