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
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import universum.studios.android.dialog.Dialog;
import universum.studios.android.dialog.TimePickerDialog;

/**
 * A {@link SettingDateTimeDialogPreference} implementation that may be used to allow to a user to pick
 * its preferred time for a specific setting preference.
 * <p>
 * This preference implementation by default displays the preferred time formatted by format specified
 * via {@link #setFormat(SimpleDateFormat)} as summary text. The default format is {@code 'hh:mm a'}.
 * If no time is specified, the standard summary text is displayed. The preferred time may be specified
 * via {@link #setTime(Date)} and obtained via {@link #getTime()} or {@link #getTimeInMillis()}.
 * <p>
 * When {@link #handleOnDialogButtonClick(Dialog, int)} is called, this preference implementation
 * handles only {@link TimePickerDialog} type of dialog. If its {@link Dialog#BUTTON_POSITIVE} button
 * has been clicked, the time provided via {@link TimePickerDialog#getTime()} is set as time for
 * this preference via {@link #setTime(long)}.
 *
 * <h3>Default value</h3>
 * Default value for this preference is parsed as {@link String} into <b>milliseconds</b> value
 * using {@link TimePickerDialog.TimeParser#parse(String)}. See {@link TypedArray#getString(int)}.
 *
 * <h3>Xml attributes</h3>
 * See {@link R.styleable#Ui_Settings_TimeDialogPreference SettingTimeDialogPreference Attributes}
 *
 * <h3>Dialog Xml attributes</h3>
 * <ul>
 * <li>{@link R.attr#dialogTime dialogTime}</li>
 * <li>{@link R.attr#dialogTimePickers dialogTimePickers}</li>
 * <li>{@link R.attr#dialogTimeQuantityText dialogTimeQuantityText}</li>
 * </ul>
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingTimeDialogPreferenceStyle uiSettingTimeDialogPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingTimeDialogPreference extends SettingDateTimeDialogPreference<TimePickerDialog.TimeOptions> {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingTimeDialogPreference";

	/**
	 * Default pattern for the time format.
	 */
	private static final String FORMAT_PATTERN = "hh:mm a";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingTimeDialogPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingTimeDialogPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingTimeDialogPreference(Context, AttributeSet, int)} with
	 * {@link R.attr#uiSettingTimeDialogPreferenceStyle} as attribute for default style.
	 */
	public SettingTimeDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingTimeDialogPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingTimeDialogPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingTimeDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * Creates a new instance of SettingInputPreference within the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingTimeDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Parses the specified string data as time via {@link TimePickerDialog.TimeParser#parse(String)}.
	 *
	 * @param timeString The desired data to parse into time.
	 * @return Parsed time in milliseconds or {@code null} if parsing failed.
	 */
	private static Long parseTime(String timeString) {
		return TextUtils.isEmpty(timeString) ? null : TimePickerDialog.TimeParser.parse(timeString);
	}

	/**
	 */
	@NonNull
	@Override
	protected TimePickerDialog.TimeOptions onCreateDialogOptions(@NonNull Resources resources) {
		return new TimePickerDialog.TimeOptions(resources);
	}

	/**
	 */
	@Override
	@SuppressWarnings("ResourceType")
	protected void onConfigureDialogOptions(@NonNull TimePickerDialog.TimeOptions options, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super.onConfigureDialogOptions(options, context, attrs, defStyleAttr, defStyleRes);
		String formatPattern = FORMAT_PATTERN;
		final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_TimeDialogPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			final int index = typedArray.getIndex(i);
			if (index == R.styleable.Ui_Settings_TimeDialogPreference_uiSettingDateFormat) {
				final String dateFormat = typedArray.getString(index);
				formatPattern = TextUtils.isEmpty(dateFormat) ? formatPattern : dateFormat;
			} else if (index == R.styleable.Ui_Settings_TimeDialogPreference_dialogTime) {
				final Long time = parseTime(typedArray.getString(index));
				if (time != null) options.time(time);
			} else if (index == R.styleable.Ui_Settings_TimeDialogPreference_dialogTimePickers) {
				options.timePickers(typedArray.getInt(index, options.timePickers()));
			} else if (index == R.styleable.Ui_Settings_TimeDialogPreference_dialogTimeQuantityText) {
				options.timeQuantityText(typedArray.getResourceId(index, 0));
			}
		}
		typedArray.recycle();
		setFormat(new SimpleDateFormat(formatPattern, Locale.getDefault()));
	}

	/**
	 */
	@Override
	long onParseDefaultValue(@NonNull Object defaultValue) {
		final Long time = parseTime((String) defaultValue);
		return time != null ? time : 0;
	}

	/**
	 * Same as {@link #setTime(long)} with milliseconds value of the given <var>date</var>.
	 *
	 * @param time The desired time with milliseconds value. May be {@code null} which corresponds
	 *             to {@code 0}.
	 * @see #getTime()
	 */
	public void setTime(@Nullable Date time) {
		setTime(time != null ? time.getTime() : 0);
	}

	/**
	 * Sets a preferred time value for this preference.
	 * <p>
	 * If value of this preference changes, it is persisted and the change listener is notified
	 * about the change.
	 *
	 * @param timeInMillis The preferred time in milliseconds to be persisted.
	 * @see #getTimeInMillis()
	 */
	public void setTime(long timeInMillis) {
		setMilliseconds(timeInMillis);
	}

	/**
	 * Like {@link #getTimeInMillis()}, but this method may be used to check whether the time value
	 * has been specified or not, as this method will return {@code null} Date object if there is
	 * no time value specified.
	 *
	 * @return Time as date object. May be {@code null} if no time value has been specified yet.
	 * @see #setTime(Date)
	 * @see #getDialogOptions()
	 */
	@Nullable
	public Date getTime() {
		return areMillisecondsSet() ? new Date(getTimeInMillis()) : null;
	}

	/**
	 * Returns the preferred time value of this preference.
	 *
	 * @return Time in milliseconds either specified by the user, as default value or the persisted
	 * one.
	 * @see #setTime(long)
	 * @see #getDialogOptions()
	 */
	public long getTimeInMillis() {
		return getMilliseconds();
	}

	/**
	 * Dialog options of this preference with the preferred time specified as
	 * {@link TimePickerDialog.TimeOptions#time(long)}, if it is set.
	 *
	 * @see #getTimeInMillis()
	 */
	@NonNull
	@Override
	public TimePickerDialog.TimeOptions getDialogOptions() {
		final TimePickerDialog.TimeOptions options = super.getDialogOptions();
		if (areMillisecondsSet()) {
			options.time(getTimeInMillis());
		}
		return options;
	}

	/**
	 */
	@Override
	protected boolean onHandleDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		if (dialog instanceof TimePickerDialog) {
			switch (button) {
				case Dialog.BUTTON_POSITIVE:
					setTime(((TimePickerDialog) dialog).getTime());
					break;
			}
			return true;
		}
		return super.onHandleDialogButtonClick(dialog, button);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
