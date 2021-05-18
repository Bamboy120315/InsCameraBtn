package com.bamboy.camerabtntest.camerabtn;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.bamboy.camerabtntest.R;
import com.bamboy.camerabtntest.camerabtn.bean.DotBean;
import com.bamboy.camerabtntest.camerabtn.bean.TotalDot;

import static com.bamboy.camerabtntest.camerabtn.Util.circleCenter;
import static com.bamboy.camerabtntest.camerabtn.Util.getDot;
import static com.bamboy.camerabtntest.camerabtn.Util.getDotRotation;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class CameraBtn extends View {
    /**
     * View宽高
     */
    private int mWidth, mHight;
    /**
     * View宽高里小的那个
     */
    private int mViewDiam;
    /**
     * 线条宽度
     */
    private float mLineWidth = -1;
    /**
     * 线宽一半，方便矩阵计算宽高
     */
    private float mLineWidthHalf;
    /**
     * 当前进度
     */
    private int progress = 0;
    /**
     * 总进度
     */
    private int mProgressMax = 1000;
    /**
     * 第几圈
     */
    private int mNumber = 1;

    /**
     * 圆圈的直径
     */
    private float mRingDiam = 100;
    /**
     * 白色圆环画笔
     */
    private Paint mRingPaint;
    /**
     * 白色圆环矩阵
     */
    private RectF mRingOval;
    /**
     * 进度画笔
     */
    private Paint mProgresSpaint;
    /**
     * 月牙外弧矩阵
     */
    private RectF mCrescentOutsideArcOval;

    /**
     * 完成时回调
     */
    private OnCameraBtnListener mCameraBtnListener;

    public CameraBtn(Context context) {
        super(context);
        init(null);
    }

    public CameraBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CameraBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CameraBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CameraBtn);
        mLineWidth = typedArray.getDimension(R.styleable.CameraBtn_cb_lineWidth, -1);

        post(() -> invalidate());
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHight = h;
        // 保证圆形，取宽高中小的那个
        mViewDiam = Math.min(w, h);
        // 圆环的线宽
        mLineWidth = mLineWidth == -1 ? mViewDiam * 0.1f : mLineWidth;
        // 线宽的一半，由于很多地方用到（mLineWidth / 2f），所以抽出来个变量，简化代码
        mLineWidthHalf = mLineWidth / 2f;
        // 圆环的直径
        mRingDiam = mViewDiam - mLineWidth * 2;

        // 初始化圆环相关
        initRing();
        // 初始化进度相关
        initProgres();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画底部白色圆圈
        onDrawBackgroundRound(canvas);

        // 画进度
        onDrawProgress(canvas);
    }

    /**
     * 初始化圆环相关
     */
    private void initRing() {
        // 声明圆环画笔
        mRingPaint = new Paint();
        // 设置抗锯齿
        mRingPaint.setAntiAlias(true);
        // 设置防抖，即边缘柔化
        mRingPaint.setDither(true);
        // 设置颜色
        mRingPaint.setColor(Color.WHITE);
        // 设置描边
        mRingPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔的宽度
        mRingPaint.setStrokeWidth(mLineWidth);

        // 声明圆环矩阵
        mRingOval = new RectF(mLineWidthHalf, mLineWidthHalf, mViewDiam - mLineWidthHalf, mViewDiam - mLineWidthHalf);
    }

    /**
     * 初始化进度相关
     */
    private void initProgres() {
        // 声明画笔
        mProgresSpaint = new Paint();
        mProgresSpaint.setStrokeJoin(Paint.Join.ROUND);
        // 设置抗锯齿
        mProgresSpaint.setAntiAlias(true);
        // 设置防抖，即边缘柔化
        mProgresSpaint.setDither(true);
        // 设置线条圆角
        mProgresSpaint.setStrokeCap(Paint.Cap.ROUND);

        // 声明月牙外弧矩阵
        mCrescentOutsideArcOval = new RectF(0, 0, mViewDiam, mViewDiam);
    }

    /**
     * 画底部白色圆圈
     *
     * @param canvas
     */
    private void onDrawBackgroundRound(Canvas canvas) {
        // 绘制圆圈
        canvas.drawArc(mRingOval, 0, 360, false, mRingPaint);
    }

    /**
     * 画进度
     *
     * @param canvas
     */
    private void onDrawProgress(Canvas canvas) {
        // 进度
        float pro = (float) progress / (float) mProgressMax;
        // 计算所有的点
        TotalDot dots = computeTotalDot(pro);
        // 圆环进度
        float sweepAngle = 0 - (dots.dotStart.getRotation() - dots.dotCenter.getRotation());

        // 设置描边
        mProgresSpaint.setStyle(Paint.Style.STROKE);
        // 设置画笔的宽度
        mProgresSpaint.setStrokeWidth(mLineWidth);
        // 设置色盘
        mProgresSpaint.setShader(getShader());

        // 绘制第一段弧线
        canvas.drawArc(mRingOval, dots.dotStart.getRotation(), sweepAngle, false, mProgresSpaint);

        if (dots.dotEnd != null) {
            // 设置实心
            mProgresSpaint.setStyle(Paint.Style.FILL);
            mProgresSpaint.setStrokeWidth(0);

            // 画出尾点
            canvas.drawCircle(
                    dots.dotEnd.getX(),
                    dots.dotEnd.getY(),
                    dots.dotEnd.getDiam() / 2,
                    mProgresSpaint);

            // 画月牙
            canvasCrescent(canvas, dots, pro);

            // 画辅助开发的辅助点，调试时打开
            // canvasDebugDot(canvas, dots);
        }
    }

    /**
     * 计算所有的点
     *
     * @param progress
     * @return
     */
    private TotalDot computeTotalDot(float progress) {
        // 旋转角度
        float rotation = progress * 360 - 90;

        // 圆心位置
        float circularCenterX = mLineWidth + mRingDiam / 2f, circularCenterY = circularCenterX;
        // 圆的半径
        float circularRadius = mRingDiam / 2f - mLineWidthHalf;

        // 中间点的中心 相对圆心的角度
        float centerAngle;

        // 尾点相对缩小的比例
        float endScale = -1;
        // 尾点的直径
        float endDotDiam = 0;
        // 尾点的所在圆的半径
        float endRadius = 0;
        // 尾点的中心 相对圆心的角度
        float endAngle = 0;

        // 辅助点的所在圆的半径
        float auxiliaryRadius = 0;
        // 辅助点的中心 相对圆心的角度
        float auxiliaryAngle = 0;

        // 根据进度，计算每个点相关的数据
        // 进度小于20%，只画前面的线条，不画月牙
        // 进度小于50%，大于20%，画前面的线条 + 动态的月牙
        // 进度大于50%，画前面的线条 + 固定大小的月牙
        if (mNumber == 1 && progress <= 0.2f) {
            // 进度小于20%，只画前面的线条，不画月牙

            // 计算中间点相关数据
            centerAngle = rotation - (360 * progress);

        } else if (mNumber == 1 && progress <= 0.5f) {
            // 进度小于50%，大于20%，画前面的线条 + 动态的月牙

            // 计算中间点相关数据
            centerAngle = rotation - (360 * 0.2f);
            // 计算尾点相关数据
            endScale = (progress - 0.2f) * ((0.5f - 0.2f) / 0.09f);
            endDotDiam = mLineWidth - (mLineWidth * endScale);
            endRadius = mRingDiam / 2f + mLineWidthHalf + mLineWidthHalf * endScale - endDotDiam;
            endAngle = -90;
            // 计算辅助点相关数据
            auxiliaryRadius = mRingDiam / 2f + mLineWidthHalf + (endScale - 1) * mLineWidthHalf;
            auxiliaryAngle = centerAngle + (endAngle - centerAngle) / 2f;

        } else {
            // 进度大于50%，画前面的线条 + 固定大小的月牙

            // 计算中间点相关数据
            centerAngle = rotation - (360 * 0.2f);
            // 计算尾点相关数据
            endScale = 0.99f;
            endDotDiam = mLineWidth - (mLineWidth * endScale);
            endRadius = mRingDiam / 2f + mLineWidthHalf + mLineWidthHalf * endScale - endDotDiam;
            endAngle = rotation - (360 * 0.5f);
            // 计算辅助点相关数据
            auxiliaryRadius = mRingDiam / 2f + mLineWidthHalf + (endScale - 1) * mLineWidthHalf;
            auxiliaryAngle = centerAngle + (endAngle - centerAngle) / 2f;
        }


        // 起始点
        DotBean dotStart = getDot(circularCenterX, circularCenterY, circularRadius, rotation, mLineWidth);
        // 中间点
        DotBean dotCenter = getDot(circularCenterX, circularCenterY, circularRadius, centerAngle, mLineWidth);
        // 结尾点
        DotBean dotEnd = endScale == -1 ? null : getDot(circularCenterX, circularCenterY, endRadius, endAngle, endDotDiam);
        // 辅助点
        DotBean dotAuxiliary = endScale == -1 ? null : getDot(circularCenterX, circularCenterY, auxiliaryRadius, auxiliaryAngle, 0);

        // 所有的点
        return new TotalDot(dotStart, dotCenter, dotEnd, dotAuxiliary);
    }

    /**
     * 画月牙
     */
    private void canvasCrescent(Canvas canvas, TotalDot dots, float progress) {
        Path path = new Path();

        // 画内弧
        pathInsideArc(canvas, path, dots);

        // 跨过中间的点
        path.lineTo(dots.dotCenter.getFarX(), dots.dotCenter.getFarY());

        // 画外弧
        // 旋转角度
        float rotation = ((mNumber == 1 && progress < 0.5f) ? 0f : progress - 0.5f) * 360 - 90;
        rotation -= dots.dotEnd.getRotation() - dots.dotCenter.getRotation();
        float angle = dots.dotEnd.getRotation() - dots.dotCenter.getRotation();
        path.arcTo(mCrescentOutsideArcOval, rotation, angle);

        // 跨过尾点
        path.lineTo(dots.dotEnd.getNearX(), dots.dotEnd.getNearY());

        mProgresSpaint.setStyle(Paint.Style.FILL);
        mProgresSpaint.setStrokeWidth(0);

        // 画出Path
        canvas.drawPath(path, mProgresSpaint);
    }

    /**
     * 连线 内圆的弧线，简称内弧
     */
    private void pathInsideArc(Canvas canvas, Path path, TotalDot dots) {

        // 计算小圆的圆心坐标
        float[] dot1 = new float[]{dots.dotCenter.getNearX(), dots.dotCenter.getNearY()};
        float[] dot2 = new float[]{dots.dotAuxiliary.getX(), dots.dotAuxiliary.getY()};
        float[] dot3 = new float[]{dots.dotEnd.getNearX(), dots.dotEnd.getNearY()};
        float[] circleCenter = circleCenter(dot1, dot2, dot3);

        // 尾点在内圆上的角度
        float dot3Rotation = getDotRotation(circleCenter, dot3);

        // 中间点在内圆上的角度
        float dot2Rotation = getDotRotation(circleCenter, dot1);

        // 弧线在内圆上划过的角度差
        float insideSweepAngle = dot2Rotation - dot3Rotation;

        // 角度差异常时处理，
        // 比如中间点的角度是30°，
        // 尾点是282°，
        // 两个点在圆上的角度，
        // 算出来是-252°，但实际上应该是30° + 360° - 282° = 108°
        if (insideSweepAngle < 0) {
            insideSweepAngle += 360;
        }

        // 计算内圆的半径
        float intervalX = abs(circleCenter[0] - dot2[0]);
        float intervalY = abs(circleCenter[1] - dot2[1]);
        float insideCircleRadius = (float) sqrt(pow(intervalX, 2) + pow(intervalY, 2));
        // 内圆的矩阵
        RectF ovalInterval = new RectF(circleCenter[0] - insideCircleRadius, circleCenter[1] - insideCircleRadius, circleCenter[0] + insideCircleRadius, circleCenter[1] + insideCircleRadius);
        // 在内圆上连出弧线
        path.arcTo(ovalInterval, dot3Rotation, insideSweepAngle);

        // 画辅助开发的内圆，调试时打开
        // canvasDebugCrescentCircle(canvas, ovalInterval);
    }

    /**
     * 获取线性色盘
     *
     * @return
     */
    private Shader getShader() {
        // 进度
        float pro = (float) progress / (float) mProgressMax;
        // 渐变色
        float offset = mNumber % 2 == 0 ? pro : 1 - pro;
        float offset2 = offset - 0.5f * 2f;
        float start = mViewDiam * 1.2f * offset * offset ;
        Shader shader = new LinearGradient(start, start,
                start + mViewDiam / 0.7f + (mViewDiam * 0.02f * offset2),
                start + mViewDiam / 0.7f + (mViewDiam * 0.02f * offset2),
                new int[]{0xFFFFD66F, 0xFFFF7E5B, 0xFFFF5C5C, 0xFFC11ABA},
                new float[]{0.08f, 0.35f, 0.65f, 0.90f},
                Shader.TileMode.MIRROR);

        // 让色盘旋转
        Matrix matrix = new Matrix();
        matrix.setRotate(-offset * 280);
        shader.setLocalMatrix(matrix);

        return shader;
    }

    /**
     * 画辅助开发的辅助点
     */
    private void canvasDebugDot(Canvas canvas, TotalDot dots) {
        mProgresSpaint.setShader(null);

        // 起始点相关的点
        canvasDotInfo(canvas, dots.dotStart, Color.BLUE);

        // 中间点相关的点
        canvasDotInfo(canvas, dots.dotCenter, Color.RED);

        // 尾点相关的点
        canvasDotInfo(canvas, dots.dotEnd, Color.GREEN);

        // 小圆辅助点
        mProgresSpaint.setStyle(Paint.Style.FILL);
        mProgresSpaint.setColor(Color.RED);
        canvas.drawCircle(
                dots.dotAuxiliary.getX(),
                dots.dotAuxiliary.getY(),
                5,
                mProgresSpaint);
    }

    /**
     * 画出一个点的相关辅助点
     *
     * @param canvas
     * @param dot
     * @param color
     */
    private void canvasDotInfo(Canvas canvas, DotBean dot, int color) {
        mProgresSpaint.setColor(color);

        // 距离圆心最远的点
        canvas.drawCircle(
                dot.getFarX(),
                dot.getFarY(),
                9,
                mProgresSpaint);

        // 中心点
        canvas.drawCircle(
                dot.getX(),
                dot.getY(),
                7,
                mProgresSpaint);

        // 距离圆心最近的点
        canvas.drawCircle(
                dot.getNearX(),
                dot.getNearY(),
                5,
                mProgresSpaint);
    }

    /**
     * 画辅助开发的内圆
     */
    private void canvasDebugCrescentCircle(Canvas canvas, RectF ovalInterval) {
        // 画内圆（辅助）
        mProgresSpaint.setStyle(Paint.Style.STROKE);
        mProgresSpaint.setStrokeWidth(2);
        mProgresSpaint.setColor(0xFFFF0000);
        canvas.drawArc(ovalInterval, 0, 360, false, mProgresSpaint);
    }

    /**
     * 设置完成时回调
     *
     * @param listener
     */
    public void setOnCameraBtnListener(OnCameraBtnListener listener) {
        mCameraBtnListener = listener;
    }

    /**
     * 获取进度
     *
     * @return
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        int num = progress / 1000;
        progress = progress - num * 1000;
        if (num != 0 && progress == 0) {
            progress = 1000;

            // 触发完成回调
            if (mCameraBtnListener != null) {
                mCameraBtnListener.onFinish(mNumber);
            }
        } else {
            // 圈数加一
            num++;

            updateNumber(num);
        }

        this.progress = progress;

        // 更新UI
        invalidate();
    }

    private void updateNumber(int num) {
        if (mNumber == num) {
            return;
        }

        mNumber = num;

        // 触发完成回调
        if (mCameraBtnListener != null) {
            mCameraBtnListener.onFinish(mNumber - 1);
        }
    }
}
