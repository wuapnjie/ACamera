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

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.util.Size;

import com.xiaopo.flying.acamera.model.FaceDetectMode;

import java.util.List;

/**
 * The properties describing a OneCamera device. These properties are fixed for
 * a given OneCamera device.
 */
public interface ACameraCharacteristics {

  /**
   * Gets the supported picture sizes for the given image format.
   *
   * @param imageFormat The specific image format listed on
   *                    {@link ImageFormat}.
   */
  List<Size> getSupportedPictureSizes(int imageFormat);

  /**
   * Gets the supported preview sizes.
   */
  List<Size> getSupportedPreviewSizes();


  /**
   * @return The supported face detection modes.
   */
  List<FaceDetectMode> getSupportedFaceDetectModes();

  Rect getSensorInfoActiveArraySize();
}
