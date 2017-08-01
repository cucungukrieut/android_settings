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
package universum.studios.android.test.runner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;

/**
 * A runner that extends {@link AndroidJUnitRunner} and installs {@link MultiDex} in order to support
 * running of Android instrumented tests of which overall methods count (dependencies including)
 * exceeds {@code .dex} file methods count of {@code 65,535}.
 *
 * @author Martin Albedinsky
 */
public final class MultiDexAndroidJUnitRunner extends AndroidJUnitRunner {

	/**
	 */
	@Override
	public void onCreate(@NonNull final Bundle arguments) {
		MultiDex.install(getTargetContext());
		super.onCreate(arguments);
	}
}
