package com.tsystems.logistics.controller;

import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.service.WaypointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/waypoints")
public class WaypointController {

    @Autowired
    private WaypointService waypointService;

    @GetMapping
    public ResponseEntity<List<Waypoint>> getAllWaypoints() {
        return ResponseEntity.ok(waypointService.getAllWaypoints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Waypoint> getWaypointById(@PathVariable Integer id) {
        return ResponseEntity.ok(waypointService.getWaypointById(id));
    }

    @PostMapping
    public ResponseEntity<Waypoint> addWaypoint(@RequestBody Waypoint waypoint) {
        return ResponseEntity.ok(waypointService.addWaypoint(waypoint));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Waypoint> updateWaypoint(@PathVariable Integer id, @RequestBody Waypoint waypoint) {
        waypoint.setId(id);
        return ResponseEntity.ok(waypointService.updateWaypoint(waypoint));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaypoint(@PathVariable Integer id) {
        waypointService.deleteWaypoint(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reorder/{orderId}")
    public ResponseEntity<Void> reorderWaypoints(
            @PathVariable Integer orderId,
            @RequestBody List<Integer> waypointIds
    ) {
        waypointService.reorderWaypoints(orderId, waypointIds);
        return ResponseEntity.ok().build();
    }
}
