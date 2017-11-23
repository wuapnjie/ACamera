package com.xiaopo.flying.acamera.state;

import com.xiaopo.flying.acamera.base.Supplier;

import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class CameraState<T> implements Supplier<T> {

  private BehaviorSubject<T> stateSubject;

  public CameraState(T defaultValue) {
    stateSubject = BehaviorSubject.createDefault(defaultValue);
  }

  public void update(T newValue) {
    stateSubject.onNext(newValue);
  }

  public void registryStateObserver(Observer<T> observer) {
    stateSubject.subscribe(observer);
  }

  @Override
  public T get() {
    return stateSubject.getValue();
  }
}
