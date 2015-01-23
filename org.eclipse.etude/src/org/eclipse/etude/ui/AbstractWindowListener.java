package org.eclipse.etude.ui;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

abstract class AbstractWindowListener implements IWindowListener {

    protected abstract void when(IWorkbenchWindow window, UIEvent event);

    @Override
    public void windowActivated(IWorkbenchWindow window) {
    }

    @Override
    public void windowClosed(IWorkbenchWindow window) {
        when(window, UIEvent.CLOSE);
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow window) {
    }

    @Override
    public void windowOpened(IWorkbenchWindow window) {
        when(window, UIEvent.OPEN);
    }

}