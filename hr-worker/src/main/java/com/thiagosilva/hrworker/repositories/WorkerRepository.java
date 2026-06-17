package com.thiagosilva.hrworker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thiagosilva.hrworker.entities.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Long>{

}
