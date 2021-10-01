package com.slackow.hypigeon.games.connect4;

import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.core.InviteGui;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.Random;

import static com.slackow.hypigeon.Reference.*;
import static com.slackow.hypigeon.gui.MainGui.ICON_SIZE;

public class Connect4InviteGui extends InviteGui {
    public Connect4InviteGui(String name) {
        super(name, ICON_SIZE * 3, 0, Connect4Game.INSTANCE.getName());
    }

    private int setting = 0;

    @Override
    protected void sendInvite() {
        byte setting = randomizeIfRandom(this.setting);
        byte[] data = new byte[]{CONNECT4, INVITE, setting};
        HyPigeon.sendPacket(name, data);
    }

    public static final String[] whoGoesFirst = {"You as Red", "You as Yellow", "Random"};

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(1, width / 2 + 10, height / 2 + ICON_SIZE / 2 - 40, 100, 20, whoGoesFirst[setting]));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            setting = (setting + 1) % whoGoesFirst.length;
            button.displayString = whoGoesFirst[setting];
        }
        super.actionPerformed(button);
    }

    @Override
    protected void onButtonRightClick(GuiButton button) {
        if (button.id == 1) {
            setting = (setting + whoGoesFirst.length - 1) % whoGoesFirst.length;
            button.displayString = whoGoesFirst[setting];
        }
        super.onButtonRightClick(button);
    }
}
