package net.okocraft.scoreboard.display.line;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.okocraft.scoreboard.board.Line;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketDisplayedLine extends DisplayedLine {

    private final String id;
    private final WrappedChatComponent teamNameComponent;
    private WrappedChatComponent currentLineComponent;

    private final int score;

    public PacketDisplayedLine(@NotNull Player player, @NotNull Line line, int num, int score) {
        super(player, line, num);

        this.id = Long.toHexString(System.nanoTime());
        this.teamNameComponent = WrappedChatComponent.fromText(getTeamName());
        this.currentLineComponent = WrappedChatComponent.fromText(getCurrentLine());

        this.score = score;
    }

    @NotNull
    public WrappedChatComponent getTeamNameComponent() {
        return teamNameComponent;
    }

    @NotNull
    public WrappedChatComponent getCurrentLineComponent() {
        currentLineComponent = WrappedChatComponent.fromText(getCurrentLine());
        return currentLineComponent;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }
}
