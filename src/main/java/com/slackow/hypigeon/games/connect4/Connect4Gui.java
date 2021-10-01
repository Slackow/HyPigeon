package com.slackow.hypigeon.games.connect4;

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

import static com.slackow.hypigeon.Reference.CONNECT4;
import static com.slackow.hypigeon.Reference.NEXT_MOVE;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
@SideOnly(Side.CLIENT)
public class Connect4Gui extends GameGui<Connect4Session> {

    public static final ResourceLocation texture = new ResourceLocation(HyPigeon.MODID, "textures/gui/connect4.png");
    private Task autoClose;


    public Connect4Gui(Connect4Session session) {
        super(session);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        mc.renderEngine.bindTexture(texture);

        drawTexturedModalRect(width / 2 - 64, height / 2 - 64, 0, 0, 128, 128);
        Connect4Session session = getSession();
        Boolean[][] data = session.getData();

        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2 - 64, height / 2 - 64, 0);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] != null) {
                    drawTexturedModalRect(i * 16 + 8 + 2, j * 16 + 16 + 2, data[i][j] ? 128 : 128 + 12, 0, 12, 12);
                }
            }
        }
        GlStateManager.popMatrix();

        int state = getSession().getState();

        if (state != 0) {
            if (autoClose == null) {
                HyPigeon.activeSessions.remove(getSession());
                autoClose = new Task(new CancelableRunnable(() -> {}), 59);
            }
            if (state > 0) {
                int type = state >> 6;
                int col = state >> 3 & 0b111;
                int row = state & 0b111;
                for (int i = 0; i < 4; i++) {
                    drawTexturedModalRect(width / 2 - 64 + col * 16 + 8 + 2, height / 2 - 64 + row * 16 + 16 + 2, 128 + 24, 0, 12, 12);
                    switch (type) {
                        case 0:
                            col++;
                            break;
                        case 1:
                            row++;
                            break;
                        case 2:
                            col++;
                            row--;
                            break;
                        case 3:
                            col++;
                            row++;
                            break;
                    }
                }
            }
        }

        if (!session.isLocked()) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1, 0.5f);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            buttonList.stream().filter(GuiButton::isMouseOver).filter(button -> button.id >= 0 && button.id < 7).findAny().ifPresent(button -> {
                if (session.canDrop(button.id)) {
                    drawTexturedModalRect(button.xPosition + 2, height / 2 - 62, session.doIMoveFirst() ? 128 : 128 + 12, 0, 12, 12);
                }
            });
            GlStateManager.popMatrix();
        }
        buttonList.stream().filter(b -> b.id >= 0 && b.id < 7).forEach(b -> b.enabled = !session.isLocked() && session.canDrop(b.id));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        for (int i = 0; i < 7; i++) {
            buttonList.add(new InvisibleButton(i, width / 2 - 64 + 8 + i * 16, height / 2 - 64, 16, 128).noHover());
        }
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 0 && button.id < 7) {
            if (!getSession().waitingOnOpponent()) {
                byte[] data = {CONNECT4, NEXT_MOVE, (byte) button.id};
                HyPigeon.sendPacket(getOpponent(), data);
            }
        }
        super.actionPerformed(button);
    }
}
