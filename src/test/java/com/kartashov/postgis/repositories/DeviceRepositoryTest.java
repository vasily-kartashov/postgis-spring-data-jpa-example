package com.kartashov.postgis.repositories;

import com.kartashov.postgis.Application;
import com.kartashov.postgis.entities.Device;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Before
    public void setUp() throws ParseException {
        Device device1 = new Device();
        device1.setId("de-001");
        Point point1 = (Point) new WKTReader().read("POINT(5 5)");
        point1.setSRID(4326);
        device1.setLocation(point1);
        device1.getStatus().setStateOfCharge(0.2);
        device1.getStatus().setLifeCycle("ready");
        deviceRepository.save(device1);

        Device device2 = new Device();
        device2.setId("de-002");
        Point point2 = (Point) new WKTReader().read("POINT(13 2)");
        point2.setSRID(4326);
        device2.setLocation(point2);
        deviceRepository.save(device2);
    }

    @Test
    public void testFindWithinPolygon() throws ParseException {
        Polygon polygon = (Polygon) new WKTReader().read("POLYGON((0 0,0 10,10 10,10 0,0 0))");
        polygon.setSRID(4326);
        List<Device> devices = deviceRepository.findWithin(polygon);

        System.out.println(devices);

        assertEquals(1, devices.size());
        assertEquals("de-001", devices.get(0).getId());
    }

    @Test
    public void testFindHealthy() throws ParseException {
        List<Device> devices = deviceRepository.findHealthyDevices();

        System.out.println(devices);

        assertEquals(1, devices.size());
        assertEquals("de-001", devices.get(0).getId());
    }
}
