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
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;

import universum.studios.android.widget.adapter.SimpleSpinnerAdapter;
import universum.studios.android.widget.adapter.ViewHolder;

/**
 * A {@link SettingPreference} implementation that provides {@link Spinner} widget with its related
 * functionality. Entries that should be available for selection as drop down items of the Spinner
 * widget may be specified via {@link #setEntries(CharSequence[])} along with theirs corresponding
 * entry values that should be specified via {@link #setEntryValues(CharSequence[])}
 * <p>
 * This preference implementation by default displays a single {@link Spinner} widget with a primary
 * view containing title text of the preference and summary text which corresponds to the current
 * selected entry value. The preferred entry value may be specified via {@link #setValue(String)} and
 * obtained via {@link #getValue()}.
 *
 * <h3>Default value</h3>
 * Default value for this preference is parsed as {@link String} which should contain a single entry
 * value that should be by default selected. See {@link TypedArray#getString(int)}.
 *
 * <h3>Xml attributes</h3>
 * See {@link SettingPreference},
 * {@link R.styleable#Ui_Settings_SpinnerPreference SettingSpinnerPreference Attributes}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#uiSettingSpinnerPreferenceStyle uiSettingSpinnerPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingSpinnerPreference extends SettingPreference {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingSpinnerPreference";

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
	 * Listener that is used to receive callbacks about changes in selection within the Spinner widget
	 * of this preference.
	 */
	private final AdapterView.OnItemSelectedListener LISTENER = new AdapterView.OnItemSelectedListener() {

		/**
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			setValueIndex(position);
		}

		/**
		 */
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Ignored.
		}
	};

	/**
	 * Array containing entries that are displayed as items in the {@link Spinner} widget.
	 */
	private CharSequence[] mEntries;

	/**
	 * Array containing values where each value is associated with one entry item. Value for the
	 * associated selected entry is persisted in the preferences.
	 */
	private CharSequence[] mEntryValues;

	/**
	 * Boolean flag indicating whether the selected value for this preference has been set or not.
	 * This flag is used to handle case when the same value is being specified for this preference,
	 * but for the first time, to properly refresh view of this preference and notify listeners about
	 * the change.
	 */
	private boolean mValueSet;

	/**
	 * Current selected value specified for this preference. This may be either value specified by
	 * the user, default value or persisted value.
	 */
	private String mValue;

	/**
	 * Adapter used for the {@link Spinner} widget of this preference to provide views for entries
	 * specified via {@link #setEntries(CharSequence[])}.
	 */
	private EntriesAdapter mAdapter;

	/**
	 * Spinner widget of this preference.
	 *
	 * @see #onBindView(View)
	 * @see #onPrepareForRemoval()
	 */
	private Spinner mSpinner;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingSpinnerPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingSpinnerPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingSpinnerPreference(Context, AttributeSet, int)} with
	 * {@link R.attr#uiSettingSpinnerPreferenceStyle} as attribute for default style.
	 */
	public SettingSpinnerPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.uiSettingSpinnerPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingSpinnerPreference(Context, AttributeSet, int, int)} with {@code 0}
	 * as default style.
	 */
	public SettingSpinnerPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs, defStyleAttr, 0);
	}

	/**
	 * Creates a new instance of SettingSpinnerPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingSpinnerPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
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
		this.mAdapter = new EntriesAdapter(context);
		this.mAdapter.setTitle(getTitle());
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_SpinnerPreference, defStyleAttr, defStyleRes);
		for (int i = 0; i < attributes.getIndexCount(); i++) {
			final int index = attributes.getIndex(i);
			if (index == R.styleable.Ui_Settings_SpinnerPreference_android_entries) {
				setEntries(attributes.getTextArray(index));
			} else if (index == R.styleable.Ui_Settings_SpinnerPreference_android_entryValues) {
				setEntryValues(attributes.getTextArray(index));
			} else if (index == R.styleable.Ui_Settings_SpinnerPreference_uiSettingSpinnerViewLayout) {
				mAdapter.setViewLayoutResource(attributes.getResourceId(index, R.layout.ui_setting_spinner_view));
			} else if (index == R.styleable.Ui_Settings_SpinnerPreference_uiSettingSpinnerDropDownViewLayout) {
				mAdapter.setDropDownViewLayoutResource(attributes.getResourceId(index, R.layout.ui_setting_spinner_drop_down_view));
			}
		}
		attributes.recycle();
	}

	/**
	 */
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		mAdapter.setTitle(title);
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
	 * Specifies an array of entries that should be displayed in the {@link Spinner} widget of
	 * this preference.
	 *
	 * @param entries The desired array of entries. May be {@code null} to clear the current ones.
	 * @see android.R.attr#entries
	 * @see #setEntryValues(CharSequence[])
	 */
	public void setEntries(@Nullable CharSequence[] entries) {
		this.mEntries = entries;
		if (entries == null) {
			this.mAdapter.changeItems(Collections.<CharSequence>emptyList());
		} else {
			this.mAdapter.changeItems(Arrays.asList(entries));
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
	 */
	@Override
	protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
		return typedArray.getString(index);
	}

	/**
	 */
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		setValue(restorePersistedValue ? getPersistedString(mValue) : (String) defaultValue);
	}

	/**
	 * Sets an index of the entry value that should be selected.
	 *
	 * @param index Index of the desired entry value to be selected.
	 * @see #getValueIndex()
	 * @see #setValue(String)
	 */
	void setValueIndex(int index) {
		if (mEntryValues != null && index < mEntryValues.length) {
			setValue(mEntryValues[index].toString());
		}
	}

	/**
	 * Returns the index of the current selected value.
	 *
	 * @return Index of the selected value or {@link EntriesAdapter#NO_POSITION} if no value is selected
	 * or there are not entry values specified yet.
	 * @see #setValueIndex(int)
	 * @see #getValue()
	 */
	private int getValueIndex() {
		if (TextUtils.isEmpty(mValue) || mEntryValues == null || mEntryValues.length == 0) {
			return EntriesAdapter.NO_POSITION;
		}
		for (int i = 0; i < mEntryValues.length; i++) {
			if (TextUtils.equals(mValue, mEntryValues[i])) return i;
		}
		return EntriesAdapter.NO_POSITION;
	}

	/**
	 * Sets a preferred entry value for this preference.
	 * <p>
	 * If value of this preference changes, it is persisted and the change listener is notified
	 * about the change.
	 *
	 * @param value The preferred entry value to be persisted.
	 * @see #getValue()
	 */
	public void setValue(@Nullable String value) {
		final boolean changed = !TextUtils.equals(mValue, value);
		if (callChangeListener(value) && (changed || !mValueSet)) {
			this.mValue = value;
			this.mValueSet = true;
			persistString(mValue);
			if (changed) {
				notifyChanged();
			}
		}
	}

	/**
	 * Returns the preferred entry value of this preference.
	 *
	 * @return Entry value either specified by the user, as default value or the persisted one.
	 * @see #setValue(String)
	 */
	@Nullable
	public String getValue() {
		return mValue;
	}

	/**
	 */
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		final Spinner spinner = (Spinner) view.findViewById(R.id.ui_setting_spinner);
		if (spinner != null) {
			spinner.setOnItemSelectedListener(null);
			spinner.setAdapter(mAdapter);
			final int valueIndex = getValueIndex();
			if (valueIndex != EntriesAdapter.NO_POSITION) {
				spinner.setSelection(valueIndex, false);
			}
			spinner.setOnItemSelectedListener(LISTENER);
			this.mSpinner = spinner;
		}
	}

	/**
	 */
	@Override
	protected void onClick() {
		super.onClick();
		if (mSpinner != null) {
			mSpinner.performClick();
		}
	}

	/**
	 */
	@Override
	protected void onPrepareForRemoval() {
		super.onPrepareForRemoval();
		this.mSpinner = null;
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * A {@link SimpleSpinnerAdapter} implementation that is used to provide entry items for the
	 * {@link Spinner} widget of the parent spinner preference.
	 */
	private static final class EntriesAdapter extends SimpleSpinnerAdapter<CharSequence, EntryViewHolder, EntryDropDownViewHolder> {

		/**
		 * Title text specified for this adapter to be displayed in the primary view.
		 */
		private CharSequence title;

		/**
		 * Layout resource of primary view inflated by this adapter in {@link #onCreateView(ViewGroup, int)}.
		 */
		private int viewLayoutResource;

		/**
		 * Layout resource of drop down view inflated by this adapter in {@link #onCreateDropDownView(ViewGroup, int)}.
		 */
		private int dropDownViewLayoutResource;

		/**
		 * Creates a new instance EntriesAdapter without initial entries.
		 *
		 * @see SimpleSpinnerAdapter#SimpleSpinnerAdapter(Context)
		 */
		private EntriesAdapter(@NonNull Context context) {
			super(context);
		}

		/**
		 * Sets a title to be displayed in the primary view provided by this adapter.
		 *
		 * @param title The desired title text. May be {@code null} to clear the current one.
		 */
		void setTitle(@Nullable CharSequence title) {
			if (!TextUtils.equals(this.title, title)) {
				this.title = title;
				notifyDataSetChanged();
			}
		}

		/**
		 * Sets a layout resource for primary view provided by this adapter.
		 *
		 * @param layoutResource The desired layout resource.
		 */
		void setViewLayoutResource(@LayoutRes int layoutResource) {
			this.viewLayoutResource = layoutResource;
			if (!isEmpty()) {
				notifyDataSetChanged();
			}
		}

		/**
		 * Sets a layout resource for drop down views provided by this adapter.
		 *
		 * @param layoutResource The desired layout resource.
		 */
		void setDropDownViewLayoutResource(@LayoutRes int layoutResource) {
			this.dropDownViewLayoutResource = layoutResource;
			if (!isEmpty()) {
				notifyDataSetChanged();
			}
		}

		/**
		 */
		@NonNull
		@Override
		protected View onCreateView(@NonNull ViewGroup parent, int position) {
			return inflate(viewLayoutResource, parent);
		}

		/**
		 */
		@Nullable
		@Override
		protected EntryViewHolder onCreateViewHolder(@NonNull View itemView, int position) {
			return new EntryViewHolder(itemView);
		}

		/**
		 */
		@NonNull
		@Override
		protected View onCreateDropDownView(@NonNull ViewGroup parent, int position) {
			return inflate(dropDownViewLayoutResource, parent);
		}

		/**
		 */
		@Nullable
		@Override
		protected EntryDropDownViewHolder onCreateDropDownViewHolder(@NonNull View itemView, int position) {
			return new EntryDropDownViewHolder(itemView);
		}

		/**
		 */
		@Override
		protected void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
			holder.titleView.setText(title);
			holder.summaryView.setText(getSelectedItem());
		}

		/**
		 */
		@Override
		protected void onUpdateViewHolder(@NonNull EntryDropDownViewHolder holder, @NonNull CharSequence entry, int position) {
			holder.titleView.setText(entry);
		}
	}

	/**
	 * A {@link ViewHolder} implementation used as holder for primary view of {@link EntriesAdapter}.
	 */
	private static final class EntryViewHolder extends ViewHolder {

		/**
		 * View displaying title text of the parent preference.
		 */
		final TextView titleView;

		/**
		 * View displaying summary text of the parent preference.
		 */
		final TextView summaryView;

		/**
		 * Creates a new instance of EntryViewHolder.
		 *
		 * @param itemView Instance of view to be hold by the holder.
		 */
		private EntryViewHolder(@NonNull View itemView) {
			super(itemView);
			this.titleView = (TextView) itemView.findViewById(android.R.id.title);
			this.summaryView = (TextView) itemView.findViewById(android.R.id.summary);
		}
	}

	/**
	 * A {@link ViewHolder} implementation used as holder for drop down views of {@link EntriesAdapter}.
	 */
	private static final class EntryDropDownViewHolder extends ViewHolder {

		/**
		 * View displaying title text of a specific entry item.
		 */
		final TextView titleView;

		/**
		 * Creates a new instance of EntryDropDownViewHolder.
		 *
		 * @param itemView Instance of view to be hold by the holder.
		 */
		private EntryDropDownViewHolder(@NonNull View itemView) {
			super(itemView);
			this.titleView = (TextView) itemView.findViewById(android.R.id.title);
		}
	}
}
