package com.slackow.hypigeon.games.tictactoe;

import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.core.InviteGui;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.Random;

import static com.slackow.hypigeon.Reference.*;
import static com.slackow.hypigeon.gui.MainGui.ICON_SIZE;

public class TicTacToeInviteGui extends InviteGui {

    public TicTacToeInviteGui(String name) {
        super(name, 0, 0, TicTacToeGame.INSTANCE.getName());
    }


    @Override
    protected void sendInvite() {
        byte setting = randomizeIfRandom(this.setting);
        byte[] data = new byte[]{TIC_TAC_TOE, INVITE, setting};
        HyPigeon.sendPacket(name, data);
    }

    int setting = 0;

    public static final String[] whoGoesFirst = {"You as X", "You as O", "Random"};

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
