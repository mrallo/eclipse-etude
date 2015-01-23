package org.eclipse.etude.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.etude.EtudePlugin;
import org.eclipse.etude.preferences.PreferencePage;
import org.eclipse.jface.menus.IMenuStateIds;
import org.eclipse.jface.preference.IPreferenceStore;

public final class ShowLinesHandler extends AbstractHandler {

    private final IPreferenceStore store;

    public ShowLinesHandler() {
        store = EtudePlugin.getDefault().getPreferenceStore();
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        boolean isChecked = isChecked(event.getCommand());
        store.setValue(PreferencePage.TOGGLE_PLUGIN, isChecked);
        return Void.TYPE;
    }

    private boolean isChecked(Command command) {
        final State commandState = command.getState(IMenuStateIds.STYLE);
        boolean isChecked = !(Boolean) commandState.getValue();
        return isChecked;
    }

}