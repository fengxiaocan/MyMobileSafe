package com.evil.mobilesafe.view;

import com.evil.mobilesafe.R;
import com.evil.mobilesafe.interfaces.Constants;
import com.evil.mobilesafe.utils.SpUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这是自定义setting设置里的itemView
 */
public class SettingItemView extends RelativeLayout {

	private View mInflate;
	private TextView mTv;
	private ImageView mIv;
	private RelativeLayout mLy;

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflate = View.inflate(context, R.layout.setting_item_view, this);
		mTv = (TextView) mInflate.findViewById(R.id.tv_view_name);
		mIv = (ImageView) mInflate.findViewById(R.id.iv_view_button);
		mLy = (RelativeLayout) mInflate.findViewById(R.id.rl_view_bg);
		
		
		//获取自定义view里的所有属性值
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
		//获取文本
		String sivText = ta.getString(R.styleable.SettingItemView_sivText);
		//设置文本
		mTv.setText(sivText);
		
		//查找图片
		boolean isShow = ta.getBoolean(R.styleable.SettingItemView_sivImage, true);
		//设置开关是否显示
		mIv.setVisibility(isShow?mIv.VISIBLE:mIv.GONE);
		
		//设置背景
		int viewBg = ta.getInt(R.styleable.SettingItemView_sivBg,0);
		switch (viewBg) {
		case 0:
			mLy.setBackgroundResource(R.drawable.settingcentre_bg_frist_selector);
			break;
		case 1:
			mLy.setBackgroundResource(R.drawable.settingcentre_bg_middle_selector);
			break;
		case 2:
			mLy.setBackgroundResource(R.drawable.settingcentre_bg_end_selector);
			break;

		default:
			mLy.setBackgroundResource(R.drawable.settingcentre_bg_frist_selector);
			break;
		}
	}

	public SettingItemView(Context context) {
		// 调用自身的构造方法
		this(context, null);
	}
	
	
	
	//提供方法改变开关状态
	public void setSettingOnButton(boolean isOpen){
		if (isOpen) {
			mIv.setImageResource(R.drawable.on);
		}else{
			mIv.setImageResource(R.drawable.off);
		}
	}
}
