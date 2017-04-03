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
import android.support.annotation.NonNull;

/**
 * A simple implementation of {@link PreferenceKeyModificator} that uses {@link KeyModificator} supplied
 * to {@link #SimplePreferenceKeyModificator(KeyModificator)} constructor to perform modification of
 * key for any preference passed to {@link #modifyKey(Preference)}.
 *
 * @author Martin Albedinsky
 */
public class SimplePreferenceKeyModificator implements PreferenceKeyModificator {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SimplePreferenceKeyModificator";

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
	 * Key modificator used to modify key of preferences passed to {@link #modifyKey(Preference)}.
	 */
	private final KeyModificator mModificator;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of SimplePreferenceKeyModificator with the specified key <var>modificator</var>
	 *
	 * @param modificator The desired modificator that should be used for key modification.
	 * @see #modifyKey(Preference)
	 */
	public SimplePreferenceKeyModificator(@NonNull final KeyModificator modificator) {
		this.mModificator = modificator;
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	public boolean modifyKey(@NonNull final Preference preference) {
		final String key = preference.getKey();
		if (key == null || key.length() == 0) {
			return false;
		}
		final String modifiedKey = mModificator.modifyKey(key);
		if (modifiedKey.equals(key)) {
			return false;
		}
		preference.setKey(modifiedKey);
		return true;
	}

	/*
	 * Inner classes ===============================================================================
	 */
}
