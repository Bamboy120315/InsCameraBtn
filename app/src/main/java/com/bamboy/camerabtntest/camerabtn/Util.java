package com.bamboy.camerabtntest.camerabtn;

import com.bamboy.camerabtntest.camerabtn.bean.DotBean;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Util {

    /**
     * 根据角度 获取点位置信息
     *
     * @param circularCenterX 圆心X坐标
     * @param circularCenterY 圆心Y坐标
     * @param radius          半径
     * @param angle           角度
     * @param dotDiam         点的直径
     * @return
     */
    public static DotBean getDot(float circularCenterX, float circularCenterY, float radius, float angle, float dotDiam) {
        // 点的中心位置的X坐标
        float dotX = (float) (circularCenterX + (radius + dotDiam) * cos(angle * PI / 180f));
        // 点的中心位置的Y坐标
        float dotY = (float) (circularCenterY + (radius + dotDiam) * sin(angle * PI / 180f));

        // 声明出点的对象
        DotBean dot = new DotBean();
        dot.setX(dotX);
        dot.setY(dotY);
        dot.setRotation(angle);
        dot.setDiam(dotDiam);

        dot.setFarX((float) (circularCenterX + (radius + dotDiam * 1.5f) * cos(angle * PI / 180f)));
        dot.setFarY((float) (circularCenterY + (radius + dotDiam * 1.5f) * sin(angle * PI / 180f)));
        dot.setNearX((float) (circularCenterX + (radius + dotDiam * 0.5f) * cos(angle * PI / 180f)));
        dot.setNearY((float) (circularCenterY + (radius + dotDiam * 0.5f) * sin(angle * PI / 180f)));

        return dot;
    }

    /**
     * 根据三个点坐标获取圆心坐标
     *
     * @param dot1
     * @param dot2
     * @param dot3
     * @return
     */
    public static float[] circleCenter(float[] dot1, float[] dot2, float[] dot3) {

        float yDelta_a = dot2[1] - dot1[1];
        float xDelta_a = dot2[0] - dot1[0];
        float yDelta_b = dot3[1] - dot2[1];
        float xDelta_b = dot3[0] - dot2[0];

        float aSlope = yDelta_a / xDelta_a;
        float bSlope = yDelta_b / xDelta_b;

        float[] center = new float[2];
        center[0] = (aSlope * bSlope * (dot1[1] - dot3[1]) + bSlope * (dot1[0] + dot2[0])
                - aSlope * (dot2[0] + dot3[0])) / (2 * (bSlope - aSlope));
        center[1] = -1 * (center[0] - (dot1[0] + dot2[0]) / 2) / aSlope + (dot1[1] + dot2[1]) / 2;

        return center;
    }

    /**
     * 根据坐标计算角度
     *
     * @param circleCenter 圆心坐标
     * @param dot          圆上某点的坐标
     * @return 此点在圆上的角度
     */
    public static float getDotRotation(float[] circleCenter, float[] dot) {
        // 利用两个点的坐标，算出直角三角形的直角顶点，再利用反正切函数得到角度
        float rotation = (float) (Math.atan((dot[1] - circleCenter[1]) / (dot[0] - circleCenter[0])) / PI * 180f);
        // 因为利用的是直角三角形的反正切，所以当点的X坐标在圆心的左边时，得到的是其实是对角的点的角度，故此要加上360的一半。
        if (dot[0] < circleCenter[0]) {
            rotation += 180;
        }

        return rotation;
    }
}
