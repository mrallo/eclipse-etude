package org.eclipse.etude;

import static org.eclipse.etude.preferences.PreferencePage.TOGGLE_PLUGIN;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;

public class PluginStartup implements IStartup {

    @Override
    public void earlyStartup() {
        synchronizeWithPreferences();
    }

    private void synchronizeWithPreferences() {
        IPreferenceStore store;
        store = EtudePlugin.getDefault().getPreferenceStore();
        boolean isChecked = store.getBoolean(TOGGLE_PLUGIN);
        store.firePropertyChangeEvent(TOGGLE_PLUGIN, isChecked, isChecked);
    }

}
