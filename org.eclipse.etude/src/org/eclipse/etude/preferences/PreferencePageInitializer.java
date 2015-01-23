package org.eclipse.etude.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.etude.EtudePlugin;

public final class PreferencePageInitializer extends
        AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        IPreferenceStore store = EtudePlugin.getDefault().getPreferenceStore();
        store.setDefault(PreferencePage.TOGGLE_PLUGIN, true);
        store.setDefault(PreferencePage.SHOW_AS, PreferencePage.SQUARED[1]);
        PreferenceConverter.setDefault(store,
                PreferencePage.LINE_COLOR, new RGB(188, 188, 222));
    }

}
