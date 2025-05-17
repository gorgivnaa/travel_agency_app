package com.popytka.popytka.dto;

import com.popytka.popytka.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ManagerAssignmentDto {

    private User manager;

    private boolean assigned;

    public Long getId() {
        return manager.getId();
    }

    public String getFullName() {
        return manager.getLastName() + " " + manager.getFirstName();
    }

    public String getEmail() {return manager.getEmail();}
}
