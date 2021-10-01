package com.slackow.hypigeon.games.core;

import net.minecraft.util.IChatComponent;

public abstract class AbstractGame {

    private final byte index;

    protected AbstractGame(byte index){

        this.index = index;
    }

    public byte getIndex(){
        return index;
    }

    public abstract String getName();

    public abstract IChatComponent invite(String fromTo, String opponent, byte[] data);

    public abstract IChatComponent cancelInvite(String fromTo, String opponent, byte[] data);

    public abstract IChatComponent deny(String fromTo, String opponent, byte[] data);

    public abstract IChatComponent accept(String fromTo, String opponent, byte[] data, byte[] inviteData);

    public abstract IChatComponent nextMove(String fromTo, String opponent, byte[] data);

    public abstract IChatComponent resume(String fromTo, String opponent, byte[] data);

    public abstract IChatComponent save(String fromTo, String opponent, byte[] data);


}
