package com.example.sqldelight.hockey.data;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.EnumColumnAdapter;
import com.squareup.sqldelight.RowMapper;

import java.util.Calendar;

/**
 * 玩家bean，使用AutoValue进行自动生成bean代码
 */
@AutoValue
public abstract class Player implements PlayerModel {

    /**
     * 射击左还是右
     */
    public enum Shoots {
        RIGHT, LEFT
    }

    /**
     * 位置： 左翼、右翼，中锋、防御、守门员
     */
    public enum Position {
        LEFT_WING, RIGHT_WING, CENTER, DEFENSE, GOALIE
    }

    //Calendar适配器
    private static final DateAdapter DATE_ADAPTER = new DateAdapter();
    //Shoots枚举适配器
    private static final EnumColumnAdapter<Shoots> SHOOTS_ADAPTER = EnumColumnAdapter.create(Shoots.class);
    //Position枚举适配器
    private static final EnumColumnAdapter<Position> POSITION_ADAPTER = EnumColumnAdapter.create(Position.class);

    // 构建器，传入对应的适配器参数
    public static final Factory<Player> FACTORY = new Factory<>(new Creator<Player>() {
        @Override
        public Player create(long id, String firstName, String lastName, int number, Long team, int age,
                             float weight, Calendar birthDate, Shoots shoots, Position position) {
            return new AutoValue_Player(id, firstName, lastName, number, team, age, weight, birthDate,
                    shoots, position);
        }
    }, DATE_ADAPTER, SHOOTS_ADAPTER, POSITION_ADAPTER);

    //行映射
    public static final RowMapper<ForTeam> FOR_TEAM_MAPPER = FACTORY.for_teamMapper(
            new For_teamCreator<Player, Team, ForTeam>() {
                @Override
                public ForTeam create(Player player, Team team) {
                    return new AutoValue_Player_ForTeam(player, team);
                }
            }, Team.FACTORY);


    /**
     * for_team查询语句返回的bean
     */
    @AutoValue
    public static abstract class ForTeam implements For_teamModel<Player, Team> {
    }
}
