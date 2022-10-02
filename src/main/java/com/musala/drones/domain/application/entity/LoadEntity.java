package com.musala.drones.domain.application.entity;

import com.musala.drones.domain.application.utils.NanoId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "load")
public class LoadEntity {

    @Id
    @Column(name = "id")
    private String id;

    @JoinColumn(name = "drone")
    @ManyToOne(fetch = FetchType.LAZY)
    private DroneEntity drone;

    @JoinColumn(name = "medication")
    @ManyToOne(fetch = FetchType.LAZY)
    private MedicationEntity medication;

    @PrePersist
    private void prePersist() { // See solution notes
        if (id == null || id.isBlank()) {
            id = NanoId.next();
        }
    }
}
