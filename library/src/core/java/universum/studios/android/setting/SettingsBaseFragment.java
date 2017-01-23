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

import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link PreferenceFragment} implementation which inflates its layout from a style specified in
 * the current theme.
 *
 * <h3>Theme style attribute</h3>
 * {@link R.attr#uiSettingsFragmentStyle uiSettingsFragmentStyle}
 *
 * @author Martin Albedinsky
 */
public abstract class SettingsBaseFragment extends PreferenceFragment {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingsBaseFragmentCompat";

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
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final TypedArray typedArray = inflater.getContext().obtainStyledAttributes(
				null,
				R.styleable.Ui_Settings_Fragment,
				R.attr.uiSettingsFragmentStyle,
				0
		);
		final int layoutResource = typedArray.getResourceId(
				R.styleable.Ui_Settings_Fragment_android_layout,
				R.layout.ui_settings_fragment
		);
		typedArray.recycle();
		return inflater.inflate(layoutResource, container, false);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
