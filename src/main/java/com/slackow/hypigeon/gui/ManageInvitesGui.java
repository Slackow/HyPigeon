package com.slackow.hypigeon.gui;

import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.core.HyPigeonGui;
import net.minecraft.client.gui.GuiButton;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

import static com.slackow.hypigeon.Reference.*;

public class ManageInvitesGui extends HyPigeonGui {
    private boolean blank;

    @Override
    public void initGui() {
        // creating modifiable integer within lambda
        int[] id = new int[]{0};
        HyPigeon.invites.forEach(pair -> {
            String buttonText = getButtonText(pair);
            buttonList.add(setDisplayString(new GuiButton(id[0]++, 0, height / 2 - 30 + id[0] * 20, 0, 20, ""), buttonText));
        });
        blank = HyPigeon.invites.isEmpty();
        super.initGui();
    }

    private String getButtonText(Pair<String, byte[]> pair) {
        return pair.getLeft() + " in " + HyPigeon.getRegisteredGame(pair.getRight()[GAME_INDEX]).getName();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        if (blank) {
            drawCenteredString(fontRendererObj, "No Invites :(", width / 2, height / 2, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static final String confirmAccept = "Click to Accept | Right click to go back";
    private static final String confirmDeny = "Click to Deny | Right click to go back";
    private static final String confirmCancel = "Click to Cancel | Right click to go back";
    
    private GuiButton setDisplayString(GuiButton button, String displayString) {
        button.displayString = displayString;
        button.width = fontRendererObj.getStringWidth(button.displayString) + 50;
        button.xPosition = width / 2 - button.width / 2;
        return button;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        
        if (button.displayString.startsWith("Click")){
            Pair<String, byte[]> pair = HyPigeon.invites.get(button.id);
            String relation = pair.getLeft();
            byte[] data = pair.getRight();

            if (relation.startsWith("From")) {
                data[MESSAGE_TYPE] = button.displayString.equals(confirmAccept) ? ACCEPT : DENY;
                HyPigeon.sendPacket(relation.substring(6), data);
                System.out.println("got here");
            } else {
                data[MESSAGE_TYPE] = CANCEL_INVITE;
                HyPigeon.sendPacket(relation.substring(4), data);
            }
            super.actionPerformed(button);
        } else {
            setDisplayString(button, button.displayString.startsWith("From") ? confirmAccept : confirmCancel);
        }
        
    }

    @Override
    protected void onButtonRightClick(GuiButton button) {
        if (button.displayString.startsWith("Click")) {
            setDisplayString(button, getButtonText(HyPigeon.invites.get(button.id)));
        } else {
            if (button.displayString.startsWith("From")) {
                setDisplayString(button, confirmDeny);
            }
        }
        super.onButtonRightClick(button);
    }
}
