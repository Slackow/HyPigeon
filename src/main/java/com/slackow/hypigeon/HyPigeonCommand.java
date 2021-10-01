package com.slackow.hypigeon;

import com.slackow.hypigeon.games.core.HyPigeonGui;
import com.slackow.hypigeon.gui.MainGui;
import com.slackow.hypigeon.gui.ManageGamesGui;
import com.slackow.hypigeon.gui.ManageInvitesGui;
import com.slackow.hypigeon.games.debug.DebugGui;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static com.slackow.hypigeon.HyPigeon.openedGuiNextTick;

public class HyPigeonCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "hypigeon";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Opens GUI";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        openedGuiNextTick = getGui(args);
    }

    private static HyPigeonGui getGui(String[] args){
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "invites":
                    return new ManageInvitesGui();
                case "games":
                    return new ManageGamesGui();
                case "debug":
                    return args.length >= 2 ? new DebugGui(args[1]) : new DebugGui();
                default:
                    return new MainGui(args[0]);

            }
        }
        return new MainGui();
    }
}
