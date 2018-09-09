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
package org.springframework.samples.petclinic.visit;

import java.util.Map;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author jshao
 */
@Controller
class OtherVisitController {

    private static final String PETS_DELETE_APPOINMENT_FORM = "pets/deleteVisitForm";

    private final VisitRepository visits;

    public OtherVisitController(VisitRepository visits) {
        this.visits = visits;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/cancel")
    public String initVisitDeleteForm(@PathVariable("visitId") Integer visitId, Map<String, Object> model) {
        Visit visit = visits.findById(visitId);
        if (visit != null) {
            model.put("visit", visit);
        }
        return PETS_DELETE_APPOINMENT_FORM;
    }

    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/cancel")
    public String processVisitDeleteForm(@PathVariable("visitId") Integer visitId, Visit visit, BindingResult result, Map<String, Object> model) {
        visits.removeById(visitId);
        return "redirect:/owners/{ownerId}";
    }

}
