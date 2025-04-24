package com.spribe.services.units.booking.service.model.repo;

import com.spribe.services.units.booking.service.model.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
}
