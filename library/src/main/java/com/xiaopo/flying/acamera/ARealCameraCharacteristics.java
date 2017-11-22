/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaopo.flying.acamera;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import com.xiaopo.flying.acamera.model.FaceDetectMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a OneCamera device which is on top of camera2 API. This is
 * essential a wrapper for #{link
 * android.hardware.camera2.CameraCharacteristics}.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ARealCameraCharacteristics
    implements ACameraCharacteristics {
  private static final int CONTROL_SCENE_MODE_HDR = 0x12;
  private static final String TAG = "OneCamCharImpl";

  private final CameraCharacteristics cameraCharacteristics;

  public ARealCameraCharacteristics(CameraCharacteristics cameraCharacteristics) {
    this.cameraCharacteristics = cameraCharacteristics;
  }

  @Override
  public List<Size> getSupportedPictureSizes(int imageFormat) {
    StreamConfigurationMap configMap;
    try {
      configMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    } catch (Exception ex) {
      Log.e(TAG, "Unable to obtain picture sizes.", ex);
      // See b/19623115   where java.lang.AssertionError can be thrown due to HAL error
      return new ArrayList<>(0);
    }

    if (configMap == null) {
      return new ArrayList<>(0);
    }

    ArrayList<Size> supportedPictureSizes = new ArrayList<>();
    for (Size androidSize : configMap.getOutputSizes(imageFormat)) {
      supportedPictureSizes.add(new Size(androidSize.getWidth(), androidSize.getHeight()));
    }
    return supportedPictureSizes;
  }

  @Override
  public List<Size> getSupportedPreviewSizes() {
    StreamConfigurationMap configMap;
    try {
      configMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    } catch (Exception ex) {
      Log.e(TAG, "Unable to obtain preview sizes.", ex);
      // See b/19623115   where java.lang.AssertionError can be thrown due to HAL error
      return new ArrayList<>(0);
    }

    if (configMap == null) {
      return new ArrayList<>(0);
    }

    ArrayList<Size> supportedPictureSizes = new ArrayList<>();
    for (Size androidSize : configMap.getOutputSizes(SurfaceTexture.class)) {
      supportedPictureSizes.add(new Size(androidSize.getWidth(), androidSize.getHeight()));
    }
    return supportedPictureSizes;
  }

  @Override
  public List<FaceDetectMode> getSupportedFaceDetectModes() {
    int[] modes = cameraCharacteristics.get(
        CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);

    if (modes == null) {
      return new ArrayList<>(0);
    }

    List<FaceDetectMode> oneModes = new ArrayList<>(modes.length);

    for (int mode : modes) {
      if (mode == CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL) {
        oneModes.add(FaceDetectMode.FULL);
      }
      if (mode == CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE) {
        oneModes.add(FaceDetectMode.SIMPLE);
      }
      if (mode == CameraMetadata.STATISTICS_FACE_DETECT_MODE_OFF) {
        oneModes.add(FaceDetectMode.NONE);
      }
    }

    return oneModes;
  }

  @Override
  public Rect getSensorInfoActiveArraySize() {
    return cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
  }

}
