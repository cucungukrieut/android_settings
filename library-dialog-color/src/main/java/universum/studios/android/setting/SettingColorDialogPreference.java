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
package universum.studios.android.setting;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

import universum.studios.android.dialog.ColorPickerDialog;
import universum.studios.android.dialog.Dialog;
import universum.studios.android.setting.widget.SettingColorView;

/**
 * A {@link SettingDialogPreference} implementation that may be used to allow to a user to pick
 * its preferred color for a specific setting preference.
 * <p>
 * This preference implementation by default displays the preferred color via {@link SettingColorView}
 * widget in the widget area (at the end of layout hierarchy). The preferred color may be specified
 * via {@link #setColor(int)} and obtained via {@link #getColor()}.
 * <p>
 * When {@link #handleOnDialogButtonClick(Dialog, int)} is called, this preference implementation
 * handles only {@link ColorPickerDialog} type of dialog. If its {@link Dialog#BUTTON_POSITIVE} button
 * has been clicked, the color provided via {@link ColorPickerDialog#getColor()} is set as color for
 * this preference via {@link #setColor(int)}.
 *
 * <h3>Default value</h3>
 * Default value for this preference is parsed as color {@link Integer}. See {@link TypedArray#getColor(int, int)}.
 *
 * <h3>Xml attributes</h3>
 * See {@link SettingDialogPreference},
 * {@link R.styleable#Ui_Settings_ColorDialogPreference SettingColorDialogPreference Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingColorDialogPreferenceStyle uiSettingColorDialogPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
public final class SettingColorDialogPreference extends SettingDialogPreference<ColorPickerDialog.ColorOptions> {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingColorDialogPreference";

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Boolean flag indicating whether the color value for this preference has been set or not.
	 * This flag is used to handle case when the same value is being specified for this preference,
	 * but for the first time, to properly refresh view of this preference and notify listeners about
	 * the change.
	 */
	private boolean mColorSet;

	/**
	 * Current color value specified for this preference. This may be either value specified by the
	 * user, default value or persisted value.
	 */
	private int mColor = Color.TRANSPARENT;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingColorDialogPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingColorDialogPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingColorDialogPreference(Context, AttributeSet, int)} with
	 * {@link R.attr#uiSettingColorDialogPreferenceStyle} as attribute for default style.
	 */
	public SettingColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingColorDialogPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingColorDialogPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingColorDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * Creates a new instance of SettingColorDialogPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@NonNull
	@Override
	protected ColorPickerDialog.ColorOptions onCreateDialogOptions(@NonNull Resources resources) {
		return new ColorPickerDialog.ColorOptions(resources);
	}

	/**
	 */
	@Override
	protected void onConfigureDialogOptions(@NonNull ColorPickerDialog.ColorOptions options, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super.onConfigureDialogOptions(options, context, attrs, defStyleAttr, defStyleRes);
		int canvasColor = options.canvasColor();
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_ColorDialogPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < attributes.getIndexCount(); i++) {
			final int index = attributes.getIndex(i);
			if (index == R.styleable.Ui_Settings_ColorDialogPreference_dialogColor) {
				options.color(attributes.getColor(index, options.color()));
			} else if (index == R.styleable.Ui_Settings_ColorDialogPreference_dialogColorCanvas) {
				canvasColor = attributes.getColor(index, canvasColor);
			}
		}
		attributes.recycle();
		options.canvasColor(canvasColor);
	}

	/**
	 */
	@Override
	protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
		return typedArray.getColor(index, mColor);
	}

	/**
	 */
	@Override
	@SuppressWarnings("ResourceType")
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		setColor(restorePersistedValue ? getPersistedInt(mColor) : (Integer) defaultValue);
	}

	/**
	 * Sets a preferred color value for this preference.
	 * <p>
	 * If value of this preference changes, it is persisted and the change listener is notified
	 * about the change.
	 *
	 * @param color The preferred color to be persisted as {@link Integer}.
	 * @see #getColor()
	 */
	public void setColor(@ColorInt int color) {
		final boolean changed = mColor != color;
		if (callChangeListener(color) && (changed || !mColorSet)) {
			this.mColor = color;
			this.mColorSet = true;
			persistInt(mColor);
			if (changed) {
				notifyChanged();
			}
		}
	}

	/**
	 * Returns the preferred color value of this preference.
	 *
	 * @return Color integer either specified by the user, as default value or the persisted one.
	 * @see #setColor(int)
	 * @see #getDialogOptions()
	 */
	@ColorInt
	public int getColor() {
		return mColor;
	}

	/**
	 */
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		final SettingColorView colorView = (SettingColorView) view.findViewById(R.id.ui_setting_color_view);
		if (colorView != null) {
			colorView.setCanvasColor(getDialogOptions().canvasColor());
			colorView.setColor(mColor);
		}
	}

	/**
	 * Dialog options of this preference with the preferred color specified as
	 * {@link ColorPickerDialog.ColorOptions#color(int)}, if it is set.
	 *
	 * @see #getColor()
	 */
	@NonNull
	@Override
	public ColorPickerDialog.ColorOptions getDialogOptions() {
		final ColorPickerDialog.ColorOptions options = super.getDialogOptions();
		if (mColorSet) {
			options.color(mColor);
		}
		return options;
	}

	/**
	 */
	@Override
	protected boolean onHandleDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		if (dialog instanceof ColorPickerDialog) {
			switch (button) {
				case Dialog.BUTTON_POSITIVE:
					setColor(((ColorPickerDialog) dialog).getColor());
					return true;
				default:
					return true;
			}
		}
		return super.onHandleDialogButtonClick(dialog, button);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}