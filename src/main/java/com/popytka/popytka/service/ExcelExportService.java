package com.popytka.popytka.service;

import com.popytka.popytka.entity.ManagerTour;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.repository.ManagerTourRepository;
import com.popytka.popytka.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final ManagerTourRepository managerTourRepository;
    private final OrderRepository orderRepository;

    public ByteArrayInputStream generateManagerTourReport() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Статистика по менеджерам");

        // Заголовок таблицы
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Менеджер");
        header.createCell(1).setCellValue("Тур");
        header.createCell(2).setCellValue("Количество заявок");

        int rowIdx = 1;

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        List<ManagerTour> managerTours = managerTourRepository.findAll();
        for (ManagerTour mt : managerTours) {
            User manager = mt.getManager();
            Tour tour = mt.getTour();
            long orderCount = orderRepository.countByTour(tour);

            String managerName = manager.getLastName() + " " + manager.getFirstName();
            String label = managerName + " – " + tour.getTitle();

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(managerName);
            row.createCell(1).setCellValue(tour.getTitle());
            row.createCell(2).setCellValue(orderCount);

            labels.add(label);
            values.add((double) orderCount);
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // График
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, rowIdx + 2, 10, rowIdx + 20);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Количество заявок по турам и менеджерам");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromArray(labels.toArray(new String[0]));
        XDDFNumericalDataSource<Double> data = XDDFDataSourcesFactory.fromArray(values.toArray(new Double[0]));

        // Добавление осей
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Менеджер – Тур");

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Количество заявок");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFChartData chartData = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series = chartData.addSeries(categories, data);
        series.setTitle("Количество заявок", null);
        chart.plot(chartData);

        // Поворот графика (горизонтальная гистограмма)
        ((XDDFBarChartData) chartData).setBarDirection(BarDirection.BAR);

        XDDFBarChartData bar = (XDDFBarChartData) chartData;
        bar.setBarDirection(BarDirection.BAR);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
