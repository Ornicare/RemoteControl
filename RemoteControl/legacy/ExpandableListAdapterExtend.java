package com.ornilabs.remote;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ornilabs.classes.ExpandableListAdapter;
import com.ornilabs.interfaces.ICategory;

public class ExpandableListAdapterExtend extends ExpandableListAdapter {

	public ExpandableListAdapterExtend(Context mContext,
			List<ICategory> dataList) {
		super(mContext, dataList);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		//
		LinearLayout linear = new LinearLayout(mContext);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		// linear.setLongClickable(true);
		//
		TextView tv = new TextView(mContext);
		tv.setText(((ICategory) getGroup(groupPosition)).getText());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		tv.setPadding(60, 0, 0, 0);

		// to mesure after
		tv.measure(0, 0);

		ImageView image = new ImageView(mContext);
		// image.setImageResource(R.drawable.vignetterouge);
		// image.setScaleType(ScaleType.MATRIX);
		// Matrix matrix = new Matrix();
		//
		// matrix.postScale(tv.getMeasuredHeight(), tv.getMeasuredHeight());
		// image.setImageMatrix(matrix);

		// load the origial BitMap (500 x 500 px)
		Bitmap bitmapOrg = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.vignetterouge);

		int scale = tv.getMeasuredHeight();
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) scale) / width;
		float scaleHeight = ((float) scale) / height;

		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
				height, matrix, true);

		// make a Drawable from Bitmap to allow to set the BitMap
		// to the ImageView, ImageButton or what ever
		BitmapDrawable bmd = new BitmapDrawable(mContext.getResources(),
				resizedBitmap);

		// set the Drawable on the ImageView
		image.setImageDrawable(bmd);

		// center the Image
		image.setScaleType(ScaleType.FIT_START);

		// // linear.addView(new CustomDrawableView(mContext));


//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.weight = 10.0f;
//		params.gravity = Gravity.LEFT;
//		
////		image.setLayoutParams(params);
//		
//		RelativeLayout.LayoutParams params1 = 
//				   new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
//				                                   RelativeLayout.LayoutParams.WRAP_CONTENT);
//				params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//
//				image.setLayoutParams(params1);
//		image.setLayoutParams(new FrameLayout.LayoutParams(params1));
		
		image.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,                       
                LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
		linear.addView(tv);
		linear.addView(image);
//		linear.setLayoutParams(new LayoutParams(Gravity.RIGHT));

		linear.setPadding(0, scale / 10, 0, scale / 10);
//
//		RelativeLayout relativeLayout = new RelativeLayout(mContext);
//		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.MATCH_PARENT);
//		lp.addRule(RelativeLayout.RIGHT_OF, tv.getId());
//
//		 relativeLayout.addView(tv);
//		 relativeLayout.addView(image, lp);

		return linear;
	}
	//
	// public class CustomDrawableView extends View {
	// private ShapeDrawable mDrawable;
	//
	// public CustomDrawableView(Context context) {
	// super(context);
	//
	// int x = 10;
	// int y = 10;
	// int width = 300;
	// int height = 50;
	//
	// mDrawable = new ShapeDrawable(new OvalShape());
	// mDrawable.getPaint().setColor(0xff74AC23);
	// mDrawable.setBounds(x, y, x + width, y + height);
	// }
	//
	// protected void onDraw(Canvas canvas) {
	// mDrawable.draw(canvas);
	// }
	// }

}
