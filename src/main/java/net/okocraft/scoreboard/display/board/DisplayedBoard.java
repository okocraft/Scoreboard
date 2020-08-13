package net.okocraft.scoreboard.display.board;

import org.bukkit.entity.Player;

public interface DisplayedBoard {

    Player getPlayer();

    void update();
}
