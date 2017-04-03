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

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingSwitchPreference";

	/*
	 * Interface ===================================================================================
	 */

	/*
	 * Static members ==============================================================================
	 */

	/*
	 * Members =====================================================================================
	 */

	/**
	 * Decorator used to extend API of this setting preference by functionality otherwise not supported
	 * or not available due to current API level.
	 */
	private PreferenceDecorator mDecorator;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingSwitchPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingSwitchPreference(@NonNull final Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingSwitchPreference(Context, AttributeSet, int)} with
	 * {@link android.R.attr#switchPreferenceStyle} as attribute for default style.
	 */
	public SettingSwitchPreference(@NonNull final Context context, @Nullable final AttributeSet attrs) {
		this(context, attrs, android.R.attr.switchPreferenceStyle);
	}

	/**
	 * Same as {@link #SettingSwitchPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingSwitchPreference(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.ensureDecorator();
		// Enable layout recycling otherwise checkbox animations are not working.
		mDecorator.setCanRecycleLayout(true);
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
	public SettingSwitchPreference(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.ensureDecorator();
		// Enable layout recycling otherwise checkbox animations are not working.
		mDecorator.setCanRecycleLayout(true);
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 * Ensures that the decorator for this view is initialized.
	 */
	private void ensureDecorator() {
		if (mDecorator == null) this.mDecorator = new PreferenceDecorator(this) {

			/**
			 */
			@Nullable
			@Override
			Object onGetDefaultValue(@NonNull final TypedArray attributes, final int index) {
				return SettingSwitchPreference.this.onGetDefaultValue(attributes, index);
			}

			/**
			 */
			@Override
			void onUpdateInitialValue(final boolean restorePersistedValue, @Nullable final Object defaultValue) {
				SettingSwitchPreference.this.onSetInitialValue(restorePersistedValue, defaultValue);
			}
		};
	}

	/**
	 */
	@Override
	public void setKey(@NonNull final String key) {
		final boolean keyChanged = !key.equals(getKey());
		super.setKey(key);
		if (keyChanged) {
			mDecorator.handleKeyChange();
		}
	}

	/**
	 */
	@Override
	public void onBindView(@NonNull final View view) {
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

	/*
	 * Inner classes ===============================================================================
	 */
}
