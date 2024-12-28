package org.example.progressbar;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class StockProgressBarRenderer extends JProgressBar implements TableCellRenderer {
    public StockProgressBarRenderer() {
        setStringPainted(true); // Yüzdeyi göstermek için
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (value instanceof Integer) {
            int stockPercentage = (int) value;
            setValue(stockPercentage);

            // UIManager ile renk değişimi
            if (stockPercentage < 20) {
                UIManager.put("ProgressBar.foreground", Color.RED);
            } else if (stockPercentage < 50) {
                UIManager.put("ProgressBar.foreground", Color.ORANGE);
            } else {
                UIManager.put("ProgressBar.foreground", Color.GREEN);
            }
            setForeground(UIManager.getColor("ProgressBar.foreground"));
            setString(stockPercentage + "%");
        }
        return this;
    }
}
