package dev.redholms.svo.network.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dev.redholms.svo.SVOMod;
import dev.redholms.svo.common.Team;
import dev.redholms.svo.common.BaseTeamService;
import dev.redholms.svo.network.TeamMembersPacket;

public class TeamMembersPacketHandler implements IMessageHandler<TeamMembersPacket, IMessage> {
  @Override
  public IMessage onMessage(TeamMembersPacket message, MessageContext ctx) {
    BaseTeamService teamService = SVOMod.CLIENT.currentGame.teamService;

    Team team = teamService.getTeam(message.teamName);

    if (team == null) {
      SVOMod.LOG.error("Server sent members for unexisting team {}", message.teamName);
      return null;
    }

    teamService.clearTeamMembers(team);

    for (String member : message.members) {
      SVOMod.LOG.info("Got TeamMembersPacket: {} assigned to the team {}", member, message.teamName);
      teamService.setPlayersTeam(member, team);
    }

    return null;
  }
}
