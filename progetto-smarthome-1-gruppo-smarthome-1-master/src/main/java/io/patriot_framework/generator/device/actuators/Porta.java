package io.patriot_framework.generator.device.actuators;

import io.patriot_framework.generator.device.passive.actuators.AbstractActuator;
import io.patriot_framework.generator.device.passive.actuators.StateMachine;

public class Porta extends AbstractActuator
{
    public Porta(String label)
    {
        super(label);
        StateMachine sm = new StateMachine()
                .addState("Chiusa")
                .addState("Apertura", 5000)
                .addState("Aperta")
                .addState("Chiusura", 5000)
                .build();

        setStateMachine(sm);
    }
}
