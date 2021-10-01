package com.slackow.hypigeon.games.debug;

import com.slackow.hypigeon.games.core.AbstractGame;
import net.minecraft.util.IChatComponent;

class DebugGame extends AbstractGame {
    protected DebugGame() {
        super((byte) 100);
    }

    @Override
    public String getName() {
        return "Debug Menu";
    }

    @Override
    public IChatComponent invite(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent cancelInvite(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent deny(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent accept(String fromTo, String opponent, byte[] data, byte[] inviteData) {
        return null;
    }

    @Override
    public IChatComponent nextMove(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent resume(String fromTo, String opponent, byte[] data) {
        return null;
    }

    @Override
    public IChatComponent save(String fromTo, String opponent, byte[] data) {
        return null;
    }
}
