package com.popytka.popytka.dto;

import com.popytka.popytka.entity.Tour;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponse {

    private List<Tour> data;
    private List<ValidationError> errors;

    public TourResponse(List<ValidationError> errors) {
        this.errors = errors;
    }

    public TourResponse(List<Tour> data) {
        this.data = data;
    }
}
