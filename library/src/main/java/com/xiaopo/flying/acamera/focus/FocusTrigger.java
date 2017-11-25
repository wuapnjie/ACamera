package com.xiaopo.flying.acamera.focus;

import android.graphics.PointF;

import com.xiaopo.flying.acamera.command.CameraCommand;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;
import com.xiaopo.flying.acamera.state.CameraState;
import com.xiaopo.flying.acamera.state.CameraStateManager;

import java.util.concurrent.TimeUnit;

/**
 * @author wupanjie
 */
public class FocusTrigger implements FocusFunction {

  private final CameraCommandFactory commandFactory;
  private final CameraStateManager cameraStateManager;
  private final int sensorOrientation;
  private final Settings3A settings3A;

  public FocusTrigger(CameraCommandFactory commandFactory, CameraStateManager cameraStateManager, int sensorOrientation) {
    this.commandFactory = commandFactory;
    this.cameraStateManager = cameraStateManager;
    this.sensorOrientation = sensorOrientation;
    this.settings3A = new Settings3A();
  }


  @Override
  public void triggerFocusAt(float x, float y) {
    PointF point = new PointF(x, y);
    CameraState<MeteringParameters> cameraState = cameraStateManager.getMeteringState();
    cameraState.update(PointMeteringParameters.createForNormalizedCoordinates(point /* afPoint */, point /* aePoint */,
        sensorOrientation, settings3A));

    CameraCommand focusCommand = commandFactory.create(CameraCommandType.SCAN_FAOCUS);
    CameraCommandCenter.getInstance().nextCommand(focusCommand);

    CameraCommand previewCommand = commandFactory.create(CameraCommandType.PREVIEW);
    CameraCommandCenter.getInstance().nextCommand(previewCommand, 3, TimeUnit.SECONDS);
  }
}
