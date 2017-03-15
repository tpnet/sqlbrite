package com.tpnet.sqlbrite_sqldelight_sample.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;
import com.tpnet.sqlbrite_sqldelight_sample.TodoItemModel;

import rx.functions.Func1;

/**
 * Created by litp on 2017/3/15.
 */

@AutoValue
public abstract class TodoItem implements TodoItemModel {


    public final static Factory<TodoItem> FACTORY = new Factory<>(new Creator<TodoItem>() {
        @Override
        public TodoItem create(long _id, @Nullable Long todo_list_id, @NonNull String description, boolean complete) {
            return new AutoValue_TodoItem(_id, todo_list_id, description, complete);
        }
    });

    //查询全部的行映射
    public final static RowMapper<TodoItem> ROW_LIST_QUERY_MAPPER = FACTORY.list_queryMapper();


    //查询数量的行映射
    public final static RowMapper<Long> ROW_COUNT_QUERY_MAPPER = FACTORY.count_queryMapper();


    public static final Func1<Cursor, TodoItem> MAPPER = new Func1<Cursor, TodoItem>() {
        @Override
        public TodoItem call(Cursor cursor) {
            return ROW_LIST_QUERY_MAPPER.map(cursor);
        }
    };


}
