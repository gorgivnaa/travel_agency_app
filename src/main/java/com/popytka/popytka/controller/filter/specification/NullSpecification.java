package com.popytka.popytka.controller.filter.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NullSpecification<T> extends NestedPath {

    default Specification<T> buildSpecFromNull(
            final List<String> pathToParam,
            final Boolean notNull,
            final Specification<T> baseSpec
    ) {
        Specification<T> finalSpec = (baseSpec != null)
                ? baseSpec
                : (root, query, cb) -> cb.conjunction();
        Specification<T> booleanSpec = buildSpecFromNull(pathToParam, notNull);
        return finalSpec.and(booleanSpec);
    }

    default Specification<T> buildSpecFromNull(final List<String> pathToParam, final Boolean notNull) {
        if (Boolean.TRUE.equals(notNull)) {
            return (root, query, cb) -> cb.isNotNull(getNestedPath(root, pathToParam));
        } else if (Boolean.FALSE.equals(notNull)) {
            return (root, query, cb) -> cb.isNull(getNestedPath(root, pathToParam));
        }
        return null;
    }
}
