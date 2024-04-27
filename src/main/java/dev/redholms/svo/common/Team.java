package dev.redholms.svo.common;

import net.minecraft.util.ChunkCoordinates;

import java.awt.*;
import java.util.HashSet;

public class Team {
  public final String name;
  public String displayName;
  public Color color;
  public final HashSet<String> membership;
  public ChunkCoordinates spawnpoint;

  public Team(String name) {
    this.name = name;
    this.displayName = name;
    this.color = Color.WHITE;
    this.membership = new HashSet<>();
    this.spawnpoint = new ChunkCoordinates(0, 0, 0);
  }
}
