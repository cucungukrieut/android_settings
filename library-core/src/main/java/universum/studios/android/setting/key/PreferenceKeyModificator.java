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
 * A simple interface of which implementations may be used to modify keys of {@link Preference}
 * instances.
 *
 * @author Martin Albedinsky
 */
public interface PreferenceKeyModificator {

	/**
	 * Implementation of {@link PreferenceKeyModificator} that does not modify key of any preference
	 * passed to {@link #modifyKey(Preference)}.
	 */
	@SuppressWarnings("unused")
	PreferenceKeyModificator EMPTY = new PreferenceKeyModificator() {

		/**
		 */
		@Override
		public boolean modifyKey(@NonNull Preference preference) {
			return false;
		}
	};

	/**
	 * Modifies a key of the specified <var>preference</var> according to implementation of this modificator.
	 *
	 * @param preference The preference of which key value to modify.
	 * @return {@code True} if key of the preference has been modified, {@code false} otherwise.
	 */
	boolean modifyKey(@NonNull Preference preference);
}