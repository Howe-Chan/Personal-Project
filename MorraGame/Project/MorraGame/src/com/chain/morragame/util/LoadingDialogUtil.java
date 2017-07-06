package com.chain.morragame.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chain.morragame.R;

public class LoadingDialogUtil {

	private Dialog dialog;
	private String textInfo = null;
	private Activity mActivity;
	private ImageView spaceshipImage;
	private Animation hyperspaceJumpAnimation;
	
	public LoadingDialogUtil(Activity mActivity,String textInfo){
		this.mActivity = mActivity;
		this.textInfo = textInfo;
		intiView();
	}
	
	public LoadingDialogUtil(Activity mActivity){
		this.mActivity = mActivity;
		intiView();
	}
	
	private void intiView(){
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View v = inflater.inflate(R.layout.waiting_dialogview, null);

		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		
		if(textInfo!=null){
			TextView mTextView = (TextView)v.findViewById(R.id.textview_loading);
			mTextView.setText(textInfo);
		}
		spaceshipImage = (ImageView) v.findViewById(R.id.img);
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mActivity,
				R.anim.waiting_dialog_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		dialog = new Dialog(mActivity,R.style.FullHeightDialog);
		dialog.setCancelable(false);
		//设置loading文字的长和宽
		dialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
	}
	
	public void show(){
		if(dialog != null){
			// 使用ImageView显示动画
			spaceshipImage.startAnimation(hyperspaceJumpAnimation);
			dialog.show();
		}
	}
	
	public void dismiss(){
		if(dialog != null){
			dialog.dismiss();
		}
	}
	
	public void cancel(){
		if(dialog != null){
			dialog.cancel();
		}
	}
	
	public boolean isShow(){
		if(dialog != null){
			return dialog.isShowing();
		}
		return false;
	}
	
}
