package com.slackow.hypigeon.gui.component;

import com.slackow.hypigeon.HyPigeon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class InvisibleButton extends GuiButton {

    private boolean showOnHover = true;
    private boolean showOnClick = true;

    public InvisibleButton(int buttonId, int x, int y, int widthIn, int heightIn) {
        super(buttonId, x, y, widthIn, heightIn, "");
    }

    public InvisibleButton noHover() {
        this.showOnHover = false;
        return this;
    }

    public InvisibleButton hoverWithNoPress(){
        this.showOnClick = false;
        return this;
    }

    private static final ResourceLocation texture = new ResourceLocation(HyPigeon.MODID, "textures/gui/invisiblebutton.png");

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        if (showOnHover && enabled && hovered && visible && (showOnClick || !Mouse.isButtonDown(0))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            GlStateManager.color(1, 1, 1, 0.5f);
            mc.getTextureManager().bindTexture(texture);

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 86, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 86, this.width / 2 + (this.width & 1), this.height);

            GlStateManager.popMatrix();
        }
    }
}
