package org.springframework.samples.petclinic.visit;

import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jshao on 9/11/18.
 */
public class VisitTests {

    @Test
    public void testIsAppointment() {
        Visit visit1 = new Visit();
        visit1.setTime(LocalDateTime.now().plusDays(1));
        Assert.assertTrue(visit1.isAppointment());

        Visit visit2 = new Visit();
        visit2.setTime(LocalDateTime.now().minusDays(1));
        Assert.assertFalse(visit2.isAppointment());
    }
}
