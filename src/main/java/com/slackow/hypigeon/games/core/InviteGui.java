package com.slackow.hypigeon.games.core;

import com.slackow.hypigeon.gui.MainGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import static com.slackow.hypigeon.Reference.RANDOM;
import static com.slackow.hypigeon.gui.MainGui.ICON_SIZE;

public abstract class InviteGui extends HyPigeonGui {
    protected final String name;
    private final ItemStack skull;
    private final int x;
    private final int y;
    private final String game;

    public InviteGui(String name, int x, int y, String game) {
        this.name = name;
        skull = MainGui.getSkull(name);
        this.x = x;
        this.y = y;
        this.game = game;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        GlStateManager.pushMatrix();
        //noinspection IntegerDivisionInFloatingPointContext
        GlStateManager.translate(width / 2 - 32 - fontRendererObj.getStringWidth(name) / 2, 20 - 16, 0);
        GlStateManager.scale(2, 2, 2);
        mc.getRenderItem().renderItemIntoGUI(skull, 0, 0);
        GlStateManager.popMatrix();

        mc.renderEngine.bindTexture(MainGui.ICONS);

        drawTexturedModalRect(width / 2 - ICON_SIZE - 10, height / 2 - ICON_SIZE / 2, x, y, ICON_SIZE, ICON_SIZE);
        drawCenteredString(fontRendererObj, name, width / 2, 20 - fontRendererObj.FONT_HEIGHT / 2, 0xFFFF00);
        drawCenteredString(fontRendererObj, game, width / 2 - ICON_SIZE / 2 - 10, height / 2 - 32 - fontRendererObj.FONT_HEIGHT, 0x00FF00);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected static byte randomizeIfRandom(int setting) {
        return (byte) (setting == RANDOM ? (setting + Math.round(Math.random())) : setting);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == -1) {
            mc.displayGuiScreen(new MainGui(name));
        }
        if (button.id == 0) {
            sendInvite();
            mc.displayGuiScreen(null);
        }
    }

    protected abstract void sendInvite();

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(-1, 10, 10, 20, 20, "<-"));
        buttonList.add(new GuiButton(0, width / 2 + 10, height / 2 + ICON_SIZE / 2 - 20, 100, 20, "Send invite"));
        super.initGui();
    }
}
