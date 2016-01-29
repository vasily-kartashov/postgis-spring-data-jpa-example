package com.kartashov.postgis.entities;

import com.kartashov.postgis.hibernate.Properties;
import com.vividsolutions.jts.geom.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Column(name = "status", nullable = false, columnDefinition = "jsonb")
    private Properties status = new Properties();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Properties getStatus() {
        return status;
    }

    public void setStatus(Properties status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Device " + id + " @ " + location.toText() + ". " + status;
    }
}
