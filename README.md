# OverScrollLayout
类似iOS上下拉回弹效果的视图，附加上下拉加载更多的功能

# 效果图
![效果图](https://github.com/ShaqCc/OverScrollLayout/blob/master/OverScrollLayout/screenshot/jdfw.gif)

# 使用方法
1.下载代码，将该项目中的library作为自己的本地依赖
2.XML文件如下：

```xml
<FrameLayout
xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="match_parent">

    <com.bayin.library.view.OsContainer
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>

</FrameLayout>
```
3.java代码如下：

```java
OsContainer container = (OsContainer) findViewById(R.id.container);
//参数传递自己的xml布局
container.setChildView(new OsTopLayout(this, R.layout.top_layout), 
                new OsBottomLayout(this, R.layout.bottom_layout));
```
