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

import universum.studios.android.dialog.DatePickerDialog;
import universum.studios.android.dialog.Dialog;

/**
 * A {@link SettingDateTimeDialogPreference} implementation that may be used to allow to a user to pick
 * its preferred date for a specific setting preference.
 * <p>
 * This preference implementation by default displays the preferred date formatted by format specified
 * via {@link #setFormat(SimpleDateFormat)} as summary text. The default format is {@code 'MMM dd, yyyy'}.
 * If no date is specified, the standard summary text is displayed. The preferred date may be specified
 * via {@link #setDate(long)} and obtained via {@link #getDate()} or {@link #getDateInMillis()}.
 * <p>
 * When {@link #handleOnDialogButtonClick(Dialog, int)} is called, this preference implementation
 * handles only {@link DatePickerDialog} type of dialog. If its {@link Dialog#BUTTON_POSITIVE} button
 * has been clicked, the date provided via {@link DatePickerDialog#getDate()} is set as date for
 * this preference via {@link #setDate(long)}.
 *
 * <h3>Default value</h3>
 * Default value for this preference is parsed as {@link String} into <b>milliseconds</b> value
 * using {@link DatePickerDialog.DateParser#parse(String)}. See {@link TypedArray#getString(int)}.
 *
 * <h3>Xml attributes</h3>
 * See {@link R.styleable#Ui_Settings_DateDialogPreference SettingDateDialogPreference Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingDateDialogPreferenceStyle uiSettingDateDialogPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
public final class SettingDateDialogPreference extends SettingDateTimeDialogPreference<DatePickerDialog.DateOptions> {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingDateDialogPreference";

	/**
	 * Default pattern for the date format.
	 */
	private static final String FORMAT_PATTERN = "MMM dd, yyyy";

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
	 * Same as {@link #SettingDateDialogPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingDateDialogPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingDateDialogPreference(Context, AttributeSet, int)} with
	 * {@link R.attr#uiSettingDateDialogPreferenceStyle} as attribute for default style.
	 */
	public SettingDateDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingDateDialogPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingDateDialogPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingDateDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
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
	public SettingDateDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Parses the specified string data as date via {@link DatePickerDialog.DateParser#parse(String)}.
	 *
	 * @param dateString The desired data to parse into date.
	 * @return Parsed date in milliseconds or {@code null} if parsing failed.
	 */
	private static Long parseDate(String dateString) {
		return TextUtils.isEmpty(dateString) ? null : DatePickerDialog.DateParser.parse(dateString);
	}

	/**
	 */
	@NonNull
	@Override
	protected DatePickerDialog.DateOptions onCreateDialogOptions(@NonNull Resources resources) {
		return new DatePickerDialog.DateOptions(resources);
	}

	/**
	 */
	@Override
	protected void onConfigureDialogOptions(@NonNull DatePickerDialog.DateOptions options, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super.onConfigureDialogOptions(options, context, attrs, defStyleAttr, defStyleRes);
		String formatPattern = FORMAT_PATTERN;
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_DateDialogPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < attributes.getIndexCount(); i++) {
			final int index = attributes.getIndex(i);
			if (index == R.styleable.Ui_Settings_DateDialogPreference_uiSettingDateFormat) {
				final String dateFormat = attributes.getString(index);
				formatPattern = TextUtils.isEmpty(dateFormat) ? formatPattern : dateFormat;
			} else if (index == R.styleable.Ui_Settings_DateDialogPreference_dialogDate) {
				final Long date = parseDate(attributes.getString(index));
				if (date != null) options.date(date);
			} else if (index == R.styleable.Ui_Settings_DateDialogPreference_dialogDateMin) {
				final Long date = parseDate(attributes.getString(index));
				if (date != null) options.minDate(date);
			} else if (index == R.styleable.Ui_Settings_DateDialogPreference_dialogDateMax) {
				final Long date = parseDate(attributes.getString(index));
				if (date != null) options.maxDate(date);
			}
		}
		attributes.recycle();
		setFormat(new SimpleDateFormat(formatPattern, Locale.getDefault()));
	}

	/**
	 */
	@Override
	long onParseDefaultValue(@NonNull Object defaultValue) {
		final Long date = parseDate((String) defaultValue);
		return date != null ? date : 0;
	}

	/**
	 * Same as {@link #setDate(long)} with milliseconds value of the given <var>date</var>.
	 *
	 * @param date The desired date with milliseconds value. May be {@code null} which corresponds
	 *             to {@code 0}.
	 * @see #getDate()
	 */
	public void setDate(@Nullable Date date) {
		setDate(date != null ? date.getTime() : 0);
	}

	/**
	 * Sets a preferred date value for this preference.
	 * <p>
	 * If value of this preference changes, it is persisted and the change listener is notified
	 * about the change.
	 *
	 * @param dateInMillis The preferred date in milliseconds to be persisted.
	 * @see #getDateInMillis()
	 */
	public void setDate(long dateInMillis) {
		setMilliseconds(dateInMillis);
	}

	/**
	 * Like {@link #getDateInMillis()}, but this method may be used to check whether the date value
	 * has been specified or not, as this method will return {@code null} Date object if there is
	 * no date value specified.
	 *
	 * @return Date as date object. May be {@code null} if no date value has been specified yet.
	 * @see #setDate(Date)
	 * @see #getDialogOptions()
	 */
	@Nullable
	public Date getDate() {
		return areMillisecondsSet() ? new Date(getDateInMillis()) : null;
	}

	/**
	 * Returns the preferred date value of this preference.
	 *
	 * @return Date in milliseconds either specified by the user, as default value or the persisted
	 * one.
	 * @see #setDate(long)
	 * @see #getDialogOptions()
	 */
	public long getDateInMillis() {
		return getMilliseconds();
	}

	/**
	 * Dialog options of this preference with the preferred date specified as
	 * {@link DatePickerDialog.DateOptions#date(long)}, if it is set.
	 *
	 * @see #getDateInMillis()
	 */
	@NonNull
	@Override
	public DatePickerDialog.DateOptions getDialogOptions() {
		final DatePickerDialog.DateOptions options = super.getDialogOptions();
		if (areMillisecondsSet()) {
			options.date(getDateInMillis());
		}
		return options;
	}

	/**
	 */
	@Override
	protected boolean onHandleDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		if (dialog instanceof DatePickerDialog) {
			switch (button) {
				case Dialog.BUTTON_POSITIVE:
					setDate(((DatePickerDialog) dialog).getDate());
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
