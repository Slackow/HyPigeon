package com.slackow.hypigeon;

import com.slackow.hypigeon.games.core.AbstractGame;
import com.slackow.hypigeon.games.core.AbstractSession;
import com.slackow.hypigeon.gui.MainGui;
import com.slackow.hypigeon.games.core.GameGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.slackow.hypigeon.HyPigeon.*;
import static com.slackow.hypigeon.Reference.*;
import static net.minecraft.util.EnumChatFormatting.*;

public class OnChatReceived {
    private static final Pattern test = Pattern.compile("^(To|From) ((?:\\[\\w{0,3}\\+{0,2}] )?(\\w{1,16})): ([*\\w-]+)" +
            Pattern.quote(SUFFIX));
    private static final Pattern regularMessage = Pattern.compile("^From (?:\\[\\w{0,3}\\+{0,2}] )?((\\w{1,16}): (.*))");
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        String message = getTextWithoutFormattingCodes(e.message.getUnformattedText());
        if (message.endsWith(SUFFIX)) {
            Matcher matcher = test.matcher(message);


            if (matcher.matches()) {
                final IChatComponent feedback;
                e.setCanceled(true);
                String fromTo = matcher.group(1);
                String opponent = matcher.group(3);
                byte[] data = fromBase64(matcher.group(4));

                boolean selfSent = "To".equals(fromTo);

                AbstractGame game = getRegisteredGame(data[GAME_INDEX]);
                if (game == null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Message received from unregistered game"));
                    return;
                }
                switch (data[MESSAGE_TYPE]) {
                    case INVITE:
                        HyPigeon.invite(fromTo + ": " + opponent, data);
                        feedback = game.invite(fromTo, opponent, data);
                        feedback.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hypigeon invites"));
                        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
                        if (currentScreen instanceof MainGui) {
                            ((MainGui) currentScreen).refresh();
                        }
                        if ("From".equals(fromTo)) {
                            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.anvil_land"), 1f));
                        }
                        break;
                    case ACCEPT:
                        Pair<String, byte[]> pair = invites.stream().filter(p -> p.getLeft().equals(("To".equals(fromTo) ? "From" : "To") + ": " + opponent)).findFirst().orElseGet(() -> Pair.of(null, null));
                        invites.remove(pair);
                        byte[] inviteData = pair.getRight();
                        if (inviteData == null || inviteData[GAME_INDEX] != data[GAME_INDEX]) {
                            feedback = text(DARK_PURPLE + opponent + RED + " tried to accept expired/outdated invite");
                            break;
                        }
                        feedback = game.accept(fromTo, opponent, data, inviteData);
                        break;
                    case NEXT_MOVE:
                        feedback = game.nextMove(fromTo, opponent, data);
                        break;
                    case CANCEL_INVITE:
                        invites.removeIf(p -> p.getLeft().equals(fromTo + ": " + opponent));
                        feedback = game.cancelInvite(fromTo, opponent, data);
                        break;
                    case DENY:
                        feedback = game.deny(fromTo, opponent, data);
                        break;
                    case RESUME:
                        feedback = game.resume(fromTo, opponent, data);
                        break;
                    case SAVE:
                        feedback = game.save(fromTo, opponent, data);
                        break;
                    case RESIGN:
                        Optional<AbstractSession<?>> session = activeSessions.stream()
                                .filter(sess -> sess.getOpponent().equals(opponent) && sess.getGame() == game)
                                .findAny();
                        if (session.isPresent()) {
                            if (selfSent) {
                                session.get().unlock();
                                session.get().resign();
                                activeSessions.remove(session.get());
                                feedback = text(GREEN + "You resigned");
                            } else {
                                session.get().lock();
                                session.get().resign();
                                activeSessions.remove(session.get());
                                feedback = text(DARK_PURPLE + opponent + GREEN + " has resigned");
                            }
                        } else {
                            feedback = text(RED + "No game found");
                        }
                        break;
                    default:
                        feedback = text(RED + "");
                        break;
                }

                if (feedback == null) {
                    System.out.println("--------------------------------FEEDBACK WAS NULL! " +
                            fromTo + " " + opponent + " " + Arrays.toString(data));
                } else {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(feedback);
                }
            }


        } else {
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
            if (currentScreen instanceof GameGui<?>) {
                GameGui<?> screen = (GameGui<?>) currentScreen;
                Matcher matcher = regularMessage.matcher(message);
                if (matcher.matches() && matcher.group(2).equals(screen.getOpponent())) {
                    screen.addMessage(YELLOW + matcher.group(2) + RESET + ": " +matcher.group(3));
                    if (!screen.isBoxOpen()) {
                        screen.addNotification();
                    }
                }
            }
        }
    }

    public static ChatComponentText text(String msg) {
        return new ChatComponentText(msg);
    }

}
