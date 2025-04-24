package com.popytka.popytka.controller.filter.specification;

import com.popytka.popytka.common.CommonConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public interface NumberSpecification<T> extends NestedPath, NullSpecification<T> {

    default <N extends Number> Specification<T> buildSpecFromNumber(
            final List<String> pathToParam,
            final N value
    ) {
        if (value == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(getNestedPath(root, pathToParam), value);
    }

    default <N extends Number> Specification<T> buildSpecFromNumberNotNull(
            final List<String> pathToParam,
            final N value,
            final Boolean notNull
    ) {
        Specification<T> spec = buildSpecFromNumber(pathToParam, value);
        return buildSpecFromNull(pathToParam, notNull, spec);
    }

    default Specification<T> buildSpecFromNumbers(
            final List<String> pathToParam,
            final List<String> numbers
    ) {
        if (CollectionUtils.isEmpty(numbers)) {
            return null;
        }

        return numbers.stream()
                .map(this::getNumberAndParameter)
                .map(dateParameter -> getNumberSpecification(dateParameter, pathToParam))
                .reduce(Specification::and)
                .orElse(null);
    }

    private Map.Entry<Long, ParameterEnum> getNumberAndParameter(final String numberWithParameter) {
        final String pattern = getPattern(numberWithParameter);
        final MatchResult matchResult = Pattern.compile(pattern)
                .matcher(numberWithParameter)
                .results()
                .findFirst()
                .orElseThrow(() -> createWrongSearchException(numberWithParameter));

        final ParameterEnum parameter = ParameterEnum.fromString(
                matchResult.group(CommonConstants.PREFIX_DATA_GROUP_NUMBER)
        );
        final Number number = parseNumber(matchResult, pattern);
        return Map.entry(number.longValue(), parameter);
    }

    private String getPattern(final String numberWithParameter) {
        if (numberWithParameter.matches(CommonConstants.NUMBER_WITH_PARAMETER_REGEX)) {
            return CommonConstants.NUMBER_WITH_PARAMETER_REGEX;
        } else {
            throw createWrongSearchException(numberWithParameter);
        }
    }

    private Number parseNumber(final MatchResult matchResult, final String pattern) {
        String numberText = matchResult.group(CommonConstants.DATA_GROUP_NUMBER);
        if (!CommonConstants.NUMBER_WITH_PARAMETER_REGEX.equals(pattern)) {
            throw new RuntimeException();
        }
        return Long.valueOf(numberText);
    }

    private RuntimeException createWrongSearchException(final String numberWithParameter) {
        return new RuntimeException("Неверно указанный формат! " + numberWithParameter);
    }

    private Specification<T> getNumberSpecification(
            final Map.Entry<Long, ParameterEnum> numberParameter,
            final List<String> pathToParam
    ) {
        if (numberParameter == null || numberParameter.getKey() == null) {
            return null;
        }
        return switch (numberParameter.getValue()) {
            case GT -> (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(
                    getNestedPath(root, pathToParam).as(Long.class),
                    numberParameter.getKey()
            );
            case GE -> (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
                    getNestedPath(root, pathToParam).as(Long.class),
                    numberParameter.getKey()
            );
            case LT -> (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(
                    getNestedPath(root, pathToParam).as(Long.class),
                    numberParameter.getKey()
            );
            case LE -> (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(
                    getNestedPath(root, pathToParam).as(Long.class),
                    numberParameter.getKey()
            );
            case EQ -> (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                    getNestedPath(root, pathToParam).as(Long.class),
                    numberParameter.getKey()
            );
            case NE -> (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(
                    getNestedPath(root, pathToParam).as(Long.class),
                    numberParameter.getKey()
            );
        };
    }
}
