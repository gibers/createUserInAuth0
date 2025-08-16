package com.oidccall.createUserInAuth0.dtos;

import com.oidccall.createUserInAuth0.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMetada {
  private GenderEnum gender;
}
