package com.smarthome.smarthome.device;

import com.smarthome.smarthome.room.Room;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor extends Device
{
    public Sensor() {}

    public Sensor(Long id, String label, Category category, Room room) {
        super(id, label, category, room, false);
    }

    public Sensor(String label, Category category, Room room) {
        super(label, category, room, false);
    }

    public double getDataFeed()
    {
        double value = -1.0;

        try
        {
            URI uri = new URI("coap://localhost:5683/" + getLabel());
            CoapClient client = new CoapClient(uri);

            CoapResponse response = client.get();
            value = Double.parseDouble(response.getResponseText());
        }
        catch (Exception e)
        {
            Logger logger = Logger.getLogger(Sensor.class.getName());

            logger.log(Level.WARNING, e.getMessage());
        }

        return value;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
