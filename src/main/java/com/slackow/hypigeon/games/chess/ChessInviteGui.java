package com.slackow.hypigeon.games.chess;

import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.core.InviteGui;
import net.minecraft.client.gui.GuiButton;

import java.util.Random;

import static com.slackow.hypigeon.Reference.*;
import static com.slackow.hypigeon.gui.MainGui.ICON_SIZE;

public class ChessInviteGui extends InviteGui {

    public ChessInviteGui(String name) {
        super(name, 0, 0, ChessGame.INSTANCE.getName());
    }


    @Override
    protected void sendInvite() {
        byte setting = randomizeIfRandom(this.setting);
        byte[] data = new byte[]{CHESS, INVITE, setting};
        HyPigeon.sendPacket(name, data);
    }

    int setting = 0;

    public static final String[] whoGoesFirst = {"You as White", "You as Black", "Random"};

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

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(1, width / 2 + 10, height / 2 + ICON_SIZE / 2 - 40, 100, 20, whoGoesFirst[setting]));
        super.initGui();
    }
}
