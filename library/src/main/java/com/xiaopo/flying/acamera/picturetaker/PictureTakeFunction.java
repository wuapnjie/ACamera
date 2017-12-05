package com.xiaopo.flying.acamera.picturetaker;

import com.xiaopo.flying.acamera.model.Photo;

import io.reactivex.Single;

/**
 * @author wupanjie
 */
public interface PictureTakeFunction {

  Single<Photo> takePicture();

}
