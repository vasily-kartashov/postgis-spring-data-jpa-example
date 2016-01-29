package com.kartashov.postgis.repositories;

import com.kartashov.postgis.entities.Device;
import com.vividsolutions.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceRepository extends CrudRepository<Device, String> {

    @Query("SELECT d FROM Device AS d WHERE within(d.location, :polygon) = TRUE")
    List<Device> findWithin(@Param("polygon") Polygon polygon);

    @Query("SELECT d FROM Device AS d WHERE CAST(extract(d.status, 'stateOfCharge') float) > 0.1")
    List<Device> findHealthyDevices();
}

