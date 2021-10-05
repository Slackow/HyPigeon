package com.slackow.hypigeon.games.core;

import com.github.bhlangonijr.chesslib.Side;
import com.google.common.collect.Lists;
import com.slackow.hypigeon.HyPigeon;
import com.slackow.hypigeon.gui.component.InvisibleButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

import static com.slackow.hypigeon.Reference.RESIGN;
import static net.minecraft.util.EnumChatFormatting.RESET;
import static net.minecraft.util.EnumChatFormatting.YELLOW;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public abstract class GameGui<T extends AbstractSession<T>> extends HyPigeonGui {
    protected final T session;
    protected String title;
    private InvisibleButton chatButton;
    private InvisibleButton rematch;
    private InvisibleButton denyRematch;
    private GuiButton resign;
    private boolean popupClosed = false;

    protected GameGui(T session) {
        this.session = session;
        this.title = session.getGame().getName();
    }

    public T getSession() {
        return session;
    }

    public String getOpponent() {
        return getSession().getOpponent();
    }

    private boolean isOpen = false;

    private boolean notification = false;

    private boolean hasHovered = false;

    private GuiTextField textField;

    private GuiSlider slider;

    private int textFieldWidth = 100;

    private static final int buttonSize = 21;

    private static final ResourceLocation chatTextures = new ResourceLocation(HyPigeon.MODID, "textures/gui/chat.png");

    private static final int windowWidth = 132;

    private static final int windowHeight = 157;

    private static final int chatBoxWidth = 94;

    private static final int chatBoxHeight = windowHeight - 16;

    private static final int titleHeight = 23;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1, 1, 1, 1);

        mc.renderEngine.bindTexture(chatTextures);

        drawTexturedModalRect(width / 2 - windowWidth / 2, height / 2 - windowHeight / 2 - 26 / 2, 28, 0, windowWidth, windowHeight);

        if (notification) {
            drawTexturedModalRect(width / 2 + windowWidth / 2 - buttonSize - 4, height / 2 - windowHeight / 2 - 26 / 2 + 2, 0, buttonSize, buttonSize + 1, buttonSize + 1);
        } else {
            drawTexturedModalRect(width / 2 + windowWidth / 2 - buttonSize - 3, height / 2 - windowHeight / 2 - 26 / 2 + 3, 0, 0, buttonSize, buttonSize);
        }
        if (isBoxOpen()) {
            int b = textFieldWidth;
            int x = width / 2 + windowWidth / 2;

            while (b >= chatBoxWidth) {
                int val = chatBoxWidth - 2;
                drawTexturedModalRect(x, height / 2 - windowHeight / 2 - 26 / 2 + 8, 160, 8, val, chatBoxHeight);
                x += val;
                b -= val;
            }

            drawTexturedModalRect(x, height / 2 - windowHeight / 2 - 26 / 2 + 8, 253 - b, 8, b, chatBoxHeight);

            textField.drawTextBox();
            int[] y = new int[]{0};
            GlStateManager.pushMatrix();
            GlStateManager.translate(width / 2 + windowWidth / 2 + 5, height / 2 + windowHeight / 2 - buttonSize * 2 - 8, 0);
            GlStateManager.scale(0.7f, 0.7f, 0.7f);
            GlStateManager.color(1f, 1f, 1f, 1f);
            getSession().messages.stream()
                    .flatMap(str -> Lists.reverse(fontRendererObj.listFormattedStringToWidth(str, (int) (textFieldWidth / 0.7 - 15))).stream())
                    .forEach(line -> {
                        if (y[0] >= -chatBoxHeight / 0.7 + buttonSize / 0.7 + 20) {
                            drawString(fontRendererObj, line, 0, y[0] -= fontRendererObj.FONT_HEIGHT + 2, 0xFFFFFF);
                        }
                    });
            GlStateManager.popMatrix();
        }
        int state = session.getState();

        if (!session.isGameOver()) {
            String topText = session.waitingOnOpponent() ? "Waiting for Opponent..." : "Your Move";
            int topColor = session.waitingOnOpponent() ? 0xFFFFFF : 0xFFAA00;

            drawCenteredString(fontRendererObj, topText, width / 2, height / 2 - windowHeight / 2 - 26 / 2 - 10, topColor);
        } else if (!popupClosed) {
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            boolean isTransparent = mouseX < width / 2 - 25 || mouseY < height / 2 - 25 ||
                    mouseX >= width / 2 + 25 || mouseY > height / 2 + 25;
            if (!hasHovered && !isTransparent) {
                hasHovered = true;
            }
            isTransparent &= hasHovered;


            if (isTransparent) {
                GlStateManager.color(1, 1, 1, 0.8f);
            } else {
                GlStateManager.color(1, 1, 1, 1);
            }
            mc.renderEngine.bindTexture(chatTextures);
            drawTexturedModalRect(width / 2 - 25, height / 2 - 25, 0, 157, 50, 50);
            if (state == -1) {
                drawCenteredString(fontRendererObj, "Tie!", width / 2, height / 2 - 15, 0xFFAA00 | (isTransparent ? 0xCC_00_00_00 : 0));
            } else {
                int color = (session.didIWin() ? 0xFF_AA_00 : 0xFE_38_32) | (isTransparent ? 0xCC_00_00_00 : 0);
                String secondLine = session.didIWin() ? "Won!" : "Lost!";
                drawCenteredString(fontRendererObj, "You", width / 2, height / 2 - 20, color);
                drawCenteredString(fontRendererObj, secondLine, width / 2, height / 2 - 10, color);
            }
            GlStateManager.popMatrix();
        }
        rematch.visible = denyRematch.visible = session.isGameOver() && !popupClosed;
        resign.enabled = !session.isGameOver();
        drawString(fontRendererObj, title, width / 2 - fontRendererObj.getStringWidth(title) / 2, height / 2 - windowHeight / 2 - 26 / 2 + 2 + titleHeight / 2 - fontRendererObj.FONT_HEIGHT / 2, 0xFFAA00);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void addMessage(String message) {
        getSession().messages.add(0, message);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        textField = new GuiTextField(0, fontRendererObj, 0, height / 2 + 66 - 8 - buttonSize - 6, textFieldWidth, buttonSize);
        textField.setMaxStringLength(230);
        textField.setVisible(isBoxOpen());
        slider = new GuiSlider(-20, width - 124, height / 2 + 66 - 7, textFieldWidth, 20, "", "px", 0, textFieldWidth, textFieldWidth, false, false) {
            @Override
            public void updateSlider() {
                super.updateSlider();
                maxValue = Math.max(getValueInt() + 4, 30);
                setValue(maxValue);
                textFieldWidth = getValueInt();
                textFieldWidth = Math.min(textFieldWidth, GameGui.this.width - xPosition - 1);
                textField.width = textFieldWidth - 11;
                xPosition = (textField.xPosition = GameGui.this.width / 2 + windowWidth / 2 + 5) - 5;
                width = textFieldWidth + 1;
            }
        };
        slider.visible = false;
        slider.updateSlider();
        buttonList.add(slider);
        buttonList.add(chatButton = new InvisibleButton(-50, width / 2 + windowWidth / 2 - buttonSize - 3, height / 2 - windowHeight / 2 - 26 / 2 + 3, buttonSize, buttonSize));
        buttonList.add(new InvisibleButton(-49, width / 2 - windowWidth / 2 + 3, height / 2 - windowHeight / 2 - 26 / 2 + 3, buttonSize, buttonSize));

        int buttonWidth = fontRendererObj.getStringWidth("Resign");
        buttonList.add(resign = new GuiButton(-46, width / 2 - windowWidth / 2 - buttonWidth - 50 - 10, height/2 - 10, buttonWidth + 50, 20, "Resign"));
        rematch = new InvisibleButton(-48, width / 2 - buttonSize - 1, height / 2 + 1, buttonSize, buttonSize);
        buttonList.add(rematch);
        denyRematch = new InvisibleButton(-47, width / 2 + 1, height / 2 + 1, buttonSize, buttonSize);
        buttonList.add(denyRematch);
        rematch.visible = denyRematch.visible = session.isGameOver() && !popupClosed;
        super.initGui();
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case -50:
                isOpen = !isOpen;
                if (isBoxOpen()) {
                    notification = false;
                } else {
                    slider.visible = false;
                }
                textField.setVisible(isBoxOpen());
                textField.setFocused(isBoxOpen());
                break;
            case -49:
                mc.displayGuiScreen(null);
                break;
            case -47:
                popupClosed = true;
                break;
            case -46:
                HyPigeon.sendPacket(getOpponent(), new byte[]{session.getGame().getIndex(), RESIGN});
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    protected void onButtonRightClick(GuiButton button) {
        if (isBoxOpen()) {
            if (button == chatButton) {
                slider.visible ^= true;
            } else if (slider.visible && slider == button) {
                slider.maxValue = 100;
                slider.setValue(100);
                slider.updateSlider();
            }
        }
        super.onButtonRightClick(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        if (HyPigeon.activeSessions.contains(getSession())) {
            ChatComponentText text = new ChatComponentText("You can reopen the game by clicking here");
            text.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hypigeon games"));
            mc.thePlayer.addChatMessage(text);
        }
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        textField.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        textField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_RETURN && isBoxOpen() && !textField.getText().isEmpty()) {
            HyPigeon.outgoingMessages.add("/w " + getOpponent() + " " + textField.getText());
            addMessage(YELLOW + mc.getSession().getUsername() + RESET + ": " + textField.getText());
            textField.setText("");
        }
        if (!textField.isFocused() && typedChar == 'c') {
            actionPerformed(chatButton);
            chatButton.playPressSound(this.mc.getSoundHandler());
        }
        super.keyTyped(typedChar, keyCode);
    }

    public void addNotification() {
        notification = true;
    }

    public boolean isBoxOpen() {
        return isOpen;
    }
}
