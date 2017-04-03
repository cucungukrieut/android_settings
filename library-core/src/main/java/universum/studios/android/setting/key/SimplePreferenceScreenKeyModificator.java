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
package universum.studios.android.setting.key;

import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

/**
 * A simple implementation of {@link PreferenceScreenKeyModificator} that uses {@link PreferenceKeyModificator}
 * supplied to {@link #SimplePreferenceScreenKeyModificator(PreferenceKeyModificator)} constructor to
 * perform modification of preference's key for all preferences associated with preference screen
 * passed to {@link #modifyKeys(PreferenceScreen)}.
 *
 * @author Martin Albedinsky
 */
public class SimplePreferenceScreenKeyModificator implements PreferenceScreenKeyModificator {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "PreferenceScreenKeyModificator";

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
	 * Preference key modificator used to modify key of preferences associated with preference screen
	 * passed to {@link #modifyKeys(PreferenceScreen)}.
	 */
	private final PreferenceKeyModificator mModificator;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of SimplePreferenceScreenKeyModificator with the specified preference
	 * key modificator.
	 *
	 * @param modificator The desired modificator that should be used for preference key modification.
	 */
	public SimplePreferenceScreenKeyModificator(@NonNull final PreferenceKeyModificator modificator) {
		this.mModificator = modificator;
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 * This implementation simply iterates all preferences of the specified <var>preferenceScreen</var>
	 * and calls {@link PreferenceKeyModificator#modifyKey(Preference)} for each one of them.
	 */
	@Override
	public int modifyKeys(@NonNull final PreferenceScreen preferenceScreen) {
		int modifiedCount = 0;
		final int n = preferenceScreen.getPreferenceCount();
		for (int i = 0; i < n; i++) {
			if (mModificator.modifyKey(preferenceScreen.getPreference(i))) modifiedCount++;
		}
		return modifiedCount;
	}

	/*
	 * Inner classes ===============================================================================
	 */
}
