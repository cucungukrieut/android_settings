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
package universum.studios.android.samples.setting.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.List;

import universum.studios.android.samples.setting.R;
import universum.studios.android.setting.SettingHeadersAdapter;
import universum.studios.android.setting.SettingsBaseActivity;

/**
 * @author Martin Albedinsky
 */
public final class SettingsActivity extends SettingsBaseActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "SettingsActivity";

	@Override
	@SuppressWarnings("ConstantConditions")
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addToolbar();
		final ListAdapter headersAdapter = getHeadersAdapter();
		if (headersAdapter instanceof SettingHeadersAdapter) {
			((SettingHeadersAdapter) headersAdapter).setUseVectorIcons(true);
		}
	}

	@NonNull
	@Override
	protected Toolbar onCreateToolbar(@NonNull LayoutInflater inflater, @NonNull ViewGroup root) {
		return (Toolbar) inflater.inflate(R.layout.samples_toolbar, root, false);
	}

	@Override
	public void onBuildHeaders(List<PreferenceActivity.Header> target) {
		super.onBuildHeaders(target);
		loadHeadersFromResource(R.xml.settings, target);
	}

	@Override
	protected boolean isValidFragment(String fragmentName) {
		return true;
	}
}
