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
import android.preference.Preference;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import universum.studios.android.dialog.Dialog;
import universum.studios.android.dialog.DialogOptions;

/**
 * A {@link SettingPreference} implementation that may be used to show a preference dialog. Each
 * dialog preference has specified id of the associated dialog, via {@link R.attr#dialogId dialog:dialogId}
 * attribute, that may be obtained via {@link #getDialogId()}. Dialog preference also provides an
 * options for its associated dialog that may be obtained via {@link #getDialogOptions()}. Default
 * values for the dialog options are parsed from the Xml for the attributes specified in the section
 * below.
 * <p>
 * Subclasses should override {@link #onCreateDialogOptions(Resources)} to create type of options
 * that are specific for theirs associated type of dialog along with
 * {@link #onConfigureDialogOptions(DialogOptions, Context, AttributeSet, int, int)} where these
 * options should be configured from the provided {@link AttributeSet}. Also
 * {@link #onHandleDialogButtonClick(Dialog, int)} should be override to provide default handling of
 * dialog button click event.
 *
 * <h3>Default value</h3>
 * This preference implementation does not parse any default value. However parsing of default value
 * may be performed by the subclasses of this preference.
 *
 * <h3>Dialog Xml attributes</h3>
 * See {@link SettingPreference},
 * {@link R.styleable#Ui_Settings_DialogPreference SettingDialogPreference Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link android.R.attr#dialogPreferenceStyle android:dialogPreferenceStyle}
 *
 * @param <O> Type of the dialog options specific for type of the dialog associated with this preference.
 * @author Martin Albedinsky
 */
public class SettingDialogPreference<O extends DialogOptions<O>> extends SettingPreference {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingDialogPreference";

	/**
	 * Constant identifying no dialog id.
	 *
	 * @see #getDialogId()
	 */
	public static final int NO_DIALOG_ID = -1;

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Listener that may be used to receive a callback that a specific dialog preference has been clicked.
	 *
	 * @author Martin Albedinsky
	 */
	public interface OnClickListener {

		/**
		 * Invoked whenever the specified <var>dialogPreference</var> has been clicked.
		 *
		 * @param dialogPreference The clicked dialog preference.
		 * @return {@code True} if the click event has been handled, {@code false} otherwise.
		 */
		boolean onDialogPreferenceClick(@NonNull SettingDialogPreference dialogPreference);
	}

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Id of the dialog associated with this dialog preference.
	 */
	private int mDialogId = NO_DIALOG_ID;

