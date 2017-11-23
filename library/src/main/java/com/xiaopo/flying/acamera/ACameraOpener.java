package com.xiaopo.flying.acamera;

import android.os.Handler;

import com.xiaopo.flying.acamera.model.CameraId;

import io.reactivex.Single;

/**
 * @author wupanjie
 */
public abstract class ACameraOpener {

  public static ACameraOpener with(CameraId cameraId, Handler cameraHandler) {
    return new ARealCamera2Opener(cameraId, cameraHandler);
  }

  public abstract Single<ACamera> open();
}
