package com.popytka.popytka.controller;

import com.popytka.popytka.config.security.CustomUserDetails;
import com.popytka.popytka.entity.User;
import com.popytka.popytka.service.ExcelExportService;
import com.popytka.popytka.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticController {

    private final UserService userService;
    private final ExcelExportService excelExportService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showStatistics(Authentication authentication, ModelMap model) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            List<User> managers = userService.getByRoleName("MANAGER");
            model.addAttribute("managers", managers);
        }
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
