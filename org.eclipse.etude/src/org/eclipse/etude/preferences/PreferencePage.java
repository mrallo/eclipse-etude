package org.eclipse.etude.preferences;

import org.eclipse.etude.EtudePlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public final class PreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public static final String LINE_COLOR = "etude.lineColor";

    public static final String[] RULED = new String[] { "Ruled", "ruled" };

    public static final String SHOW_AS = "etude.showLinesAs";

    public static final String[] SQUARED = new String[] { "Squared", "squared" };

    public static final String TOGGLE_PLUGIN = "etude.toggleFineLines";

    public PreferencePage() {
        super(GRID);
        setPreferenceStore(EtudePlugin.getDefault().getPreferenceStore());
        setDescription("Print fine lines in text editors.");
    }

    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        addField(new BooleanFieldEditor(TOGGLE_PLUGIN, "Show fine lines",
                parent));
        addField(new ComboFieldEditor(SHOW_AS, "Show as:", new String[][] {
                SQUARED, RULED }, parent));
        addField(new ColorFieldEditor(LINE_COLOR, "Line color:", parent));
    }

    public void init(IWorkbench workbench) {
    }

}