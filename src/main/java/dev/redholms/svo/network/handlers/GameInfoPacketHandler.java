package dev.redholms.svo.network.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dev.redholms.svo.SVOMod;
import dev.redholms.svo.network.GameInfoPacket;

public class GameInfoPacketHandler implements IMessageHandler<GameInfoPacket, IMessage> {
  @Override
  public IMessage onMessage(GameInfoPacket message, MessageContext ctx) {
    SVOMod.LOG.info("Got game from server");

    SVOMod.CLIENT.currentGame = new IGame(message.game);
    SVOMod.CLIENT.currentTeam = null;

    return null;
  }
}
