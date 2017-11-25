package com.xiaopo.flying.acamera.base;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Enables handling the shut-down of components in a structured way.
 * <p>
 * Lifetimes are nestable sets of {@link SafeCloseable}s useful for guaranteeing
 * that resources, such as threads, files, hardware devices, etc., are properly
 * closed when necessary.
 * <p>
 * Child lifetimes are closed when their parent is closed, or when they are
 * closed directly, whichever comes first. Objects added to a particular
 * lifetime will only ever be closed once by that lifetime.
 * </p>
 */
public class Lifetime implements SafeCloseable {
  /**
   * The parent, or null if there is no parent lifetime.
   */
  private final Lifetime parent;
  private final Object lock;
  private final Set<SafeCloseable> closeables;
  private boolean closed;

  public Lifetime() {
    lock = new Object();
    closeables = new LinkedHashSet<>();
    parent = null;
    closed = false;
  }

  public Lifetime(Lifetime parent) {
    lock = new Object();
    closeables = new LinkedHashSet<>();
    this.parent = parent;
    closed = false;
    this.parent.closeables.add(this);
  }

  /**
   * Adds the given object to this lifetime and returns it.
   */
  public <T extends SafeCloseable> T add(T closeable) {
    boolean needToClose = false;
    synchronized (lock) {
      if (closed) {
        needToClose = true;
      } else {
        closeables.add(closeable);
      }
    }
    if (needToClose) {
      closeable.close();
    }
    return closeable;
  }

  @Override
  public void close() {
    List<SafeCloseable> toClose = new ArrayList<>();
    synchronized (lock) {
      if (closed) {
        return;
      }
      closed = true;
      // Remove from parent to avoid leaking memory if a long-lasting
      // lifetime has lots of shorter-lived lifetimes created and
      // destroyed repeatedly.
      if (parent != null) {
        parent.closeables.remove(this);
      }
      toClose.addAll(closeables);
      closeables.clear();
    }
    // Invoke close() outside the critical section
    for (SafeCloseable closeable : toClose) {
      closeable.close();
    }
  }
}
