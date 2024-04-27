package dev.redholms.svo.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public abstract class PacketBase implements IMessage {
  protected String readSizedString(ByteBuf buf) {
    byte length = buf.readByte();
    return buf.readBytes(length).toString(StandardCharsets.UTF_8);
  }

  protected void writeSizedString(ByteBuf buf, String s) {
    buf.writeByte(s.length());
    buf.writeBytes(s.getBytes(StandardCharsets.UTF_8));
  }
}
