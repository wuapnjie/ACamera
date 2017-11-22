package com.xiaopo.flying.acamera.command;

/**
 * @author wupanjie
 */
public interface CommandFactory<TYPE, COMMAND extends Command> {
  COMMAND create(TYPE type);
}
