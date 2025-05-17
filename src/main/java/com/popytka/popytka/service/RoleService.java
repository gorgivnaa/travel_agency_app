package com.popytka.popytka.service;

import com.popytka.popytka.entity.Role;

import java.util.List;

public interface RoleService {

    Role getDefaultRoles();

    List<Role> getAllRoles();

    Role getById(Long id);
}
