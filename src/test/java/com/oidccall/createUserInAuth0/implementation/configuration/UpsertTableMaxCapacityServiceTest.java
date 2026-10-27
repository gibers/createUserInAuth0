package com.oidccall.createUserInAuth0.implementation.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

class UpsertTableMaxCapacityServiceTest {

    private final RestaurateurRepository restaurateurRepository = mock(RestaurateurRepository.class);
    private final SeatingCapacityRepository seatingCapacityRepository = mock(SeatingCapacityRepository.class);
    private final AddNewSeatingCapacityInDB addNewSeatingCapacityInDB = mock(AddNewSeatingCapacityInDB.class);
    private final UpsertTableMaxCapacityService service = new UpsertTableMaxCapacityService(
            restaurateurRepository,
            seatingCapacityRepository,
            addNewSeatingCapacityInDB);

    @Test
    @SuppressWarnings("unchecked")
    void upsertTableMaxCapacityImpl_updatesExistingTablesDeletesRemovedTablesAndDelegatesNewTables() {
        Users users = new Users();
        Restaurateur restaurateur = Restaurateur.builder().name("Restaurant").build();
        SeatingCapacity existingTable = seatingCapacity(restaurateur, "T1", 2, "#111111");
        SeatingCapacity removedTable = seatingCapacity(restaurateur, "T2", 4, "#222222");
        List<SeatingCapacity> seatingCapacitiesInDatabase = List.of(existingTable, removedTable);
        List<SeatingCapacityMax> requestedTables = List.of(
                new SeatingCapacityMax("T1", 6, null),
                new SeatingCapacityMax("T3", 4, null));
        ArgumentCaptor<List<SeatingCapacity>> deletedTablesCaptor = ArgumentCaptor.forClass(List.class);

        when(restaurateurRepository.findByUsers(users)).thenReturn(Optional.of(restaurateur));
        when(seatingCapacityRepository.findAllByRestaurateur(restaurateur)).thenReturn(seatingCapacitiesInDatabase);

        service.upsertTableMaxCapacityImpl(users, requestedTables);

        assertEquals(6, existingTable.getCapacity());
        verify(seatingCapacityRepository).deleteAll(deletedTablesCaptor.capture());
        assertEquals(List.of(removedTable), deletedTablesCaptor.getValue());
        verify(addNewSeatingCapacityInDB).execute(eq(restaurateur), eq(java.util.Map.of("T1", 6, "T3", 4)));
    }

    @Test
    void seatingCapacityFromUserIdImpl_mapsTableCapacityAndColorCode() {
        Restaurateur restaurateur = Restaurateur.builder().name("Restaurant").build();
        SeatingCapacity seatingCapacity = seatingCapacity(restaurateur, "T1", 4, "#7C3E91");
        when(restaurateurRepository.findByUsersId(42L)).thenReturn(Optional.of(restaurateur));
        when(seatingCapacityRepository.findAllByRestaurateur(restaurateur)).thenReturn(List.of(seatingCapacity));

        List<SeatingCapacityMax> result = service.seatingCapacityFromUserIdImpl(42L);

        assertEquals(List.of(new SeatingCapacityMax("T1", 4, "#7C3E91")), result);
        verify(restaurateurRepository).findByUsersId(42L);
        verify(seatingCapacityRepository).findAllByRestaurateur(restaurateur);
    }

    private SeatingCapacity seatingCapacity(Restaurateur restaurateur, String tableNumber, int capacity, String colorCode) {
        return SeatingCapacity.builder()
                .restaurateur(restaurateur)
                .tableNumber(tableNumber)
                .capacity(capacity)
                .colorCode(colorCode)
                .build();
    }
}
