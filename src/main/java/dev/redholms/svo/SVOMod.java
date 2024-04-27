package dev.redholms.svo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import dev.redholms.svo.commands.CommandSVO;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

@Mod(
  modid = "SVO",
  version = "1.0",
  acceptableRemoteVersions = "*"
)
public class SVOMod {
  public static Logger LOG;

  public static final EventHandler EVENT_HANDLER = new EventHandler();

  public static final Set<String> PLAYERS = new HashSet<>();

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    LOG = event.getModLog();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    FMLCommonHandler.instance().bus().register(EVENT_HANDLER);
    MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
  }

  @Mod.EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandSVO());
  }
}
