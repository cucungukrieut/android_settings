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

import android.support.annotation.NonNull;

/**
 * A simple interface of which implementations may be used to modify keys associated with shared preferences.
 *
 * @author Martin Albedinsky
 */
public interface KeyModificator {

	/**
	 * Implementation of {@link KeyModificator} that does not modify any key passed to {@link #modifyKey(String)}.
	 */
	@SuppressWarnings("unused")
	KeyModificator EMPTY = new KeyModificator() {

		/**
		 */
		@NonNull
		@Override
		public String modifyKey(@NonNull String key) {
			return key;
		}
	};

	/**
	 * Modifies the specified <var>key</var> according to implementation of this modificator.
	 *
	 * @param key The key of which value to modify.
	 * @return Modified key or the same key as the given one if this modificator does not perform
	 * any modification.
	 */
	@NonNull
	String modifyKey(@NonNull String key);
}
