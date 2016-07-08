package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RoleUnitTest {
    @Test
    public void testRoleCreation() throws Exception {
        Role godRole = new Role("God");
        assertEquals("God", godRole.getName());
    }

    @Test
    public void testRoleEquality() throws Exception {
        Role godRole1 = new Role("God");
        Role godRole2 = new Role("God");
        assertEquals(godRole1, godRole2);
    }
}
