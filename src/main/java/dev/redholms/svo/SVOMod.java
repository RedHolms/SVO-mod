package dev.redholms.svo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dev.redholms.svo.client.SVOClient;
import dev.redholms.svo.commands.CommandSVOGames;
import dev.redholms.svo.commands.CommandSVOTeams;
import dev.redholms.svo.network.GameInfoPacket;
import dev.redholms.svo.network.TeamMembersPacket;
import dev.redholms.svo.network.handlers.GameInfoPacketHandler;
import dev.redholms.svo.network.handlers.TeamMembersPacketHandler;
import dev.redholms.svo.server.SVOServer;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

@Mod(
  modid = "SVO",
  version = "1.0",
  acceptableRemoteVersions = "*"
)
public class SVOMod {
  public static Logger LOG;

  public static final EventHandler EVENT_HANDLER = new EventHandler();
  public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("svo_channel");

  public static SVOClient CLIENT;
  public static SVOServer SERVER;

  public static int FIRST_FREE_PACKET_ID = 0;

  <REQ extends IMessage, REPLY extends IMessage>
  void registerClientPacket(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType) {
    NETWORK_WRAPPER.registerMessage(
      messageHandler, requestMessageType,
      FIRST_FREE_PACKET_ID,
      Side.CLIENT
    );

    ++FIRST_FREE_PACKET_ID;
  }

  private void registerPackets() {
    registerClientPacket(GameInfoPacketHandler.class, GameInfoPacket.class);
    registerClientPacket(TeamMembersPacketHandler.class, TeamMembersPacket.class);
  }

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    LOG = event.getModLog();

    registerPackets();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    FMLCommonHandler.instance().bus().register(EVENT_HANDLER);
    MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);

    if (event.getSide().isClient()) {
      LOG.info("RUNNING ON CLIENT");
      CLIENT = new SVOClient();
    }
    else {
      LOG.info("RUNNING ON SERVER");
    }
  }

  @Mod.EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    LOG.info("Server starting...");

    SERVER = new SVOServer();

    event.registerServerCommand(new CommandSVOTeams());
    event.registerServerCommand(new CommandSVOGames());
  }

  @Mod.EventHandler
  public void serverShutdown(FMLServerStoppingEvent event) {
    SERVER = null;
  }
}
