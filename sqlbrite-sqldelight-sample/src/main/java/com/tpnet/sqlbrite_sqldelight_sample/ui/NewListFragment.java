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
import com.tpnet.sqlbrite_sqldelight_sample.TodoListModel;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static butterknife.ButterKnife.findById;

public final class NewListFragment extends DialogFragment {


    public static NewListFragment newInstance() {
        return new NewListFragment();
    }

    //PublishSubject观察者，先订阅，后发送。
    private final PublishSubject<String> createClicked = PublishSubject.create();

    @Inject
    BriteDatabase db;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BaseApplication.getAppComponent(context).inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        View view = LayoutInflater.from(context).inflate(R.layout.new_list, null);

        EditText name = findById(view, android.R.id.input);

        //合并点击创建的事件和文本的内容事件
        Observable.combineLatest(createClicked, RxTextView.textChanges(name),
                new Func2<String, CharSequence, String>() {
                    @Override
                    public String call(String ignored, CharSequence text) {
                        return text.toString();
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String name) {
                        //初始化插入对象
                        TodoListModel.Insert_list insertList = new TodoListModel.Insert_list(db.getWritableDatabase());
                        insertList.bind(name, "");
                        db.executeInsert(insertList.table, insertList.program);
                        //这里不能使用下面的代码进行插入，会导致Rxjava不会异步回调
                        //insertList.program.executeInsert();
                    }
                });

        //创建弹窗
        return new AlertDialog.Builder(context) //
                .setTitle(R.string.new_list)
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
