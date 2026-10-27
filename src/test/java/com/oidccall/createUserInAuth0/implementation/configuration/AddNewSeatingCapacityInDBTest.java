package com.oidccall.createUserInAuth0.implementation.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

class AddNewSeatingCapacityInDBTest {

    private final SeatingCapacityRepository seatingCapacityRepository = mock(SeatingCapacityRepository.class);

    @Test
    @SuppressWarnings("unchecked")
    void execute_savesOnlyMissingTablesAndUsesAnUnusedColor() {
        Restaurateur restaurateur = Restaurateur.builder().name("Restaurant").build();
        SeatingCapacity existingTable = SeatingCapacity.builder()
                .restaurateur(restaurateur)
                .tableNumber("T1")
                .capacity(2)
                .colorCode("#111111")
                .build();
        when(seatingCapacityRepository.findAllByRestaurateur(restaurateur)).thenReturn(List.of(existingTable));
        when(seatingCapacityRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        AddNewSeatingCapacityInDB service = new AddNewSeatingCapacityInDB(
                seatingCapacityRepository,
                Map.of("color0", "#111111", "color1", "#222222"));
        ArgumentCaptor<List<SeatingCapacity>> savedTablesCaptor = ArgumentCaptor.forClass(List.class);

        service.execute(restaurateur, Map.of("T1", 3, "T2", 5));

        verify(seatingCapacityRepository).saveAll(savedTablesCaptor.capture());
        List<SeatingCapacity> savedTables = savedTablesCaptor.getValue();
        assertEquals(1, savedTables.size());
        assertEquals("T2", savedTables.getFirst().getTableNumber());
        assertEquals(5, savedTables.getFirst().getCapacity());
        assertEquals("#222222", savedTables.getFirst().getColorCode());
        assertSame(restaurateur, savedTables.getFirst().getRestaurateur());
    }

    @Test
    void execute_doesNotSaveWhenAllRequestedTablesAlreadyExist() {
        Restaurateur restaurateur = Restaurateur.builder().name("Restaurant").build();
        SeatingCapacity existingTable = SeatingCapacity.builder()
                .restaurateur(restaurateur)
                .tableNumber("T1")
                .capacity(2)
                .colorCode("#111111")
                .build();
        when(seatingCapacityRepository.findAllByRestaurateur(restaurateur)).thenReturn(List.of(existingTable));
        AddNewSeatingCapacityInDB service = new AddNewSeatingCapacityInDB(
                seatingCapacityRepository,
                Map.of("color0", "#111111"));

        service.execute(restaurateur, Map.of("T1", 3));

        verify(seatingCapacityRepository, never()).saveAll(anyList());
    }

    @Test
    void execute_throwsWhenEveryConfiguredColorIsAlreadyUsed() {
        Restaurateur restaurateur = Restaurateur.builder().name("Restaurant").build();
        SeatingCapacity existingTable = SeatingCapacity.builder()
                .restaurateur(restaurateur)
                .tableNumber("T1")
                .capacity(2)
                .colorCode("#111111")
                .build();
        when(seatingCapacityRepository.findAllByRestaurateur(restaurateur)).thenReturn(List.of(existingTable));
        AddNewSeatingCapacityInDB service = new AddNewSeatingCapacityInDB(
                seatingCapacityRepository,
                Map.of("color0", "#111111"));

        assertThrows(DataIntegrityViolationException.class, () -> service.execute(restaurateur, Map.of("T2", 4)));

        verify(seatingCapacityRepository, never()).saveAll(anyList());
    }
}
