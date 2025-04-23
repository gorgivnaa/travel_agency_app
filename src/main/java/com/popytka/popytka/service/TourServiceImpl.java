package com.popytka.popytka.service;

import com.popytka.popytka.dto.TourResponse;
import com.popytka.popytka.dto.ValidationError;
import com.popytka.popytka.entity.Tour;
import com.popytka.popytka.repository.TourRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;

    @Override
    public TourResponse getAllServices() {
        List<Tour> tours = tourRepository.findAll();
        return new TourResponse(tours, null);
    }

    @Override
    public TourResponse getById(Long id) {
        Optional<Tour> tourOptional = tourRepository.findById(id);
        Optional<ValidationError> errors = validateGetById(tourOptional, id);
        return errors.isEmpty()
                ? buildError(List.of(errors.get()))
                : buildSuccess(tourOptional.get());
    }

    private TourResponse buildError(List<ValidationError> errors) {
        return new TourResponse(errors);
    }

    private TourResponse buildSuccess(Tour tour) {
        return new TourResponse(List.of(tour));
    }

    private Optional<ValidationError> validateGetById(Optional<Tour> tourOptional, Long id) {
        return tourOptional.isEmpty()
                ? Optional.of(new ValidationError("id", "Тура с 'id'() не существует!"))
                : Optional.empty();
    }
}
