package com.slackow.hypigeon.games.chess;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.core.GameGui;
import com.slackow.hypigeon.gui.component.InvisibleButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;

import static com.github.bhlangonijr.chesslib.File.allFiles;
import static com.github.bhlangonijr.chesslib.Piece.NONE;
import static com.github.bhlangonijr.chesslib.Piece.*;
import static com.github.bhlangonijr.chesslib.PieceType.PAWN;
import static com.github.bhlangonijr.chesslib.Rank.*;
import static com.slackow.hypigeon.Reference.CHESS;
import static com.slackow.hypigeon.Reference.NEXT_MOVE;

@SideOnly(Side.CLIENT)
public class ChessGui extends GameGui<ChessSession> {


    public static final ResourceLocation texture = new ResourceLocation(HyPigeon.MODID, "textures/gui/chess.png");
    static final int guiWidth = 128;
    static final int guiHeight = 128;

    public ChessGui(ChessSession session) {
        super(session);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int centerX = width / 2 - 64;
        int centerY = height / 2 - 64;

        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(centerX, centerY, session.doIMoveFirst() ? 0 : 128, 0, guiWidth, guiHeight);
        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX, centerY, 0);

        Board board = session.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square sq = Square.encode(allRanks[i], allFiles[j]);
                Piece piece = board.getPiece(sq);
                if (piece != NONE) {
                    int x = !session.doIMoveFirst() ? j * 16 : 7 * 16 - j * 16;
                    int y = !session.doIMoveFirst() ? i * 16 : 7 * 16 - i * 16;
                    displayPiece(piece, x, y);
                }
            }
        }
        if (selectedSlot >= 0) {
            session.legalMoves().stream()
                    .filter(move -> move.getFrom().ordinal() == selectedSlot)
                    .map(Move::getTo)
                    .forEach(to -> drawTexturedModalRect(
                            !session.doIMoveFirst() ? to.getFile().ordinal() * 16 : 7 * 16 - to.getFile().ordinal() * 16,
                            !session.doIMoveFirst() ? to.getRank().ordinal() * 16 : 7 * 16 - to.getRank().ordinal() * 16,
                            96, 128,
                            16, 16));
        }
        GlStateManager.popMatrix();
        buttonList.stream().filter(button -> button.id >= 0).forEach(button -> button.enabled = !getSession().isLocked());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    private void displayPiece(Piece piece, int x, int y) {
        drawTexturedModalRect(x, y, piece.getPieceType().ordinal() * 16, 144 - piece.getPieceSide().ordinal() * 16, 16, 16);
    }

    @Override
    public void initGui() {
        int centerX = width / 2 - 64;
        int centerY = height / 2 - 64;

        for (int i = 0; i < allRanks.length - 1; i++) {
            for (int j = 0; j < allFiles.length - 1; j++) {
                buttonList.add(new InvisibleButton(j * 8 + i, centerX + i * 16, centerY + j * 16, 16, 16));
            }
        }

        if (getSession().isLocked()) {
            buttonList.forEach(button -> button.enabled = false);
        }
        super.initGui();
    }

    private byte selectedSlot = -1;




    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int spot = button.id;
        if (session.doIMoveFirst()) spot = 63 - spot;
        if (spot >= 0) {
            Square to = Square.squareAt(spot);
            Piece piece = session.getBoard().getPiece(to);
            if (selectedSlot >= 0) {
                Square from = Square.squareAt(selectedSlot);
                Move move;
                Piece fromPiece = session.getBoard().getPiece(from);
                if (from.getRank() == (session.doIMoveFirst() ? RANK_7 : RANK_2) &&
                        fromPiece.getPieceType() == PAWN) {
                    move = new Move(from, to, session.doIMoveFirst() ? WHITE_QUEEN : BLACK_QUEEN);
                } else {
                    move = new Move(from, to);
                }
                if (session.legalMoves().contains(move)) {
                    byte[] data = new byte[]{CHESS, NEXT_MOVE, selectedSlot, (byte) spot, (byte) (move.getPromotion().ordinal())};
                    HyPigeon.sendPacket(getOpponent(), data);
                    selectedSlot = -1;
                } else {
                    selectedSlot = piece == NONE || spot == selectedSlot ? -1 : (byte) spot;
                }
            } else {
                selectedSlot = piece == NONE ? -1 : (byte) spot;
            }
            System.out.println(selectedSlot);
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
