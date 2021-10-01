package com.slackow.hypigeon.gui;

import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.chess.ChessGui;
import com.slackow.hypigeon.games.chess.ChessSession;
import com.slackow.hypigeon.games.core.AbstractSession;
import com.slackow.hypigeon.games.connect4.Connect4Session;
import com.slackow.hypigeon.games.core.HyPigeonGui;
import com.slackow.hypigeon.games.tictactoe.TicTacToeSession;
import com.slackow.hypigeon.games.connect4.Connect4Gui;
import com.slackow.hypigeon.games.tictactoe.TicTacToeGui;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class ManageGamesGui extends HyPigeonGui {
    private boolean blank;

    @Override
    public void initGui() {
        int buttonId = 0;
        for (AbstractSession game : HyPigeon.activeSessions) {
            String buttonText = game.getOpponent() + " in " + game.getGame().getName();
            int stringWidth = fontRendererObj.getStringWidth(buttonText);
            buttonList.add(new GuiButton(buttonId++, width / 2 - 25 - stringWidth / 2, height / 2 - 30 + 20 * buttonId, 50 + stringWidth, 20, buttonText));
        }
        blank = HyPigeon.activeSessions.isEmpty();
        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        if (blank) {
            drawCenteredString(fontRendererObj, "No Active Games :(", width / 2, height / 2, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        mc.displayGuiScreen(null);
        int id = button.id;
        AbstractSession game = HyPigeon.activeSessions.get(id);
        if (game instanceof TicTacToeSession) {
            mc.displayGuiScreen(new TicTacToeGui((TicTacToeSession) game));
        } else if (game instanceof Connect4Session) {
            mc.displayGuiScreen(new Connect4Gui(((Connect4Session) game)));
        } else if (game instanceof ChessSession) {
            mc.displayGuiScreen(new ChessGui(((ChessSession) game)));
        }
        super.actionPerformed(button);
    }
}
