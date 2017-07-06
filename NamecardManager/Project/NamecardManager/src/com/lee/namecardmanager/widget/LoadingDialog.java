package com.lee.namecardmanager.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.namecardmanager.R;

/**
 * 加载提示框
 * @author lee
 *
 */
public class LoadingDialog extends Dialog{

	public LoadingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public LoadingDialog(Context context) {
		super(context);
	}

	public static class Builder{
		
		private LoadingDialog mDialog;
		private Context mContext;

		private View mView;						//dialog视图
		private ImageView mImg_loading;			//滚动图片
		private TextView mTextView_loading;		//加载提示
		
		public Builder(Context context){
			this.mContext = context;
			initView();
		}

		/**
		 * 初始化视图
		 */
		@SuppressLint("InflateParams")
		private void initView() {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = mInflater.inflate(R.layout.dialog_loading, null);
			mImg_loading = (ImageView) mView.findViewById(R.id.dialog_loading_img);
			mTextView_loading = (TextView) mView.findViewById(R.id.dialog_loading_text);
			//设置动画
			Animation mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.dialog_loading_animation);
			mImg_loading.startAnimation(mAnimation);
		}
		
		/**
		 * 设置加载文字
		 * @param text
		 * @return
		 */
		public Builder setText(String text) {
			mTextView_loading.setText(text);
            return this;
        }
		
		/**
		 * 关闭dialog
		 */
		public void closeDialog(){
			if(mDialog != null){
				mDialog.dismiss();
			}
		}
		
		/**
		 * 创建
		 * @return
		 */
		@SuppressWarnings("static-access")
		public LoadingDialog create(){
			mDialog = new LoadingDialog(mContext, R.style.LoadingDialog);
			mDialog.setContentView(mView);
			//设置dialog的大小
			android.view.WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
			lp.width = 600;//dp
			lp.height = lp.WRAP_CONTENT;
			mDialog.getWindow().setAttributes(lp);
			//设置点击除开dialog的其他地方不关闭dialog
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
			return mDialog;
		}
	}
}
