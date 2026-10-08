package com.oidccall.createUserInAuth0.implementation.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.TemplateRepository;

class TemplatePeriodServiceTest {

    private final RestaurateurRepository restaurateurRepository = mock(RestaurateurRepository.class);
    private final TemplateRepository templateRepository = mock(TemplateRepository.class);
    private final TemplatePeriodService service = new TemplatePeriodService(restaurateurRepository, templateRepository);

    private Restaurateur restaurateur;
    private Template template;

    @BeforeEach
    void setUp() {
        template = Template.builder()
                .id(7L)
                .dateSolo(LocalDate.of(2026, 10, 8))
                .build();
        restaurateur = Restaurateur.builder().templates(List.of(template)).build();
        when(restaurateurRepository.findByUsersId(42L)).thenReturn(Optional.of(restaurateur));
    }

    @Test
    void modifyDaysOfWeekOrSingleDateImpl_keepsSingleDateWhenNoWeekdayIsSelected() {
        LocalDate requestedDate = LocalDate.of(2026, 10, 12);
        TemplateWithoutServiceCapacityDto dto = TemplateWithoutServiceCapacityDto.builder()
                .id(7L)
                .dateSolo(requestedDate)
                .build();

        service.modifyDaysOfWeekOrSingleDateImpl(42L, dto);

        assertEquals(requestedDate, template.getDateSolo());
        assertFalse(template.isMonday());
        assertFalse(template.isTuesday());
        assertFalse(template.isWednesday());
        assertFalse(template.isThursday());
        assertFalse(template.isFriday());
        assertFalse(template.isSaturday());
        assertFalse(template.isSunday());
        verify(restaurateurRepository).findByUsersId(42L);
        verify(templateRepository).save(template);
    }

    @Test
    void modifyDaysOfWeekOrSingleDateImpl_clearsSingleDateWhenAWeekdayIsSelected() {
        TemplateWithoutServiceCapacityDto dto = TemplateWithoutServiceCapacityDto.builder()
                .id(7L)
                .dateSolo(LocalDate.of(2026, 10, 12))
                .monday(true)
                .wednesday(true)
                .saturday(true)
                .build();

        service.modifyDaysOfWeekOrSingleDateImpl(42L, dto);

        assertNull(template.getDateSolo());
        assertTrue(template.isMonday());
        assertFalse(template.isTuesday());
        assertTrue(template.isWednesday());
        assertFalse(template.isThursday());
        assertFalse(template.isFriday());
        assertTrue(template.isSaturday());
        assertFalse(template.isSunday());
        verify(restaurateurRepository).findByUsersId(42L);
        verify(templateRepository).save(template);
    }
}
