package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeBuilder;
import fr.unice.polytech.cookieFactory.Shop.Schedule;
import org.junit.Before;
import org.junit.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScheduleTest {

    Schedule scheduleManager;


    @Before
    public void setUp() {
        scheduleManager = new Schedule();
        scheduleManager.addScheduleOfDay(LocalTime.of(8,30), LocalTime.of(18,0), LocalDate.of(2018,7,12));
        scheduleManager.addScheduleOfDay(LocalTime.of(8,30), LocalTime.of(18,0), LocalDate.of(2018,7,25));
    }

    @Test
    public void test_schedule() {
        LocalTime pickupTime = LocalTime.of(14,30);

        assertTrue(scheduleManager.isDuringSchedule(pickupTime,LocalDate.of(2018,7,12)));
        assertTrue(scheduleManager.isDuringSchedule(pickupTime, LocalDate.of(2018,7,25)));
        scheduleManager.changeOpeningScheduleOfDay(15, 45, LocalDate.of(2018,7,25));
        assertTrue(scheduleManager.isDuringSchedule(pickupTime, LocalDate.of(2018,7,12)));
        assertFalse(scheduleManager.isDuringSchedule(pickupTime, LocalDate.of(2018,7,25)));
//        assertTrue(shop.getShopSchedule().)
    }

    @Test
    public void test_isDuringSchedule() {
        assertTrue(scheduleManager.isDuringSchedule(LocalTime.of(10,00), LocalDate.of(2018,7,12)));
        assertTrue(scheduleManager.isDuringSchedule(10, 00, LocalDate.of(2018,7,25)));

        assertFalse(scheduleManager.isDuringSchedule(LocalTime.of(7,20), LocalDate.of(2018,7,12)));

        assertFalse(scheduleManager.isDuringSchedule(23, 20, LocalDate.of(2018,7,25)));
    }

    @Test
    public void test_changeSchedule() {

        LocalDate date = LocalDate.of(2012,12,12);
        scheduleManager.addScheduleOfDay(LocalTime.of(8,30), LocalTime.of(17,50), date);
        assertEquals("8h30, 17h50", scheduleManager.getScheduleIntervalOfDay(date).toString());

        scheduleManager.changeScheduleOfDay(LocalTime.of(10,30), LocalTime.of(15,35), date);
        assertEquals("10h30, 15h35", scheduleManager.getScheduleIntervalOfDay(date).toString());
    }
}
