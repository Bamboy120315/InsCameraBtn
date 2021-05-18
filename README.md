
本文github链接：[https://github.com/Bamboy120315/InsCameraBtn](https://github.com/Bamboy120315/InsCameraBtn)

刚入职一家公司，
导师让我实现一个，
仿ins相机按钮进度的效果，
乍一看感觉也不是太复杂，
思路也基本上能捋清，
于是就接手了。

看下效果：
![ins拍照按钮](https://img-blog.csdnimg.cn/20210517190458365.gif)

导师说只要进度条，
其他的都不要，
比如放大、灰色完圈都不要，
于是我大致想一下，
整体的开发顺序如下：
1. 画出白色圆环
2. 进度部分拆解并画出
3. 动态渐变色

### 1、画出白色圆环
这个很简单，
获取了View宽高、圆环的线宽，
就可以声明出画笔和圆形的矩阵了

计算相关变量：
```
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
```

声明画笔：
```
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
```

声明圆环的矩阵，并画出圆环：
```
// 声明圆环矩阵
mRingOval = new RectF(  // 这四个参数分别控制圆环左上右下的位置
                    mLineWidthHalf, 
                    mLineWidthHalf, 
                    mViewDiam - mLineWidthHalf,
                    mViewDiam - mLineWidthHalf);
// 绘制圆圈
canvas.drawArc(mRingOval, 0, 360, false, mRingPaint);
```

到这里，
白色的圆环部分就画出来了。


### 2、进度部分拆解并画出

其实不难发现，
这个进度条其实是分成前后两段。

第一段就是一个很常见的
和白色圆环等宽并重叠的弧线，
这个效果的重点在于第二段，
就是贴住圆环的外边，
越来越细的类似月牙的弧线。

所以，
我们可以把整个进度拆成以下几个部分：

1. 一段弧线
2. 一个随进度变小的尾点
3. 连接弧线和尾点的月牙

现在，
我们已经搞清楚了这个进度条的构成，
那么我们就来梳理下实现方案吧。

一条线需要两个点，
再加上一个尾点，
那就是三个点，
所以我们需要构建出这三个点的对象，
每个对象内包含以下属性：
```
/**
 * 坐标X
 */
private float x;
/**
 * 坐标Y
 */
private float y;
/**
 * 在圆上的角度
 */
private float rotation;
/**
 * 点的直径
 */
private float diam;
```

有了这个对象，
下面就根据进度来声明这三个点。

当进度小于0.2时，
只有前面的弧线，
并没有月牙，
所以只需要构建起始点和中间点，
并且中间点的位置一直保持在起点。

当进度在0.2~0.5之间时，
中间点跟随起始点，
并与起始点保持72°的距离。
尾点位置一直保持在起点，
并随着进度缩小。

当进度大于0.5时，
中间点跟随起始点，
并与起始点保持72°的距离。
尾点跟随中间点，
并与中间点保持108°的距离。
并大小为0。

这里逻辑并不复杂，
但涉及变量太多，
代码有点繁琐，
所以只展示出关键方法，
完整逻辑建议下载阅读源码：
```
/**
 * 根据角度 获取点位置信息
 *
 * @param circularCenterX 圆心X坐标
 * @param circularCenterY 圆心Y坐标
 * @param radius          半径
 * @param angle           角度
 * @param dotDiam         点的直径
 * @return 点的对象
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

    return dot;
}
```

现在三个点的坐标尺寸和角度都有了，
接下来就是画出前一段弧线，
和最后的这个点了。

画前弧：
```
// 圆环进度
float sweepAngle = 0 - (dots.dotStart.getRotation() - dots.dotCenter.getRotation());

// 绘制前弧
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
}
```

看下效果：
![前弧+尾点](https://img-blog.csdnimg.cn/20210517145434516.gif)

前弧和尾点已经画出来了，
下一步就是画月牙，
即把前弧和尾点连接起来。

即图中深灰色部分：
![月牙预览图](https://img-blog.csdnimg.cn/20210517153047453.png)

根据这个图可以看出，
这个月牙主要就是两个弧线，
不过画出这两个弧的前提，
是要知道中间点和尾点的内点和外点的坐标，
即下图中的四个红点：
![辅助点](https://img-blog.csdnimg.cn/20210517154039824.png)

所以刚才的getDot()方法就要多计算两个坐标：
```
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
    
    // 增加代码，获取 离圆心最近的点 和 离圆心最远的点 的坐标
    dot.setFarX((float) (circularCenterX + (radius + dotDiam * 1.5f) * cos(angle * PI / 180f)));
    dot.setFarY((float) (circularCenterY + (radius + dotDiam * 1.5f) * sin(angle * PI / 180f)));
    dot.setNearX((float) (circularCenterX + (radius + dotDiam * 0.5f) * cos(angle * PI / 180f)));
    dot.setNearY((float) (circularCenterY + (radius + dotDiam * 0.5f) * sin(angle * PI / 180f)));

    return dot;
}
```

有了离圆心最近和最远的点的坐标，
我们就可以画出月牙的**外弧**和**内弧**了。

一切看上去都很顺利，
但是，
细心的同学可能已经发现了，
**月牙的外弧是白色圆环的同心圆，
所以直接可以画出来，
但内弧好像不是啊。**

我们把内弧的圆画出来看看：
![内弧辅助线](https://img-blog.csdnimg.cn/20210517155136241.png)

也就是说，
我们想画出内弧，
首先要确定内圆，
但是现在只有图中这两个点，
怎么确定内圆呢？

我们都知道，
三个点才能确定一个圆，
我们已经有了中间点和尾点，
那么现在就加一个辅助点吧。

我们仔细看上面这个图，
仔细思考一下，
不难发现，
有一个点，
我们完全可以计算出来。

![内圆](https://img-blog.csdnimg.cn/20210517162712429.png)

这个点在圆环上的角度，
是中间点和尾点的差的一半，
相比于圆环的圆心的距离，
也是中间点和尾点的差的一半，
所以，
利用工具类的getDot()方法，
就可以得到这个辅助点的坐标了。

有了这三个点，
就可以算出内圆的圆心了。

```
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
```

计算出内圆的坐标，
下一步就是计算中间点和尾点在内圆上的角度，
以及两个点的角度差，
即弧线划过的长度，
再简单点说，
就是弧长。

```
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
```

有了内弧在内圆上的起始角度和弧长，
画出内院的矩阵，
画出内弧，
就可以连接处一个完成的月牙了。

```
// 计算内圆的半径
float intervalX = abs(circleCenter[0] - dot2[0]);
float intervalY = abs(circleCenter[1] - dot2[1]);
float insideCircleRadius = (float) sqrt(pow(intervalX, 2) + pow(intervalY, 2));
// 内圆的矩阵
RectF ovalInterval = new RectF(circleCenter[0] - insideCircleRadius, circleCenter[1] - insideCircleRadius, circleCenter[0] + insideCircleRadius, circleCenter[1] + insideCircleRadius);
// 在内圆上连出弧线
path.arcTo(ovalInterval, dot3Rotation, insideSweepAngle);
```

看下效果：
![月牙完成](https://img-blog.csdnimg.cn/20210517173038370.gif)

### 3、动态渐变色

android.graphics中提供了有关Gradient类有三种，
LinearGradient线性渐变、 
RadialGradient径向渐变、
SweepGradient梯度渐变。
感兴趣的同学可以自行百度学习一下，
这里我们用LinearGradient线性渐变。

仔细观察后可以发现，
渐变色一直是在动的，
所以我的算法中，
根据进度，
实时改变渐变色的位置、粗细、旋转角度。
```
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
```

到这里，
就算是全部完成了，
调整一下线宽，
看下最终的效果：
![最终效果](https://img-blog.csdnimg.cn/20210517191843974.gif)


  
如果有疑问的地方，  
欢迎在文章下评论，  
或者加入QQ讨论群：569614530，  
群里找我，  
我是尘少。  
![扫码加入QQ讨论群](https://img-blog.csdnimg.cn/20190312095824708.jpg)
  

本文github链接：[https://github.com/Bamboy120315/InsCameraBtn](https://github.com/Bamboy120315/InsCameraBtn)