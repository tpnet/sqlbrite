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
package com.tpnet.sqlbrite_sqldelight_sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tpnet.sqlbrite_sqldelight_sample.TodoItemModel;
import com.tpnet.sqlbrite_sqldelight_sample.TodoListModel;
import com.tpnet.sqlbrite_sqldelight_sample.model.TodoItem;
import com.tpnet.sqlbrite_sqldelight_sample.model.TodoList;

import static com.tpnet.sqlbrite_sqldelight_sample.TodoListModel.CREATE_ITEM_LIST_ID_INDEX;

final class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;


    //构造器，创建数据库
    public DbOpenHelper(Context context) {
        super(context, "todo.db", null /* factory */, VERSION);
    }


    //创建数据库的时候回调
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建对应的表和索引
        db.execSQL(TodoList.CREATE_TABLE);
        db.execSQL(TodoItem.CREATE_TABLE);
        db.execSQL(CREATE_ITEM_LIST_ID_INDEX);

        //初始化插入实体
        TodoList.Insert_list insertList = new TodoListModel.Insert_list(db);
        TodoItem.Insert_item insertItem = new TodoItemModel.Insert_item(db);


        //插入数据到todo_list表,返回id
        insertList.bind("Grocery List", "0");
        long groceryListId = insertList.program.executeInsert();


        //根据todo_list表的id，插入数据到todo_item
        insertItem.bind(groceryListId, "Beer", false);
        insertItem.program.executeInsert();

        insertItem.bind(groceryListId, "Point Break on DVD", false);
        insertItem.program.executeInsert();

        insertItem.bind(groceryListId, "Bad Boys 2 on DVD", false);
        insertItem.program.executeInsert();


        //和上面的套路一样
        insertList.bind("Holiday Presents", "0");
        long holidayPresentsListId = insertList.program.executeInsert();

        insertItem.bind(holidayPresentsListId, "Pogo Stick for Jake W.", false);
        insertItem.program.executeInsert();

        insertItem.bind(holidayPresentsListId, "Jack-in-the-box for Alec S.", false);
        insertItem.program.executeInsert();

        insertItem.bind(holidayPresentsListId, "Pogs for Matt P.", false);
        insertItem.program.executeInsert();

        insertItem.bind(holidayPresentsListId, "Cola for Jesse W.", false);
        insertItem.program.executeInsert();


        insertList.bind("Work Items", "0");
        long workListId = insertList.program.executeInsert();
        insertItem.bind(workListId, "Finish SqlBrite library", true);
        insertItem.program.executeInsert();

        insertItem.bind(workListId, "Finish SqlBrite sample app", false);
        insertItem.program.executeInsert();

        insertItem.bind(workListId, "Finish SqlBrite GitHub", false);
        insertItem.program.executeInsert();


        insertList.bind("Birthday Presents", "1");
        long birthdayPresentsListId = insertList.program.executeInsert();
        insertItem.bind(birthdayPresentsListId, "New car", true);
        insertItem.program.executeInsert();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
