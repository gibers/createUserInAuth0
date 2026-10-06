package com.oidccall.createUserInAuth0.dtos.front;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateWithoutServiceCapacityDto {

    private Long id;
    @NotNull
    @Size(max = 65)
    private String name;
    private boolean active;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private LocalDate dataSolo;
    private LocalDate validFrom;
    private String comment;

}
