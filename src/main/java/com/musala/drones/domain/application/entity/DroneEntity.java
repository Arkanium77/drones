package com.musala.drones.domain.application.entity;

import com.musala.drones.domain.application.enums.DroneModel;
import com.musala.drones.domain.application.enums.DroneState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drone")
public class DroneEntity {

    @Id // See solution notes
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "weight_limit")
    private Double weightLimit;
    @Column(name = "battery_capacity")
    private Integer batteryCapacity;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private DroneState state;
    @Column(name = "model")
    @Enumerated(EnumType.STRING)
    private DroneModel model;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "drone")
    private List<LoadEntity> load;
}
