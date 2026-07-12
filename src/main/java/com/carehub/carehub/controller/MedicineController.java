package com.carehub.carehub.controller;

import com.carehub.carehub.config.ResourceNotFoundException;
import com.carehub.carehub.entity.Medicine;
import com.carehub.carehub.repository.MedicineRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineRepository repository;

    @GetMapping
    public List<Medicine> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Medicine getOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id " + id));
    }

    @PostMapping
    public Medicine create(@Valid @RequestBody Medicine medicine) {
        return repository.save(medicine);
    }

    @PutMapping("/{id}")
    public Medicine update(@PathVariable Long id, @Valid @RequestBody Medicine updated) {
        Medicine existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id " + id));
        existing.setMedicineName(updated.getMedicineName());
        existing.setCategory(updated.getCategory());
        existing.setUnit(updated.getUnit());
        existing.setDescription(updated.getDescription());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine not found with id " + id);
        }
        repository.deleteById(id);
    }
}
