package scheper.mateus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import scheper.mateus.dto.ReportDTO;
import scheper.mateus.service.AdminService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportar(@RequestBody @Valid ReportDTO reportDTO, HttpServletRequest request) {
        adminService.reportar(reportDTO, request);
    }
}
