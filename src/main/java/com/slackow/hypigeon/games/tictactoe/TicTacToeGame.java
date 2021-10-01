package com.slackow.hypigeon.games.tictactoe;

import com.slackow.hypigeon.games.core.AbstractGame;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Optional;

import static com.slackow.hypigeon.HyPigeon.activeSessions;
import static com.slackow.hypigeon.HyPigeon.openedGuiNextTick;
import static com.slackow.hypigeon.OnChatReceived.text;
import static com.slackow.hypigeon.Reference.*;
import static net.minecraft.util.EnumChatFormatting.*;

public class TicTacToeGame extends AbstractGame {
    public static final TicTacToeGame INSTANCE = new TicTacToeGame();

    protected TicTacToeGame() {
        super(TIC_TAC_TOE);
    }

    @Override
    public String getName() {
        return "TicTacToe";
    }

    @Override
    public IChatComponent invite(String fromTo, String opponent, byte[] data) {
        IChatComponent feedback;
        if ("To".equals(fromTo)) {
            feedback = text(GREEN + "Sent TicTacToe(" +
                    TicTacToeInviteGui.whoGoesFirst[MathHelper.clamp_int(data[WHO_GOES_FIRST], 0, 2)] + ") Invite to " + opponent);
        } else {
            byte who = (byte) (data[WHO_GOES_FIRST] ^ 1);
            feedback = text(DARK_PURPLE + opponent + GREEN + " has sent you an invite for TicTacToe(" +
                    TicTacToeInviteGui.whoGoesFirst[MathHelper.clamp_int(who, 0, 2)] + ")");
        }
        return feedback;
    }

    @Override
    public IChatComponent accept(String fromTo, String opponent, byte[] data, byte[] inviteData) {
        IChatComponent feedback;
        TicTacToeSession game;
        if ("To".equals(fromTo)) {
            game = new TicTacToeSession(opponent, (data[WHO_GOES_FIRST] & 1) == HOST_MOVES_SECOND);
            feedback = text(GREEN + "Accepted Invite from " + DARK_PURPLE + opponent);
        } else {
            game = new TicTacToeSession(opponent, (inviteData[WHO_GOES_FIRST] & 1) == HOST_MOVES_FIRST);
            feedback = text(DARK_PURPLE + opponent + GREEN + " has accepted your invite");
        }
        openedGuiNextTick = new TicTacToeGui(game);
        activeSessions.add(game);
        return feedback;
    }

    @Override
    public IChatComponent nextMove(String fromTo, String opponent, byte[] data) {
        IChatComponent feedback;
        Optional<TicTacToeSession> ticTacToe = activeSessions.stream()
                .filter(game -> game.getOpponent().equals(opponent) && game instanceof TicTacToeSession)
                .map(game -> (TicTacToeSession) game)
                .findAny();
        if ("To".equals(fromTo)) {
            if (ticTacToe.isPresent()) {
                TicTacToeSession game = ticTacToe.get();
                byte move = data[2];
                if (game.tryMove(move % 3, move / 3, game.getPlayer())) {
                    game.lock();
                }
                feedback = text(GREEN + "Sent Tic Tac Toe move to " + DARK_PURPLE + opponent);
            } else {
                return text(RED + "Sent move for game that doesn't exist");
            }
        } else {
            if (ticTacToe.isPresent()) {
                TicTacToeSession game = ticTacToe.get();
                byte move = data[2];
                if (game.tryMove(move % 3, move / 3, game.doIMoveFirst() ? 'O' : 'X')) {
                    game.unlock();
                }
                feedback = text(GREEN + "Received Tic Tac Toe move from " + DARK_PURPLE + opponent);
            } else {
                return text(RED + "Received next move for unknown game");
            }
        }
        ticTacToe.get().checkWin();
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
