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
package org.springframework.samples.petclinic.vet;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

    private final VetRepository vets;
    private final SpecialtyRepository specialtyRepository;

    private static final String VIEWS_VET_CREATE_OR_UPDATE_FORM = "vets/createOrUpdateVetForm";
    private static final String VIEWS_SPECIALTIES_CREATE_FORM = "vets/addSpecialtyForm";

    public VetController(VetRepository clinicService, SpecialtyRepository specialtyRepository) {
        this.vets = clinicService;
        this.specialtyRepository = specialtyRepository;
    }

    @ModelAttribute("specialties")
    public Collection<Specialty> populateVetSpecialties() {
        return this.specialtyRepository.findAll();
    }

    @GetMapping("/vets.html")
    public String showVetList(Map<String, Object> model) {
        // Here we are returning an object of type 'Vets' rather than a collection of Vet
        // objects so it is simpler for Object-Xml mapping
        Vets vets = new Vets();
        vets.getVetList().addAll(this.vets.findAll());
        model.put("vets", vets);
        return "vets/vetList";
    }

    @GetMapping({ "/vets" })
    public @ResponseBody Vets showResourcesVetList() {
        // Here we are returning an object of type 'Vets' rather than a collection of Vet
        // objects so it is simpler for JSon/Object mapping
        Vets vets = new Vets();
        vets.getVetList().addAll(this.vets.findAll());
        return vets;
    }

    @GetMapping("/vets/new")
    public String initCreationForm(Map<String, Object> model) {
        Vet vet = new Vet();
        model.put("vet", vet);
        return VIEWS_VET_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/vets/new")
    public String processCreationForm(@Valid Vet vet, BindingResult result) {
        if (result.hasErrors()) {
            return VIEWS_VET_CREATE_OR_UPDATE_FORM;
        } else {
            this.vets.save(vet);
            return "redirect:/vets/" + vet.getId();
        }
    }

    /**
     * Custom handler for displaying a vet.
     *
     * @param vetId the ID of the vet to display
     * @return a ModelMap with the model attributes for the view
     */
    @GetMapping("/vets/{vetId}")
    public ModelAndView showVet(@PathVariable("vetId") int vetId) {
        ModelAndView mav = new ModelAndView("vets/vetDetails");
        mav.addObject(this.vets.findById(vetId));
        return mav;
    }

    @GetMapping("/vets/{vetId}/edit")
    public String initUpdateVetForm(@PathVariable("vetId") int vetId, Model model) {
        Vet vet = this.vets.findById(vetId);
        model.addAttribute(vet);
        return VIEWS_VET_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/vets/{vetId}/edit")
    public String processUpdateVetForm(@Valid Vet vet, BindingResult result, @PathVariable("vetId") int vetId, ModelMap modelMap) {
        if (result.hasErrors()) {
            return VIEWS_VET_CREATE_OR_UPDATE_FORM;
        } else {
            String firstName = vet.getFirstName();
            String lastName = vet.getLastName();
            vet = this.vets.findById(vetId);
            vet.setId(vetId);
            vet.setFirstName(firstName);
            vet.setLastName(lastName);
            this.vets.save(vet);
            return "redirect:/vets/{vetId}";
        }
    }
    @GetMapping("/vets/{vetId}/specialty/add")
    public String initAddSpecialtyForm(@PathVariable("vetId") int vetId, ModelMap model) {
        Vet vet = this.vets.findById(vetId);
        model.addAttribute(vet);
        return VIEWS_SPECIALTIES_CREATE_FORM;
    }

    @PostMapping("/vets/{vetId}/specialty/add")
    public String processAddSpecialtyForm(Vet vet, @PathVariable("vetId") int vetId, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "redirect:/vets.html";
        }
        String specialtyName = vet.getSpecialtyToAdd();
        Specialty specialty = specialtyRepository.findByName(specialtyName);
        vet = this.vets.findById(vetId);
        vet.addSpecialty(specialty);
        this.vets.save(vet);
        return "redirect:/vets/{vetId}";
    }
}
