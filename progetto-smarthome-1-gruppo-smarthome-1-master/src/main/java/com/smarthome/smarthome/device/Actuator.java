package com.smarthome.smarthome.device;


import com.smarthome.smarthome.room.Room;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Actuator extends Device
{
    public Actuator() {}

    public Actuator(Long id, String label, Category category, Room room)
    {
        super(id, label, category, room, true);
    }

    public Actuator(String label, Category category, Room room) {
        super(label, category, room, true);
    }

    public void controlSignal()
    {
        try
        {
            URI uri = new URI("coap://localhost:5683/" + getLabel());
            CoapClient client = new CoapClient(uri);

            client.post("", 0);
        }
        catch (Exception e)
        {
            Logger logger = Logger.getLogger(Actuator.class.getName());

            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public String getCurrentState()
    {
        String value = "";

        try
        {
            URI uri = new URI("coap://localhost:5683/" + getLabel());
            CoapClient client = new CoapClient(uri);

            CoapResponse response = client.get();
            value = response.getResponseText();
        }
        catch (Exception e)
        {
            Logger logger = Logger.getLogger(Actuator.class.getName());

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
