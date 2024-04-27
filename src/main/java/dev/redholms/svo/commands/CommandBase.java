package dev.redholms.svo.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CommandBase extends net.minecraft.command.CommandBase {
  protected IChatComponent parseChatComponent(ICommandSender sender, String[] args, int index) {
    return func_147178_a(sender, args, index);
  }

  protected Color parseColor(String input) {
    try {
      return Color.decode(input);
    } catch (NumberFormatException e) {
      throw new SyntaxErrorException("commands.generic.color.invalid", e);
    }
  }
}
