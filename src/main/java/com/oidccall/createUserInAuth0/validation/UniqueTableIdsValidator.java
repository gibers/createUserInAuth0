package com.oidccall.createUserInAuth0.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueTableIdsValidator implements ConstraintValidator<UniqueTableIds, List<SeatingCapacityMax>> {

    @Override
    public boolean isValid(List<SeatingCapacityMax> seatingCapacityMax, ConstraintValidatorContext context) {
        if (seatingCapacityMax == null) {
            return true;
        }

        Set<String> tableIds = new HashSet<>();
        for (SeatingCapacityMax seatingCapacity : seatingCapacityMax) {
            if (seatingCapacity != null && !tableIds.add(seatingCapacity.tableId())) {
                return false;
            }
        }

        return true;
    }
}
