package com.xiaopo.flying.acamera.command;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.base.Supplier;

import java.util.HashMap;


/**
 * @author wupanjie
 */
public abstract class CameraCommandFactory implements CommandFactory<CameraCommandType, CameraCommand> {

  public static CameraCommandFactory factory(Consumer<CameraCommandBuilder> consumer) {
    final HashMap<CameraCommandType, Supplier<CameraCommand>> commands = new HashMap<>();

    consumer.accept(new CameraCommandBuilder() {
      @Override
      public void add(CameraCommandType type, Supplier<CameraCommand> supplier) {
        commands.put(type, supplier);
      }
    });

    return new CameraCommandFactory() {
      @Override
      public CameraCommand create(CameraCommandType type) {
        return commands.get(type).get();
      }
    };
  }

}
