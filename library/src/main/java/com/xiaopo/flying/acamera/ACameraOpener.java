package com.xiaopo.flying.acamera;

import android.os.Handler;

import com.xiaopo.flying.acamera.model.CameraId;

import io.reactivex.Single;

/**
 * @author wupanjie
 */
public interface ACameraOpener {
  Single<ACamera> open(CameraId cameraId, Handler cameraHandler);
}
