package com.xiaopo.flying.acamera.base;

import android.support.annotation.Nullable;

/**
 * @author wupanjie
 */
public interface Function<F, T> {
  @Nullable
  T apply(@Nullable F input);

  @Override
  boolean equals(@Nullable Object object);
}