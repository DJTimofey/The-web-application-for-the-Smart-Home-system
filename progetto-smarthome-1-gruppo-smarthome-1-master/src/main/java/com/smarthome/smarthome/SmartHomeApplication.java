package com.smarthome.smarthome;

import com.smarthome.smarthome.agent.AgentePulizia;
import com.smarthome.smarthome.device.*;
import com.smarthome.smarthome.emergenza.EmergenzaRepository;
import com.smarthome.smarthome.rilevation.RilevationRepository;
import com.smarthome.smarthome.room.Room;
import com.smarthome.smarthome.room.RoomRepository;
import com.smarthome.smarthome.user.User;
import com.smarthome.smarthome.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Timer;

@SpringBootApplication
public class SmartHomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHomeApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository userRepo, RoomRepository roomRepo, DeviceRepository deviceRepo, RilevationRepository rilevationRepo, EmergenzaRepository emergencyRepo){
		return args -> {
			// Create users
			User u1 = new User(
					"Federico Lanzani",
					"federico@lanzani.com"
			);
			User u2 = new User(
					"Lorenzo Mazzoletti",
					"lorenzo@mazzoletti.com"
			);
			User u3 = new User(
					"Fabio Cavaleri",
					"fabio@cavaleri.com"
			);
			User u4 = new User(
					"Davide Pirolo",
					"davide@pirolo.com"
			);
			userRepo.saveAll(List.of(u1, u2, u3, u4));

			// Create rooms
			Room r1 = new Room("Cucina");
			Room r2 = new Room("Soggiorno");
			Room r3 = new Room("Bagno");
			Room r4 = new Room("Camera");
			Room r5 = new Room("Camera Ospiti");
			roomRepo.saveAll(List.of(r1, r2, r3, r4, r5));

			// SENSORI

			Device d1 = new Device("thermometer1", Category.TERMOMETRO, r1, false);
			Device d2 = new Device("thermometer2", Category.TERMOMETRO, r2, false);
			Device d3 = new Device("thermometer3", Category.TERMOMETRO, r3, false);
			Device d4 = new Device("thermometer4", Category.TERMOMETRO, r4, false);
			Device d5 = new Device("thermometer5", Category.TERMOMETRO, r5, false);

			Device d6 = new Device("movement1", Category.MOVIMENTO, r1, false);
			Device d7 = new Device("movement2", Category.MOVIMENTO, r2, false);
			Device d8 = new Device("movement3", Category.MOVIMENTO, r3, false);
			Device d9 = new Device("movement4", Category.MOVIMENTO, r4, false);
			Device d10 = new Device("movement5", Category.MOVIMENTO, r5, false);

			Device d11 = new Device("smoke1", Category.FUMO, r1, false);
			Device d12 = new Device("smoke2", Category.FUMO, r2, false);
			Device d13 = new Device("smoke3", Category.FUMO, r3, false);
			Device d14 = new Device("smoke4", Category.FUMO, r4, false);
			Device d15 = new Device("smoke5", Category.FUMO, r5, false);

			Device d16 = new Device("gas1", Category.GAS, r1, false);
			Device d17 = new Device("gas2", Category.GAS, r2, false);
			Device d18 = new Device("gas3", Category.GAS, r3, false);
			Device d19 = new Device("gas4", Category.GAS, r4, false);
			Device d20 = new Device("gas5", Category.GAS, r5, false);

			// ATTUATORI
			Device d21 = new Device("condizionatore1", Category.CONDIZIONATORE, r1, true);
			Device d22 = new Device("condizionatore2", Category.CONDIZIONATORE, r2, true);
			Device d23 = new Device("condizionatore3", Category.CONDIZIONATORE, r3, true);
			Device d24 = new Device("condizionatore4", Category.CONDIZIONATORE, r4, true);
			Device d25 = new Device("condizionatore5", Category.CONDIZIONATORE, r5, true);

			Device d26 = new Device("termosifone1", Category.TERMOSIFONE, r1, true);
			Device d27 = new Device("termosifone2", Category.TERMOSIFONE, r2, true);
			Device d28 = new Device("termosifone3", Category.TERMOSIFONE, r3, true);
			Device d29 = new Device("termosifone4", Category.TERMOSIFONE, r4, true);
			Device d30 = new Device("termosifone5", Category.TERMOSIFONE, r5, true);

			Device d31 = new Device("finestra1", Category.FINESTRA, r1, true);
			Device d32 = new Device("finestra2", Category.FINESTRA, r2, true);
			Device d33 = new Device("finestra3", Category.FINESTRA, r3, true);
			Device d34 = new Device("finestra4", Category.FINESTRA, r4, true);
			Device d35 = new Device("finestra5", Category.FINESTRA, r5, true);

			Device d36 = new Device("pulizia", Category.PULIZIA, r2, true);

			Device d37 = new Device("luce1", Category.LAMPADA, r1, true);
			Device d38 = new Device("luce2", Category.LAMPADA, r2, true);
			Device d39 = new Device("luce3", Category.LAMPADA, r2, true);
			Device d40 = new Device("luce4", Category.LAMPADA, r3, true);
			Device d41 = new Device("luce5", Category.LAMPADA, r4, true);
			Device d42 = new Device("luce5", Category.LAMPADA, r5, true);

			deviceRepo.saveAll(List.of(d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13, d14, d15, d16, d17, d18, d19, d20, d21, d22, d23, d24, d25, d26, d27, d28, d29, d30, d31, d32, d33, d34, d35, d36, d37, d38, d39, d40, d41, d42));


			AgentePulizia cleaning = new AgentePulizia(new Actuator(d36.getId(), d36.getLabel(), d36.getCategory(), d36.getRoom()), 60, 90000);
			Timer timer = new Timer();
			timer.schedule(cleaning, cleaning.getDelay(), cleaning.getInterval());
		};
	}


}
