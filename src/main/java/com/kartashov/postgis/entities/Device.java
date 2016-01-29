package com.kartashov.postgis.entities;

import com.kartashov.postgis.types.StatusType;
import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "devices")
@TypeDefs({
        @TypeDef(name = "deviceStatusType", typeClass = StatusType.class)
})
public class Device {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Column(name = "status", nullable = false, columnDefinition = "jsonb")
    @Type(type = "deviceStatusType")
    private Status status = new Status();

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Device " + id + " @ " + location.toText() + ". " + status;
    }
}
