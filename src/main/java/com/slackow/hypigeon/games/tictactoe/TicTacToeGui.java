package com.slackow.hypigeon.games.tictactoe;

import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.core.GameGui;
import com.slackow.hypigeon.gui.component.InvisibleButton;
import com.slackow.hypigeon.util.CancelableRunnable;
import com.slackow.hypigeon.util.Task;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

import static com.slackow.hypigeon.Reference.NEXT_MOVE;
import static com.slackow.hypigeon.Reference.TIC_TAC_TOE;

@SideOnly(Side.CLIENT)
public class TicTacToeGui extends GameGui<TicTacToeSession> {

    public static final ResourceLocation texture = new ResourceLocation(HyPigeon.MODID, "textures/gui/tictactoe.png");
    static final int guiWidth = 128;
    static final int guiHeight = 128;

    private Task autoClose;

    public TicTacToeGui(TicTacToeSession session) {
        super(session);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        mc.renderEngine.bindTexture(texture);
        int centerX = width / 2 - 64;
        int centerY = height / 2 - 64;

        drawTexturedModalRect(centerX, centerY, 0, 0, guiWidth, guiHeight);

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(centerX, centerY, 0);
            char[][] data = getSession().getData();
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    if (data[i][j] != ' ') {
                        int textureX = data[i][j] == 'O' ?  44 : 0;
                        drawTexturedModalRect(i * 44, j * 44, textureX, 128, 40, 40);
                    }
                }
            }
            buttonList.stream().filter(b -> b.id >= 0 && b.id < 9).forEach(b -> b.enabled = !getSession().isLocked() && data[b.id % 3][b.id / 3] == ' ');
        }
        GlStateManager.popMatrix();

        int state = getSession().getState();
        if (state != 0) {
            if (autoClose == null) {
                HyPigeon.activeSessions.remove(getSession());
                autoClose = new Task(new CancelableRunnable(() -> {}), 99);
            }
            if (state > 0) {
                if (state <= 3) {
                    drawTexturedModalRect(centerX + state * 44 - 26, centerY, 111, 128, 4, 128);
                } else if (state <= 6) {
                    drawTexturedModalRect(centerX, centerY + state * 44 - 4 * 44 + 18, 0, 180, 128, 4);
                } else if (state <= 7) {
                    drawTexturedModalRect(centerX, centerY, 128, 128, 128, 128);
                } else if (state <= 8) {
                    drawTexturedModalRect(centerX, centerY, 128, 0, 128, 128);
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        int centerX = width / 2 - 64;
        int centerY = height / 2 - 64;
        buttonList.add(new InvisibleButton(0, centerX, centerY, 40, 40));
        buttonList.add(new InvisibleButton(1, centerX + 40 + 3, centerY, 42, 40));
        buttonList.add(new InvisibleButton(2, centerX + 43 + 45, centerY, 40, 40));

        buttonList.add(new InvisibleButton(3, centerX, centerY + 43, 40, 42));
        buttonList.add(new InvisibleButton(4, centerX + 40 + 3, centerY + 43, 42, 42));
        buttonList.add(new InvisibleButton(5, centerX + 43 + 45, centerY + 43, 40, 42));

        buttonList.add(new InvisibleButton(6, centerX, centerY + 43 + 45, 40, 40));
        buttonList.add(new InvisibleButton(7, centerX + 40 + 3, centerY + 43 + 45, 42, 40));
        buttonList.add(new InvisibleButton(8, centerX + 43 + 45, centerY + 43 + 45, 40, 40));
        if (getSession().isLocked()) {
            buttonList.forEach(button -> button.enabled = false);
        }
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 0) {
            byte[] data = new byte[]{TIC_TAC_TOE, NEXT_MOVE, (byte) button.id};
            HyPigeon.sendPacket(getOpponent(), data);
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
