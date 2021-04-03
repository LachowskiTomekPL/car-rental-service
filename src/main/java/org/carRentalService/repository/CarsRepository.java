package org.carRentalService.repository;

import org.carRentalService.entities.Cars;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarsRepository extends CrudRepository<Cars,Integer> {
}
