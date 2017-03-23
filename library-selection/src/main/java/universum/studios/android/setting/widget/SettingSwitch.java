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
package universum.studios.android.setting.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import universum.studios.android.setting.SettingSwitchPreference;
import universum.studios.android.setting.R;
import universum.studios.android.ui.widget.SwitchWidget;

/**
 * A {@link SwitchWidget} implementation that is used as widget for {@link SettingSwitchPreference}.
 * <p>
 * This extended switch widget fixes a common issue when the thumb is not being animated when checked
 * state of the switch is changed due to {@link #isShown()} method returning always {@code false}.
 * The incorporated fix overrides behavior of {@link #isShown()} method in a way that it returns
 * {@code true} whenever the current visibility of the switch widget is {@link View#VISIBLE}.
 * <p>
 * <b>Note, that due to workaround described above, this switch widget should be only used for purpose
 * of preference layouts. For application layouts use rather {@link SwitchWidget}.</b>
 *
 * <h3>Xml attributes</h3>
 * See {@link SwitchWidget}
 *
 * <h3>Default style attribute</h3>
 * {@link R.attr#switchStyle switchStyle}
 *
 * @author Martin Albedinsky
 */
public class SettingSwitch extends SwitchWidget {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingSwitch";

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
	 * Same as {@link #SettingSwitch(Context, AttributeSet)} without attributes.
	 */
	public SettingSwitch(@NonNull Context context) {
		this(context, null);
	}

	/**
	 * Same as {@link #SettingSwitch(Context, AttributeSet, int)} with {@link  R.attr#switchStyle}
	 * as attribute for default style.
	 */
	public SettingSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, R.attr.switchStyle);
	}

	/**
	 * Same as {@link #SettingSwitch(Context, AttributeSet, int, int)} with {@code 0} as default style.
	 */
	public SettingSwitch(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * Creates a new instance of SettingSwitch for the given <var>context</var>.
	 *
	 * @param context      Context in which will be the new view presented.
	 * @param attrs        Set of Xml attributes used to configure the new instance of this view.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     this view within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the new view.
	 */
	@SuppressWarnings("unused")
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SettingSwitch(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(SettingSwitch.class.getName());
	}

	/**
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
		info.setClassName(SettingSwitch.class.getName());
	}

	/**
	 * @return {@code True} whenever the current visibility of this widget is {@link View#VISIBLE}.
	 *
	 * @see #getVisibility()
	 */
	@Override
	public boolean isShown() {
		return getVisibility() == VISIBLE;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}