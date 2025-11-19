package com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightData {
    private String departureTime;
    private String arrivalTime;
    private String airlineName;
    private double price;
    private String connectionInfo;
    private String duration;

    @Override
    public String toString() {
        return "FlightData{" +
                "departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", airlineName='" + airlineName + '\'' +
                ", price=" + price +
                ", connectionInfo='" + connectionInfo + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}