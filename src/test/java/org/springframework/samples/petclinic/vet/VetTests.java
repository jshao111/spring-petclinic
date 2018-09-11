/*
 * Copyright 2016-2017 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.Assert;
import org.springframework.util.SerializationUtils;

/**
 * @author Dave Syer
 *
 */
public class VetTests {

    @Test
    public void testSerialization() {
        Vet vet = new Vet();
        vet.setFirstName("Zaphod");
        vet.setLastName("Beeblebrox");
        vet.setId(123);
        Vet other = (Vet) SerializationUtils
                .deserialize(SerializationUtils.serialize(vet));
        assertThat(other.getFirstName()).isEqualTo(vet.getFirstName());
        assertThat(other.getLastName()).isEqualTo(vet.getLastName());
        assertThat(other.getId()).isEqualTo(vet.getId());
    }

    @Test
    public void testAddSpecialty() {
        Vet vet = new Vet();
        Specialty specialty1 = new Specialty();
        specialty1.setName("surgery");
        vet.addSpecialty(specialty1);
        Assert.assertEquals(1, vet.getNrOfSpecialties());

        Specialty specialty2 = new Specialty();
        specialty2.setName("dentistry");
        vet.addSpecialty(specialty2);
        Assert.assertEquals(2, vet.getNrOfSpecialties());

        vet.addSpecialty(specialty1);
        Assert.assertEquals(2, vet.getNrOfSpecialties());

        vet.addSpecialty(null);
        Assert.assertEquals(2, vet.getNrOfSpecialties());

        vet.addSpecialty(new Specialty());
        Assert.assertEquals(2, vet.getNrOfSpecialties());

        Assert.assertEquals("dentistry", vet.getSpecialties().get(0).getName());
    }
}
