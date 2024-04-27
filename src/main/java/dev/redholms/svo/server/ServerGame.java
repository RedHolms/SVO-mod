package dev.redholms.svo.server;

public class ServerGame {
  public final ServerTeamService teamService;
  public final ServerPointService pointService;

  public ServerGame() {
    this.teamService = new ServerTeamService(this);
    this.pointService = new ServerPointService();
  }
}
