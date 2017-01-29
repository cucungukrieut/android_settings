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
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * A {@link SettingPreference} implementation that provides {@link SeekBar} widget with its related
 * functionality.
 * <p>
 * This preference implementation by default displays the preferred progress via {@link SeekBar}
 * widget in the area of the standard summary text (instead of it). The preferred progress may be
 * specified via {@link #setProgress(int)} and obtained via {@link #getProgress()}. Also the maximum
 * progress value that should be handled by the associated SeekBar widget may be specified via
 * {@link #setMaxProgress(int)}.
 *
 * <h3>Default value</h3>
 * Default value for this preference is parsed as {@link Integer}. See {@link TypedArray#getInt(int, int)}.
 *
 * <h3>Xml attributes</h3>
 * See {@link R.styleable#Ui_Settings_SeekBarPreference SettingSeekBarPreference Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingSeekBarPreferenceStyle uiSettingSeekBarPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
class SettingSeekBarPreference extends SettingPreference {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingSeekBarPreference";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Listener that is used to receive callbacks about changed progress in the SeekBar widget of
	 * this preference.
	 */
	private final SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {

		/**
		 */
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// Ignored. See onStopTrackingTouch(...).
		}

		/**
		 */
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// Ignored. See onStopTrackingTouch(...).
		}

		/**
		 */
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			setProgress(seekBar.getProgress());
		}
	};

	/**
	 * Maximum value for the progress that may be specified for this preference. This maximum value
	 * is used only to specified max value for {@link SeekBar} widget associated with this preference
	 * via {@link SeekBar#setMax(int)}.
	 *
	 * @see #onBindView(View)
	 */
	private int mMaxProgress = 100;

	/**
	 * Boolean flag indicating whether the progress value for this preference has been set or not.
	 * This flag is used to handle case when the same value is being specified for this preference,
	 * but for the first time, to properly refresh view of this preference and notify listeners about
	 * the change.
	 */
	private boolean mProgressSet;

	/**
	 * Current progress value specified for this preference. This may be either value specified by
	 * the user, default value or persisted value.
	 */
	private int mProgress;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingSeekBarPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingSeekBarPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingSeekBarPreference(Context, AttributeSet, int)} with
	 * {@link R.attr#uiSettingSeekBarPreferenceStyle} as attribute for default style.
	 */
	public SettingSeekBarPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingSeekBarPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingSeekBarPreference(Context, AttributeSet, int, int)} with {@code 0}
	 * as default style.
	 */
	public SettingSeekBarPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs, defStyleAttr, 0);
	}

	/**
	 * Creates a new instance of SettingSeekBarPreference within the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingSeekBarPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.init(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Called from one of constructors of this setting preference to perform its initialization.
	 * <p>
	 * Initialization is done via parsing of the specified <var>attrs</var> set and obtaining for
	 * this preference specific data from it that can be used to configure this new preference instance.
	 * The specified <var>defStyleAttr</var> and <var>defStyleRes</var> are used to obtain default
	 * data from the current theme provided by the specified <var>context</var>.
	 */
	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_SeekBarPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			final int index = typedArray.getIndex(i);
			if (index == R.styleable.Ui_Settings_SeekBarPreference_android_max) {
				this.mMaxProgress = typedArray.getInt(index, mMaxProgress);
			}
		}
		typedArray.recycle();
	}

	/**
	 */
	@Override
	protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
		return typedArray.getInt(index, mProgress);
	}

	/**
	 */
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		setProgress(restorePersistedValue ? getPersistedInt(mProgress) : (Integer) defaultValue);
	}

	/**
	 * Sets a maximum value for the progress that may be specified for this preference.
	 * <p>
	 * <b>Note</b>, that this value is not used to ensure that the progress specified via {@link #setProgress(int)}
	 * is in bounds of the maximum progress value, but only for purpose of {@link SeekBar#setMax(int)}.
	 *
	 * @param maxProgress The desired maximum progress value.
	 * @see android.R.attr#max
	 * @see #getMaxProgress()
	 */
	public void setMaxProgress(int maxProgress) {
		this.mMaxProgress = maxProgress;
	}

	/**
	 * Returns the maximum value for progress specified for this preference.
	 *
	 * @return Maximum progress value.
	 * @see #setMaxProgress(int)
	 */
	public int getMaxProgress() {
		return mMaxProgress;
	}

	/**
	 * Sets a preferred progress value for this preference.
	 * <p>
	 * If value of this preference changes, it is persisted and the change listener is notified
	 * about the change.
	 *
	 * @param progress The preferred progress to be persisted. Should be from the range {@code [0, getMaxProgress()]}.
	 * @see #getProgress()
	 */
	public void setProgress(int progress) {
		final boolean changed = mProgress != progress;
		if (callChangeListener(progress) && (changed || !mProgressSet)) {
			this.mProgress = progress;
			this.mProgressSet = true;
			persistInt(mProgress);
			if (changed) {
				notifyChanged();
			}
		}
	}

	/**
	 * Returns the preferred progress value of this preference.
	 *
	 * @return Progress either specified by the user, as default value or the persisted one.
	 * @see #setProgress(int)
	 */
	public int getProgress() {
		return mProgress;
	}

	/**
	 */
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		final SeekBar seekBar = (SeekBar) view.findViewById(R.id.ui_setting_seek_bar);
		if (seekBar != null) {
			seekBar.setOnSeekBarChangeListener(null);
			seekBar.setMax(mMaxProgress);
			seekBar.setProgress(mProgress);
			seekBar.setOnSeekBarChangeListener(mListener);
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
