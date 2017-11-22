package com.xiaopo.flying.acamera.command;

import com.xiaopo.flying.acamera.base.Supplier;

/**
 * @author wupanjie
 */
public interface CameraCommandBuilder {

  void add(CameraCommandType type, Supplier<CameraCommand> supplier);

}
