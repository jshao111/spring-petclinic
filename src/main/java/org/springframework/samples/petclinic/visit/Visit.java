/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.visit;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.vet.Vet;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Dave Syer
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

    @Column(name = "visit_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:00")
    private LocalDateTime time;

    @NotBlank
    @Column(name = "description")
    private String description;

    @Column(name = "pet_id")
    private Integer petId;

    @ManyToOne
    @JoinColumn(name = "vet_id")
    private Vet vet;

    @Transient
    private String inputVetFullName;

    /**
     * Creates a new instance of Visit for the current date
     */
    public Visit() {
        this.time = LocalDateTime.now();
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPetId() {
        return this.petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public Vet getVet() {
        return vet;
    }

    public void setVet(Vet vet) {

        this.vet = vet;
    }

    public String getInputVetFullName() {
        return inputVetFullName;
    }

    public void setInputVetFullName(String inputVetFullName) {
        this.inputVetFullName = inputVetFullName;
    }

    public boolean isAppointment() {
        return this.getTime().isAfter(LocalDateTime.now());
    }
}
