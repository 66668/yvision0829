package com.yvision.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;


/**
 * 自定义topbar控件，包括左 中 右三个功能控件
 */
public class WifiTopbar extends RelativeLayout{
    //定义控件
    private Button leftButton;
    private TextView title;
    private Button rightButton;
    //定义属性
    private String tileName;
    private float tileSize;
    private int titleColor;

    private String leftTextName;
    private int leftTextColor;
    private float leftTextSize;
    private Drawable leftBackground; 
    
    private String rightTextName;
    private int rightTextColor;
    private float rightTextSize;
    private Drawable rightBackground;

    private LayoutParams btnLeftImgParams,titleParams,btnRightParams;

    //添加接口回调
    private TopbarClickListener listener;
    public interface TopbarClickListener{
        public void leftClick();
        public void rightclick();
    }
    public void setOnTopbarClickListener(TopbarClickListener listener){
        this.listener = listener;
    }
    public WifiTopbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //关联attrs.xml中的自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WifiTopbar);


        leftTextName = typedArray.getString(R.styleable.WifiTopbar_leftTextName);
        leftTextSize = typedArray.getDimension(R.styleable.WifiTopbar_leftTextSize,0);
        leftTextColor = typedArray.getColor(R.styleable.WifiTopbar_leftTextColor,0);
        leftBackground = typedArray.getDrawable(R.styleable.WifiTopbar_leftBackground);
        
        tileName = typedArray.getString(R.styleable.WifiTopbar_tileName);
        tileSize = typedArray.getDimension(R.styleable.WifiTopbar_tileSize, 0);
        titleColor = typedArray.getColor(R.styleable.WifiTopbar_tileColor, 0);

        rightTextName = typedArray.getString(R.styleable.WifiTopbar_rightTextName);
        rightTextSize = typedArray.getDimension(R.styleable.WifiTopbar_rightTextSize,0);
        rightTextColor = typedArray.getColor(R.styleable.WifiTopbar_rightTextColor,0);
        rightBackground = typedArray.getDrawable(R.styleable.WifiTopbar_rightBackground);

        typedArray.recycle();//回收

        //控件处理
        leftButton = new Button(context);
        title = new TextView(context);
        rightButton = new Button(context);

        leftButton.setText(leftTextName);
        leftButton.setTextColor(leftTextColor);
        leftButton.setTextSize(leftTextSize);
        leftButton.setBackground(leftBackground);
        leftButton.setGravity(Gravity.CENTER);

        title.setText(tileName);
        title.setTextSize(tileSize);
        title.setTextColor(titleColor);
        title.setGravity(Gravity.CENTER);

        rightButton.setText(rightTextName);
        rightButton.setTextColor(rightTextColor);
        rightButton.setTextSize(rightTextSize);
        rightButton.setBackground(rightBackground);
        rightButton.setGravity(Gravity.CENTER);

        setBackgroundColor(0xFF173c6f);

        //控件详细设置
        btnLeftImgParams = new LayoutParams(50, ViewGroup.LayoutParams.MATCH_PARENT);
        btnLeftImgParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);//TRUE为常量
        addView(leftButton,btnLeftImgParams);

        titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(title,titleParams);

        btnRightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        btnRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
        addView(rightButton,btnRightParams);

        //接口回调
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.leftClick();
            }
        });

        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.rightclick();
            }
        });

    }

    //button可见
    public void setLeftVisiable(boolean flag){
        if(flag){
            leftButton.setVisibility(View.VISIBLE);
        }else{
            leftButton.setVisibility(View.GONE);
        }
    }

    public void setRightVisiable(boolean flag){
        if(flag){
            rightButton.setVisibility(View.VISIBLE);
        }else{
            rightButton.setVisibility(View.GONE);
        }
    }

}
