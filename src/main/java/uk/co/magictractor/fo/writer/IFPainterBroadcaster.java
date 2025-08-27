/**
 * Copyright 2025 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.fo.writer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.render.intermediate.IFException;
import org.apache.fop.render.intermediate.IFPainter;
import org.apache.fop.traits.BorderProps;
import org.apache.fop.traits.RuleStyle;
import org.w3c.dom.Document;

public class IFPainterBroadcaster implements IFPainter {

    private static final Log LOG = LogFactory.getLog(IFPainterBroadcaster.class);

    private final List<IFPainter> painters;

    public IFPainterBroadcaster(List<IFPainter> painters) {
        this.painters = painters;
    }

    @Override
    public void startViewport(AffineTransform transform, Dimension size, Rectangle clipRect) throws IFException {
        LOG.trace("startViewport");
        for (IFPainter painter : painters) {
            painter.startViewport(transform, size, clipRect);
        }
    }

    @Override
    public void startViewport(AffineTransform[] transforms, Dimension size, Rectangle clipRect) throws IFException {
        LOG.trace("startViewport");
        for (IFPainter painter : painters) {
            painter.startViewport(transforms, size, clipRect);
        }
    }

    @Override
    public void endViewport() throws IFException {
        LOG.trace("endViewport");
        for (IFPainter painter : painters) {
            painter.endViewport();
        }
    }

    @Override
    public void startGroup(AffineTransform[] transforms, String layer) throws IFException {
        LOG.trace("startGroup");
        for (IFPainter painter : painters) {
            painter.startGroup(transforms, layer);
        }
    }

    @Override
    public void startGroup(AffineTransform transform, String layer) throws IFException {
        LOG.trace("startGroup");
        for (IFPainter painter : painters) {
            painter.startGroup(transform, layer);
        }
    }

    @Override
    public void endGroup() throws IFException {
        LOG.trace("endGroup");
        for (IFPainter painter : painters) {
            painter.endGroup();
        }
    }

    @Override
    public void setFont(String family, String style, Integer weight, String variant, Integer size, Color color) throws IFException {
        LOG.trace("setFont");
        for (IFPainter painter : painters) {
            painter.setFont(family, style, weight, variant, size, color);
        }
    }

    @Override
    public void drawText(int x, int y, int letterSpacing, int wordSpacing, int[][] dp, String text) throws IFException {
        LOG.trace("drawText");
        for (IFPainter painter : painters) {
            painter.drawText(x, y, letterSpacing, wordSpacing, dp, text);
        }
    }

    @Override
    public void drawText(int x, int y, int letterSpacing, int wordSpacing, int[][] dp, String text, boolean nextIsSpace) throws IFException {
        LOG.trace("drawText");
        for (IFPainter painter : painters) {
            painter.drawText(x, y, letterSpacing, wordSpacing, dp, text, nextIsSpace);
        }
    }

    @Override
    public void clipRect(Rectangle rect) throws IFException {
        LOG.trace("clipRect");
        for (IFPainter painter : painters) {
            painter.clipRect(rect);
        }
    }

    @Override
    public void clipBackground(Rectangle rect, BorderProps bpsBefore, BorderProps bpsAfter, BorderProps bpsStart, BorderProps bpsEnd) throws IFException {
        LOG.trace("clipBackground");
        for (IFPainter painter : painters) {
            painter.clipBackground(rect, bpsBefore, bpsAfter, bpsStart, bpsEnd);
        }
    }

    @Override
    public boolean isBackgroundRequired(BorderProps bpsBefore, BorderProps bpsAfter, BorderProps bpsStart, BorderProps bpsEnd) {
        // Needs investigation. Maybe true if any painter is true.
        // throw new UnsupportedOperationException();

        boolean isBackgroundRequired = painters.get(0).isBackgroundRequired(bpsBefore, bpsAfter, bpsStart, bpsEnd);
        for (int i = 1; i < painters.size(); i++) {
            if (isBackgroundRequired != painters.get(i).isBackgroundRequired(bpsBefore, bpsAfter, bpsStart, bpsEnd)) {
                throw new IllegalStateException("Inconsistent values for isBackgroundRequired()");
            }
        }

        return isBackgroundRequired;
    }

    @Override
    public void fillRect(Rectangle rect, Paint fill) throws IFException {
        LOG.trace("fillRect");
        for (IFPainter painter : painters) {
            painter.fillRect(rect, fill);
        }
    }

    @Override
    public void drawBorderRect(Rectangle rect, BorderProps top, BorderProps bottom, BorderProps left, BorderProps right, Color innerBackgroundColor) throws IFException {
        LOG.trace("drawBorderRect");
        for (IFPainter painter : painters) {
            painter.drawBorderRect(rect, top, bottom, left, right, innerBackgroundColor);
        }
    }

    @Override
    public void drawLine(Point start, Point end, int width, Color color, RuleStyle style) throws IFException {
        LOG.trace("drawLine");
        for (IFPainter painter : painters) {
            painter.drawLine(start, end, width, color, style);
        }
    }

    @Override
    public void drawImage(String uri, Rectangle rect) throws IFException {
        LOG.trace("drawImage");
        for (IFPainter painter : painters) {
            painter.drawImage(uri, rect);
        }
    }

    @Override
    public void drawImage(Document doc, Rectangle rect) throws IFException {
        LOG.trace("drawImage");
        for (IFPainter painter : painters) {
            painter.drawImage(doc, rect);
        }
    }

}
