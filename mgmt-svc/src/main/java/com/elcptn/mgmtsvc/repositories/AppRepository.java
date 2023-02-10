package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.App;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppRepository extends JpaRepository<App, UUID> {

}