package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration test of the Specialty Repository layer.
 * <p>
 * SpecialtyRepository SpringDataJpaTests subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application contrext lookups. See the use of {@link Autowired @Autowired} on the <code>{@link
 * SpecialtyRepositoryTests# specialtyRepository}</code> instance variable, which uses autowiring <em>by
 * type</em>. <li><strong>Transaction maement</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 *
 * @author Jackie Shao
 */@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class SpecialtyRepositoryTests {

    @Autowired
    protected SpecialtyRepository specialtyRepository;

    @Test
    public void shouldFindSpecialtyByName() {
        Specialty specialty = this.specialtyRepository.findByName("radiology");
        Assert.assertEquals("radiology", specialty.getName());
    }

    @Test
    public void shouldFindSpecialtyById() {
        Specialty specialty = this.specialtyRepository.findById(2);
        Assert.assertEquals("surgery", specialty.getName());
    }

    @Test
    public void shouldFindAll() {
        Collection<Specialty> specialties = this.specialtyRepository.findAll();
        assertThat(specialties.size() > 0);
    }
}
