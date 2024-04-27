package dev.redholms.svo;

import cpw.mods.fml.relauncher.Side;
import dev.redholms.svo.common.*;
import dev.redholms.svo.common.Point;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SVOSavedData extends WorldSavedData {
  private static final String SAVE_NAME = "svo_data";

  public Map<String, IGame> games;

  public SVOSavedData(String name) {
    super(name);

    this.games = new HashMap<>();
  }

  public static SVOSavedData getFromWorld(World world) {
    MapStorage storage = world.perWorldStorage;
    SVOSavedData data = (SVOSavedData) storage.loadData(SVOSavedData.class, SAVE_NAME);

    if (data == null) {
      data = new SVOSavedData(SAVE_NAME);
      storage.setData(SAVE_NAME, data);
    }

    return data;
  }

  private void writeTeams(BaseTeamService teamService, NBTTagCompound nbt) {
    NBTTagList list = new NBTTagList();

    for (Team team : teamService.getTeams()) {
      NBTTagCompound teamNbt = new NBTTagCompound();

      teamNbt.setString("name", team.name);
      teamNbt.setString("display", team.displayName);
      teamNbt.setInteger("color", team.color.getRGB());
      NBTUtils.setChunkCoordinates(teamNbt, "spawnpoint", team.spawnpoint);

      NBTTagList members = new NBTTagList();
      for (String member : team.membership.toArray(new String[0]))
        members.appendTag(new NBTTagString(member));

      teamNbt.setTag("members", members);

      list.appendTag(teamNbt);
    }

    nbt.setTag("teams", list);
  }

  @Override
  public void writeToNBT(NBTTagCompound nbt) {
    SVOMod.LOG.info("Writing world data");

    for (Map.Entry<String, IGame> pair : games.entrySet()) {
      String name = pair.getKey();
      IGame game = pair.getValue();

      SVOMod.LOG.info("Saving game {}", name);

      NBTTagCompound gameNbt = new NBTTagCompound();

      writeTeams(game.teamService, gameNbt);
      gameNbt.setTag("points", new NBTTagList());

      nbt.setTag(name, gameNbt);
    }
  }

  private void readTeams(BaseTeamService teamService, NBTTagCompound nbt) {
    NBTTagList teamsNbt = nbt.getTagList("teams", Constants.NBT.TAG_COMPOUND);

    for (int i = 0; i < teamsNbt.tagCount(); ++i) {
      NBTTagCompound teamNbt = teamsNbt.getCompoundTagAt(i);

      String name = teamNbt.getString("name");
      String displayName = teamNbt.getString("display");
      Color color = new Color(teamNbt.getInteger("color"));
      ChunkCoordinates spawnpoint = NBTUtils.getChunkCoordinates(teamNbt, "spawnpoint");
      NBTTagList membership = teamNbt.getTagList("members", Constants.NBT.TAG_STRING);

      SVOMod.LOG.info("Loaded team {}", name);

      Team team = teamService.createTeam(name, displayName);

      team.color = color;
      team.spawnpoint = spawnpoint;

      for (int j = 0; j < membership.tagCount(); ++j) {
        String member = membership.getStringTagAt(j);
        teamService.setPlayersTeam(member, name);
      }
    }
  }

  private void readPoints(BasePointService pointService, NBTTagCompound nbt) {
    NBTTagList pointsNbt = nbt.getTagList("points", Constants.NBT.TAG_COMPOUND);

    for (int i = 0; i < pointsNbt.tagCount(); ++i) {
      NBTTagCompound pointNbt = pointsNbt.getCompoundTagAt(i);

      String id = pointNbt.getString("id");
      String name = pointNbt.getString("name");
      Vector3d position = NBTUtils.getVector3d(pointNbt, "position");
      double radius = pointNbt.getDouble("radius");

      Point point = pointService.addPoint(id, name);

      point.position = position;
      point.radius = radius;
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    games.clear();

    // func_150296_c - getKeysSet
    Set<String> keys = nbt.func_150296_c();

    for (String key : keys) {
      NBTTagCompound gameNbt = nbt.getCompoundTag(key);

      // games actually can only be loaded from the server,
      //  but we're settings side to the client, so game won't send anything on the network
      IGame game = new IGame(Side.CLIENT);

      readTeams(game.teamService, gameNbt);
      readPoints(game.pointService, gameNbt);

      games.put(key, game);
    }
  }
}
