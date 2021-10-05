package com.slackow.hypigeon;

import com.slackow.hypigeon.games.chess.ChessGame;
import com.slackow.hypigeon.games.connect4.Connect4Game;
import com.slackow.hypigeon.games.core.AbstractGame;
import com.slackow.hypigeon.games.core.AbstractSession;
import com.slackow.hypigeon.games.tictactoe.TicTacToeGame;
import com.slackow.hypigeon.util.Task;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.slackow.hypigeon.Reference.*;

@Mod(modid = HyPigeon.MODID, version = HyPigeon.VERSION, clientSideOnly = true)
public class HyPigeon
{
    public static final String MODID = "hypigeon";
    public static final String VERSION = "1.0";

    public static final String SUFFIX = "<hp>";

    public static final String BASE_64;
    static {
        StringBuilder b = new StringBuilder();
        String a = "09azAZ";
        for (int i = 0; i < a.length(); i+=2) {
            char max = a.charAt(i + 1);
            for (char c = a.charAt(i); c <= max; c++) {
                b.append(c);
            }
        }
        b.append("-_");
        BASE_64 = b.toString();
    }

    private static final List<AbstractGame> registeredGames = new ArrayList<>();

    public static final List<AbstractSession<?>> activeSessions = new ArrayList<>();

    public static final List<Pair<String, byte[]>> invites = new ArrayList<>();

    public static final Queue<String> outgoingMessages = new ArrayDeque<>();

    public static final Queue<Task> scheduler = new ArrayDeque<>();

    public static byte fromBase64(char c) {
        return (byte) BASE_64.indexOf(c);
    }
    public static char toBase64(byte i) {
        if (i == 64) return '*';
        return BASE_64.charAt(i & 63);
    }
    public static byte[] fromBase64(String s) {
        int index = s.indexOf('*');
        if (index >= 0) {
            s = s.substring(0, index);
        }
        byte[] result = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = fromBase64(s.charAt(i));
        }
        return result;
    }
    public static String toBase64(byte[] in){
        StringBuilder result = new StringBuilder(in.length);
        for (byte i : in) {
            result.append(toBase64(i));
        }
        return result.toString();
    }

    public static void sendPacket(String name, byte[] data){
        if (data.length < 100) {
            data = ArrayUtils.addAll(data, (byte) 64, getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte(), getAByte());
        }
        HyPigeon.outgoingMessages.add("/w " + name + " " + HyPigeon.toBase64(data) + SUFFIX);
        System.out.println(HyPigeon.outgoingMessages.peek());
    }

    private static byte getAByte() {
        return (byte) (9 + new Random().nextInt(53));
    }

    public static void registerGame(byte index, AbstractGame game) {
        while (index >= registeredGames.size()) {
            registeredGames.add(null);
        }
        registeredGames.set(index, game);
    }

    public static AbstractGame getRegisteredGame(byte index) {
        return registeredGames.get(index);
    }

    public static GuiScreen openedGuiNextTick;

    public static void invite(String relation, byte[] data) {
        invites.add(Pair.of(relation, data));
    }


    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        registerGame(TIC_TAC_TOE, TicTacToeGame.INSTANCE);
        registerGame(CHESS, ChessGame.INSTANCE);
        registerGame(CONNECT4, Connect4Game.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientTickEvent());
        MinecraftForge.EVENT_BUS.register(new OnChatReceived());
        ClientCommandHandler.instance.registerCommand(new HyPigeonCommand());
    }

}
