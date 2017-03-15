package com.example.sqldelight.hockey.data;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

import java.util.Calendar;

/**
 * 队伍的bean，使用AutoValue生成代码
 */
@AutoValue
public abstract class Team implements TeamModel {

    //日期适配器
    private static final DateAdapter DATE_ADAPTER = new DateAdapter();


    //构建器
    public static final Factory<Team> FACTORY = new Factory<>(new Creator<Team>() {
        @Override
        public Team create(long Id, String name, Calendar founded, String coach, Long captain,
                           boolean wonCup) {
            return new AutoValue_Team(Id, name, founded, coach, captain, wonCup);
        }
    }, DATE_ADAPTER);

    //行映射
    public static final RowMapper<Team> MAPPER = FACTORY.select_allMapper();
}
