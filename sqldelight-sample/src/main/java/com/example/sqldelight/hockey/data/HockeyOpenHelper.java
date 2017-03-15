package com.example.sqldelight.hockey.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.GregorianCalendar;


/**
 * SQLiteOpenHelper实现类，单例
 */
public final class HockeyOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    private static HockeyOpenHelper instance;

    public static HockeyOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HockeyOpenHelper(context);
        }
        return instance;
    }

    public HockeyOpenHelper(Context context) {
        super(context, null, null, DATABASE_VERSION);
    }

    //数据库第一次创建的时候回调
    @Override
    public void onCreate(SQLiteDatabase db) {
        //执行创建表操作
        db.execSQL(Team.CREATE_TABLE);
        db.execSQL(Player.CREATE_TABLE);

        //初始化插入玩家对象
        Player.Insert_player insertPlayer = new PlayerModel.Insert_player(db, Player.FACTORY);
        //初始化插入队伍对象
        Team.Insert_team insertTeam = new Team.Insert_team(db, Team.FACTORY);
        //初始化更新队长对象
        Team.Update_captain updateCaptain = new TeamModel.Update_captain(db);

        // 初始化填充数据

        //插入队伍
        insertTeam.bind("Anaheim Ducks", new GregorianCalendar(1993, 3, 1), "Randy Carlyle", true);
        //返回影响行id
        long ducks = insertTeam.program.executeInsert();

        //插入玩家
        insertPlayer.bind("Corey", "Perry", 10, ducks, 30, 210, new GregorianCalendar(1985, 5, 16),
                Player.Shoots.RIGHT, Player.Position.RIGHT_WING);
        insertPlayer.program.executeInsert();

        insertPlayer.bind("Ryan", "Getzlaf", 15, ducks, 30, 221, new GregorianCalendar(1985, 5, 10),
                Player.Shoots.RIGHT, Player.Position.CENTER);
        long getzlaf = insertPlayer.program.executeInsert();

        //更新队长
        updateCaptain.bind(getzlaf, ducks);
        updateCaptain.program.execute();

        //继续插入队伍，下面套路都一样了
        insertTeam.bind("Pittsburgh Penguins", new GregorianCalendar(1966, 2, 8), "Mike Sullivan", true);
        long pens = insertTeam.program.executeInsert();

        insertPlayer.bind("Sidney", "Crosby", 87, pens, 28, 200, new GregorianCalendar(1987, 8, 7),
                Player.Shoots.LEFT, Player.Position.CENTER);
        long crosby = insertPlayer.program.executeInsert();

        updateCaptain.bind(crosby, pens);
        updateCaptain.program.execute();

        insertTeam.bind("San Jose Sharks", new GregorianCalendar(1990, 5, 5), "Peter DeBoer", false);
        long sharks = insertTeam.program.executeInsert();

        insertPlayer.bind("Patrick", "Marleau", 12, sharks, 36, 220, new GregorianCalendar(1979, 9, 15),
                Player.Shoots.LEFT, Player.Position.LEFT_WING);
        insertPlayer.program.executeInsert();

        insertPlayer.bind("Joe", "Pavelski", 8, sharks, 31, 194, new GregorianCalendar(1984, 7, 18),
                Player.Shoots.RIGHT, Player.Position.CENTER);
        long pavelski = insertPlayer.program.executeInsert();

        updateCaptain.bind(pavelski, sharks);
        updateCaptain.program.execute();
    }

    //数据库打开的时候回调
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //设置外键限制为开启，从3.6.19开始，默认的FK强制限制是OFF。
        db.execSQL("PRAGMA foreign_keys=ON");
    }


    /**
     * 更新数据库版本的时候回调
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:      //更新教练
                Team.Update_coach_for_team updateCoachForTeam = new TeamModel.Update_coach_for_team(db);
                updateCoachForTeam.bind("Randy Carlyle", "Anaheim Ducks");
                updateCoachForTeam.program.execute();
        }
    }


}
