package io.patriot_framework.generator.device.actuators;

import io.patriot_framework.generator.device.passive.actuators.AbstractActuator;
import io.patriot_framework.generator.device.passive.actuators.StateMachine;

public class Condizionatore extends AbstractActuator{
    public Condizionatore(String label)
    {
        super(label);
        StateMachine sm = new StateMachine()
                            .addState("OFF")
                            .addState("Accensione", 5000)
                            .addState("ON")
                            .addState("Spegnimento", 0)
                            .build();

        setStateMachine(sm);
    }

}
