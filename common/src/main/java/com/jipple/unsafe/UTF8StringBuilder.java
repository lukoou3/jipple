package com.jipple.unsafe;

import com.jipple.unsafe.array.ByteArrayMethods;
import com.jipple.unsafe.types.UTF8String;

/**
 * A helper class to write {@link UTF8String}s to an internal buffer and build the concatenated
 * {@link UTF8String} at the end.
 */
public class UTF8StringBuilder {

  private static final int ARRAY_MAX = ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH;

  private byte[] buffer;
  private int cursor = Platform.BYTE_ARRAY_OFFSET;

  public UTF8StringBuilder() {
    // Since initial buffer size is 16 in `StringBuilder`, we set the same size here
    this(16);
  }

  public UTF8StringBuilder(int initialSize) {
    if (initialSize < 0) {
      throw new IllegalArgumentException("Size must be non-negative");
    }
    if (initialSize > ARRAY_MAX) {
      throw new IllegalArgumentException(
        "Size " + initialSize + " exceeded maximum size of " + ARRAY_MAX);
    }
    this.buffer = new byte[initialSize];
  }

  // Grows the buffer by at least `neededSize`
  private void grow(int neededSize) {
    if (neededSize > ARRAY_MAX - totalSize()) {
      throw new UnsupportedOperationException(
        "Cannot grow internal buffer by size " + neededSize + " because the size after growing " +
          "exceeds size limitation " + ARRAY_MAX);
    }
    final int length = totalSize() + neededSize;
    if (buffer.length < length) {
      int newLength = length < ARRAY_MAX / 2 ? length * 2 : ARRAY_MAX;
      final byte[] tmp = new byte[newLength];
      Platform.copyMemory(
        buffer,
        Platform.BYTE_ARRAY_OFFSET,
        tmp,
        Platform.BYTE_ARRAY_OFFSET,
        totalSize());
      buffer = tmp;
    }
  }

  private int totalSize() {
    return cursor - Platform.BYTE_ARRAY_OFFSET;
  }

  public void append(UTF8String value) {
    grow(value.numBytes());
    value.writeToMemory(buffer, cursor);
    cursor += value.numBytes();
  }

  public void append(String value) {
    append(UTF8String.fromString(value));
  }

  public void appendBytes(Object base, long offset, int length) {
    grow(length);
    Platform.copyMemory(
      base,
      offset,
      buffer,
      cursor,
      length);
    cursor += length;
  }

  public UTF8String build() {
    return UTF8String.fromBytes(buffer, 0, totalSize());
  }

  public void appendCodePoint(int codePoint) {
    if (codePoint <= 0x7F) {
      grow(1);
      buffer[cursor - Platform.BYTE_ARRAY_OFFSET] = (byte) codePoint;
      ++cursor;
    } else if (codePoint <= 0x7FF) {
      grow(2);
      buffer[cursor - Platform.BYTE_ARRAY_OFFSET] = (byte) (0xC0 | (codePoint >> 6));
      buffer[cursor + 1 - Platform.BYTE_ARRAY_OFFSET] = (byte) (0x80 | (codePoint & 0x3F));
      cursor += 2;
    } else if (codePoint <= 0xFFFF) {
      grow(3);
      buffer[cursor - Platform.BYTE_ARRAY_OFFSET] = (byte) (0xE0 | (codePoint >> 12));
      buffer[cursor + 1 - Platform.BYTE_ARRAY_OFFSET] = (byte) (0x80 | ((codePoint >> 6) & 0x3F));
      buffer[cursor + 2 - Platform.BYTE_ARRAY_OFFSET] = (byte) (0x80 | (codePoint & 0x3F));
      cursor += 3;
    } else if (codePoint <= 0x10FFFF) {
      grow(4);
      buffer[cursor - Platform.BYTE_ARRAY_OFFSET] = (byte) (0xF0 | (codePoint >> 18));
      buffer[cursor + 1 - Platform.BYTE_ARRAY_OFFSET] = (byte) (0x80 | ((codePoint >> 12) & 0x3F));
      buffer[cursor + 2 - Platform.BYTE_ARRAY_OFFSET] = (byte) (0x80 | ((codePoint >> 6) & 0x3F));
      buffer[cursor + 3 - Platform.BYTE_ARRAY_OFFSET] = (byte) (0x80 | (codePoint & 0x3F));
      cursor += 4;
    } else {
      throw new IllegalArgumentException("Invalid Unicode codePoint: " + codePoint);
    }
  }

}
