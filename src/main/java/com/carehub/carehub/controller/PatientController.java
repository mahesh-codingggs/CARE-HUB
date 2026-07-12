package com.carehub.carehub.controller;

import com.carehub.carehub.config.ResourceNotFoundException;
import com.carehub.carehub.entity.Patient;
import com.carehub.carehub.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientRepository repository;

    @GetMapping
    public List<Patient> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Patient getOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + id));
    }

    @PostMapping
    public Patient create(@Valid @RequestBody Patient patient) {
        return repository.save(patient);
    }

    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient updated) {
        Patient existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + id));
        existing.setName(updated.getName());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setGender(updated.getGender());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setBloodGroup(updated.getBloodGroup());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id " + id);
        }
        repository.deleteById(id);
    }
}
