package com.kartashov.postgis.repositories;

import com.kartashov.postgis.entities.Device;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceRepository extends CrudRepository<Device, String> {

    @Query("SELECT d FROM Device AS d WHERE within(d.location, :location) = TRUE")
    List<Device> findWithinPolygon(@Param("location") Geometry location);
}
