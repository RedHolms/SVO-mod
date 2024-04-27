package dev.redholms.svo.commands;

import dev.redholms.svo.PlayerUtils;
import dev.redholms.svo.SVOMod;
import dev.redholms.svo.common.Team;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;

import java.awt.*;
import java.util.Collection;

public class CommandSVOTeams extends CommandBase {
  @Override
  public String getCommandName() {
    return "svo.teams";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "commands.svo.teams.usage";
  }

  private void addTeam(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.teams.add.usage");

    String teamName = args[1];

    if (teamName.isEmpty())
      throw new WrongUsageException("commands.svo.teams.add.usage");

    if (SVOMod.SERVER.game.teamService.getTeam(teamName) != null)
      throw new CommandException("commands.svo.teams.alreadyExists", teamName);

    String displayName = teamName;

    if (args.length > 2) {
      displayName = parseChatComponent(sender, args, 2).getUnformattedText();

      if (displayName.isEmpty())
        displayName = teamName;

      SVOMod.LOG.info("displayName={}", displayName);
    }

    SVOMod.SERVER.game.teamService.createTeam(teamName, displayName);

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.add.success", displayName, teamName));
  }

  private void removeTeam(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.teams.remove.usage");

    String teamName = args[1];

    if (teamName.isEmpty())
      throw new WrongUsageException("commands.svo.teams.remove.usage");

    Team team = SVOMod.SERVER.game.teamService.getTeam(teamName);

    if (team == null)
      throw new CommandException("commands.svo.teams.noTeam", teamName);

    SVOMod.SERVER.game.teamService.removeTeam(team);

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.remove.success", team.displayName));
  }

  private void listTeams(ICommandSender sender, String[] ignoredArgs) {
    Collection<Team> teams = SVOMod.SERVER.game.teamService.getTeams();

    if (teams.isEmpty())
      throw new CommandException("commands.svo.teams.list.noTeams");

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.list.count", teams.size()));

    for (Team team : teams) {
      sender.addChatMessage(
        new ChatComponentTranslation(
          "commands.svo.teams.list.entry",
          team.displayName, team.name,
          team.membership.size()
        )
      );
    }
  }

  private void listTeamMembers(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.teams.members.usage");

    String teamName = args[1];

    if (teamName.isEmpty())
      throw new WrongUsageException("commands.svo.teams.members.usage");

    Team team = SVOMod.SERVER.game.teamService.getTeam(teamName);

    if (team == null)
      throw new CommandException("commands.svo.teams.noTeam", teamName);

    if (team.membership.isEmpty())
      throw new CommandException("commands.svo.teams.members.noMembers");

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.members.count", team.displayName, team.membership.size()));

    for (String member : team.membership)
      sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.members.entry", member));
  }

  private void setTeamColor(ICommandSender sender, String[] args) {
    if (args.length < 3)
      throw new WrongUsageException("commands.svo.teams.color.usage");

    String teamName = args[1];
    Color color = this.parseColor(args[2]);

    Team team = SVOMod.SERVER.game.teamService.getTeam(teamName);

    if (team == null)
      throw new CommandException("commands.svo.teams.noTeam", teamName);

    team.color = color;

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.color.success", team.displayName, color.toString()));
  }

  private void setTeamSpawnpoint(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.teams.spawnpoint.usage");

    String teamName = args[1];

    ChunkCoordinates coords;

    if (args.length > 2) {
      if (args.length < 5)
        throw new WrongUsageException("commands.svo.teams.spawnpoint.usage");

      int x = parseIntBounded(sender, args[2], -30000000, 30000000);
      int y = parseIntBounded(sender, args[3], 0, 256);
      int z = parseIntBounded(sender, args[4], -30000000, 30000000);
      coords = new ChunkCoordinates(x, y, z);
    }
    else {
      EntityPlayerMP player = getCommandSenderAsPlayer(sender);
      coords = player.getPlayerCoordinates();
    }

    Team team = SVOMod.SERVER.game.teamService.getTeam(teamName);

    if (team == null)
      throw new CommandException("commands.svo.teams.noTeam", teamName);

    for (String member : team.membership) {
      EntityPlayerMP player = PlayerUtils.getPlayerFromName(member);
      player.setSpawnChunk(coords, true);
    }

    team.spawnpoint = coords;

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.spawnpoint.success", team.displayName, coords.posX, coords.posY, coords.posZ));
  }

  private void joinTeam(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.teams.join.usage");

    String teamName = args[1];
    Team team = SVOMod.SERVER.game.teamService.getTeam(teamName);

    if (team == null)
      throw new CommandException("commands.svo.teams.noTeam", teamName);

    String playerName;

    if (args.length > 2)
      playerName = args[2];
    else
      playerName = getCommandSenderAsPlayer(sender).getDisplayName();

    SVOMod.SERVER.game.teamService.setPlayersTeam(playerName, team);

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.join.success", playerName, team.displayName));
  }

  private void leaveTeam(ICommandSender sender, String[] args) {
    String playerName;
    if (args.length > 1)
      playerName = args[1];
    else
      playerName = getCommandSenderAsPlayer(sender).getDisplayName();

    Team team = SVOMod.SERVER.game.teamService.getPlayersTeam(playerName);

    if (team == null) {
      if (args.length > 1)
        throw new CommandException("commands.svo.teams.leave.noTeam", playerName);
      else
        throw new CommandException("commands.svo.teams.leave.usage");
    }

    SVOMod.SERVER.game.teamService.removePlayerFromTeam(playerName);

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.teams.leave.success", playerName, team.displayName));
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    if (args.length < 1)
      throw new WrongUsageException("commands.svo.teams.usage");

    switch (args[0].toLowerCase()) {
      case "add":
        addTeam(sender, args);
        return;
      case "remove":
        removeTeam(sender, args);
        return;
      case "list":
        listTeams(sender, args);
        return;
      case "members":
        listTeamMembers(sender, args);
        return;
      case "color":
        setTeamColor(sender, args);
        return;
      case "spawnpoint":
        setTeamSpawnpoint(sender, args);
        return;
      case "join":
        joinTeam(sender, args);
        return;
      case "leave":
        leaveTeam(sender, args);
        return;
    }

    throw new WrongUsageException("commands.svo.teams.usage");
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }
}
