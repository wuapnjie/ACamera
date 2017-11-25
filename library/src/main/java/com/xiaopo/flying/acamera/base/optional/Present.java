package com.xiaopo.flying.acamera.base.optional;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Set;


/**
 * Implementation of an {@link Optional} containing a reference.
 */

final class Present<T> extends Optional<T> {
  private final T reference;


  Present(T reference) {
    this.reference = reference;
  }


  @Override
  public boolean isPresent() {
    return true;
  }


  @Override
  public T get() {
    return reference;
  }


  @Override
  public T or(T defaultValue) {
    Preconditions.checkNotNull(defaultValue, "use Optional.orNull() instead of Optional.or(null)");
    return reference;
  }


  @Override
  public Optional<T> or(Optional<? extends T> secondChoice) {
    Preconditions.checkNotNull(secondChoice);
    return this;
  }


  @Override
  public T orNull() {
    return reference;
  }


  @Override
  public Set<T> asSet() {
    return Collections.singleton(reference);
  }


  @Override
  public boolean equals(@Nullable Object object) {
    if (object instanceof Present) {
      Present<?> other = (Present<?>) object;
      return reference.equals(other.reference);
    }
    return false;
  }


  @Override
  public int hashCode() {
    return 0x598df91c + reference.hashCode();
  }


  @Override
  public String toString() {
    return "Optional.of(" + reference + ")";
  }


  private static final long serialVersionUID = 0;
}
