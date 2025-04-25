package com.popytka.popytka.controller.filter;

import com.popytka.popytka.controller.filter.specification.StringSpecification;
import com.popytka.popytka.entity.AdditionalService;
import com.popytka.popytka.entity.AdditionalService_;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Getter
@Setter
public class AdditionalServiceFilter implements StringSpecification<AdditionalService> {

    private String name;

    public Specification<AdditionalService> toSpecification() {
        return Specification.allOf(
                nameSpec()
        );
    }

    private Specification<AdditionalService> nameSpec() {
        return buildSpecFromString(List.of(AdditionalService_.NAME), name);
    }
}
