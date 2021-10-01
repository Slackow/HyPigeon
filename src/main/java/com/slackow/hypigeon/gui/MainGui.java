package com.slackow.hypigeon.gui;

import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.games.chess.ChessInviteGui;
import com.slackow.hypigeon.games.core.HyPigeonGui;
import com.slackow.hypigeon.gui.component.InvisibleButton;
import com.slackow.hypigeon.games.connect4.Connect4InviteGui;
import com.slackow.hypigeon.games.tictactoe.TicTacToeInviteGui;
import com.slackow.hypigeon.util.CancelableRunnable;
import com.slackow.hypigeon.util.Task;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

import static com.slackow.hypigeon.HyPigeon.MODID;
import static com.slackow.hypigeon.Reference.*;

public class MainGui extends HyPigeonGui {
    GuiTextField textField;
    private ItemStack itemStack;

    private String cachedName;
    private CancelableRunnable last;

    public MainGui(String name) {
        cachedName = name;
    }

    public static final ResourceLocation ICONS = new ResourceLocation(MODID, "textures/gui/games.png");

    public static final int ICON_SIZE = 64;

    public static ItemStack getSkull(String name) {
        ItemStack result = new ItemStack(Items.skull, 1, 3);
        getSkull(result, name);
        return result;
    }

    public static void getSkull(ItemStack skull, String name) {
        if (!name.isEmpty()) {
            skull.setTagCompound(new NBTTagCompound());
            skull.getTagCompound().setTag("SkullOwner", new NBTTagString(name));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        textField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        textField.drawTextBox();

        if (itemStack != null) {
            GlStateManager.pushMatrix();
            //noinspection IntegerDivisionInFloatingPointContext
            GlStateManager.translate(width / 2 - 82, height / 4 - 16, 0);
            GlStateManager.scale(2, 2, 2);
            GlStateManager.color(1, 1, 1);
            mc.getRenderItem().renderItemIntoGUI(itemStack, 0, 0);
            GlStateManager.popMatrix();
        }
        mc.renderEngine.bindTexture(ICONS);

        if (textField.getText().isEmpty()) {
            GlStateManager.color(0.4f, 0.4f, 0.4f, 1);
        }

        drawTexturedModalRect(width / 2 - ICON_SIZE * 2 - 30, height / 2 - ICON_SIZE / 2, 0, 0, ICON_SIZE, ICON_SIZE);
        drawTexturedModalRect(width / 2 - ICON_SIZE - 10, height / 2 - ICON_SIZE / 2, ICON_SIZE, 0, ICON_SIZE, ICON_SIZE);
        drawTexturedModalRect(width / 2 + 10, height / 2 - ICON_SIZE / 2, ICON_SIZE * 2, 0, ICON_SIZE, ICON_SIZE);
        drawTexturedModalRect(width / 2 + ICON_SIZE + 30, height / 2 - ICON_SIZE / 2, ICON_SIZE * 3, 0, ICON_SIZE, ICON_SIZE);


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public MainGui() {
        this("");
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean b = textField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_TAB) {
            textField.setFocused(!textField.isFocused());
        }
        if (b) {
            cachedName = textField.getText();
            boolean noName = cachedName.isEmpty();
            buttonList.stream().filter(button -> button.id >= 0).forEach(button -> button.enabled = !noName);
            if (last != null) last.cancel();
            new Task(last = new CancelableRunnable(() -> itemStack = getSkull(cachedName)), 8);
        }

        super.keyTyped(typedChar, keyCode);

    }

    @Override
    public void initGui() {
        String name = cachedName;
        itemStack = getSkull(name);

        textField = new GuiTextField(0, fontRendererObj, width / 2 - 50, height / 4 - 10, 100, 20);
        textField.setText(name);
        textField.setFocused(true);
        buttonList.add(new InvisibleButton(TIC_TAC_TOE, width / 2 - ICON_SIZE * 2 - 30, height / 2 - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE));
        buttonList.add(new InvisibleButton(CHESS, width / 2 - ICON_SIZE - 10, height / 2 - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE));
        buttonList.add(new InvisibleButton(CONNECT4, width / 2 + ICON_SIZE + 30, height / 2 - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE));
        if (!HyPigeon.invites.isEmpty()) {
            buttonList.add(new GuiButton(-1, 10, 10, 150, 20, "Manage Invites"));
        }
        if (!HyPigeon.activeSessions.isEmpty()) {
            buttonList.add(new GuiButton(-2, width - 150 - 10, 10, 150, 20, "Open A Game"));
        }
        if (name.isEmpty()) buttonList.stream().filter(button -> button.id >= 0).forEach(button -> button.enabled = false);
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case TIC_TAC_TOE:
                mc.displayGuiScreen(new TicTacToeInviteGui(cachedName));
                break;
            case CONNECT4:
                mc.displayGuiScreen(new Connect4InviteGui(cachedName));
                break;
            case CHESS:
                mc.displayGuiScreen(new ChessInviteGui(cachedName));
                break;
            case -1:
                mc.displayGuiScreen(new ManageInvitesGui());
                break;
            case -2:
                mc.displayGuiScreen(new ManageGamesGui());
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
