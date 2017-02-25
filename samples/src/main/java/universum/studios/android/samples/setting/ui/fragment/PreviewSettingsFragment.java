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
package universum.studios.android.samples.setting.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import universum.studios.android.dialog.Dialog;
import universum.studios.android.samples.setting.R;
import universum.studios.android.setting.SettingDialogPreferencesManager;
import universum.studios.android.setting.SettingsBaseFragment;

/**
 * @author Martin Albedinsky
 */
public final class PreviewSettingsFragment extends SettingsBaseFragment
		implements
		Dialog.OnDialogListener {

	@SuppressWarnings("unused")
	private static final String TAG = "PreviewSettingsFragment";

	private SettingDialogPreferencesManager dialogPreferencesManager;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_preview);
		this.dialogPreferencesManager = new SettingDialogPreferencesManager(this);
		this.dialogPreferencesManager.attachToPreferenceScreen(getPreferenceScreen());
	}

	@Override
	public boolean onDialogButtonClick(@NonNull Dialog dialog, int button) {
		return dialogPreferencesManager.handleOnPreferenceDialogButtonClick(dialog, button);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dialogPreferencesManager.detachFromPreferenceScreen(getPreferenceScreen());
	}
}
