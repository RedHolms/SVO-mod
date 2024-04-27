package dev.redholms.svo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.Constants;

import javax.vecmath.Vector3d;

public class NBTUtils {
  public static Vector3d getVector3d(NBTTagCompound nbt, String key) {
    NBTTagList list = nbt.getTagList(key, Constants.NBT.TAG_DOUBLE);

    if (list.tagCount() != 3)
      return null;

    double x, y, z;

    // func_150309_d - getDoubleAt
    x = list.func_150309_d(0);
    y = list.func_150309_d(1);
    z = list.func_150309_d(2);

    return new Vector3d(x, y, z);
  }

  public static void setVector3d(NBTTagCompound nbt, String key, Vector3d value) {
    NBTTagList list = new NBTTagList();

    list.appendTag(new NBTTagDouble(value.x));
    list.appendTag(new NBTTagDouble(value.y));
    list.appendTag(new NBTTagDouble(value.z));

    nbt.setTag(key, list);
  }

  public static ChunkCoordinates getChunkCoordinates(NBTTagCompound nbt, String key) {
    int[] array = nbt.getIntArray(key);

    if (array.length != 3)
      return null;

    return new ChunkCoordinates(array[0], array[1], array[2]);
  }

  public static void setChunkCoordinates(NBTTagCompound nbt, String key, ChunkCoordinates value) {
    nbt.setIntArray(key, new int[]{ value.posX, value.posY, value.posZ });
  }
}
