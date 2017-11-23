package com.xiaopo.flying.acamera.base;

/**
 * An {@link AutoCloseable} which should not throw in {@link #close}.
 */
public interface SafeCloseable extends AutoCloseable {
  /**
   * Implementations must tolerate multiple calls to close().
   */
  @Override
  void close();
}