	/**
	 * Dialog options specific for this type of dialog preference.
	 *
	 * @see #onCreateDialogOptions(Resources)
	 */
	private O mDialogOptions;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingDialogPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingDialogPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingDialogPreference(Context, AttributeSet, int)} with
	 * {@link android.R.attr#dialogPreferenceStyle} as attribute for default style.
	 */
	public SettingDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, android.R.attr.dialogPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingDialogPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs, defStyleAttr, 0);
	}

	/**
	 * Creates a new instance of SettingDialogPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
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
		this.mDialogOptions = onCreateDialogOptions(context.getResources());
		onConfigureDialogOptions(mDialogOptions, context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Called from one of constructors to configure dialog options specific for this dialog preference.
	 *
	 * @param options The options created via {@link #onCreateDialogOptions(Resources)}.
	 */
	@SuppressWarnings("ResourceType")
	protected void onConfigureDialogOptions(@NonNull O options, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_DialogPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < attributes.getIndexCount(); i++) {
			final int index = attributes.getIndex(i);
			if (index == R.styleable.Ui_Settings_DialogPreference_dialogId) {
				this.mDialogId = attributes.getResourceId(index, NO_DIALOG_ID);
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogIcon) {
				options.icon(attributes.getResourceId(index, options.icon()));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogVectorIcon) {
				options.vectorIcon(attributes.getResourceId(index, options.vectorIcon()));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogTitle) {
				options.title(attributes.getText(index));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogContent) {
				options.content(attributes.getText(index));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogPositiveButton) {
				options.positiveButton(attributes.getText(index));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogNeutralButton) {
				options.neutralButton(attributes.getText(index));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogNegativeButton) {
				options.negativeButton(attributes.getText(index));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogButtonsWidthMode) {
				options.buttonsWidthMode(attributes.getInt(index, options.buttonsWidthMode()));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogCancelable) {
				options.cancelable(attributes.getBoolean(index, options.shouldBeCancelable()));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogDismissOnRestore) {
				options.dismissOnRestore(attributes.getBoolean(index, options.shouldDismissOnRestore()));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogRemain) {
				options.remain(attributes.getBoolean(index, options.shouldRemain()));
			} else if (index == R.styleable.Ui_Settings_DialogPreference_dialogTheme) {
				options.theme(attributes.getResourceId(index, options.theme()));
			}
		}
		attributes.recycle();
	}

	/**
	 * Called to create instance of dialog options specific for this dialog preference.
	 * <p>
	 * Default implementation creates instance of {@link DialogOptions}.
	 *
	 * @param resources Application resources that may be used supply default values from resources
	 *                  for the options.
	 * @return New instance of dialog options.
	 */
	@NonNull
	@SuppressWarnings("unchecked")
	protected O onCreateDialogOptions(@NonNull Resources resources) {
		return (O) new DialogOptions(resources);
	}

	/**
	 * Registers a callback to be invoked whenever this dialog preference is clicked.
	 * <p>
	 * <b>Note</b>, that this method uses {@link #setOnPreferenceClickListener(OnPreferenceClickListener)}
	 * to register the requested callback. Any previously registered {@link OnPreferenceClickListener}
	 * will be replaced.
	 *
	 * @param listener The desired callback. May be {@code null} to clear the current one.
	 */
	public void setOnClickListener(@Nullable final OnClickListener listener) {
		setOnPreferenceClickListener(listener == null ? null : new OnPreferenceClickListener() {

			/**
			 */
			@Override
			public boolean onPreferenceClick(Preference preference) {
				return listener.onDialogPreferenceClick((SettingDialogPreference) preference);
			}
		});
	}

	/**
	 * Returns id of the dialog associated with this dialog preference.
	 *
	 * @return This preference's associated dialog id.
	 * @see R.attr#dialogId dialog:dialogId
	 */
	public final int getDialogId() {
		return mDialogId;
	}

	/**
	 * Returns the dialog options specific for the dialog associated with this dialog preference.
	 * <p>
	 * Subclasses may override this method and provide the dialog options with the current preferred
	 * value so it may be properly displayed in the associated dialog.
	 *
	 * @return This preference's dialog options.
	 */
	@NonNull
	public O getDialogOptions() {
		return mDialogOptions;
	}

	/**
	 * Performs synchronization of the summary view of this dialog preference.
	 * <p>
	 * Default implementation takes the summary text provided by {@link #onGetSummaryText()} method
	 * and sets is text for the summary view, changing its visibility so the summary view is visible
	 * if there is some summary text available or gone if there is no summary text available.
	 * <p>
	 * This method may be called from {@link #onBindView(View)}.
	 *
	 * @param view The root view of this preference.
	 */
	protected void synchronizeSummaryView(@NonNull View view) {
		final TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
		if (summaryView != null) {
			final CharSequence summaryText = onGetSummaryText();
			if (TextUtils.isEmpty(summaryText)) {
				summaryView.setVisibility(View.GONE);
			} else {
				summaryView.setText(summaryText);
				summaryView.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Called from {@link #synchronizeSummaryView(View)} to retrieve the current summary text for
	 * this dialog preference.
	 * <p>
	 * Default implementation returns the text provided by {@link #getSummary()} method.
	 *
	 * @return The actual summary text that should be presented in the summary view of this preference.
	 */
	@Nullable
	protected CharSequence onGetSummaryText() {
		return getSummary();
	}

	/**
	 * Handles click event for the specified <var>button</var> occurred in the given <var>dialog</var>.
	 *
	 * @param dialog The dialog where the button has been clicked.
	 * @param button The clicked dialog button.
	 * @return {@code True} if the dialog has id associated with this preference and the button click
	 * event has been successfully handled by this preference, {@code false} otherwise so it should
	 * be handled by the caller.
	 */
	public boolean handleOnDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		return dialog.getDialogId() == mDialogId && onHandleDialogButtonClick(dialog, button);
	}

	/**
	 * Called from {@link #handleOnDialogButtonClick(Dialog, int)} if the given <var>dialog</var> has
	 * id associated with this dialog preference.
	 *
	 * @param dialog The dialog where the button has been clicked.
	 * @param button The clicked dialog button.
	 * @return {@code True} if the button click event has been successfully handled, {@code false}
	 * otherwise.
	 */
	protected boolean onHandleDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		return false;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}