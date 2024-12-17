package com.popytka.popytka.repos;

import com.popytka.popytka.models.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Long> {
    @Query("select c from Country as c")
    List<Country> getCountries();
}
