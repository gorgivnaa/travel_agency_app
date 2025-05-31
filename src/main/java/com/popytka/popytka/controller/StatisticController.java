package com.popytka.popytka.controller;

import com.popytka.popytka.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticController {

    private final ExcelExportService excelExportService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showStatistics() {
        return "statistics/all-statistic";
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=manager-tour-statistics.xlsx");

        ByteArrayInputStream stream = excelExportService.generateManagerTourReport();
        stream.transferTo(response.getOutputStream());
    }
}
