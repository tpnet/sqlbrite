package com.example.sqldelight.hockey.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.sqldelight.hockey.R;
import com.example.sqldelight.hockey.data.HockeyOpenHelper;
import com.example.sqldelight.hockey.data.Player;
import com.squareup.sqldelight.SqlDelightStatement;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 玩家页面
 */
public final class PlayersActivity extends Activity {
    public static final String TEAM_ID = "team_id";

    @BindView(R.id.list)
    ListView players;    //玩家列表

    private Cursor playersCursor;   //玩家Cursor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        ButterKnife.bind(this);

        //获取数据库
        SQLiteDatabase db = HockeyOpenHelper.getInstance(this).getReadableDatabase();

        //判断是不是在TeamActivity打开的，传来了team_id
        long teamId = getIntent().getLongExtra(TEAM_ID, -1);
        if (teamId == -1) {
            //查询所有的玩家
            playersCursor = db.rawQuery(Player.FACTORY.select_all().statement, new String[0]);
        } else {
            //查询指定队伍的玩家
            SqlDelightStatement playerForTeam = Player.FACTORY.for_team(teamId);
            playersCursor = db.rawQuery(playerForTeam.statement, playerForTeam.args);
        }
        //显示
        players.setAdapter(new PlayersAdapter(this, playersCursor));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playersCursor.close();
    }


    /**
     * 列表适配器
     */
    private static final class PlayersAdapter extends CursorAdapter {
        public PlayersAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return View.inflate(context, R.layout.player_row, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Player.ForTeam playerForTeam = Player.FOR_TEAM_MAPPER.map(cursor);
            ((PlayerRow) view).populate(playerForTeam.player(), playerForTeam.team());
        }
    }
}
