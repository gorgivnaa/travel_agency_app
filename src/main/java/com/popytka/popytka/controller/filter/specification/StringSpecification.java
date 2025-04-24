package com.popytka.popytka.controller.filter.specification;

import com.popytka.popytka.common.CommonConstants;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.function.Function;

public interface StringSpecification <T> extends NullSpecification<T> {

    default Specification<T> buildSpecFromString(final List<String> pathToParam, final String value) {
        return buildSpecFromStringFunction(root -> getNestedPath(root, pathToParam), value);
    }

    default Specification<T> buildSpecFromString(
            final List<String> pathToParam,
            final String value,
            final JoinType joinType
    ) {
        return buildSpecFromStringFunction(root -> getNestedPath(root, pathToParam, joinType), value);
    }

    default Specification<T> buildSpecFromStringFunction(
            final Function<Root<?>, Expression<String>> pathToParamFunction,
            final String value
    ) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String trimmedInput = value.trim();
        long countOccurrences = trimmedInput.chars()
                .filter(c -> c == CommonConstants.CHAR_ASTERISK_DELIMITER)
                .count();

        if (countOccurrences == 0) {
            return createAbsoluteSpecification(pathToParamFunction, trimmedInput);
        } else if (countOccurrences == 2
                && trimmedInput.startsWith(CommonConstants.STRING_ASTERISK_DELIMITER)
                && trimmedInput.endsWith(CommonConstants.STRING_ASTERISK_DELIMITER)
        ) {
            String innerValue = trimmedInput.substring(1, trimmedInput.length() - 1).trim();
            return createRelativeSpecification(pathToParamFunction, innerValue);
        }
        return null;
    }

    private Specification<T> createAbsoluteSpecification(
            final Function<Root<?>, Expression<String>> pathToParamFunction,
            final String value
    ) {
        return (root, query, cb) -> cb.equal(
                cb.lower(pathToParamFunction.apply(root)),
                value.toLowerCase()
        );
    }

    private Specification<T> createRelativeSpecification(
            final Function<Root<?>, Expression<String>> pathToParamFunction,
            final String value
    ) {
        return (root, query, cb) -> cb.like(
                cb.lower(pathToParamFunction.apply(root)),
                cb.lower(cb.literal(
                        String.format(CommonConstants.LIKE_CONDITION_FORMAT, value.trim())
                ))
        );
    }
}
