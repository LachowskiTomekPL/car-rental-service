package org.carRentalService.entities;

import javax.persistence.*;

@Entity
public class Cars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String brand;

    private String model;

    @Column(name = "avg_consumption")
    private float avgConsumption;

    private float power;

    @Column(name = "car_image")
    private String carImage;

    public Cars() {
    }

    public Cars(String brand, String model, float avgConsumption, float power, String carImage) {
        this.brand = brand;
        this.model = model;
        this.avgConsumption = avgConsumption;
        this.power = power;
        this.carImage = carImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public float getAvgConsumption() {
        return avgConsumption;
    }

    public void setAvgConsumption(float avgConsumption) {
        this.avgConsumption = avgConsumption;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    @Override
    public String toString() {
        return "Cars{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", avgConsumption=" + avgConsumption +
                ", power=" + power +
                ", carImage='" + carImage + '\'' +
                '}';
    }
}
