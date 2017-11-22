package com.xiaopo.flying.sample;

import android.app.Application;

import com.xiaopo.flying.acamera.util.AndroidContext;


/**
 * @author wupanjie
 */
public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    AndroidContext.initialize(this);
  }
}
