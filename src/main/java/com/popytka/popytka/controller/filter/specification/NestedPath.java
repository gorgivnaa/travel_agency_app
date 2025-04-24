package com.popytka.popytka.controller.filter.specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.List;

public interface NestedPath {

    default Expression<String> getNestedPath(Root<?> root, List<String> pathToParam) {
        return getNestedPath(root, pathToParam, JoinType.INNER);
    }

    default Expression<String> getNestedPath(Root<?> root, List<String> pathToParam, JoinType joinType) {
        if (pathToParam.isEmpty()) {
            throw new RuntimeException("Путь не может быть пустым!");
        }

        From<?, ?> current = root;
        for (int index = 0; index < pathToParam.size() - 1; index++) {
            current = current.join(pathToParam.get(index), joinType);
        }
        return current.get(pathToParam.get(pathToParam.size() - 1));
    }
}
