package com.oidccall.createUserInAuth0.implementation.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.TemplateRepository;

class TemplateServiceTest {

    private final RestaurateurRepository restaurateurRepository = mock(RestaurateurRepository.class);
    private final TemplateRepository templateRepository = mock(TemplateRepository.class);
    private final TemplateServiceError templateServiceError = mock(TemplateServiceError.class);

    private final TemplateService service = new TemplateService(
            restaurateurRepository,
            templateRepository,
            templateServiceError);

    @Test
    void addTemplateImpl_createsTemplateWithAvailableNameAndReturnsDto() {
        Restaurateur restaurateur = Restaurateur.builder()
                .templates(List.of(Template.builder().name("Template's name").build()))
                .build();
        when(restaurateurRepository.findByUsersId(42L)).thenReturn(Optional.of(restaurateur));
        when(templateRepository.save(any(Template.class))).thenAnswer(invocation -> {
            Template template = invocation.getArgument(0);
            return Template.builder()
                    .id(7L)
                    .restaurateur(restaurateur)
                    .name(template.getName())
                    .build();
        });
        ArgumentCaptor<Template> templateCaptor = ArgumentCaptor.forClass(Template.class);

        TemplateWithoutServiceCapacityDto result = service.addTemplateImpl(42L);

        verify(restaurateurRepository).findByUsersId(42L);
        verify(templateRepository).save(templateCaptor.capture());
        assertSame(restaurateur, templateCaptor.getValue().getRestaurateur());
        assertEquals("Template's name1", templateCaptor.getValue().getName());
        assertEquals(7L, result.getId());
        assertEquals("Template's name1", result.getName());
        assertTrue(result.isActive());
    }
}
