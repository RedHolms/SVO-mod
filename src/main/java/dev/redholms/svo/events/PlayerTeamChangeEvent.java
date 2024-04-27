package dev.redholms.svo.events;

import cpw.mods.fml.relauncher.Side;
import dev.redholms.svo.common.Team;

public class PlayerTeamChangeEvent {
  public final String playerName;
  public final Side side;
  public final Team oldTeam;
  public final Team newTeam;

  public PlayerTeamChangeEvent(String playerName, Side side, Team oldTeam, Team newTeam) {
    this.playerName = playerName;
    this.side = side;
    this.oldTeam = oldTeam;
    this.newTeam = newTeam;
  }
}
