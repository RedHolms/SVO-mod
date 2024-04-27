package dev.redholms.svo.network;

import cpw.mods.fml.relauncher.Side;
import dev.redholms.svo.common.*;
import dev.redholms.svo.common.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ChunkCoordinates;

import javax.vecmath.Vector3d;
import java.awt.*;

public class GameInfoPacket extends PacketBase {
  public final IGame game;

  public GameInfoPacket() {
    this.game = new IGame(Side.CLIENT);
  }

  public GameInfoPacket(IGame game) {
    this.game = new IGame(game);
  }

  private void deserializeTeam(ByteBuf buf) {
    String name = readSizedString(buf);
    String displayName = readSizedString(buf);

    Team team = game.teamService.createTeam(name, displayName);

    team.color = new Color(buf.readInt());
    team.spawnpoint = new ChunkCoordinates(
      buf.readInt(),
      buf.readInt(),
      buf.readInt()
    );
  }

  private void deserializePoint(ByteBuf buf) {
    String id = readSizedString(buf);
    String name = readSizedString(buf);

    Point point = game.pointService.addPoint(id, name);

    point.position = new Vector3d(
      buf.readDouble(),
      buf.readDouble(),
      buf.readDouble()
    );
    point.radius = buf.readDouble();
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    game.clear();

    byte teamsCount = buf.readByte();
    byte pointsCount = buf.readByte();

    for (byte i = 0; i < teamsCount; ++i)
      deserializeTeam(buf);

    for (byte i = 0; i < pointsCount; ++i)
      deserializePoint(buf);
  }

  private void serializeTeam(ByteBuf buf, Team team) {
    writeSizedString(buf, team.name);
    writeSizedString(buf, team.displayName);

    buf.writeInt(team.color.getRGB());
    buf.writeInt(team.spawnpoint.posX);
    buf.writeInt(team.spawnpoint.posY);
    buf.writeInt(team.spawnpoint.posZ);
  }

  private void serializePoint(ByteBuf buf, Point point) {
    writeSizedString(buf, point.id);
    writeSizedString(buf, point.name);

    buf.writeDouble(point.position.x);
    buf.writeDouble(point.position.y);
    buf.writeDouble(point.position.z);
    buf.writeDouble(point.radius);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    BaseTeamService teamService = game.teamService;
    BasePointService pointService = game.pointService;

    buf.writeByte(teamService.getTeams().size());
    buf.writeByte(pointService.getPoints().size());

    for (Team team : teamService.getTeams())
      serializeTeam(buf, team);

    for (Point point : pointService.getPoints())
      serializePoint(buf, point);
  }
}
