package org.openmrs.module.auditlog.service.impl;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.auditlog.contract.AuditLogPayload;
import org.openmrs.module.auditlog.dao.impl.AuditLogDaoImpl;
import org.openmrs.module.auditlog.model.AuditLog;
import org.openmrs.module.auditlog.util.DateUtil;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class AuditLogServiceImplTest {
    @InjectMocks
    private AuditLogServiceImpl auditLogService;
    @Mock
    private AuditLogDaoImpl auditLogDao;
    @Mock
    private Patient patient_1;
    @Mock
    private User user_1;
    @Mock
    private Patient patient_2;
    @Mock
    private User user_2;
    @Mock
    private PatientIdentifier patientIdentifier_1;
    @Mock
    private PatientIdentifier patientIdentifier_2;

    private Date dateCreated_1;
    private Date dateCreated_2;
    private ArrayList<AuditLog> mockAuditLogs;

    @Mock
    PatientService patientService;


    @Before
    public void setUp() throws Exception {
        dateCreated_1 = DateUtil.convertToDate("2017-03-15T16:57:09.0Z", DateUtil.DateFormatType.UTC);
        dateCreated_2 = DateUtil.convertToDate("2017-03-15T16:57:10.0Z", DateUtil.DateFormatType.UTC);
        when(patient_1.getPatientIdentifier()).thenReturn(patientIdentifier_1);
        when(patient_2.getPatientIdentifier()).thenReturn(patientIdentifier_2);
        mockAuditLogs = new ArrayList<AuditLog>();
    }

    @Test
    public void getLogs_shouldGiveMappedAuditLogs() throws Exception {
        when(patientIdentifier_1.getIdentifier()).thenReturn("GAN2000");
        when(user_1.getUsername()).thenReturn("superman");
        when(patientIdentifier_2.getIdentifier()).thenReturn("GAN2001");
        when(user_2.getUsername()).thenReturn("batman");

        AuditLog auditLog_1 = new AuditLog();
        AuditLog auditLog_2 = new AuditLog();

        auditLog_1.setPatient(patient_1);
        auditLog_1.setMessage("message 1");
        auditLog_1.setUser(user_1);
        auditLog_1.setAuditLogId(1);
        auditLog_1.setDateCreated(dateCreated_1);
        auditLog_1.setEventType("event_type_1");
        auditLog_1.setUuid("uuid1");
        auditLog_1.setModule("clinical");

        auditLog_2.setPatient(patient_2);
        auditLog_2.setMessage("message 2");
        auditLog_2.setUser(user_2);
        auditLog_2.setAuditLogId(2);
        auditLog_2.setDateCreated(dateCreated_2);
        auditLog_2.setEventType("event_type_2");
        auditLog_2.setUuid("uuid2");
        auditLog_2.setModule("reports");

        mockAuditLogs.add(auditLog_1);
        mockAuditLogs.add(auditLog_2);

        when(auditLogDao.getLogs("username", "patientId", null, null, null, 1,
                false, false)).thenReturn(mockAuditLogs);
        ArrayList<SimpleObject> logs = auditLogService.getLogs("username", "patientId", null,
                null, null, 1, false, false);
        assertEquals(2, logs.size());
        SimpleObject auditLogResponse_1 = logs.get(0);
        SimpleObject auditLogResponse_2 = logs.get(1);

        assertEquals("message 1", auditLogResponse_1.get("message"));
        assertEquals("GAN2000", auditLogResponse_1.get("patientId"));
        assertEquals("superman", auditLogResponse_1.get("userId"));
        assertEquals("event_type_1", auditLogResponse_1.get("eventType"));
        assertEquals(dateCreated_1, auditLogResponse_1.get("dateCreated"));
        assertEquals(Integer.valueOf(1), auditLogResponse_1.get("auditLogId"));
        assertEquals("clinical", auditLogResponse_1.get("module"));

        assertEquals("message 2", auditLogResponse_2.get("message"));
        assertEquals("GAN2001", auditLogResponse_2.get("patientId"));
        assertEquals("batman", auditLogResponse_2.get("userId"));
        assertEquals("event_type_2", auditLogResponse_2.get("eventType"));
        assertEquals(dateCreated_2, auditLogResponse_2.get("dateCreated"));
        assertEquals(Integer.valueOf(2), auditLogResponse_2.get("auditLogId"));
        assertEquals("reports", auditLogResponse_2.get("module"));
    }

    @Test
    public void shouldCreateAuditLog() throws Exception {
        String patientUuid = "patientUuid";
        AuditLogPayload log = new AuditLogPayload(patientUuid, "message", "eventType", "registration");
        mockStatic(Context.class);
        User user = new User();
        user.setName("auditlogger");
        when(Context.getAuthenticatedUser()).thenReturn(user);
        when(Context.getPatientService()).thenReturn(patientService);
        Patient patient = new Patient();
        patient.setUuid(patientUuid);
        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);

        ArgumentCaptor<AuditLog> argument = ArgumentCaptor.forClass(AuditLog.class);

        auditLogService.createAuditLog(log);

        verify(auditLogDao).saveAuditLog(argument.capture());
        Assert.assertEquals(patientUuid, argument.getValue().getPatient().getUuid());
        Assert.assertEquals(log.getMessage(), argument.getValue().getMessage());
        Assert.assertEquals(log.getEventType(), argument.getValue().getEventType());
        Assert.assertEquals(log.getModule(), argument.getValue().getModule());
    }

    @Test
    public void shouldCreateAuditLogWithParams() throws Exception {
        String patientUuid = "patientUuid";
        mockStatic(Context.class);
        User user = new User();
        user.setName("auditlogger");
        when(Context.getAuthenticatedUser()).thenReturn(user);
        when(Context.getPatientService()).thenReturn(patientService);
        Patient patient = new Patient();
        patient.setUuid(patientUuid);
        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        
        ArgumentCaptor<AuditLog> argument = ArgumentCaptor.forClass(AuditLog.class);
        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("encounterUuid", "81f57a25-3f10-11e4-821f-0800271c1b75");
        messageParams.put("encounterType", "REG");
        auditLogService.createAuditLog(patientUuid, "eventType", "message", messageParams, "registration");
        String savedMessage = "message~{\"encounterUuid\":\"81f57a25-3f10-11e4-821f-0800271c1b75\",\"encounterType\":\"REG\"}";
        verify(auditLogDao).saveAuditLog(argument.capture());
        Assert.assertEquals(patientUuid, argument.getValue().getPatient().getUuid());
        Assert.assertEquals(savedMessage, argument.getValue().getMessage());
        Assert.assertEquals("eventType", argument.getValue().getEventType());
        Assert.assertEquals("registration", argument.getValue().getModule());
    }
    
    @Test
    public void shouldIgnoreParamsIfIsEmptyWhenCreateAuditLogWithParams() throws Exception {
        String patientUuid = "patientUuid";
        mockStatic(Context.class);
        User user = new User();
        user.setName("auditlogger");
        when(Context.getAuthenticatedUser()).thenReturn(user);
        when(Context.getPatientService()).thenReturn(patientService);
        Patient patient = new Patient();
        patient.setUuid(patientUuid);
        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        
        ArgumentCaptor<AuditLog> argument = ArgumentCaptor.forClass(AuditLog.class);
        Map<String, String> messageParams = new HashMap<>();
        auditLogService.createAuditLog(patientUuid, "eventType", "message", messageParams, "registration");
        verify(auditLogDao).saveAuditLog(argument.capture());
        Assert.assertEquals(patientUuid, argument.getValue().getPatient().getUuid());
        Assert.assertEquals("message", argument.getValue().getMessage());
        Assert.assertEquals("eventType", argument.getValue().getEventType());
        Assert.assertEquals("registration", argument.getValue().getModule());
    }
}
