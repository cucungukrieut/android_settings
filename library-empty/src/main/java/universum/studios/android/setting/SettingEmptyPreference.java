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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link SettingPreference} implementation that may be used to present an empty (invisible)
 * preference in preference screen. This may be useful to fix a problem when view of the first
 * preference in preference screen is not being animated properly or at all.
 *
 * <h3>Xml attributes</h3>
 * See {@link SettingPreference}
 *
 * <h3>Default style attribute</h3>
 * {@code none}
 *
 * @author Martin Albedinsky
 */
public class SettingEmptyPreference extends SettingPreference {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingEmptyPreference";

	/*
	 * Interface ===================================================================================
	 */

	/*
	 * Static members ==============================================================================
	 */

	/*
	 * Members =====================================================================================
	 */

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #SettingEmptyPreference(Context, AttributeSet)} without attributes.
	 */
	public SettingEmptyPreference(@NonNull final Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingEmptyPreference(Context, AttributeSet, int)} with {@code 0} as
	 * attribute for default style.
	 */
	public SettingEmptyPreference(@NonNull final Context context, @Nullable final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Same as {@link #SettingEmptyPreference(Context, AttributeSet, int, int)} with {@code 0} as
	 * default style.
	 */
	public SettingEmptyPreference(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setEnabled(false);
		setSelectable(false);
	}

	/**
	 * Creates a new instance of SettingEmptyPreference for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new setting preference presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this preference.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new preference.
	 */
	@SuppressWarnings("unused")
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingEmptyPreference(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		setEnabled(false);
		setSelectable(false);
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	@SuppressLint("MissingSuperCall")
	protected View onCreateView(@NonNull final ViewGroup parent) {
		final Space space = new Space(parent.getContext());
		space.setVisibility(View.GONE);
		return space;
	}

	/*
	 * Inner classes ===============================================================================
	 */
}
