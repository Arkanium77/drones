package com.musala.drones.domain.application.entity;

import com.musala.drones.domain.application.enums.DroneModel;
import com.musala.drones.domain.application.enums.DroneState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drone")
public class DroneEntity {

    @Id
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "model")
    private DroneModel model;
    @Column(name = "weight_limit")
    private Double weightLimit;
    @Column(name = "battery_capacity")
    private Integer batteryCapacity;
    @Column(name = "state")
    private DroneState state;
}
