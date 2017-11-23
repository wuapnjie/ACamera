package com.xiaopo.flying.acamera.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author wupanjie
 */
public interface Function<F, T> {
  @NonNull
  T apply(@NonNull F input);

  @Override
  boolean equals(@Nullable Object object);
}