package org.springframework.samples.petclinic.vet;

import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.util.Lists;
import org.hamcrest.core.StringStartsWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Test class for the {@link VetController}
 */
@RunWith(SpringRunner.class)
@WebMvcTest(VetController.class)
public class VetControllerTests {

    private static final int TEST_VET_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetRepository vets;

    @MockBean
    private SpecialtyRepository specialtyRepository;

    @Before
    public void setup() {
        Vet james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setId(1);
        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setId(2);
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        helen.addSpecialty(radiology);
        given(this.vets.findAll()).willReturn(Lists.newArrayList(james, helen));
    }

    @Test
    public void testShowVetListHtml() throws Exception {
        mockMvc.perform(get("/vets.html"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("vets"))
            .andExpect(view().name("vets/vetList"));
    }

    @Test
    public void testShowResourcesVetList() throws Exception {
        ResultActions actions = mockMvc.perform(get("/vets")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        actions.andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.vetList[0].id").value(1));
    }

    @Test
    public void testShowVetListXml() throws Exception {
        mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_XML))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_XML_VALUE))
            .andExpect(content().node(hasXPath("/vets/vetList[id=1]/id")));
    }

    @Test
    public void testNewVetGetRequest() throws Exception {
        mockMvc.perform(get("/vets/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("vets/createOrUpdateVetForm"));
    }

    @Test
    public void testNewVetPostRequest() throws Exception {
        mockMvc.perform(post("/vets/new")
            .param("firstName","Bob")
            .param("lastName", "Handsaker"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name(new StringStartsWith("redirect:/vets/")));
    }

    @Test
    public void testNewVetPostRequestNoLastName() throws Exception {
        mockMvc.perform(post("/vets/new")
            .param("firstName","Bob"))
            .andExpect(model().attributeHasErrors("vet"))
            .andExpect(status().isOk())
            .andExpect(view().name("vets/createOrUpdateVetForm"));
    }

    @Test
    public void testNewVetPostRequestNoFirstName() throws Exception {
        mockMvc.perform(post("/vets/new")
            .param("lastName","Handsaker"))
            .andExpect(model().attributeHasErrors("vet"))
            .andExpect(status().isOk())
            .andExpect(view().name("vets/createOrUpdateVetForm"));
    }

    @Test
    public void testEditVetGetRequest() throws Exception {
        Vet vet = new Vet();
        given(this.vets.findById(anyInt())).willReturn(vet);
        mockMvc.perform(get("/vets/{vetId}/edit", TEST_VET_ID))
            .andExpect(status().isOk())
            .andExpect(view().name("vets/createOrUpdateVetForm"));
    }
}
