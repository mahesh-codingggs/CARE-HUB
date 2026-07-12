package com.carehub.carehub.repository;
import com.carehub.carehub.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
