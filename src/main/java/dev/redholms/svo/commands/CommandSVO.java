package dev.redholms.svo.commands;

import dev.redholms.svo.PlayerUtils;
import dev.redholms.svo.SVOMod;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.List;

public class CommandSVO extends CommandBase {
  @Override
  public String getCommandName() {
    return "svo";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "commands.svo.usage";
  }

  private void add(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.add.usage");

    String name = args[1];

    SVOMod.PLAYERS.add(name);

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.add.success", name));
  }

  private void remove(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.remove.usage");

    String name = args[1];

    SVOMod.PLAYERS.remove(name);

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.remove.success", name));
  }

  private void list(ICommandSender sender, String[] args) {
    for (String player : SVOMod.PLAYERS)
      sender.addChatMessage(new ChatComponentTranslation("commands.svo.list.entry", player));
  }

  private void give(ICommandSender sender, String[] args) {
    if (args.length < 2)
      throw new WrongUsageException("commands.svo.give.usage");

    int amount = parseIntBounded(sender, args[1], 1, 9999);
    int diamondsAmount = amount;

    List<ItemStack> stacks = new ArrayList<>();

    while (amount > 0) {
      stacks.add(
        new ItemStack(
          Item.getItemById(264), // diamond
          Math.min(amount, 64)
        )
      );
      amount -= 64;
    }

    for (String name : SVOMod.PLAYERS) {
      EntityPlayerMP player = PlayerUtils.getPlayerFromName(name);

      if (player == null)
        throw new CommandException("commands.svo.give.noPlayer", name);

      InventoryEnderChest enderChest = player.getInventoryEnderChest();

      int stackI = 0;
      for (int slot = 0; slot < enderChest.getSizeInventory(); ++slot) {
        if (stacks.size() == stackI)
          break;

        ItemStack stackInSlot = enderChest.getStackInSlot(slot);
        if (stackInSlot != null)
          continue;

        ItemStack stack = stacks.get(stackI);
        ++stackI;

        enderChest.setInventorySlotContents(slot, stack);
      }

      if (stacks.size() != stackI)
        // NO SPACE!!!
        throw new CommandException("commands.svo.give.noSpace", name);

      // translate on server, so we don't need this mod on client
      player.addChatMessage(
        new ChatComponentText(
          String.format(" - Получено %d алмазов", diamondsAmount)
        ).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
      );
    }

    sender.addChatMessage(new ChatComponentTranslation("commands.svo.give.success"));
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    if (args.length < 1)
      throw new WrongUsageException("commands.svo.usage");

    switch (args[0].toLowerCase()) {
      case "add":
        add(sender, args);
        return;
      case "remove":
        remove(sender, args);
        return;
      case "list":
        list(sender, args);
        return;
      case "give":
        give(sender, args);
        return;
    }

    throw new WrongUsageException("commands.svo.usage");
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
    if (args.length <= 1)
      return getListOfStringsMatchingLastWord(args, "add", "remove", "list", "give");

    if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))
      return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());

    return null;
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {
    if (args.length < 1)
      return false;

    return args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove");
  }
}
