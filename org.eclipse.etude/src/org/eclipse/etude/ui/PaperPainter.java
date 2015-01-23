package org.eclipse.etude.ui;

import org.eclipse.etude.EtudePlugin;
import org.eclipse.etude.preferences.PreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

abstract class PaperPainter implements IPainter, PaintListener, DisposeListener {

    private final class FontChangeListener implements IPropertyChangeListener {

        private void activate() {
            JFaceResources.getFontRegistry().addListener(this);
        }

        private void deactivate() {
            JFaceResources.getFontRegistry().removeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            paint(CONFIGURATION);
        }

    }

    private final class PropertyListener implements IPropertyListener {

        private void activate() {
            initializePaintColor();
            editor.addPropertyListener(this);
        }

        private void deactivate() {
            editor.removePropertyListener(this);
        }

        private void initializePaintColor() {
            if (editor.isDirty())
                paintColor = CHANGE_COLOR;
            else
                paintColor = lineColor;
        }

        @Override
        public void propertyChanged(Object source, int propId) {
            initializePaintColor();

            Display.getCurrent().asyncExec(new Runnable() {
                @Override
                public void run() {
                    redrawAll(TEXT_CHANGE);
                }
            });
        }
    }

    private static final Color CHANGE_COLOR;

    static {
        CHANGE_COLOR = new Color(Display.getCurrent(), 204, 163, 205);
    }

    public static PaperPainter newInstance(ITextViewer viewer) {
        IPreferenceStore store = EtudePlugin.getDefault().getPreferenceStore();
        String painterKey = store.getString(PreferencePage.SHOW_AS);
        PaperPainter painter = null;

        if (PreferencePage.RULED[1].equals(painterKey))
            painter = new RuledPainter(viewer);
        else if (PreferencePage.SQUARED[1].equals(painterKey))
            painter = new GraphPainter(viewer);

        return painter;
    }

    private IEditorPart editor;

    private final FontChangeListener fontChangeListener;

    private boolean isActive;

    private Color lineColor;

    protected Color paintColor;

    private final PropertyListener propertyListener;

    protected Rectangle square;

    private ITextViewer textViewer;

    protected final StyledText textWidget;

    PaperPainter(ITextViewer viewer) {
        textViewer = viewer;
        textWidget = viewer.getTextWidget();
        fontChangeListener = new FontChangeListener();
        propertyListener = new PropertyListener();
    }

    private void computeMetrics(int reason) {
        if (square == null || reason == CONFIGURATION || reason == INTERNAL) {
            final GC gc = new GC(textWidget);
            int width = gc.getFontMetrics().getAverageCharWidth() * 2;
            int height = textWidget.getLineHeight();
            square = new Rectangle(0, 0, width, height);
            gc.dispose();
        }
    }

    @Override
    public final void deactivate(boolean redraw) {
        if (isActive) {
            isActive = false;
            textWidget.removePaintListener(this);
            textWidget.removeDisposeListener(this);
            fontChangeListener.deactivate();
            propertyListener.deactivate();
            if (redraw)
                redrawAll(CONFIGURATION);
        }
    }

    @Override
    public void dispose() {
    }

    protected abstract void draw(GC gc, int x, int y, int width, int height);

    @Override
    public final void paint(final int reason) {
        if (isActive) {
            if (reason == CONFIGURATION || reason == INTERNAL)
                redrawAll(reason);
        } else {
            isActive = true;
            fontChangeListener.activate();
            propertyListener.activate();
            textWidget.addPaintListener(this);
            textWidget.addDisposeListener(this);
            redrawAll(reason);
        }
    }

    @Override
    public final void paintControl(PaintEvent e) {
        if (textWidget != null)
            draw(e.gc, e.x, e.y, e.width, e.height);
    }

    private void redrawAll(int reason) {
        computeMetrics(reason);
        textWidget.redraw();
    }

    public final void setEditor(IEditorPart part) {
        editor = part;
    }

    public final void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    @Override
    public final void setPositionManager(IPaintPositionManager manager) {
    }

    @Override
    public final void widgetDisposed(DisposeEvent e) {
        ((ITextViewerExtension2) textViewer).removePainter(this);
    }

}