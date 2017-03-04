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
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import universum.studios.android.dialog.Dialog;
import universum.studios.android.dialog.SelectionDialog;
import universum.studios.android.dialog.adapter.DialogSelectionAdapter;

/**
 * A {@link SettingDialogPreference} implementation that may be used to allow to a user to select
 * its preferred values, either in <b>single</b> or <b>multiple</b> choice mode, for a specific
 * setting preference. Entries that should be available for selection may be specified via
 * {@link #setEntries(CharSequence[])} along with theirs corresponding entry values that should be
 * specified via {@link #setEntryValues(CharSequence[])}.
 * <p>
 * This preference implementation by default displays the preferred entry values as its summary text,
 * using {@link SummaryTextBuilder}, that may be specified via {@link #setSummaryTextBuilder(SummaryTextBuilder)},
 * to build the summary text from the selected entry values. If there are no preferred entry values
 * selected yet, the standard summary text is displayed. The selected entry values are persisted as
 * {@link String} in Json Array format. The preferred entry values may be specified via {@link #setSelection(long[])}
 * where the passed array should contain indexes of entry values specified via {@link #setEntryValues(CharSequence[])}
 * to be persisted. The current selection array may be obtained via {@link #getSelection()}. Array
 * of persisted entry values may be obtained via {@link #getSelecedEntryValues()}. Outside of context
 * of the selection preference it may be obtained via {@link #selectedEntryValuesFromPersistedValues(String)}
 * method which accepts {@link String} containing persisted entry values in Json Array format.
 * <p>
 * When {@link #handleOnDialogButtonClick(Dialog, int)} is called, this preference implementation
 * handles only {@link SelectionDialog} type of dialog. If its {@link Dialog#BUTTON_POSITIVE} button
 * has been clicked, the selection array provided via {@link SelectionDialog#getSelection()} is set
 * as selection for this preference via {@link #setSelection(long[])}.
 *
 * <h3>Default value</h3>
 * Default value for this preference is parsed as {@link String} which should contain entry values
 * that should be by default selected, in Json Array format like {@code "["entry_value_1", "entry_value_2"]"}.
 * See {@link TypedArray#getString(int)}.
 *
 * <h3>Xml attributes</h3>
 * See {@link SettingDialogPreference},
 * {@link R.styleable#Ui_Settings_SelectionDialogPreference SettingSelectionDialogPreference Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingSelectionDialogPreferenceStyle uiSettingSelectionDialogPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingSelectionDialogPreference extends SettingDialogPreference<SelectionDialog.SelectionOptions> {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingSelectionDialogPreference";

	/**
	 * Default separator for entry items presented in the summary text.
	 */
	private static final String SUMMARY_ENTRIES_SEPARATOR = ", ";

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Interface for builder that is used by {@link SettingSelectionDialogPreference} to build its
	 * summary text from the current selection of entry items.
	 *
	 * @author Martin Albedinsky
	 * @see #setSummaryTextBuilder(SummaryTextBuilder)
	 */
	public interface SummaryTextBuilder {

		/**
		 * Clears the current content of this builder.
		 * <p>
		 * This is called by the associated selection preference whenever a new summary text is about
		 * to be build.
		 *
		 * @return This builder to allow methods chaining.
		 */
		SummaryTextBuilder clear();

		/**
		 * Appends the given <var>entry</var> into content of this builder. If this builder uses
		 * separator to separate multiple entries, such separator will be included accordingly.
		 * <p>
		 * This is called by the associated selection preference for each selected entry.
		 *
		 * @param entry The entry to be appended.
		 * @return This builder to allow methods chaining.
		 */
		SummaryTextBuilder appendEntry(@NonNull CharSequence entry);

		/**
		 * Builds summary text from the current content (entries) appended into this builder.
		 * <p>
		 * This is called by the associated selection preference when all currently selected entries
		 * have been appended into this builder.
		 *
		 * @return Summary text with entries.
		 */
		@NonNull
		CharSequence build();
	}

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Array containing entries from which are created {@link #mDialogItems} to be displayed in the
	 * selection dialog.
	 */
	private CharSequence[] mEntries;

	/**
	 * Array containing values where each value is associated with one entry item. Value for the
	 * associated selected entry is persisted in the preferences.
	 */
	private CharSequence[] mEntryValues;

	/**
	 * Boolean flag indicating whether the selection value for this preference has been set or not.
	 * This flag is used to handle case when the same value is being specified for this preference,
	 * but for the first time, to properly refresh view of this preference and notify listeners about
	 * the change.
	 */
	private boolean mSelectionSet;

	/**
	 * Current selection value specified for this preference. This may be either value specified by
	 * the user, default value or persisted value.
	 * <p>
	 * Note, that this selection contains indexes of the preferred entry values from the {@link #mEntryValues}
	 * array. The selection is created from the persisted Json array containing selected entry values
	 * via {@link #createSelectionFromPersistedValues(String)} or transformed into persistable Json
	 * array of entry values via {@link #createPersistableValuesFromSelection(long[])} and persisted
	 * as {@link String} via {@link #persistString(String)}.
	 */
	private long[] mSelection;

	/**
	 * List containing selectable items converted from array of {@link #mEntries} to be displayed in
	 * the selection dialog.
	 */
	private List<DialogSelectionAdapter.Item> mDialogItems;

	/**
	 * Builder that is used to build a summary text for the current selected items.
	 */
	private SummaryTextBuilder mSummaryTextBuilder;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingSelectionDialogPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingSelectionDialogPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingSelectionDialogPreference(Context, AttributeSet, int)} with
	 * {@link R.attr#uiSettingSelectionDialogPreferenceStyle} as attribute for default style.
	 */
	public SettingSelectionDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingSelectionDialogPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingSelectionDialogPreference(Context, AttributeSet, int, int)} with {@code 0}
	 * as default style.
	 */
	public SettingSelectionDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs, defStyleAttr, 0);
	}

	/**
	 * Creates a new instance of SettingSelectionDialogPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingSelectionDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
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
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_SelectionDialogPreference, defStyleAttr, defStyleRes);
		setEntries(attributes.getTextArray(R.styleable.Ui_Settings_SelectionDialogPreference_android_entries));
		setEntryValues(attributes.getTextArray(R.styleable.Ui_Settings_SelectionDialogPreference_android_entryValues));
		attributes.recycle();
		this.mSummaryTextBuilder = new DefaultSummaryTextBuilder(SUMMARY_ENTRIES_SEPARATOR);
	}

	/**
	 */
	@NonNull
	@Override
	protected SelectionDialog.SelectionOptions onCreateDialogOptions(@NonNull Resources resources) {
		return new SelectionDialog.SelectionOptions(resources)
				.title(getTitle())
				.selectionMode(DialogSelectionAdapter.SINGLE)
				.emptySelectionAllowed(true);
	}

	/**
	 */
	@Override
	@SuppressWarnings("ResourceType")
	protected void onConfigureDialogOptions(@NonNull SelectionDialog.SelectionOptions options, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super.onConfigureDialogOptions(options, context, attrs, defStyleAttr, defStyleRes);
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_SelectionDialogPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < attributes.getIndexCount(); i++) {
			final int index = attributes.getIndex(i);
			if (index == R.styleable.Ui_Settings_SelectionDialogPreference_dialogSelectionMode) {
				options.selectionMode(attributes.getInt(index, options.selectionMode()));
			} else if (index == R.styleable.Ui_Settings_SelectionDialogPreference_dialogEmptySelectionAllowed) {
				options.emptySelectionAllowed(attributes.getBoolean(index, options.shouldAllowEmptySelection()));
			}
		}
		attributes.recycle();
	}

	/**
	 * Same as {@link #setEntries(CharSequence[])} for resource id.
	 *
	 * @param resId Resource id of the desired text array with entries.
	 */
	public void setEntries(@ArrayRes int resId) {
		setEntries(getContext().getResources().getTextArray(resId));
	}

	/**
	 * Specifies an array of entries that should be displayed in the associated selection dialog.
	 *
	 * @param entries The desired array of entries. May be {@code null} to clear the current ones.
	 * @see android.R.attr#entries
	 * @see #setEntryValues(CharSequence[])
	 */
	public void setEntries(@Nullable CharSequence[] entries) {
		this.mEntries = entries;
		if (entries == null) {
			this.mDialogItems = null;
		} else {
			this.mDialogItems = new ArrayList<>(entries.length);
			for (int i = 0; i < entries.length; i++) {
				final CharSequence entry = entries[i];
				mDialogItems.add(new SelectionDialog.TextItem(i, entry));
			}
		}
	}

	/**
	 * Returns the array of entries specified for this preference.
	 *
	 * @return Array with entries. May be {@code null} if no entries have been specified.
	 * @see #setEntries(CharSequence[])
	 * @see #getEntryValues()
	 */
	@Nullable
	public CharSequence[] getEntries() {
		return mEntries;
	}

	/**
	 * Same as {@link #setEntryValues(CharSequence[])} for resource id.
	 *
	 * @param resId Resource id of the desired text array with values for entries.
	 */
	public void setEntryValues(@ArrayRes int resId) {
		setEntryValues(getContext().getResources().getTextArray(resId));
	}

	/**
	 * Specifies an array of entry values where each value should be associated with corresponding
	 * entry from the entries array specified via {@link #setEntries(CharSequence[])}.
	 * <p>
	 * <b>Note</b>, that this method does not check if the entries and entry values arrays are
	 * consistent, that is that they are equal in length.
	 *
	 * @param entryValues The desired array of values associated with entries. May be {@code null}
	 *                    to clear the current ones.
	 * @see android.R.attr#entryValues
	 */
	public void setEntryValues(@Nullable CharSequence[] entryValues) {
		this.mEntryValues = entryValues;
	}

	/**
	 * Returns the array of entry values associated with the entries specified for this preference.
	 *
	 * @return Array with entry values. May be {@code null} if no entry values have been specified.
	 * @see #setEntryValues(CharSequence[])
	 * @see #getEntries()
	 */
	@Nullable
	public CharSequence[] getEntryValues() {
		return mEntryValues;
	}

	/**
	 * Sets a builder that should be used by this preference to build its summary text for the current
	 * selected entry items.
	 * <p>
	 * By default, this preference uses internal implementation of the summary builder which
	 * separates the entry items in the summary text by ', '.
	 *
	 * @param textBuilder The desired builder for the summary text. May be {@code null} to use the
	 *                    default one.
	 */
	public void setSummaryTextBuilder(@Nullable SummaryTextBuilder textBuilder) {
		this.mSummaryTextBuilder = textBuilder == null ? new DefaultSummaryTextBuilder(SUMMARY_ENTRIES_SEPARATOR) : textBuilder;
	}

	/**
	 */
	@Override
	protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
		return typedArray.getText(index);
	}

	/**
	 */
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		if (restorePersistedValue) {
			final String persistedValues = getPersistedString(null);
			setSelection(persistedValues == null ? mSelection : createSelectionFromPersistedValues(persistedValues));
		} else {
			setSelection(createSelectionFromPersistedValues((String) defaultValue));
		}
	}

	/**
	 * Creates an array of selected entry values from the given <var>persistedValues</var> String.
	 *
	 * @param persistedValues The string with entry values in Json Array format persisted for a
	 *                        particular {@link SettingSelectionDialogPreference}.
	 * @return Array of entry values that have been persisted or {@code null} if no preferred values
	 * have been selected/persisted yet.
	 */
	@Nullable
	public static String[] selectedEntryValuesFromPersistedValues(@NonNull String persistedValues) {
		try {
			final JSONArray persistedArray = new JSONArray(persistedValues);
			final String[] selectedValues = new String[persistedArray.length()];
			for (int i = 0; i < persistedArray.length(); i++) {
				selectedValues[i] = persistedArray.getString(i);
			}
			return selectedValues;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates the selection array for this preference from the given <var>persistedValues</var>.
	 *
	 * @param persistedValues The persisted selection values, created via {@link #createPersistableValuesFromSelection(long[])}.
	 * @return Selection array containing indexes of the selected entries.
	 */
	private long[] createSelectionFromPersistedValues(String persistedValues) {
		try {
			final JSONArray persistedArray = new JSONArray(persistedValues);
			final long[] selection = new long[persistedArray.length()];
			for (int i = 0; i < persistedArray.length(); i++) {
				final String selectedValue = persistedArray.getString(i);
				for (int j = 0; j < mEntryValues.length; j++) {
					if (mEntryValues[j].equals(selectedValue)) {
						selection[i] = j;
						break;
					}
				}
			}
			return selection;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates the persistable values string from the given <var>selection</var> array.
	 *
	 * @param selection The selection to be transformed into persistable values string.
	 * @return String containing array of entry values according to the specified selection in the
	 * Json Array format.
	 */
	private String createPersistableValuesFromSelection(long[] selection) {
		final JSONArray persistableArray = new JSONArray();
		for (final long itemIndex : selection) {
			if (((int) itemIndex) < mEntryValues.length) {
				persistableArray.put(mEntryValues[(int) itemIndex]);
			}
		}
		return persistableArray.toString();
	}

	/**
	 * Sets a selection array containing indexes of preferred entry values that have been specified
	 * via {@link #setEntryValues(CharSequence[])} for this preference.
	 *
	 * @param selection The desired selection array with indexes of the preferred entry values to
	 *                  be persisted as {@link String} in Json Array format.
	 * @see #getSelection()
	 */
	public void setSelection(@Nullable long[] selection) {
		final boolean changed = !Arrays.equals(mSelection, selection);
		final String selectionValues = createPersistableValuesFromSelection(selection);
		if (callChangeListener(selectionValues) && (changed || !mSelectionSet)) {
			this.mSelection = selection;
			this.mSelectionSet = true;
			persistString(selectionValues);
			if (changed) {
				notifyChanged();
			}
		}
	}

	/**
	 * Returns the array of indexes of the preferred entry values of this preference.
	 *
	 * @return Selection array either specified by the user, as default value or the persisted one.
	 * @see #setSelection(long[])
	 * @see #getSelecedEntryValues()
	 * @see #getDialogOptions()
	 */
	@Nullable
	public long[] getSelection() {
		return mSelection;
	}

	/**
	 * Returns the array of preferred entry values of this preference.
	 *
	 * @return Array containing entry values that have been selected either by user, as default value
	 * or the persisted one.
	 * @see #getSelection()
	 */
	@Nullable
	public CharSequence[] getSelecedEntryValues() {
		if (mSelectionSet) {
			final CharSequence[] selectedValues = new CharSequence[mSelection.length];
			for (int i = 0; i < mSelection.length; i++) {
				selectedValues[i] = mEntryValues[(int) mSelection[i]];
			}
			return selectedValues;
		}
		return null;
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
		if (mSelectionSet && mSelection.length > 0) {
			mSummaryTextBuilder.clear();
			for (final long itemIndex : mSelection) {
				mSummaryTextBuilder.appendEntry(mEntries[(int) itemIndex]);
			}
			return mSummaryTextBuilder.build();
		}
		return super.onGetSummaryText();
	}

	/**
	 */
	@NonNull
	@Override
	public SelectionDialog.SelectionOptions getDialogOptions() {
		final SelectionDialog.SelectionOptions options = super.getDialogOptions();
		options.items(mDialogItems);
		if (mSelectionSet) {
			options.selection(mSelection);
		}
		return options;
	}

	/**
	 */
	@Override
	protected boolean onHandleDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		if (dialog instanceof SelectionDialog) {
			switch (button) {
				case Dialog.BUTTON_POSITIVE:
					setSelection(((SelectionDialog) dialog).getSelection());
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

	/**
	 * A {@link SummaryTextBuilder} implementation that is used as default builder by the selection
	 * preference.
	 */
	private static final class DefaultSummaryTextBuilder implements SummaryTextBuilder {

		/**
		 * String builder used when building the summary text.
		 */
		private final StringBuilder builder;

		/**
		 * Separator used to separate entry items.
		 */
		private final String separator;

		/**
		 * Creates a new instance of DefaultSummaryTextBuilder with the specified <var>separator</var>.
		 *
		 * @param separator The desired separator used to separate entry items.
		 */
		private DefaultSummaryTextBuilder(@NonNull String separator) {
			this.builder = new StringBuilder(64);
			this.separator = separator;
		}

		/**
		 */
		@Override
		public SummaryTextBuilder clear() {
			builder.setLength(0);
			return this;
		}

		/**
		 */
		@Override
		public SummaryTextBuilder appendEntry(@NonNull CharSequence entry) {
			if (builder.length() > 0) {
				builder.append(separator);
			}
			builder.append(entry);
			return this;
		}

		/**
		 */
		@NonNull
		@Override
		public CharSequence build() {
			return builder.toString();
		}
	}
}
