package org.openmrs.module.auditlog.dao.impl;


import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.auditlog.dao.AuditLogDao;
import org.openmrs.module.auditlog.model.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class AuditLogDaoImpl implements AuditLogDao {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PatientService patientService;

    protected static Integer LIMIT = 50;

    @Override
    public List<AuditLog> getLogs(String username, String patientIdentifier, String moduleFilter, Date startDateTime, Date endDateTime,
                                  Integer lastAuditLogId, Boolean prev, Boolean defaultView) {
        // prev will be always not null boolean value
        List<AuditLog> logs = new ArrayList<AuditLog>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AuditLog.class, "auditLog");
        criteria.createAlias("auditLog.user", "user", JoinType.LEFT_OUTER_JOIN);

        criteria.setMaxResults(LIMIT);
        if (prev || defaultView) {
            criteria.addOrder(Order.desc("auditLogId"));
        }
        if (lastAuditLogId != null) {
            criteria.add(prev ? Restrictions.lt("auditLogId", lastAuditLogId)
                    : Restrictions.gt("auditLogId", lastAuditLogId));
        }
        if (startDateTime != null) {
            criteria.add(Restrictions.ge("dateCreated", startDateTime));
        }
        if (endDateTime != null) {
            criteria.add(Restrictions.le("dateCreated", endDateTime));
        }
        if (username != null) {
            criteria.add(Restrictions.eq("user.username", username));
        }
        if (patientIdentifier != null) {
            List<Patient> patients = patientService.getPatients((String)null, patientIdentifier, (List)null, true);
            ;
            if(patients.size() == 0){
                return logs;
            }
            criteria.add(Restrictions.eq("patient", patients.get(0)));
        }
        if (moduleFilter != null) {
            criteria.add(Restrictions.eq("module", moduleFilter));
        }

        logs.addAll(criteria.list());
        if (prev) {
            Collections.reverse(logs);
        }
        return logs;
    }

    @Transactional
    @Override
    public void saveAuditLog(AuditLog auditLog) {
        sessionFactory.getCurrentSession().saveOrUpdate(auditLog);
    }


}
