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
import android.os.Build;
import android.preference.PreferenceCategory;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;

/**
 * Extended {@link PreferenceCategory} that provides additional features supported by the <b>Settings</b>
 * library.
 *
 * <h3>Xml attributes</h3>
 * See {@link PreferenceCategory}
 *
 * <h3>Default style attribute</h3>
 * {@link android.R.attr#preferenceCategoryStyle android:preferenceCategoryStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingPreferenceCategory extends PreferenceCategory {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingPreferenceCategory";

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
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingPreferenceCategory(Context, AttributeSet)} without attributes.
	 */
	public SettingPreferenceCategory(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingPreferenceCategory(Context, AttributeSet, int)} with
	 * {@link android.R.attr#preferenceCategoryStyle} as attribute for default style.
	 */
	public SettingPreferenceCategory(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, android.R.attr.preferenceCategoryStyle);
	}

	/**
	 * Same as {@link #SettingPreferenceCategory(Context, AttributeSet, int, int)} with {@code 0} as default
	 * style.
	 */
	public SettingPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * Creates a new instance of SettingPreferenceCategory for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingPreferenceCategory(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Inner classes ===============================================================================
	 */
}