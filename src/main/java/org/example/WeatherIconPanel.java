package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Draws a simple, clean weather icon (sun / cloud / rain / storm / snow / fog)
 * using plain Java2D shapes, so the app doesn't depend on any external
 * icon images or internet access for graphics.
 */
public class WeatherIconPanel extends JPanel {

    private String condition = "Clear"; // OpenWeatherMap "main" field

    public WeatherIconPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(120, 120));
    }

    public void setCondition(String condition) {
        this.condition = condition == null ? "Clear" : condition;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        switch (condition) {
            case "Clear" -> drawSun(g2, w, h);
            case "Clouds" -> drawCloud(g2, w, h, new Color(176, 190, 197));
            case "Rain", "Drizzle" -> drawRain(g2, w, h);
            case "Thunderstorm" -> drawThunder(g2, w, h);
            case "Snow" -> drawSnow(g2, w, h);
            case "Mist", "Fog", "Haze", "Smoke", "Dust", "Sand" -> drawFog(g2, w, h);
            default -> drawCloud(g2, w, h, new Color(176, 190, 197));
        }

        g2.dispose();
    }

    private void drawSun(Graphics2D g2, int w, int h) {
        int cx = w / 2, cy = h / 2;
        int r = Math.min(w, h) / 5;

        g2.setColor(new Color(255, 183, 3));
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            int x1 = cx + (int) ((r + 8) * Math.cos(angle));
            int y1 = cy + (int) ((r + 8) * Math.sin(angle));
            int x2 = cx + (int) ((r + 22) * Math.cos(angle));
            int y2 = cy + (int) ((r + 22) * Math.sin(angle));
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(new Color(255, 202, 58));
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
    }

    private void drawCloud(Graphics2D g2, int w, int h, Color color) {
        int cx = w / 2, cy = h / 2 + 8;
        g2.setColor(color);
        g2.fillOval(cx - 32, cy - 10, 40, 40);
        g2.fillOval(cx - 8, cy - 26, 50, 50);
        g2.fillOval(cx + 22, cy - 8, 42, 42);
        g2.fillRoundRect(cx - 32, cy + 6, 86, 26, 20, 20);
    }

    private void drawRain(Graphics2D g2, int w, int h) {
        drawCloud(g2, w, h - 14, new Color(120, 144, 156));
        g2.setColor(new Color(66, 165, 245));
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cy = h / 2 + 8;
        int[] xs = {w / 2 - 22, w / 2, w / 2 + 22};
        for (int x : xs) {
            g2.drawLine(x, cy + 22, x - 6, cy + 38);
        }
    }

    private void drawThunder(Graphics2D g2, int w, int h) {
        drawCloud(g2, w, h - 14, new Color(96, 125, 139));
        g2.setColor(new Color(255, 202, 40));
        int cx = w / 2;
        int cy = h / 2 + 20;
        Polygon bolt = new Polygon();
        bolt.addPoint(cx + 6, cy);
        bolt.addPoint(cx - 10, cy + 22);
        bolt.addPoint(cx, cy + 22);
        bolt.addPoint(cx - 8, cy + 44);
        bolt.addPoint(cx + 14, cy + 16);
        bolt.addPoint(cx + 2, cy + 16);
        g2.fillPolygon(bolt);
    }

    private void drawSnow(Graphics2D g2, int w, int h) {
        drawCloud(g2, w, h - 14, new Color(176, 190, 197));
        g2.setColor(Color.WHITE);
        int cy = h / 2 + 30;
        int[] xs = {w / 2 - 22, w / 2, w / 2 + 22};
        for (int x : xs) {
            g2.fillOval(x - 4, cy - 4, 8, 8);
        }
    }

    private void drawFog(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(176, 190, 197));
        g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cy = h / 2 - 10;
        for (int i = 0; i < 4; i++) {
            int lineW = (i % 2 == 0) ? 70 : 50;
            g2.drawLine(w / 2 - lineW / 2, cy + i * 16, w / 2 + lineW / 2, cy + i * 16);
        }
    }
}
