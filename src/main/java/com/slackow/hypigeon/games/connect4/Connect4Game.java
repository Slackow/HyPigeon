package com.slackow.hypigeon.games.connect4;

import com.slackow.hypigeon.games.core.AbstractGame;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Optional;

import static com.slackow.hypigeon.HyPigeon.activeSessions;
import static com.slackow.hypigeon.HyPigeon.openedGuiNextTick;
import static com.slackow.hypigeon.OnChatReceived.text;
import static com.slackow.hypigeon.Reference.*;
import static net.minecraft.util.EnumChatFormatting.*;

public class Connect4Game extends AbstractGame {
    public static final Connect4Game INSTANCE = new Connect4Game();

    protected Connect4Game() {
        super(CONNECT4);
    }

    @Override
    public String getName() {
        return "Connect 4";
    }

    @Override
    public IChatComponent invite(String fromTo, String opponent, byte[] data) {
        if ("To".equals(fromTo)) {
            return text(GREEN + "Sent Connect4(" +
                    Connect4InviteGui.whoGoesFirst[MathHelper.clamp_int(data[WHO_GOES_FIRST], 0, 2)] + ") Invite to " + opponent);
        } else {
            byte who = (byte) (data[WHO_GOES_FIRST] ^ 1);
            return text(DARK_PURPLE + opponent + GREEN + " has sent you an invite for TicTacToe(" +
                    Connect4InviteGui.whoGoesFirst[MathHelper.clamp_int(who, 0, 2)] + ")");
        }
    }

    @Override
    public IChatComponent accept(String fromTo, String opponent, byte[] data, byte[] inviteData) {
        IChatComponent feedback;
        Connect4Session game;
        if ("To".equals(fromTo)) {
            game = new Connect4Session(opponent, (data[WHO_GOES_FIRST] & 1) == HOST_MOVES_SECOND);
            feedback = text(GREEN + "Accepted Invite from " + DARK_PURPLE + opponent);
        } else {
            game = new Connect4Session(opponent, (inviteData[WHO_GOES_FIRST] & 1) == HOST_MOVES_FIRST);
            feedback = text(DARK_PURPLE + opponent + GREEN + " has accepted your invite");
        }
        openedGuiNextTick = new Connect4Gui(game);
        activeSessions.add(game);
        return feedback;
    }

    @Override
    public IChatComponent nextMove(String fromTo, String opponent, byte[] data) {
        IChatComponent feedback;
        Optional<Connect4Session> connect4 = activeSessions.stream()
                .filter(game -> game.getOpponent().equals(opponent) && game instanceof Connect4Session)
                .map(game -> (Connect4Session) game)
                .findAny();
        if ("To".equals(fromTo)) {
            if (connect4.isPresent()) {
                Connect4Session game = connect4.get();
                byte move = data[2];
                if (game.drop(move) >= 0) {
                    game.lock();
                }
                feedback = text(GREEN + "Sent Connect4 move to " + DARK_PURPLE + opponent);
            } else {
                return text(RED + "Sent move for game that doesn't exist");
            }
        } else {
            if (connect4.isPresent()) {
                Connect4Session game = connect4.get();
                byte move = data[2];
                if (game.drop(move) >= 0) {
                    game.unlock();
                }
                feedback = text(GREEN + "Received Tic Tac Toe move from " + DARK_PURPLE + opponent);
            } else {
                return text(RED + "Received next move for unknown game");
            }
        }
        connect4.get().checkWin();
        return feedback;
    }

    @Override
    public IChatComponent cancelInvite(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent deny(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent resume(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent save(String fromTo, String opponent, byte[] data) {
        return null;
    }
}
