/*
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.samples.petclinic.owner;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

    private static final String PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdateVisitForm";
    private static final String Name_DELIMITER = " ";

    private final VisitRepository visits;
    private final PetRepository pets;
    private final VetRepository vets;

    public static enum TimeError {
        NONE,
        BEFORE_CURRENT,
        WRONG_DATE
    };

    public VisitController(VisitRepository visits, PetRepository pets, VetRepository vets) {
        this.visits = visits;
        this.pets = pets;
        this.vets = vets;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    /**
     * Called before each and every @RequestMapping annotated method.
     * 2 goals:
     * - Make sure we always have fresh data
     * - Since we do not use the session scope, make sure that Pet object always has an id
     * (Even though id is not part of the form fields)
     *
     * @param petId
     * @return Pet
     */
    @ModelAttribute("visit")
    public Visit loadPetWithVisit(@PathVariable("petId") int petId, Map<String, Object> model) {
        Pet pet = this.pets.findById(petId);
        model.put("pet", pet);
        Visit visit = new Visit();
        pet.addVisit(visit);
        return visit;
    }

    @ModelAttribute("vets")
    public Collection<String> populateVets() {
        List<String> allVestNames;
        Collection<Vet> allVets = this.vets.findAll();
        if (Objects.nonNull(allVets) && allVets.size() > 0) {
            allVestNames = allVets.stream().map(k ->
                k.getFirstName() + Name_DELIMITER + k.getLastName())
                .collect(Collectors.toList());
        }
        else {
            allVestNames = new ArrayList<>();
        }
        return allVestNames;
    }

    // Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is called
    @GetMapping("/owners/*/pets/{petId}/visits/new")
    public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
        return "pets/createOrUpdateVisitForm";
    }

    // Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is called
    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    public String processNewVisitForm(@Valid Visit visit, BindingResult result, Map<String, Object> model) {
        TimeError timeError = validateAppointment(visit);
        switch (timeError) {
            case BEFORE_CURRENT:
                result.rejectValue("time", "past", visit.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00")) + " is in the past.");
                break;
            case WRONG_DATE:
                result.rejectValue("time", "weekend", "The day is a " + visit.getTime().getDayOfWeek());
                break;
            default:
                break;
        }
        if (result.hasErrors()) {
            return PETS_CREATE_OR_UPDATE_FORM;
        }
        else {
            String vetFullName = visit.getInputVetFullName();
            String[] vetFullNameArr = vetFullName.split(Name_DELIMITER);
            String firstName = vetFullNameArr[0];

            String lastName = vetFullNameArr[1];

            Collection<Vet> found_vets = this.vets.findByFirstAndLastName(firstName,
                lastName);
            if (Objects.isNull(found_vets) || found_vets.isEmpty()) {
                // no vets found
                result.rejectValue("inputVetFullName", "notFound", vetFullName + " is not found.");
                return PETS_CREATE_OR_UPDATE_FORM;
            } else if (found_vets.size() > 1) {
                result.rejectValue("inputVetFullName", "multiFound", "Found Multiple Vets with name " + vetFullName);
                return PETS_CREATE_OR_UPDATE_FORM;
            }
            else {
                Vet vet = found_vets.iterator().next();
                LocalDateTime appointmentTime = visit.getTime();
                if (isTimeTaken(vet, appointmentTime)) {
                    result.rejectValue("inputVetFullName", "taken", vetFullName + " already has appointment at " + appointmentTime);
                    return PETS_CREATE_OR_UPDATE_FORM;
                }
                visit.setVet(vet);
                this.visits.save(visit);
                return "redirect:/owners/{ownerId}";
            }
        }
    }

    private static TimeError validateAppointment(Visit visit) {
        LocalDateTime visit_time = visit.getTime();
        if (LocalDateTime.now().isAfter(visit_time)) {
            return TimeError.BEFORE_CURRENT;
        }

        DayOfWeek dayOfWeek = visit_time.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return  TimeError.WRONG_DATE;
        }
        else return TimeError.NONE;
    }

    private boolean isTimeTaken(Vet vet, LocalDateTime appointmentTime) {
        Collection<Visit> same_time_visits = visits.findByVetAndTime(vet.getId(), appointmentTime);
        if (Objects.nonNull(same_time_visits) && same_time_visits.size() > 0) {
            return true;
        }
        else return false;
    }

}
