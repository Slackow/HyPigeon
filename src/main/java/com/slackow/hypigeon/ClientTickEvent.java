package com.slackow.hypigeon;

import com.slackow.hypigeon.util.Task;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.slackow.hypigeon.HyPigeon.*;

public class ClientTickEvent {
    public static int messageCooldown = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (openedGuiNextTick != null) {
                Minecraft.getMinecraft().displayGuiScreen(openedGuiNextTick);
                openedGuiNextTick = null;
            }
            if (messageCooldown > 0) {
                messageCooldown--;
            } else if (!outgoingMessages.isEmpty()) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage(outgoingMessages.remove());
                messageCooldown = 20;
            }
            if (!scheduler.isEmpty()) {
                scheduler.removeIf(Task::decrementOrRun);
            }
        }
    }
}
