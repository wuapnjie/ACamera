package com.xiaopo.flying.acamera.state;

import com.xiaopo.flying.acamera.base.SafeCloseable;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.base.Updatable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class CameraState<T> implements Supplier<T>, Updatable<T>, SafeCloseable {

  private BehaviorSubject<T> stateSubject;
  private CompositeDisposable compositeDisposable;

  public CameraState(T defaultValue) {
    stateSubject = BehaviorSubject.createDefault(defaultValue);
    compositeDisposable = new CompositeDisposable();
  }

  @Override
  public void update(T newValue) {
    if (newValue == stateSubject.getValue()) return;
    stateSubject.onNext(newValue);
  }

  public void registryStateObserver(Consumer<T> onNext) {
    compositeDisposable.add(stateSubject.subscribe(onNext));
  }

  public void registryStateObserver(Consumer<T> onNext, Consumer<Throwable> onError) {
    compositeDisposable.add(stateSubject.subscribe(onNext, onError));
  }

  @Override
  public T get() {
    return stateSubject.getValue();
  }

  @Override
  public void close() {
    stateSubject.onComplete();
    compositeDisposable.dispose();
  }
}
