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
 * A simple interface of which implementations may be used to modify keys of {@link Preference}
 * instances associated with {@link PreferenceScreen}.
 *
 * @author Martin Albedinsky
 */
public interface PreferenceScreenKeyModificator {

	/**
	 * Implementation of {@link PreferenceScreenKeyModificator} that does not modify key of any preference
	 * associated with preference screen passed to {@link #modifyKeys(PreferenceScreen)}.
	 */
	@SuppressWarnings("unused")
	PreferenceScreenKeyModificator EMPTY = new PreferenceScreenKeyModificator() {

		/**
		 */
		@Override
		public int modifyKeys(@NonNull PreferenceScreen preferenceScreen) {
			return 0;
		}
	};

	/**
	 * Modifies keys of preferences that are associated with the specified <var>preferenceScreen</var>
	 * according to implementation of this modificator.
	 *
	 * @param preferenceScreen The preference screen where to modify keys of its associated preferences.
	 * @return Number determining count of preferences of which key has been modified. If no modification
	 * has been performed by this modificator then {@code 0} is returned.
	 */
	int modifyKeys(@NonNull PreferenceScreen preferenceScreen);
}
