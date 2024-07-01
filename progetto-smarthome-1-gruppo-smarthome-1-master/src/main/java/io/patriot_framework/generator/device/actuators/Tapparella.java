package io.patriot_framework.generator.device.actuators;

import io.patriot_framework.generator.device.passive.actuators.AbstractActuator;
import io.patriot_framework.generator.device.passive.actuators.StateMachine;

public class Tapparella extends AbstractActuator
{
    public Tapparella(String label)
    {
        super(label);
        StateMachine sm = new StateMachine()
                .addState("Chiusa")
                .addState("Apertura", 8000)
                .addState("Aperta")
                .addState("Chiusura", 8000)
                .build();

        setStateMachine(sm);
    }
}
