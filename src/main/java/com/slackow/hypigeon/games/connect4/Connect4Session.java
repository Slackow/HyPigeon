package com.slackow.hypigeon.games.connect4;

import com.slackow.hypigeon.games.core.AbstractSession;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.stream.Stream;

public class Connect4Session extends AbstractSession<Connect4Session> {
    public Connect4Session(String opponent, boolean doIMoveFirst) {
        super(opponent, doIMoveFirst, Connect4Game.INSTANCE);
    }

    private final Boolean[][] data = new Boolean[7][6];

    private int turnCount;

    public boolean canDrop(int column) {
        return data[MathHelper.clamp_int(column, 0, data.length)][0] == null;
    }

    public int drop(int column) {
        column = MathHelper.clamp_int(column, 0, data.length);
        for (int i = 0; i < data[column].length; i++) {
            if (data[column][i] != null) {
                if (i == 0) {
                    return -1;
                }
                data[column][i - 1] = (turnCount & 1) == 0;
                turnCount++;
                return i - 1;
            }
        }
        int i = data[column].length - 1;
        data[column][i] = (turnCount & 1) == 0;
        turnCount++;
        return i;
    }

    public void checkWin() {
        if (turnCount >= 6 * 7) {
            state = -1;
            return;
        }
        // noinspection WrapperTypeMayBePrimitive
        Boolean coin = (turnCount & 1) != 0;
        // horizontal
        for (int i = 0; i < data.length - 3; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (Stream.of(data[i][j], data[i + 1][j], data[i + 2][j], data[i + 3][j]).allMatch(coin::equals)) {
                    state = i << 3 | j;
                    return;
                }

            }
        }
        // vertical
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length - 3; j++) {
                if (Arrays.stream(data[i]).skip(j).limit(4).allMatch(coin::equals)) {
                    state = 1 << 6 | i << 3 | j;
                    return;
                }
            }
        }
        // upward diagonal
        for (int i = 0; i < data.length - 3; i++) {
            for (int j = 3; j < data[i].length; j++) {
                if (Stream.of(data[i][j], data[i + 1][j - 1], data[i + 2][j - 2], data[i + 3][j - 3])
                        .allMatch(coin::equals)) {
                    state = 2 << 6 | i << 3 | j;
                    return;
                }
            }
        }
        // downward diagonal
        for (int i = 0; i < data.length - 3; i++) {
            for (int j = 0; j < data[i].length - 3; j++) {
                if (Stream.of(data[i][j], data[i + 1][j + 1], data[i + 2][j + 2], data[i + 3][j + 3])
                        .allMatch(coin::equals)) {
                    state = 3 << 6 | i << 3 | j;
                    return;
                }
            }
        }

    }


    public Boolean[][] getData() {
        return data;
    }
}
