package org.carRentalService.restController;

import org.carRentalService.entities.Cars;
import org.carRentalService.service.CarsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CarsController {

    @Autowired
    private CarsService carsService;

    @GetMapping("car/{id}")
    public Cars getCarById(@PathVariable String id){

        return carsService.getCarById(Integer.valueOf(id));
    }
}
