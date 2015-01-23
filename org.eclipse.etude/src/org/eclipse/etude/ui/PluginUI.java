package org.eclipse.etude.ui;

import static org.eclipse.etude.ui.UIEvent.CLOSE;
import static org.eclipse.etude.ui.UIEvent.OPEN;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.etude.preferences.PreferencePage;
import org.eclipse.jface.menus.IMenuStateIds;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public final class PluginUI {

    private final class PartListener extends AbstractPartListener {

        private Map<ITextViewer, IPainter> viewers;

        private PartListener() {
            viewers = new HashMap<ITextViewer, IPainter>();
        }

        @Override
        public void partClosed(IWorkbenchPart part) {
            if (part instanceof IEditorPart) {
                ITextViewer viewer = getTextViewer((IEditorPart) part);

                if (viewer instanceof ITextViewerExtension2) {
                    IPainter painter = viewers.get(viewer);
                    if (painter != null)
                        ((ITextViewerExtension2) viewer).removePainter(painter);
                    viewers.remove(viewer);
                }
            }
        }

        @Override
        public void partOpened(IWorkbenchPart part) {
            if (part instanceof IEditorPart) {
                ITextViewer viewer = getTextViewer((IEditorPart) part);

                if (viewer instanceof ITextViewerExtension2) {
                    PaperPainter painter = PaperPainter.newInstance(viewer);
                    painter.setEditor((IEditorPart) part);
                    painter.setLineColor(getColor(PreferencePage.LINE_COLOR));
                    ((ITextViewerExtension2) viewer).addPainter(painter);
                    viewers.put(viewer, painter);
                }
            }
        }

    }

    private final class PreferenceChangeListener implements
            IPropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            final String preferenceKey = event.getProperty();

            switch (preferenceKey) {
            case PreferencePage.LINE_COLOR:
                if (isActive) {
                    toggleStateWhen(false);
                    toggleStateWhen(true);
                }
                break;
            case PreferencePage.SHOW_AS:
                if (isActive) {
                    toggleStateWhen(false);
                    toggleStateWhen(true);
                }
                break;
            case PreferencePage.TOGGLE_PLUGIN:
                boolean isChecked;
                if (event.getNewValue() instanceof Boolean)
                    isChecked = (Boolean) event.getNewValue();
                else
                    isChecked = Boolean.valueOf(event.getNewValue().toString());
                toggleStateWhen(isChecked);
            }
        }

    }

    private final class WindowListener extends AbstractWindowListener {

        @Override
        protected void when(IWorkbenchWindow window, UIEvent event) {
            final IWorkbenchPage[] pages = window.getPages();

            for (IWorkbenchPage page : pages) {
                IEditorReference[] editorRefs = page.getEditorReferences();

                for (IEditorReference editorRef : editorRefs) {
                    IEditorPart editor = editorRef.getEditor(false);
                    if (editor != null)
                        if (event.equals(OPEN))
                            partListener.partOpened(editor);
                        else if (event.equals(CLOSE))
                            partListener.partClosed(editor);
                }
            }

            switch (event) {
            case OPEN:
                window.getPartService().addPartListener(partListener);
                break;
            case CLOSE:
                window.getPartService().removePartListener(partListener);
            }
        }

    }

    private boolean isActive;

    private final PartListener partListener;

    private final AbstractUIPlugin plugin;

    private IPreferenceStore preferences;

    private final WindowListener windowListener;

    public PluginUI(AbstractUIPlugin plugin) {
        this.plugin = plugin;
        windowListener = new WindowListener();
        partListener = new PartListener();
        preferences = plugin.getPreferenceStore();
        preferences.addPropertyChangeListener(new PreferenceChangeListener());
    }

    private void allWindowsDoWhen(final boolean install) {
        final IWorkbench workbench = plugin.getWorkbench();
        final IWorkbenchWindow[] openWindows;

        openWindows = workbench.getWorkbenchWindows();

        workbench.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                for (IWorkbenchWindow window : openWindows) {
                    if (install)
                        windowListener.windowOpened(window);
                    else
                        windowListener.windowClosed(window);
                }
                workbench.addWindowListener(windowListener);
            }
        });
    }

    private Color getColor(String lineColorKey) {
        RGB rgb = PreferenceConverter.getColor(preferences, lineColorKey);
        return EditorsUI.getSharedTextColors().getColor(rgb);
    }

    private ITextViewer getTextViewer(IEditorPart editor) {
        ITextOperationTarget target = (ITextOperationTarget) editor
                .getAdapter(ITextOperationTarget.class);
        if (target instanceof ITextViewer)
            return (ITextViewer) target;

        return null;
    }

    public void toggleCommandState(boolean checked) {
        ICommandService service;
        Command command;
        State state;

        service = plugin.getWorkbench().getService(ICommandService.class);
        command = service.getCommand("org.eclipse.etude.toggleShowFineLines");
        state = command.getState(IMenuStateIds.STYLE);
        state.setValue((Boolean) checked);
    }

    public void toggleStateWhen(boolean isChecked) {
        allWindowsDoWhen(isChecked);
        toggleCommandState(isChecked);
        isActive = isChecked;
    }

}