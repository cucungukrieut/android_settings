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
import android.preference.SwitchPreference;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.Switch;

import universum.studios.android.setting.widget.SettingSwitch;

/**
 * Extended {@link SwitchPreference} that provides additional features supported by the <b>Settings</b>
 * library.
 *
 * <h3>Xml attributes</h3>
 * See {@link SwitchPreference}
 *
 * <h3>Default style attribute</h3>
 * {@link android.R.attr#switchPreferenceStyle android:switchPreferenceStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingSwitchPreference extends SwitchPreference {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingSwitchPreference";

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
	 * Decorator used to extend API of this setting preference by functionality otherwise not supported
	 * or not available due to current API level.
	 */
	private PreferenceDecorator mDecorator;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingSwitchPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingSwitchPreference(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingSwitchPreference(Context, AttributeSet, int)} with
	 * {@link android.R.attr#switchPreferenceStyle} as attribute for default style.
	 */
	public SettingSwitchPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, android.R.attr.switchPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingSwitchPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs, defStyleAttr, 0);
	}

	/**
	 * Creates a new instance of SettingSwitchPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingSwitchPreference(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
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
		this.ensureDecorator();
		// Enable layout recycling otherwise checkbox animations are not working.
		mDecorator.setCanRecycleLayout(true);
	}

	/**
	 * Ensures that the decorator for this view is initialized.
	 */
	private void ensureDecorator() {
		if (mDecorator == null) this.mDecorator = new PreferenceDecorator(this) {

			/**
			 */
			@Nullable
			@Override
			Object onGetDefaultValue(@NonNull TypedArray attributes, int index) {
				return SettingSwitchPreference.this.onGetDefaultValue(attributes, index);
			}

			/**
			 */
			@Override
			void onUpdateInitialValue(boolean restorePersistedValue, @Nullable Object defaultValue) {
				SettingSwitchPreference.this.onSetInitialValue(restorePersistedValue, defaultValue);
			}
		};
	}

	/**
	 */
	@Override
	public void setKey(@NonNull String key) {
		final boolean keyChanged = !key.equals(getKey());
		super.setKey(key);
		if (keyChanged) {
			mDecorator.handleKeyChange();
		}
	}

	/**
	 */
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		this.ensureDecorator();
		mDecorator.onBindView(view);
		final View checkableView = view.findViewById(R.id.ui_setting_switch);
		if (checkableView instanceof Checkable) {
			if (checkableView instanceof Switch) {
				final Switch switchView = (Switch) checkableView;
				switchView.setOnCheckedChangeListener(null);
			}
			((Checkable) checkableView).setChecked(isChecked());
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */
}