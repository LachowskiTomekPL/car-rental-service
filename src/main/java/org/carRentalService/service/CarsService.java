package org.carRentalService.service;


import org.carRentalService.entities.Cars;
import org.carRentalService.repository.CarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarsService {

    @Autowired
    private CarsRepository carsRepository;

    public Cars getCarById(int id){

        final Optional<Cars> carById = carsRepository.findById(id);

        return carById.get();
    }

}
