package org.openmrs.module.auditlog.service;

import java.util.Map;
import org.openmrs.module.auditlog.contract.AuditLogPayload;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.ArrayList;
import java.util.Date;

public interface AuditLogService {
    ArrayList<SimpleObject> getLogs(String username, String patientId, String moduleFilter, Date startDateTime, Date endDateTime, Integer lastAuditLogId,
                                    Boolean prev, Boolean defaultView);

    void createAuditLog(AuditLogPayload log);
    void createAuditLog(String patientUuid, String eventType, String message, Map<String, String> messageParams, String module);
}
