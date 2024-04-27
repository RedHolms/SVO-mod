package dev.redholms.svo.network;

import dev.redholms.svo.common.Team;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class TeamMembersPacket extends PacketBase {
  public String teamName;
  public final List<String> members;

  public TeamMembersPacket() {
    this.teamName = null;
    this.members = new ArrayList<>();
  }

  public TeamMembersPacket(Team team) {
    this.teamName = team.name;
    this.members = new ArrayList<>(team.membership);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    teamName = readSizedString(buf);

    byte membersCount = buf.readByte();
    members.clear();

    for (byte i = 0; i < membersCount; ++i)
      members.add(readSizedString(buf));
  }

  @Override
  public void toBytes(ByteBuf buf) {
    writeSizedString(buf, teamName);

    buf.writeByte(members.size());
    for (String member : members)
      writeSizedString(buf, member);
  }
}
