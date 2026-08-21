package com.mo.core.visitors.need_visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.mo.auth.User;
import com.mo.core.documents.needs.ServiceNeedDocument;
import com.mo.core.enums.NeedType;
import com.mo.core.model.needs.ServiceNeed;

public class UserNeedIndexerVisitorTest {

    @Test
    public void serviceNeedIndexer_shouldPopulateNewServiceFields() {
        ServiceNeed need = new ServiceNeed();
        need.setId(200L);
        need.setType(NeedType.SERVICE);
        User user = new User();
        user.setId(2L);
        need.setUser(user);
        need.setName("Home Coaching");
        need.setDescription("Weekly personal training sessions");
        need.setMaxPrice(BigDecimal.valueOf(150));
        need.setCurrency(com.mo.core.enums.Currency.EURO);
        need.setQuantity(1);
        need.setServiceProvider("CoachPro");
        need.setLocation("Paris");
        need.setDuration(90L);
        need.setOnlineAvailable(Boolean.TRUE);
        need.setAvailableAfter(LocalDateTime.now().plusDays(3));
        need.setPreferredLanguages(List.of("French", "English"));
        need.setServiceAreaRadiusKm(20.0);
        need.setMinimumProviderRating(4.2);
        need.setCancellationPolicyPreference("flexible");

        UserNeedIndexerVisitor visitor = new UserNeedIndexerVisitor();
        ServiceNeedDocument document = (ServiceNeedDocument) visitor.visit(need);

        assertEquals("CoachPro", document.getServiceProvider());
        assertEquals("Paris", document.getLocation());
        assertEquals(90L, document.getDuration());
        assertEquals(Boolean.TRUE, document.getOnlineAvailable());
        assertEquals(20.0, document.getServiceAreaRadiusKm());
        assertEquals(4.2, document.getMinimumProviderRating());
        assertEquals("flexible", document.getCancellationPolicyPreference());
        assertTrue(document.getAllText().contains("CoachPro"));
        assertTrue(document.getAllText().contains("flexible"));
    }
}
