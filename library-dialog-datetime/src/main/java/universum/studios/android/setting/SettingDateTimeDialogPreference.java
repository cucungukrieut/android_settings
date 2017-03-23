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

import java.text.SimpleDateFormat;

import universum.studios.android.dialog.DialogOptions;

/**
 * A {@link SettingDialogPreference} implementation that is used as base for dialog preferences through
 * which a user can pick its preferred <b>date</b> or <b>time</b> for a specific setting preference.
 * <p>
 * This preference implementation by default displays the preferred date/time value formatted by
 * format specified via {@link #setFormat(SimpleDateFormat)} as summary text. The subclasses of this
 * preference may specify theirs specific format which will be then used to format the summary text
 * whenever the picked date/time value changes. If no value is persisted the standard summary text
 * is displayed.
 *
 * @author Martin Albedinsky
 */
public abstract class SettingDateTimeDialogPreference<O extends DialogOptions<O>> extends SettingDialogPreference<O> {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingDateTimeDialogPreference";

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
	 * Format used to format preferred milliseconds (date/time) value and use it as summary text.
	 */
	private SimpleDateFormat mFormat;

	/**
	 * Boolean flag indicating whether the milliseconds value for this preference has been set or not.
	 * This flag is used to handle case when the same value is being specified for this preference,
	 * but for the first time, to properly refresh view of this preference and notify listeners about
	 * the change.
	 */
	private boolean mMillisecondsSet;

	/**
	 * Current milliseconds value specified for this preference. This may be either value specified
	 * by the user, default value or persisted value.
	 */
	private long mMilliseconds;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingDateTimeDialogPreference(Context, AttributeSet)} without attributes.
	 */
	SettingDateTimeDialogPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingDateTimeDialogPreference(Context, AttributeSet, int)} with
	 * {@link android.R.attr#dialogPreferenceStyle} as attribute for default style.
	 */
	SettingDateTimeDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, android.R.attr.dialogPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingDateTimeDialogPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	SettingDateTimeDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * Creates a new instance of SettingDateTimeDialogPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	SettingDateTimeDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
		return typedArray.getString(index);
	}

	/**
	 */
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setMilliseconds(restoreValue ? getPersistedLong(mMilliseconds) : onParseDefaultValue(defaultValue));
	}

	/**
	 * Called from {@link #onSetInitialValue(boolean, Object)} to parse the given <var>defaultValue</var>
	 * into date/time value in milliseconds.
	 *
	 * @param defaultValue The default value specified via {@link android.R.attr#defaultValue android:defaultValue}
	 *                     attribute.
	 * @return Parsed date/time value in milliseconds that will be set via {@link #setMilliseconds(long)}.
	 */
	abstract long onParseDefaultValue(@NonNull Object defaultValue);

	/**
	 * Sets a format that should be used to format the preferred date/time value of this preference
	 * as summary text.
	 *
	 * @param format The desired format.
	 * @see SimpleDateFormat
	 */
	public void setFormat(@NonNull SimpleDateFormat format) {
		this.mFormat = format;
	}

	/**
	 * Sets a preferred milliseconds value for this preference.
	 * <p>
	 * If value of this preference changes, it is persisted and the change listener is notified
	 * about the change.
	 *
	 * @param milliseconds The preferred milliseconds value to be persisted.
	 * @see #getMilliseconds()
	 * @see #areMillisecondsSet()
	 */
	void setMilliseconds(long milliseconds) {
		final boolean changed = mMilliseconds != milliseconds;
		if (callChangeListener(milliseconds) && (changed || !mMillisecondsSet)) {
			this.mMilliseconds = milliseconds;
			this.mMillisecondsSet = true;
			persistLong(mMilliseconds);
			if (changed) {
				notifyChanged();
			}
		}
	}

	/**
	 * Checks whether the milliseconds value is set for this preference.
	 *
	 * @return {@code True} if milliseconds value has been specified via {@link #setMilliseconds(long)}
	 * for this preference, {@code false} otherwise.
	 */
	boolean areMillisecondsSet() {
		return mMillisecondsSet;
	}

	/**
	 * Returns the preferred milliseconds value of this preference.
	 *
	 * @return Milliseconds either specified by the user, as default value or the persisted one.
	 * @see #setMilliseconds(long)
	 */
	long getMilliseconds() {
		return mMilliseconds;
	}

	/**
	 */
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		synchronizeSummaryView(view);
	}

	/**
	 * This implementation formats the current date/time value, if it is set, by format specified
	 * via {@link #setFormat(SimpleDateFormat)}, if any. If there is no date/time value and format
	 * set the standard summary text that may be obtained via {@link #getSummary()} is returned.
	 */
	@Nullable
	@Override
	protected CharSequence onGetSummaryText() {
		return mMillisecondsSet && mFormat != null ? mFormat.format(mMilliseconds) : super.onGetSummaryText();
	}

	/**
	 * Inner classes ===============================================================================
	 */
}