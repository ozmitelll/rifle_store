package org.example.repository;

import org.example.model.Rifle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RifleRepository extends JpaRepository<Rifle, Long> {
}
