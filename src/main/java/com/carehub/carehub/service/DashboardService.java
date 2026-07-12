package com.carehub.carehub.service;

import com.carehub.carehub.entity.Inventory;
import com.carehub.carehub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DashboardService {

    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private BillItemRepository billItemRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private AlertService alertService;
    @Autowired private AiService aiService;

    public Map<String, Object> summary() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPatients", patientRepository.count());
        stats.put("totalDoctors", doctorRepository.count());
        stats.put("totalMedicines", medicineRepository.count());
        stats.put("totalSuppliers", supplierRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalPrescriptions", prescriptionRepository.count());

        long totalStock = inventoryRepository.findAll().stream()
                .mapToLong(i -> i.getAvailableStock() == null ? 0 : i.getAvailableStock())
                .sum();
        stats.put("availableStock", totalStock);

        List<Inventory> lowStock = alertService.lowStock();
        stats.put("lowStockCount", lowStock.size());

        List<Inventory> expiring30 = alertService.expiringWithin(30);
        stats.put("expiringSoonCount", expiring30.size());

        stats.put("pendingBills", billRepository.countPendingBills());
        stats.put("totalRevenue", billRepository.totalRevenue());
        stats.put("pendingRevenue", billRepository.totalPending());
        stats.put("inventoryValue", inventoryRepository.totalInventoryValue());

        // AI notifications: medicines predicted to run out within 7 days
        long urgentPredictions = aiService.stockPredictions().stream()
                .filter(p -> p.get("daysUntilOutOfStock") != null && (int) p.get("daysUntilOutOfStock") <= 7)
                .count();
        stats.put("aiUrgentAlerts", urgentPredictions);

        return stats;
    }

    public List<Map<String, Object>> lowStockAlerts() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Inventory i : alertService.lowStock()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("inventoryId", i.getInventoryId());
            row.put("medicineName", i.getMedicine() != null ? i.getMedicine().getMedicineName() : "—");
            row.put("batchNo", i.getBatchNo());
            row.put("availableStock", i.getAvailableStock());
            row.put("minimumStock", i.getMinimumStock());
            row.put("expiryDate", i.getExpiryDate());
            row.put("supplier", i.getSupplier() != null ? i.getSupplier().getSupplierName() : null);
            out.add(row);
        }
        return out;
    }

    public List<Map<String, Object>> expiryAlerts(int days) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Inventory i : alertService.expiringWithin(days)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("inventoryId", i.getInventoryId());
            row.put("medicineName", i.getMedicine() != null ? i.getMedicine().getMedicineName() : "—");
            row.put("batchNo", i.getBatchNo());
            row.put("expiryDate", i.getExpiryDate());
            row.put("availableStock", i.getAvailableStock());
            out.add(row);
        }
        return out;
    }

    /** Chart data for the dashboard: revenue, patient growth, medicine usage, stock distribution. */
    public Map<String, Object> charts() {
        Map<String, Object> out = new LinkedHashMap<>();
        LocalDateTime sinceDT = LocalDateTime.now().minusMonths(6);
        LocalDate sinceD = LocalDate.now().minusMonths(6);

        List<Map<String, Object>> revenue = new ArrayList<>();
        for (Object[] row : billRepository.monthlyRevenueSince(sinceDT)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("month", row[0]);
            m.put("revenue", row[1]);
            revenue.add(m);
        }
        out.put("monthlyRevenue", revenue);

        List<Map<String, Object>> growth = new ArrayList<>();
        for (Object[] row : patientRepository.monthlyGrowthSince(sinceD)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("month", row[0]);
            m.put("newPatients", row[1]);
            growth.add(m);
        }
        out.put("patientGrowth", growth);

        List<Map<String, Object>> usage = new ArrayList<>();
        for (Object[] row : billItemRepository.findUsageSince(LocalDateTime.now().minusDays(30))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("medicineName", row[1]);
            m.put("unitsSold", row[2]);
            usage.add(m);
            if (usage.size() >= 8) break;
        }
        out.put("medicineUsage", usage);

        List<Map<String, Object>> stockDist = new ArrayList<>();
        Map<String, Integer> byCategory = new LinkedHashMap<>();
        for (Inventory inv : inventoryRepository.findAll()) {
            String cat = (inv.getMedicine() != null && inv.getMedicine().getCategory() != null && !inv.getMedicine().getCategory().isBlank())
                    ? inv.getMedicine().getCategory() : "Uncategorized";
            byCategory.merge(cat, inv.getAvailableStock() == null ? 0 : inv.getAvailableStock(), Integer::sum);
        }
        byCategory.forEach((cat, qty) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", cat);
            m.put("stock", qty);
            stockDist.add(m);
        });
        out.put("stockDistribution", stockDist);

        return out;
    }
}
