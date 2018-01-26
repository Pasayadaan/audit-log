package org.openmrs.module.auditlog.dao.impl;


import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.auditlog.BaseIntegrationTest;
import org.openmrs.module.auditlog.util.DateUtil;
import org.openmrs.module.auditlog.dao.AuditLogDao;
import org.openmrs.module.auditlog.model.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AuditLogDaoImplIT extends BaseIntegrationTest {
    @Autowired
    AuditLogDao auditLogDao;

    @Autowired
    PatientService patientService;

    @Autowired
    UserService userService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("auditLogTestData.xml");
        AuditLogDaoImpl.LIMIT = 2;
        Context.updateSearchIndexForType(PatientIdentifier.class);
    }

    @Test
    public void shouldGiveAllLogsCreatedAfterGivenDateTime() throws Exception {
        Date startDateTime = DateUtil.convertToDate("2017-03-15T16:57:09.0Z", DateUtil.DateFormatType.UTC);
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, startDateTime,startDateTime, null, false, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals("spiderman", auditLog_1.getUser().getUsername());
        assertEquals("SEM200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());
        assertEquals("clinical", auditLog_1.getModule());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
        assertEquals("admin", auditLog_2.getModule());
    }

    @Test
    public void shouldGivePreviousLogsFromGivenIndex() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, null, null, 5, true, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals("spiderman", auditLog_1.getUser().getUsername());
        assertEquals("SEM200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());
        assertEquals("clinical", auditLog_1.getModule());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
        assertEquals("admin", auditLog_2.getModule());
    }

    @Test
    public void shouldGivePreviousLogsFromGivenIndexEvenIfSomeEventsAreDeletedInBetween() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, null, null, 4, true, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog_1.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(1), auditLog_1.getAuditLogId());
        assertEquals("batman", auditLog_1.getUser().getUsername());
        assertEquals("GAN200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb67a", auditLog_1.getUuid());
        assertEquals("clinical", auditLog_1.getModule());

        assertEquals("EDIT_CLINICAL message", auditLog_2.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_2.getAuditLogId());
        assertEquals("spiderman", auditLog_2.getUser().getUsername());
        assertEquals("SEM200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_2.getUuid());
        assertEquals("clinical", auditLog_2.getModule());

    }

    @Test
    public void shouldGiveNextLogsFromGivenIndex() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, null, null, 2, false, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals("spiderman", auditLog_1.getUser().getUsername());
        assertEquals("SEM200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());
        assertEquals("clinical", auditLog_1.getModule());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
        assertEquals("admin", auditLog_2.getModule());
    }

    @Test
    public void getLogs_shouldGiveLogsInDescendingIfItIsDefaultView() throws Exception {
        AuditLogDaoImpl.LIMIT = 3;
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, null, null, null, false, true);
        assertEquals(3, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);
        AuditLog auditLog_3 = logs.get(2);

        assertEquals("LOGIN_FAILED message", auditLog_1.getMessage());
        assertEquals("LOGIN_FAILED", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(6), auditLog_1.getAuditLogId());
        assertEquals(null, auditLog_1.getUser());
        assertEquals(null, auditLog_1.getPatient());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87f", auditLog_1.getUuid());
        assertEquals("login", auditLog_1.getModule());

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(5), auditLog_2.getAuditLogId());
        assertEquals("thor", auditLog_2.getUser().getUsername());
        assertEquals("BAH200001", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87e", auditLog_2.getUuid());
        assertEquals("clinical", auditLog_2.getModule());

        assertEquals("VIEWED_DASHBOARD message", auditLog_3.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_3.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_3.getAuditLogId());
        assertEquals("superuser", auditLog_3.getUser().getUsername());
        assertEquals("GAN200000", auditLog_3.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_3.getUuid());
        assertEquals("admin", auditLog_3.getModule());
    }

    @Test
    public void getLogs_shouldGiveAuditLogsFilterByGivenUsername() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs("batman", null, null, null, null, null, false, false);
        assertEquals(1, logs.size());
        AuditLog auditLog = logs.get(0);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog.getEventType());
        assertEquals(Integer.valueOf(1), auditLog.getAuditLogId());
        assertEquals("batman", auditLog.getUser().getUsername());
        assertEquals("GAN200000", auditLog.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb67a", auditLog.getUuid());
        assertEquals("clinical", auditLog.getModule());
    }

    @Test
    public void getLogs_shouldGiveAuditLogsFilterByGivenPatientId() throws Exception {

        List<AuditLog> logs = auditLogDao.getLogs(null, "GAN200000", null, null, null, null, false, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog_1.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(1), auditLog_1.getAuditLogId());
        assertEquals("batman", auditLog_1.getUser().getUsername());
        assertEquals("GAN200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb67a", auditLog_1.getUuid());
        assertEquals("clinical", auditLog_1.getModule());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
        assertEquals("admin", auditLog_2.getModule());
    }

    @Test
    public void getLogs_shouldGiveEmptyListIfTheGivePatientIdentifierIsInvalid() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, "GAN200100", null, null, null, null, false, false);
        assertEquals(0, logs.size());
    }

    @Test
    public void getLogs_shouldGiveEmptyListIfTheGiveUsernameIsInvalid() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs("antman", "GAN200000", null, null, null, null, false, false);
        assertEquals(0, logs.size());
    }


    @Test
    public void shouldPersistLogs() throws Exception {
        AuditLog auditLog = new AuditLog();
        auditLog.setUuid("uuid");
        auditLog.setMessage("message");
        Patient patient = patientService.getPatientByUuid("75e04d42-3ca8-11e3-bf2b-0800271c1b81");
        auditLog.setPatient(patient);
        User user = userService.getUserByUuid("Z9fd3a7b-6482-487d-87eR-c07b123MxF99");
        auditLog.setUser(user);
        auditLog.setEventType("event1");
        Date now = new Date();
        auditLog.setDateCreated(now);
        int countBefore = auditLogDao.getLogs(user.getUsername(), "SEM200000",null,null,null,null,false,false).size();
        auditLogDao.saveAuditLog(auditLog);

        int countAfter = auditLogDao.getLogs(user.getUsername(), "SEM200000", null, null, null, null, false, false).size();
        assertEquals(1, countAfter-countBefore);
    }
}