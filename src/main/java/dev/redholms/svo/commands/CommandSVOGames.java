package dev.redholms.svo.commands;

import cpw.mods.fml.relauncher.Side;
import dev.redholms.svo.SVOMod;
import dev.redholms.svo.SVOSavedData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class CommandSVOGames extends CommandBase {
  @Override
  public String getCommandName() {
    return "svo.games";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "commands.svo.games.usage";
  }

  private void saveGame(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.games.save.usage");

    String gameName = args[1];

    World overworld = MinecraftServer.getServer().getEntityWorld();
    SVOSavedData data = SVOSavedData.getFromWorld(overworld);

    data.games.put(gameName, new IGame(SVOMod.SERVER.game));
    data.markDirty();

    sender.addChatMessage(
      new ChatComponentTranslation(
        "commands.svo.games.save.success",
        gameName,
        SVOMod.SERVER.game.teamService.getTeams().size(),
        SVOMod.SERVER.game.pointService.getPoints().size()
      )
    );
  }

  private void loadGame(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.games.load.usage");

    String gameName = args[1];

    World overworld = MinecraftServer.getServer().getEntityWorld();
    SVOSavedData data = SVOSavedData.getFromWorld(overworld);

    IGame game = data.games.get(gameName);

    if (game == null)
      throw new CommandException("commands.svo.games.noGame", gameName);

    SVOMod.SERVER.game.unload();
    SVOMod.SERVER.game = new IGame(game, Side.SERVER);
    SVOMod.SERVER.game.load();

    sender.addChatMessage(
      new ChatComponentTranslation(
        "commands.svo.games.load.success",
        gameName,
        SVOMod.SERVER.game.teamService.getTeams().size(),
        SVOMod.SERVER.game.pointService.getPoints().size()
      )
    );
  }

  public void deleteGame(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.games.delete.usage");

    String gameName = args[1];

    World overworld = MinecraftServer.getServer().getEntityWorld();
    SVOSavedData data = SVOSavedData.getFromWorld(overworld);

    IGame game = data.games.get(gameName);

    if (game == null)
      throw new CommandException("commands.svo.games.noGame", gameName);

    data.games.remove(gameName);

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.games.delete.success", gameName));
  }

  public void listGames(ICommandSender sender, String[] args) {
    World overworld = MinecraftServer.getServer().getEntityWorld();
    SVOSavedData data = SVOSavedData.getFromWorld(overworld);

    if (data.games.isEmpty())
      throw new CommandException("commands.svo.games.list.noGames");

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.games.list.count", data.games.size()));

    for (String name : data.games.keySet()) {
      sender.addChatMessage(
        new ChatComponentTranslation("commands.svo.games.list.entry", name)
      );
    }
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    if (args.length < 1)
      throw new WrongUsageException("commands.svo.games.usage");

    switch (args[0].toLowerCase()) {
      case "save":
        saveGame(sender, args);
        return;
      case "load":
        loadGame(sender, args);
        return;
      case "delete":
        deleteGame(sender, args);
        return;
      case "list":
        listGames(sender, args);
        return;
    }

    throw new WrongUsageException("commands.svo.games.usage");
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }
}
