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

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Before
    public void setUp() throws ParseException {
        Device device = new Device();
        device.setId("de-001");
        Point point = (Point) new WKTReader().read("POINT(5 5)");
        device.setLocation(point);
        deviceRepository.save(device);
    }

    @Test
    public void testFindWithinPolygon() throws ParseException {
        Polygon polygon = (Polygon) new WKTReader().read("POLYGON((0 0,0 10,10 10,10 0,0 0))");
        List<Device> devices = deviceRepository.findWithinPolygon(polygon);

        System.out.println(devices);
    }
}
