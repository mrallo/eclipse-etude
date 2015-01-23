package org.eclipse.etude;

import org.eclipse.etude.ui.PluginUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public final class EtudePlugin extends AbstractUIPlugin {

    private static EtudePlugin plugin;

    public static final String PLUGIN_ID = "org.eclipse.etude"; //$NON-NLS-1$

    public static EtudePlugin getDefault() {
        return plugin;
    }

    private PluginUI pluginUi;

    public EtudePlugin() {
    }

    public PluginUI getPluginUI() {
        return pluginUi;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        pluginUi = new PluginUI(plugin);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        pluginUi = null;
        super.stop(context);
    }

}