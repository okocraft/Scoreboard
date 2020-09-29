package net.okocraft.scoreboard.display.board;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.line.LineDisplay;
import net.okocraft.scoreboard.display.line.PacketLineDisplay;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class PacketBoardDisplay extends AbstractBoardDisplay {

    private static final WrappedChatComponent SUFFIX = WrappedChatComponent.fromText(ChatColor.RESET.toString());
    private static final String ALWAYS = "always";
    private static final Class<?> ENUM_CHAT_FORMAT = MinecraftReflection.getMinecraftClass("EnumChatFormat");

    private final PacketContainer objectivePacket;

    private final String id = Long.toHexString(System.nanoTime());

    private final PacketLineDisplay title;
    private final List<PacketLineDisplay> lines;
    private final AtomicBoolean visible;


    public PacketBoardDisplay(@NotNull ScoreboardPlugin plugin, @NotNull Board board, @NotNull Player player) {
        super(plugin, player);

        objectivePacket = newObjectivePacket(id);

        this.title = new PacketLineDisplay(player, board.getTitle(), 0, 0);

        this.lines = new LinkedList<>();

        for (int i = 0, l = board.getLines().size(), c = ChatColor.values().length; i < l && i < c; i++) {
            lines.add(new PacketLineDisplay(player, board.getLines().get(i), i, l - i));
        }

        this.visible = new AtomicBoolean(false);
    }

    @Override
    public boolean isVisible() {
        return visible.get();
    }

    @Override
    public void showBoard() {
        sendObjectiveCreationPacket();
        sendDisplaySlotPacket();
        lines.forEach(this::sendTeamCreationPacket);

        scheduleUpdateTasks();

        visible.set(true);
    }

    @Override
    public void hideBoard() {
        sendObjectiveRemovalPacket();

        cancelUpdateTasks();

        visible.set(false);
    }

    @Override
    public void applyTitle() {
        if (title.isChanged()) {
            objectivePacket.getChatComponents().write(0, title.getCurrentLineComponent());
            sendPacket(objectivePacket);
        }
    }

    @Override
    public void applyLine(@NotNull LineDisplay line) {
        if (line.isChanged() && line instanceof PacketLineDisplay) {
            PacketLineDisplay packetLine = (PacketLineDisplay) line;
            PacketContainer packet = newTeamPacket();

            packet.getStrings().write(0, packetLine.getId());
            packet.getChatComponents().write(0, packetLine.getTeamNameComponent());
            packet.getChatComponents().write(1, packetLine.getCurrentLineComponent());

            sendPacket(packet);
        }
    }

    @Override
    @NotNull
    public LineDisplay getTitle() {
        return title;
    }

    @Override
    @NotNull
    public List<LineDisplay> getLines() {
        return List.copyOf(lines);
    }

    private void sendObjectiveCreationPacket() {
        PacketContainer packet = newObjectivePacket(id);

        packet.getIntegers().write(0, 0);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(title.getCurrentLine()));

        sendPacket(packet);
    }

    private void sendObjectiveRemovalPacket() {
        PacketContainer packet = newObjectivePacket(id);

        packet.getIntegers().write(0, 1);

        sendPacket(packet);
    }

    private void sendTeamCreationPacket(@NotNull PacketLineDisplay line) {
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
