package io.patriot_framework.generator.device.actuators;

import io.patriot_framework.generator.device.passive.actuators.AbstractActuator;
import io.patriot_framework.generator.device.passive.actuators.StateMachine;

public class Pulizia extends AbstractActuator
{
    public Pulizia(String label)
    {
        super(label);
        StateMachine sm = new StateMachine()
                            .addState("In Carica")
                            .addState("In funzione", 30000)
                            .build();

        setStateMachine(sm);
    }
}
