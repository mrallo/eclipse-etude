package org.eclipse.etude.ui;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

final class RuledPainter extends PaperPainter {

    RuledPainter(ITextViewer viewer) {
        super(viewer);
    }

    @Override
    protected void draw(GC gc, int x, int y, int width, int height) {
        gc.setForeground(paintColor);
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setLineWidth(0);

        final int startLineY = textWidget.getLinePixel(textWidget
                .getLineIndex(y));

        for (int v = startLineY; v <= y + height; v += square.height)
            gc.drawLine(x, v, x + width, v);
    }

}
