package com.march.libs.mycamera;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.march.libs.R;

/**
 * CdLibsTest     com.march.libs.mycamera
 * Created by 陈栋 on 16/3/12.
 * 功能:
 */
public class CamContainerView extends FrameLayout {
    public CamContainerView(Context context) {
        this(context, null);
    }

    public CamContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_cam_container, this, true);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs,
                    R.styleable.CamContainerView);
            isShowTopBar = array.getBoolean(R.styleable.CamContainerView_isShowTop, false);
            array.recycle();
        }
        init();
    }


    private CameraSurfaceView mSurfaceView;
    private ViewGroup mBotBar;
    private ViewGroup mTopBar;

    private CameraNative cameraNative;

    private int screenW, screenH;
    private boolean isShowTopBar;

    private void init() {
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.widget_cam_container_surface);
        mBotBar = (ViewGroup) findViewById(R.id.widget_cam_container_bottombar);
        mTopBar = (ViewGroup) findViewById(R.id.widget_cam_container_topbar);
        CameraNative.newInst(getContext(), this);
        cameraNative = CameraNative.getInst();

        ViewGroup.LayoutParams layoutParams = mSurfaceView.getLayoutParams();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        screenW = displayMetrics.widthPixels;
        screenH = displayMetrics.heightPixels;
        layoutParams.height = (int) (screenW * (4.0f / 3f));
        mSurfaceView.setLayoutParams(layoutParams);

        mSurfaceView.setPointFocus(true, findViewById(R.id.widget_cam_container_focus));

        changeDisplayUI();
    }

    public SurfaceView getSurfaceView(){
        return mSurfaceView;
    }

    public void changeDisplayUI() {
        ViewGroup.LayoutParams lpBot = mBotBar.getLayoutParams();
        ViewGroup.LayoutParams lpTop = mTopBar.getLayoutParams();
        int botH = 0;
        int topH = 0;
        if (cameraNative.isSizeOne2One()) {
            if (isShowTopBar) {
                //显示顶部遮盖,上面w/6 +  中间w + 下面h - w - w/6
                topH = (int) (screenW * 1.0f / 6f);
                botH = screenH - screenW - topH;
            } else {
                //不显示顶部遮盖,控件上移w/6 + 中间w + 下面h - w
                botH = screenH - screenW;
                mSurfaceView.setY(-screenW * 1.0f / 6f);
                mSurfaceView.setX(0);
            }
        } else if (cameraNative.isFour2Three()) {
            if (isShowTopBar) {
                botH = screenH - (int) (screenW * (4.0f / 3f));
            } else {
                botH = screenH - (int) (screenW * (4.0f / 3f));
                mSurfaceView.setY(0);
                mSurfaceView.setX(0);
            }
        }

        lpBot.height = botH;
        lpTop.height = topH;
        mBotBar.setLayoutParams(lpBot);
        mTopBar.setLayoutParams(lpTop);
    }


}
