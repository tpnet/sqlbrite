package com.tpnet.sqlbrite_sqldelight_sample.model;

import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;
import com.tpnet.sqlbrite_sqldelight_sample.TodoListModel;

import rx.functions.Func1;

/**
 * 
 * Created by litp on 2017/3/15.
 */

@AutoValue
public abstract class TodoList implements TodoListModel, Parcelable {


    public final static Factory<TodoList> FACTORY = new Factory<>(new TodoListModel.Creator<TodoList>() {
        @Override
        public TodoList create(long _id, @NonNull String name, @Nullable String archived) {
            return new AutoValue_TodoList(_id, name, archived);

        }

    });


    @AutoValue
    public abstract static class ListsItem implements Parcelable, List_queryModel {

    }

    public static Func1<Cursor, ListsItem> LIST_ITEM_FUNC = new Func1<Cursor, ListsItem>() {
        @Override
        public ListsItem call(Cursor cursor) {
            return LIST_ITEM_MAPPER.map(cursor);
        }
    };

    //查询title映射
    public final static RowMapper<String> ROW_TITLE_MAPPER = FACTORY.title_queryMapper();



    public final static RowMapper<ListsItem> LIST_ITEM_MAPPER = FACTORY.list_queryMapper(new List_queryCreator<ListsItem>() {
        @Override
        public ListsItem create(long _id, @NonNull String name, long item_count) {
            return new AutoValue_TodoList_ListsItem(_id, name, item_count);
        }
    });

}
