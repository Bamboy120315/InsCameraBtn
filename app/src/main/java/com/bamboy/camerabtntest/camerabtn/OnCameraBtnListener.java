package com.bamboy.camerabtntest.camerabtn;

/**
 * 完成时的监听
 * <p>
 * Created by Bamboy on 2021/5/8.
 */
public interface OnCameraBtnListener {

    /**
     * 完成时回调
     *
     * @param number 第几次完成
     */
    void onFinish(int number);
}
