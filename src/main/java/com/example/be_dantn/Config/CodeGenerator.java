package com.example.be_dantn.Config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CodeGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public String generateCode(String tableName, String columnName, String prefix) {
        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE " + columnName + " LIKE :pattern";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("pattern", prefix + "%");
        
        @SuppressWarnings("unchecked")
        java.util.List<Object> results = query.getResultList();
        
        long maxNum = 0;
        for (Object obj : results) {
            if (obj != null) {
                String code = obj.toString();
                if (code.startsWith(prefix)) {
                    String numPart = code.substring(prefix.length());
                    try {
                        long num = Long.parseLong(numPart.trim());
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore if suffix is not a number
                    }
                }
            }
        }
        
        long nextNum = maxNum + 1;
        return prefix + String.format("%03d", nextNum);
    }
}
