package org.eclipse.etude.ui;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

final class GraphPainter extends PaperPainter {

    GraphPainter(ITextViewer viewer) {
        super(viewer);
    }

    protected void draw(GC gc, int x, int y, int width, int height) {
        gc.setForeground(paintColor);
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setLineWidth(0);
        gc.setAlpha(0x60);

        final int startLineY = textWidget.getLinePixel(textWidget
                .getLineIndex(y));

        for (int v = startLineY; v <= y + height; v += square.height) {
            gc.drawLine(x, v, x + width, v);
        }

        final int offsetX = textWidget.getHorizontalPixel();
        final int startLineX = square.width * Math.max(1, x / square.width)
                - offsetX;

        for (int w = startLineX; w <= x + width; w += square.width)
            gc.drawLine(w, y, w, y + height);
    }
}