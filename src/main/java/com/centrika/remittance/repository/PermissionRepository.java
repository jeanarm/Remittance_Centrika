package com.centrika.remittance.repository;

import com.centrika.remittance.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {}

