package org.carRentalService.repository;

import org.carRentalService.CarRentalServiceApplication;
import org.carRentalService.entities.Cars;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CarRentalServiceApplication.class)
class CarsRepositoryTest {

    @Autowired
    CarsRepository carsRepository;

    @Test
    public void shouldPassWhenSavedObjectCanBeRetrivedFromDb(){

        final Cars audi = carsRepository.save(new Cars("Audi", "A6", 10.0f, 200.0f, "http"));

        final Cars carfromDb = carsRepository.findById(audi.getId()).get();

        assertNotNull(carfromDb);

        assertEquals(audi.getBrand(),carfromDb.getBrand());
    }

}