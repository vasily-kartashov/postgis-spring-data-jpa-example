package com.kartashov.postgis.search;

import com.kartashov.postgis.Application;
import com.kartashov.postgis.entities.Device;
import com.kartashov.postgis.repositories.DeviceRepository;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class SearchTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SearchService searchService;

    @Before
    public void setUp() throws ParseException {
        Device device1 = new Device();
        device1.setId("de-001");
        Point point1 = (Point) new WKTReader().read("POINT(-37.814 144.963)");
        point1.setSRID(4326);
        device1.setLocation(point1);
        device1.getStatus().setStateOfCharge(0.2);
        device1.getStatus().setLifeCycle("ready");
        deviceRepository.save(device1);

        Device device2 = new Device();
        device2.setId("de-002");
        Point point2 = (Point) new WKTReader().read("POINT(-37.814 144.963)");
        point2.setSRID(4326);
        device2.setLocation(point2);
        device1.getStatus().setStateOfCharge(0.03);
        deviceRepository.save(device2);
    }

    @Test
    public void testSearch() throws IOException {
        List<Device> devices = searchService.search("location within 10 km from (-37.814, 144.963) and status.stateOfCharge < 10%");

        System.out.println(devices);

        assertEquals(1, devices.size());
        assertEquals("de-002", devices.get(0).getId());
    }
}
