package dev.redholms.svo.common;

import javax.vecmath.Vector3d;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePointService {
  private final Map<String, Point> points;

  public BasePointService() {
    this.points = new HashMap<>();
  }

  public BasePointService(BasePointService other) {
    this.points = new HashMap<>(other.points);
  }

  public Point getPoint(String id) {
    return points.get(id);
  }

  public Point findPoint(Vector3d position) {
    Point closestPoint = null;
    double closestDistance = Double.MAX_VALUE;

    for (Point p : points.values()) {
      Vector3d diff = new Vector3d();
      diff.sub(p.position, position);

      double distance = diff.length();

      if (distance < p.radius && distance < closestDistance) {
        closestPoint = p;
        closestDistance = diff.length();
      }
    }

    return closestPoint;
  }

  public Point addPoint(String id, String name) {
    if (this.getPoint(id) != null)
      throw new IllegalArgumentException("A point with the id \"" + id + "\" already exists!");

    Point point = new Point(id, name);
    points.put(id, point);

    return point;
  }

  public void removePoint(Point point) {
    points.remove(point.id);
  }

  public void removePoint(String id) {
    removePoint(getPoint(id));
  }

  public Collection<Point> getPoints() {
    return points.values();
  }

  public void clear() {
    points.clear();
  }
}
