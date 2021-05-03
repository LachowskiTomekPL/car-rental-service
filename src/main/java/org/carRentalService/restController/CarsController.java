package org.carRentalService.restController;

import org.carRentalService.AuthenticationRequest;
import org.carRentalService.AuthenticationResponse;
import org.carRentalService.JwtUtil;
import org.carRentalService.MyUserDetailsService;
import org.carRentalService.entities.Cars;
import org.carRentalService.service.CarsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController

public class CarsController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;



    @Autowired
    private CarsService carsService;

    @GetMapping("/car/{id}")
    public Cars getCarById(@PathVariable String id){

        return carsService.getCarById(Integer.valueOf(id));
    }

    @RequestMapping("/car")
    public Iterable<Cars> getAllCars(){

        return carsService.getAllCars();
    }



    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> createToken (@RequestBody AuthenticationRequest authenticationRequest) throws Exception{

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);


        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
