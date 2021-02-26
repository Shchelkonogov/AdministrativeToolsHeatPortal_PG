package ru.tecon.admTools.dataAnalysis.report;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

public final class BorderUtil {

    private BorderUtil() {
    }

    /**
     * Синхронизированный метод, чтобы рисовать границы
     * @param borderType тип границ
     * @param borderStyle стиль границ
     * @param cellAddresses адреса ячеек
     * @param sheet excel страница
     */
    public static synchronized void createBorder(String borderType, BorderStyle borderStyle,
                                                 CellRangeAddress cellAddresses, Sheet sheet) {
        switch (borderType) {
            case "top":
                RegionUtil.setBorderTop(borderStyle, cellAddresses, sheet);
                break;
            case "right":
                RegionUtil.setBorderRight(borderStyle, cellAddresses, sheet);
                break;
            case "bottom":
                RegionUtil.setBorderBottom(borderStyle, cellAddresses, sheet);
                break;
            case "left":
                RegionUtil.setBorderLeft(borderStyle, cellAddresses, sheet);
                break;
        }
    }
}
