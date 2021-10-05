package com.slackow.hypigeon.games.chess;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.slackow.hypigeon.games.core.AbstractGame;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Optional;

import static com.github.bhlangonijr.chesslib.Piece.allPieces;
import static com.slackow.hypigeon.HyPigeon.activeSessions;
import static com.slackow.hypigeon.HyPigeon.openedGuiNextTick;
import static com.slackow.hypigeon.OnChatReceived.text;
import static com.slackow.hypigeon.Reference.*;
import static net.minecraft.util.EnumChatFormatting.*;

public class ChessGame extends AbstractGame {
    public static final ChessGame INSTANCE = new ChessGame();

    protected ChessGame() {
        super(CHESS);
    }

    @Override
    public String getName() {
        return "Chess";
    }

    @Override
    public IChatComponent invite(String fromTo, String opponent, byte[] data) {
        IChatComponent feedback;
        if ("To".equals(fromTo)) {
            feedback = text(GREEN + "Sent Chess(" +
                    ChessInviteGui.whoGoesFirst[MathHelper.clamp_int(data[WHO_GOES_FIRST], 0, 2)] + ") Invite to " + opponent);
        } else {
            byte who = (byte) (data[WHO_GOES_FIRST] ^ 1);
            feedback = text(DARK_PURPLE + opponent + GREEN + " has sent you an invite for TicTacToe(" +
                    ChessInviteGui.whoGoesFirst[MathHelper.clamp_int(who, 0, 2)] + ")");
        }
        return feedback;
    }

    @Override
    public IChatComponent accept(String fromTo, String opponent, byte[] data, byte[] inviteData) {
        IChatComponent feedback;
        ChessSession game;
        if ("To".equals(fromTo)) {
            game = new ChessSession(opponent, (data[WHO_GOES_FIRST] & 1) == HOST_MOVES_SECOND);
            feedback = text(GREEN + "Accepted Invite from " + DARK_PURPLE + opponent);
        } else {
            game = new ChessSession(opponent, (inviteData[WHO_GOES_FIRST] & 1) == HOST_MOVES_FIRST);
            feedback = text(DARK_PURPLE + opponent + GREEN + " has accepted your invite");
        }
        openedGuiNextTick = new ChessGui(game);
        activeSessions.add(game);
        return feedback;
    }

    @Override
    public IChatComponent nextMove(String fromTo, String opponent, byte[] data) {
        IChatComponent feedback;
        Optional<ChessSession> chess = activeSessions.stream()
                .filter(game -> game.getOpponent().equals(opponent) && game instanceof ChessSession)
                .map(game -> (ChessSession) game)
                .findAny();
        ChessSession game;
        if ("To".equals(fromTo)) {
            if (chess.isPresent()) {
                game = chess.get();
                // from pos 1 to pos 2
                byte from = data[2];
                byte to = data[3];
                byte promote = data[4];
                // get sent time
                long time = data[5] << 6 | data[6] << 3 | data[7];
                boolean ignoreMove = game.isTimeControlled() && time == 0;
                if (ignoreMove || game.getBoard().doMove(new Move(Square.squareAt(from), Square.squareAt(to), allPieces[promote]))) {
                    game.lock();
                } else {
                    return text(RED + "Illegal chess move sent");
                }
                feedback = text(GREEN + "Sent Chess move to " + DARK_PURPLE + opponent);
            } else {
                return text(RED + "Sent move for game that doesn't exist");
            }
        } else {
            if (chess.isPresent()) {
                game = chess.get();
                byte from = data[2];
                byte to = data[3];
                byte promote = data[4];
                long time = data[5] << 6 | data[6] << 3 | data[7];

                boolean ignoreMove = false;
                if (game.isTimeControlled()) {
                    game.setTheirTimer(time);
                    ignoreMove = time == 0;
                }

                if (ignoreMove || game.getBoard().doMove(new Move(Square.squareAt(from), Square.squareAt(to), allPieces[promote]))) {
                    game.updateLast();
                    game.unlock();
                } else {
                    return text(RED + "Illegal chess move received");
                }
                feedback = text(GREEN + "Received Chess move from " + DARK_PURPLE + opponent);
            } else {
                return text(RED + "Received next move for unknown game");
            }
        }
        game.checkWin();
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
