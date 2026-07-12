package com.carehub.carehub.controller;

import com.carehub.carehub.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    private ResponseEntity<byte[]> file(byte[] bytes, String filename, MediaType type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return ResponseEntity.ok().headers(headers).contentType(type).body(bytes);
    }

    @GetMapping("/inventory/pdf")
    public ResponseEntity<byte[]> inventoryPdf() throws Exception {
        return file(reportService.inventoryPdf(), "carehub-inventory-report.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/inventory/excel")
    public ResponseEntity<byte[]> inventoryExcel() throws Exception {
        return file(reportService.inventoryExcel(), "carehub-inventory-report.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @GetMapping("/patients/pdf")
    public ResponseEntity<byte[]> patientsPdf() throws Exception {
        return file(reportService.patientsPdf(), "carehub-patients-report.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/patients/excel")
    public ResponseEntity<byte[]> patientsExcel() throws Exception {
        return file(reportService.patientsExcel(), "carehub-patients-report.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @GetMapping("/doctors/pdf")
    public ResponseEntity<byte[]> doctorsPdf() throws Exception {
        return file(reportService.doctorsPdf(), "carehub-doctors-report.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/doctors/excel")
    public ResponseEntity<byte[]> doctorsExcel() throws Exception {
        return file(reportService.doctorsExcel(), "carehub-doctors-report.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @GetMapping("/revenue/pdf")
    public ResponseEntity<byte[]> revenuePdf() throws Exception {
        return file(reportService.revenuePdf(), "carehub-revenue-report.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/revenue/excel")
    public ResponseEntity<byte[]> revenueExcel() throws Exception {
        return file(reportService.revenueExcel(), "carehub-revenue-report.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }
}
