package com.slackow.hypigeon.games.core;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class HyPigeonGui extends GuiScreen {

    public void refresh() {
        buttonList.clear();
        labelList.clear();
        initGui();
        super.updateScreen();
    }

    protected void onButtonRightClick(GuiButton button) {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 1) {
            Consumer<GuiButton> onButtonRightClick = this::onButtonRightClick;
            buttonList.stream().filter(GuiButton::isMouseOver).filter(b -> b.enabled && b.visible).findAny().ifPresent(onButtonRightClick
                    .andThen(button -> button.playPressSound(mc.getSoundHandler())));
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
