package dev.redholms.svo.common;

import javax.vecmath.Vector3d;

public class Point {
  public String id;
  public String name;
  public Vector3d position;
  public double radius;
  public Team controller;

  public Point(String id, String name) {
    this.id = id;
    this.name = name;
    this.position = new Vector3d();
    this.radius = 0;
    this.controller = null;
  }
}
