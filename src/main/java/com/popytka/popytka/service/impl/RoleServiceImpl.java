package com.popytka.popytka.service.impl;

import com.popytka.popytka.entity.Role;
import com.popytka.popytka.repository.RoleRepository;
import com.popytka.popytka.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    @Value("${user.default.role}")
    private String defaultRole;

    private final RoleRepository roleRepository;

    @Override
    public Role getDefaultRoles() {
        return roleRepository.findByName(defaultRole).orElseThrow(
                () -> new IllegalStateException("Role '" + defaultRole + "'does not exist!")
        );
    }
}
