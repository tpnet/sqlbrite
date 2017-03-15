package com.example.sqldelight.hockey.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.sqldelight.hockey.R;
import com.example.sqldelight.hockey.data.HockeyOpenHelper;
import com.example.sqldelight.hockey.data.Team;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 队伍activity
 */
public final class TeamsActivity extends Activity {
    @BindView(R.id.list)
    ListView teams;     //队伍列表listView

    private Cursor teamsCursor;   //队伍Cursor
    private Adapter adapter;      //列表适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        ButterKnife.bind(this);

        //获得一个可读的数据库
        SQLiteDatabase db = HockeyOpenHelper.getInstance(this).getReadableDatabase();
        //
        teamsCursor = db.rawQuery(Team.FACTORY.select_all().statement, new String[0]);
        //创建适配器
        adapter = new Adapter(this, teamsCursor);
        teams.setAdapter(adapter);

        //点击查看改队伍的玩家
        teams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamsActivity.this, PlayersActivity.class);
                //传递队伍id
                intent.putExtra(PlayersActivity.TEAM_ID, id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭Cursor
        teamsCursor.close();
    }


    //列表适配器
    private static final class Adapter extends CursorAdapter {
        public Adapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return View.inflate(context, R.layout.team_row, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //设置数据
            ((TeamRow) view).populate(Team.MAPPER.map(cursor));
        }
    }
}
