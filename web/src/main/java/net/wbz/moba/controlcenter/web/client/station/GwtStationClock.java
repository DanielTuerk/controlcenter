package net.wbz.moba.controlcenter.web.client.station;
// Copyright 2015 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland
// www.source-code.biz, www.inventec.ch/chdh
//
// This module is multi-licensed and may be used under the terms of any of the following licenses:
//
//  LGPL, GNU Lesser General Public License, V2.1 or later, http://www.gnu.org/licenses/lgpl.html
//  EPL, Eclipse Public License, V1.0 or later, http://www.eclipse.org/legal
//
// Please contact the author if you need another license.
// This module is provided "as is", without warranties of any kind.
//
// Home page: http://www.source-code.biz/snippets/java

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;
import java.util.Date;

public class GwtStationClock extends SimplePanel {

    private CanvasElement clockCanvas;
    private CanvasElement staticImageCanvas;

    public GwtStationClock() {
        clockCanvas = Canvas.createIfSupported().getCanvasElement();
        clockCanvas.setWidth(200);
        clockCanvas.setHeight(200);
        clockCanvas.getStyle().setProperty("maxWidth",100, Unit.PCT);
        if (clockCanvas == null) {
            throw new RuntimeException("Clock canvas element not found.");
        }
        getElement().appendChild(clockCanvas);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        paintClock();
        scheduleClockPainting();

    }

    private void scheduleClockPainting() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                paintClock();
                scheduleClockPainting();
            }
        };
        int delay = 1000 - (int) (System.currentTimeMillis() % 1000);
        timer.schedule(delay);
    }

    private void paintClock() {
        if (staticImageCanvas == null || staticImageCanvas.getWidth() != clockCanvas.getWidth()
            || staticImageCanvas.getHeight() != clockCanvas.getHeight()) {
            staticImageCanvas = Document.get().createCanvasElement();
            staticImageCanvas.setWidth(clockCanvas.getWidth());
            staticImageCanvas.setHeight(clockCanvas.getHeight());
            ClockImageGenerator.drawStaticImage(staticImageCanvas);
        }
        clockCanvas.getContext2d().drawImage(staticImageCanvas, 0, 0);
        ClockImageGenerator.drawDynamicImage(clockCanvas);
    }

//------------------------------------------------------------------------------

    private static class ClockImageGenerator {

        private static final String clockLabel = "source-code.biz";
        private static final String transparentColor = "rgba(0,0,0,0)";
        private static final String whiteColor = "#FFFFFF";
        private static final String blackColor = "#000000";
        private static final String borderColor = "#C0C0C0";
        private static final String secPtrColor = "#CC0000";

        private Context2d context;
        private double width;
        private double height;
        private double r;
        private double centerX;
        private double centerY;

        private ClockImageGenerator(CanvasElement canvas) {
            context = canvas.getContext2d();
            width = canvas.getWidth();
            height = canvas.getHeight();
            r = Math.min(width, height) * 7 / 16;
            centerX = width / 2;
            centerY = height / 2;
        }

        public static void drawStaticImage(CanvasElement canvas) {
            new ClockImageGenerator(canvas).drawStaticImage();
        }

        public static void drawDynamicImage(CanvasElement canvas) {
            new ClockImageGenerator(canvas).drawDynamicImage();
        }

        private void drawStaticImage() {
            // Paint outer background.
            context.save();
            context.setFillStyle(transparentColor);
            context.fillRect(0, 0, width, height);
            context.restore();
            // Paint inner background and border.
            double borderWidth = r / 54;
            context.save();
            context.beginPath();
            context.arc(centerX, centerY, r + borderWidth / 2, 0, 2 * Math.PI);
            context.setFillStyle(whiteColor);
            context.fill();
            context.setLineWidth(borderWidth);
            context.setStrokeStyle(borderColor);
            context.stroke();
            context.restore();
            // Draw 60 clock marks.
            for (int i = 0; i < 60; i++) {
                boolean big = i % 5 == 0;
                double rLength = big ? r * 10 / 44 : r * 3 / 44;
                double rWidth = big ? r * 3 / 44 : r / 44;
                double r2 = r * 42 / 44;
                double angle = 2 * Math.PI / 60 * i;
                drawRadial(angle, r2 - rLength, r2, rWidth, rWidth, blackColor);
            }
            // Draw text.
            drawClockLabel();
        }

        @SuppressWarnings("deprecation")
        private void drawDynamicImage() {
            Date date = new Date();
            int hour = date.getHours();
            int min = date.getMinutes();
            int sec = date.getSeconds();
            drawRadial(2 * Math.PI * ((hour % 12) * 60 + min) / (12 * 60), -r * 10 / 44, r * 27 / 44, r * 5 / 44,
                r * 4 / 44, blackColor);
            drawRadial(2 * Math.PI * min / 60, -r * 10 / 44, r * 40 / 44, r * 9 / 88, r * 3 / 44, blackColor);
            drawRadial(2 * Math.PI * sec / 60, -r * 14 / 44, r * 27 / 44, r / 44, r / 44, secPtrColor);
            drawRadialFilledCircle(2 * Math.PI / 60 * sec, r * 27 / 44, r * 9 / 88, secPtrColor);
        }

        private void drawRadial(double alpha, double r1, double r2, double width1, double width2, String color) {
            double sin = Math.sin(alpha);
            double cos = Math.cos(alpha);
            double pm1X = centerX + sin * r1;
            double pm1Y = centerY - cos * r1;
            double pm2X = centerX + sin * r2;
            double pm2Y = centerY - cos * r2;
            double px[] = new double[4];
            double py[] = new double[4];
            px[0] = pm1X - cos * width1 / 2;
            py[0] = pm1Y - sin * width1 / 2;
            px[3] = pm1X + cos * width1 / 2;
            py[3] = pm1Y + sin * width1 / 2;
            px[1] = pm2X - cos * width2 / 2;
            py[1] = pm2Y - sin * width2 / 2;
            px[2] = pm2X + cos * width2 / 2;
            py[2] = pm2Y + sin * width2 / 2;
            drawFilledPolygon(px, py, color);
        }

        private void drawFilledPolygon(double[] px, double[] py, String color) {
            context.save();
            context.beginPath();
            context.moveTo(px[0], py[0]);
            for (int i = 1; i < px.length; i++) {
                context.lineTo(px[i], py[i]);
            }
            context.setFillStyle(color);
            context.fill();
            context.restore();
        }

        private void drawRadialFilledCircle(double alpha, double r1, double circR, String color) {
            double p0X = centerX + Math.sin(alpha) * r1;
            double p0Y = centerY - Math.cos(alpha) * r1;
            context.save();
            context.beginPath();
            context.arc(p0X, p0Y, circR, 0, 2 * Math.PI);
            context.setFillStyle(color);
            context.fill();
            context.restore();
        }

        private void drawClockLabel() {
            context.save();
            long fontSize = Math.round(r * 20 / 200);
            context.setFont(fontSize + "px Helvetica, sans-serif");
            context.setTextAlign(Context2d.TextAlign.CENTER);
            context.setTextBaseline(Context2d.TextBaseline.MIDDLE);
            context.setFillStyle(borderColor);
            context.fillText(clockLabel, centerX, centerY + r / 2);
            context.restore();
        }

    } // end class ClockImageGenerator
} // end class GwtStationClock
