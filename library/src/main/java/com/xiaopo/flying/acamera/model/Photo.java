package com.xiaopo.flying.acamera.model;

/**
 * @author wupanjie
 */
public class Photo {
  private final byte[] bytes;

  public Photo(byte[] bytes) {
    this.bytes = bytes;
  }

  public byte[] getBytes() {
    return bytes;
  }

}
