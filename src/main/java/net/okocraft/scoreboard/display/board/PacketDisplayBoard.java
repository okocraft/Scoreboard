package net.okocraft.scoreboard.display.board;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.line.DisplayedLine;
import net.okocraft.scoreboard.display.line.PacketDisplayedLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class PacketDisplayBoard extends AbstractDisplayedBoard {

    private final static WrappedChatComponent SUFFIX = WrappedChatComponent.fromText(ChatColor.RESET.toString());
    private final static String ALWAYS = "always";
    private final static Class<?> ENUM_CHAT_FORMAT = MinecraftReflection.getMinecraftClass("EnumChatFormat");

    private final PacketContainer objectivePacket;
    private final PacketContainer teamPacket;

    private final String id = Long.toHexString(System.nanoTime());

    private final PacketDisplayedLine title;
    private final List<PacketDisplayedLine> lines;


    public PacketDisplayBoard(@NotNull ScoreboardPlugin plugin, @NotNull Board board, @NotNull Player player) {
        super(plugin, player);

        objectivePacket = newObjectivePacket(id);
        teamPacket = newTeamPacket();

        this.title = new PacketDisplayedLine(player, board.getTitle(), 0, 0);

        List<PacketDisplayedLine> lines = new LinkedList<>();

        for (int i = 0, l = board.getLines().size(), c = ChatColor.values().length; i < l && i < c; i++) {
            lines.add(new PacketDisplayedLine(player, board.getLines().get(i), i, l - i));
        }

        this.lines = List.copyOf(lines);

        plugin.getExecutor().submit(() -> {
            sendObjectiveCreationPacket();

            sendDisplaySlotPacket();

            lines.forEach(this::sendTeamCreationPacket);
        });
    }

    @Override
    protected void apply() {
        sendUpdatedTitle();
        sendUpdatedLines();
    }

    @Override
    @NotNull
    protected DisplayedLine getTitle() {
        return title;
    }

    @Override
    @NotNull
    protected List<DisplayedLine> getLines() {
        return List.copyOf(lines);
    }

    private void sendUpdatedTitle() {
        if (title.isChanged()) {
            objectivePacket.getChatComponents().write(0, title.getCurrentLineComponent());
            sendPacket(objectivePacket);
        }
    }

    private void sendUpdatedLines() {
        for (PacketDisplayedLine line : lines) {
            if (line.isChanged()) {
                teamPacket.getStrings().write(0, line.getId());

                teamPacket.getChatComponents().write(0, line.getTeamNameComponent());

                teamPacket.getChatComponents().write(1, line.getCurrentLineComponent());

                sendPacket(teamPacket);
            }
        }
    }

    private void sendObjectiveCreationPacket() {
        PacketContainer packet = newObjectivePacket(id);

        packet.getIntegers().write(0, 0);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(title.getCurrentLine()));

        sendPacket(packet);
    }

    private void sendTeamCreationPacket(@NotNull PacketDisplayedLine line) {
        PacketContainer packet = newTeamPacket();

        packet.getStrings().write(0, line.getId());

        packet.getChatComponents().write(0, line.getTeamNameComponent());

        packet.getChatComponents().write(1, line.getCurrentLineComponent());

        packet.getIntegers().write(0, 0);

        packet.getSpecificModifier(Collection.class).write(0, List.of(line.getEntryName()));

        sendPacket(packet);

        sendScorePacket(line.getEntryName(), line.getScore());
    }

    private void sendDisplaySlotPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);

        packet.getIntegers().write(0, 1);
        packet.getStrings().write(0, id);

        sendPacket(packet);
    }

    private void sendPacket(@NotNull PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, true);
        } catch (InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not send packet", e);
        }
    }

    private void sendScorePacket(@NotNull String name, int value) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);

        packet.getStrings().write(0, name);
        packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);
        packet.getStrings().write(1, id);
        packet.getIntegers().write(0, value);

        sendPacket(packet);
    }

    private enum HealthDisplay {
        INTEGER
    }

    private static PacketContainer newObjectivePacket(@NotNull String id) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);

        packet.getStrings().write(0, id);
        packet.getIntegers().write(0, 2);
        packet.getEnumModifier(HealthDisplay.class, 2).write(0, HealthDisplay.INTEGER);

        return packet;
    }

    private static PacketContainer newTeamPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        packet.getIntegers().write(0, 2);
        packet.getIntegers().write(1, 0);

        packet.getStrings().write(1, ALWAYS);
        packet.getStrings().write(2, ALWAYS);

        packet.getChatComponents().write(2, SUFFIX);

        packet.getEnumModifier(ChatColor.class, ENUM_CHAT_FORMAT).write(0, ChatColor.WHITE);

        return packet;
    }
}
