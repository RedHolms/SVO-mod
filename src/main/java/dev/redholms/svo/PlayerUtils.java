package dev.redholms.svo;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class PlayerUtils {
  public static EntityPlayerMP getPlayerFromName(String playerName) {
    return MinecraftServer.getServer().getConfigurationManager().func_152612_a(playerName);
  }
}
