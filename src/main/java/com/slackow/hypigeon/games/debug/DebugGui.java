package com.slackow.hypigeon.games.debug;

import com.slackow.hypigeon.games.core.GameGui;
import com.slackow.hypigeon.games.tictactoe.TicTacToeGui;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class DebugGui extends GameGui<DebugSession> {
    public DebugGui() {
        this("Kihron");
    }

    public DebugGui(String name) {
        super(new DebugSession(name));
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        mc.renderEngine.bindTexture(TicTacToeGui.texture);
        drawTexturedModalRect(width / 2 - 64, height / 2 - 64, 0, 0, 128, 128);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0, 0, height/2, fontRendererObj.getStringWidth("Toggle Win Screen") + 10, 20, "Toggle Win Screen"));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            getSession().toggle();
        }
        super.actionPerformed(button);
    }
}
