package com.jipple.sql.catalyst.expressions;

import com.jipple.unsafe.Platform;

/**
 * General utilities available for unsafe data
 */
final class UnsafeDataUtils {

  private UnsafeDataUtils() {
  }

  public static byte[] getBytes(Object baseObject, long baseOffset, int sizeInBytes) {
    if (baseObject instanceof byte[]
      && baseOffset == Platform.BYTE_ARRAY_OFFSET
      && (((byte[]) baseObject).length == sizeInBytes)) {
      return (byte[]) baseObject;
    }
    byte[] bytes = new byte[sizeInBytes];
    Platform.copyMemory(baseObject, baseOffset, bytes, Platform.BYTE_ARRAY_OFFSET,
      sizeInBytes);
    return bytes;
  }
}
