/*
 * =================================================================================================
 *                             Copyright (C) 2017 Universum Studios
 * =================================================================================================
 *         Licensed under the Apache License, Version 2.0 or later (further "License" only).
 * -------------------------------------------------------------------------------------------------
 * You may use this file only in compliance with the License. More details and copy of this License 
 * you may obtain at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * You can redistribute, modify or publish any part of the code written within this file but as it 
 * is described in the License, the software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES or CONDITIONS OF ANY KIND.
 * 
 * See the License for the specific language governing permissions and limitations under the License.
 * =================================================================================================
 */
package universum.studios.android.setting.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

import universum.studios.android.setting.R;

/**
 * A simple {@link View} implementation that draws a specified color in a circular shape. The
 * color to be drawn may be specified via {@link #setColor(int)}. Also there may be specified a
 * color via {@link #setCanvasColor(int)} that should be drawn behind the primary color.
 *
 * <h3>Xml attributes</h3>
 * See {@link R.styleable#Ui_Settings_ColorView SettingColorView Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingColorViewStyle uiSettingColorViewStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingColorView extends View {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingColorView";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Pain used to draw graphics of this view.
	 */
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	/**
	 * Raw color specified via {@link #setCanvasColor(int)}. This color is used as base for color
	 * used to draw canvas/background graphics of this view.
	 */
	private int mRawCanvasColor = Color.WHITE;

	/**
	 * Modified {@link #mRawCanvasColor} by the current alpha value of this view.
	 */
	private int mDrawCanvasColor = mRawCanvasColor;

	/**
	 * Raw color specified via {@link #setColor(int)}. This color is used as base for color used to
	 * draw primary graphics of this view.
	 */
	private int mRawColor = Color.argb(255, 255, 255, 255);

	/**
	 * Modified {@link #mRawColor} by the current alpha value of this view.
	 */
	private int mDrawColor = mRawColor;

	/**
	 * Maximum size dimension of this view.
	 */
	private int mMaxWidth, mMaxHeight;

	/**
	 * Size dimension of this view.
	 */
	private int mWidth, mHeight;

	/**
	 * Radius used to draw circular graphics of this view.
	 */
	private float mRadius;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingColorView(Context, AttributeSet)} without attributes.
	 */
	public SettingColorView(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingColorView(Context, AttributeSet, int)} with {@link R.attr#uiSettingColorViewStyle}
	 * as attribute for default style.
	 */
	public SettingColorView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingColorViewStyle);
	}

	/**
	 * Same as {@link #SettingColorView(Context, AttributeSet, int, int)} with {@code 0} as default style.
	 */
	public SettingColorView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs, defStyleAttr, 0);
	}

	/**
	 * Creates a new instance of SettingColorView within the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new view presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this view.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this view within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new view.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingColorView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.init(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Called from one of constructors of this view to perform its initialization.
	 * <p>
	 * Initialization is done via parsing of the specified <var>attrs</var> set and obtaining for
	 * this view specific data from it that can be used to configure this new view instance. The
	 * specified <var>defStyleAttr</var> and <var>defStyleRes</var> are used to obtain default data
	 * from the current theme provided by the specified <var>context</var>.
	 */
	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		this.mMaxWidth = mMaxHeight = Integer.MAX_VALUE;
		final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_ColorView, defStyleAttr, defStyleRes);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			final int index = typedArray.getIndex(i);
			if (index == R.styleable.Ui_Settings_ColorView_android_color) {
				setColor(typedArray.getColor(index, mRawColor));
			} else if (index == R.styleable.Ui_Settings_ColorView_android_maxWidth) {
				this.mMaxWidth = typedArray.getDimensionPixelSize(index, mMaxWidth);
			} else if (index == R.styleable.Ui_Settings_ColorView_android_maxHeight) {
				this.mMaxHeight = typedArray.getDimensionPixelSize(index, mMaxHeight);
			}
		}
		typedArray.recycle();
	}

	/**
	 * Sets a color to be drawn behind the primary color of this color view.
	 *
	 * @param color The desired color.
	 * @see #getCanvasColor()
	 * @see #setColor(int)
	 */
	public void setCanvasColor(@ColorInt int color) {
		if (mRawCanvasColor != color || mDrawCanvasColor != color) {
			this.mRawCanvasColor = mDrawCanvasColor = color;
			invalidate();
		}
	}

	/**
	 * Returns the current color drawn behind the primary color by this color view.
	 *
	 * @return Current canvas color.
	 */
	@ColorInt
	public int getCanvasColor() {
		return mDrawCanvasColor;
	}

	/**
	 * Sets a color to be drawn by this color view.
	 *
	 * @param color The desired color.
	 * @see #getColor()
	 */
	public void setColor(@ColorInt int color) {
		if (mRawColor != color || mDrawColor != color) {
			this.mRawColor = mDrawColor = color;
			invalidate();
		}
	}

	/**
	 * Returns the current color drawn by this color view.
	 *
	 * @return Current color.
	 * @see #setColor(int)
	 */
	@ColorInt
	public int getColor() {
		return mDrawColor;
	}

	/**
	 */
	@Override
	protected boolean onSetAlpha(int alpha) {
		this.mDrawColor = (mRawColor << 8 >>> 8) | (alpha << 24);
		this.mDrawCanvasColor = (mDrawCanvasColor << 8 >>> 8) | (alpha << 24);
		return true;
	}

	/**
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		// Take into count maximum size.
		width = Math.min(width, mMaxWidth);
		height = Math.min(height, mMaxHeight);
		setMeasuredDimension(width, height);
	}

	/**
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.mWidth = w;
		this.mHeight = h;
		this.mRadius = Math.min(mWidth, mHeight) / 2f;
	}

	/**
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if ((mDrawCanvasColor >>> 24) != 0) {
			mPaint.setColor(mRawCanvasColor);
			canvas.drawCircle(mWidth / 2f, mHeight / 2f, mRadius, mPaint);
		}
		if ((mDrawColor >>> 24) != 0) {
			mPaint.setColor(mDrawColor);
			canvas.drawCircle(mWidth / 2f, mHeight / 2f, mRadius, mPaint);
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
