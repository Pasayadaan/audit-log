package org.openmrs.module.auditlog.service.impl;

import java.io.IOException;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.auditlog.contract.AuditLogPayload;
import org.openmrs.module.auditlog.dao.AuditLogDao;
import org.openmrs.module.auditlog.model.AuditLog;
import org.openmrs.module.auditlog.service.AuditLogService;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private AuditLogDao auditLogDao;
    
    @Autowired
    public AuditLogServiceImpl(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    public ArrayList<SimpleObject> getLogs(String username, String patientId, String moduleFilter, Date startDateTime, Date endDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView) {
        List<AuditLog> auditLogs = auditLogDao.getLogs(username, patientId, moduleFilter, startDateTime, endDateTime, lastAuditLogId, prev, defaultView);
        return (ArrayList<SimpleObject>) (auditLogs.stream().map(AuditLog::map).collect(Collectors.toList()));
    }

    public void createAuditLog(AuditLogPayload log) {
        User user = Context.getAuthenticatedUser();
        Patient patient = null;
        if (log.getPatientUuid() != null){
            patient = Context.getPatientService().getPatientByUuid(log.getPatientUuid());
        }
        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(log.getEventType());
        auditLog.setUser(user);
        auditLog.setEventType(log.getEventType());
        auditLog.setPatient(patient);
        auditLog.setDateCreated(new Date());
        auditLog.setMessage(log.getMessage());
        auditLog.setUuid(UUID.randomUUID().toString());
        auditLog.setModule(log.getModule());
        auditLogDao.saveAuditLog(auditLog);

    }

    public void createAuditLog(String patientUuid, String eventType, String message, Map<String, String> messageParams, String module ) {
        User user = Context.getAuthenticatedUser();
        Patient patient = null;
        if (patientUuid != null){
            patient = Context.getPatientService().getPatientByUuid(patientUuid);
        }
        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(eventType);
        auditLog.setUser(user);
        auditLog.setPatient(patient);
        auditLog.setDateCreated(new Date());
        String messageWithParams = constructMessage(message, messageParams);
        auditLog.setMessage(messageWithParams);
        auditLog.setUuid(UUID.randomUUID().toString());
        auditLog.setModule(module);
        auditLogDao.saveAuditLog(auditLog);
    }

    private String constructMessage(String message, Map<String, String> messageParams) {
        String messageWithParams;
        try {
           messageWithParams = messageParams.isEmpty() ? message : message + "~" + new ObjectMapper().writeValueAsString(messageParams);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return messageWithParams;
    }
}
