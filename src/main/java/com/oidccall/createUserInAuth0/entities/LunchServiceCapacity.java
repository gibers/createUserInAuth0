package com.oidccall.createUserInAuth0.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lunch_service_capacity")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "template_id", referencedColumnName = "template_id"),
    @PrimaryKeyJoinColumn(name = "restaurateur_id", referencedColumnName = "restaurateur_id"),
    @PrimaryKeyJoinColumn(name = "table_number", referencedColumnName = "table_number")
})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LunchServiceCapacity extends ServiceCapacity {

}