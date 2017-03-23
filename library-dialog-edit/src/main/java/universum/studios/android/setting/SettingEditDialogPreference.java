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
import android.view.View;

import universum.studios.android.dialog.Dialog;
import universum.studios.android.dialog.EditDialog;
import universum.studios.android.dialog.view.InputConfig;

/**
 * A {@link SettingDialogPreference} implementation that may be used to allow to a user to specify
 * its preferred input text for a specific setting preference.
 * <p>
 * This preference implementation by default displays the preferred input text as its summary text
 * if it is specified, or the standard summary text if there is no input text persisted. The preferred
 * input text may be specified via {@link #setInput(String)} and obtained via {@link #getInput()}.
 * <p>
 * When {@link #handleOnDialogButtonClick(Dialog, int)} is called, this preference implementation
 * handles only {@link EditDialog} type of dialog. If its {@link Dialog#BUTTON_POSITIVE} button has
 * been clicked, the input provided via {@link EditDialog#getInput()} is set as input for this
 * preference via {@link #setInput(String)}.
 *
 * <h3>Default value</h3>
 * Default value for this preference is parsed as {@link String}. See {@link TypedArray#getString(int)}.
 *
 * <h3>Xml attributes</h3>
 * See {@link SettingDialogPreference},
 * {@link R.styleable#Ui_Settings_EditDialogPreference SettingEditDialogPreference Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingEditDialogPreferenceStyle uiSettingEditDialogPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingEditDialogPreference extends SettingDialogPreference<EditDialog.EditOptions> {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingEditDialogPreference";

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
	 * Boolean flag indicating whether the input value for this preference has been set or not.
	 * This flag is used to handle case when the same value is being specified for this preference,
	 * but for the first time, to properly refresh view of this preference and notify listeners about
	 * the change.
	 */
	private boolean mInputSet;

	/**
	 * Current input value specified for this preference. This may be either value specified by the
	 * user, default value or persisted value.
	 */
	private String mInput;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingEditDialogPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingEditDialogPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingEditDialogPreference(Context, AttributeSet, int)} with
	 * {@link R.attr#uiSettingEditDialogPreferenceStyle} as attribute for default style.
	 */
	public SettingEditDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingEditDialogPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingEditDialogPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingEditDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * Creates a new instance of SettingEditDialogPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingEditDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	protected void onConfigureDialogOptions(@NonNull EditDialog.EditOptions options, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super.onConfigureDialogOptions(options, context, attrs, defStyleAttr, defStyleRes);
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_EditDialogPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < attributes.getIndexCount(); i++) {
			final int index = attributes.getIndex(i);
			if (index == R.styleable.Ui_Settings_EditDialogPreference_dialogHint) {
				options.hint(attributes.getText(index));
			} else if (index == R.styleable.Ui_Settings_EditDialogPreference_dialogInputStyle) {
				options.inputConfig(InputConfig.fromStyle(context, attributes.getResourceId(index, 0)));
			} else if (index == R.styleable.Ui_Settings_EditDialogPreference_dialogShowSoftKeyboard) {
				options.showSoftKeyboard(attributes.getBoolean(index, options.shouldShowSoftKeyboard()));
			}
		}
		attributes.recycle();
	}

	/**
	 */
	@NonNull
	@Override
	protected EditDialog.EditOptions onCreateDialogOptions(@NonNull Resources resources) {
		return new EditDialog.EditOptions(resources).title(getTitle());
	}

	/**
	 */
	@Override
	protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
		return typedArray.getString(index);
	}

	/**
	 */
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		setInput(restorePersistedValue ? getPersistedString(mInput) : (String) defaultValue);
	}

	/**
	 * Sets a preferred input value for this preference.
	 * <p>
	 * If value of this preference changes, it is persisted and the change listener is notified
	 * about the change.
	 *
	 * @param input The preferred input to be persisted.
	 * @see #getInput()
	 */
	public void setInput(@Nullable String input) {
		final boolean changed = !TextUtils.equals(mInput, input);
		if (callChangeListener(input) && (changed || !mInputSet)) {
			this.mInput = input;
			this.mInputSet = true;
			persistString(mInput);
			if (changed) {
				notifyChanged();
			}
		}
	}

	/**
	 * Returns the preferred input value of this preference.
	 *
	 * @return Input either specified by the user, as default value or the persisted one.
	 * @see #setInput(String)
	 * @see #getDialogOptions()
	 */
	@Nullable
	public String getInput() {
		return mInput;
	}

	/**
	 */
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		synchronizeSummaryView(view);
	}

	/**
	 */
	@Nullable
	@Override
	protected CharSequence onGetSummaryText() {
		return mInputSet ? mInput : super.onGetSummaryText();
	}

	/**
	 * Dialog options of this preference with the preferred input specified as
	 * {@link EditDialog.EditOptions#content(CharSequence)}, if it is set.
	 *
	 * @see #getInput()
	 */
	@NonNull
	@Override
	public EditDialog.EditOptions getDialogOptions() {
		final EditDialog.EditOptions options = super.getDialogOptions();
		if (mInputSet) {
			options.content(mInput);
		}
		return options;
	}

	/**
	 */
	@Override
	protected boolean onHandleDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		if (dialog instanceof EditDialog) {
			switch (button) {
				case Dialog.BUTTON_POSITIVE:
					setInput(((EditDialog) dialog).getInput().toString());
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