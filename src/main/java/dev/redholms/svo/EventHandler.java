package dev.redholms.svo;

import dev.redholms.svo.events.PlayerTeamChangeEvent;
import dev.redholms.svo.network.GameInfoPacket;
import dev.redholms.svo.network.TeamMembersPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class EventHandler {
  public void onPlayerTeamChange(PlayerTeamChangeEvent event) {
    if (event.side.isClient())
      return;

    EntityPlayerMP player = PlayerUtils.getPlayerFromName(event.playerName);

    if (player != null) {
      if (event.newTeam != null) {
        ChunkCoordinates coords = event.newTeam.spawnpoint;
        player.setSpawnChunk(coords, true);
      }
      else {
        player.setSpawnChunk(null, true);
      }
    }

    if (event.oldTeam != null)
      SVOMod.NETWORK_WRAPPER.sendToAll(new TeamMembersPacket(event.oldTeam));
    if (event.newTeam != null)
      SVOMod.NETWORK_WRAPPER.sendToAll(new TeamMembersPacket(event.newTeam));
  }

  public void onGameLoad(IGame game) {
    SVOMod.NETWORK_WRAPPER.sendToAll(new GameInfoPacket(game));
  }
}
