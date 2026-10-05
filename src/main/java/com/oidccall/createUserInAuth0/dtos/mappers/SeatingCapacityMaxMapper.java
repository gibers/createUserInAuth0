package com.oidccall.createUserInAuth0.dtos.mappers;

import java.util.List;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;

public final class SeatingCapacityMaxMapper {

    private SeatingCapacityMaxMapper() {
    }

    public static List<SeatingCapacityMax> mapToSeatingCapacityMax(
            List<SeatingCapacity> seatingCapacities
    ) {
        return seatingCapacities.stream()
                .map(seatingCapacity -> new SeatingCapacityMax(
                        seatingCapacity.getTableNumber(),
                        seatingCapacity.getCapacity()))
                .toList();
    }
}
