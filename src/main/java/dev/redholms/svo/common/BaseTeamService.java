package dev.redholms.svo.common;

import cpw.mods.fml.relauncher.Side;
import dev.redholms.svo.SVOMod;
import dev.redholms.svo.events.PlayerTeamChangeEvent;

import java.util.Collection;
import java.util.HashMap;

public class BaseTeamService {
  private final HashMap<String, Team> teams;
  private final HashMap<String, Team> playersTeam;

  public BaseTeamService(Side side) {
    this.teams = new HashMap<>();
    this.playersTeam = new HashMap<>();
  }

  public BaseTeamService(BaseTeamService other) {
    this.teams = new HashMap<>(other.teams);
    this.playersTeam = new HashMap<>(other.playersTeam);
  }

  public BaseTeamService(BaseTeamService other, Side side) {
    this.teams = new HashMap<>(other.teams);
    this.playersTeam = new HashMap<>(other.playersTeam);
  }

  public Team getTeam(String name) {
    return teams.get(name);
  }

  public Team createTeam(String name, String displayName) {
    if (this.getTeam(name) != null)
      throw new IllegalArgumentException("A team with the name \"" + name + "\" already exists!");

    Team team = new Team(name);
    team.displayName = displayName;
    teams.put(team.name, team);

    return team;
  }

  public void removeTeam(Team team) {
    teams.remove(team.name);

    Collection<String> membership = team.membership;
    for (String member : membership)
      playersTeam.remove(member);
  }

  public void removeTeam(String name) {
    removeTeam(getTeam(name));
  }

  public void setPlayersTeam(String playerName, String teamName) {
    setPlayersTeam(playerName, getTeam(teamName));
  }

  public void setPlayersTeam(String playerName, Team team) {
    Team oldTeam = null;
    if (playersTeam.containsKey(playerName)) {
      oldTeam = playersTeam.get(playerName);
      oldTeam.membership.remove(playerName);
    }

    this.playersTeam.put(playerName, team);
    team.membership.add(playerName);

    this.postTeamChange(playerName, oldTeam, team);
  }

  public void removePlayerFromTeam(String playerName) {
    Team playerTeam = playersTeam.get(playerName);

    if (playerTeam == null)
      return;

    playersTeam.remove(playerName);
    playerTeam.membership.remove(playerName);

    this.postTeamChange(playerName, playerTeam, null);
  }

  public void clearTeamMembers(String teamName) {
    clearTeamMembers(getTeam(teamName));
  }

  public void clearTeamMembers(Team team) {
    for (String member : team.membership) {
      playersTeam.remove(member);
      team.membership.remove(member);
      this.postTeamChange(member, team, null);
    }
  }

  public Team getPlayersTeam(String playerName) {
    return playersTeam.get(playerName);
  }

  public Collection<String> getTeamNames() {
    return teams.keySet();
  }

  public Collection<Team> getTeams() {
    return teams.values();
  }

  public void clear() {
    teams.clear();
    playersTeam.clear();
  }

  private void postTeamChange(String playerName, Team oldTeam, Team newTeam) {
    if (this.side.isClient())
      return;

    SVOMod.LOG.info("postTeamChange for {}: {} => {}", playerName, oldTeam == null ? "<NONE>" : oldTeam.name, newTeam == null ? "<NONE>" : newTeam.name);

    SVOMod.EVENT_HANDLER.onPlayerTeamChange(
      new PlayerTeamChangeEvent(
        playerName,
        this.side,
        oldTeam, newTeam
      )
    );
  }
}
