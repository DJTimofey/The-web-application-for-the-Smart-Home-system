package com.smarthome.smarthome.agent;

import java.util.concurrent.atomic.AtomicBoolean;

public class AgentiStatus
{
	private static final AtomicBoolean allarme = new AtomicBoolean(false);
	private static final AtomicBoolean luci = new AtomicBoolean(true);
	private static final AtomicBoolean pericoli = new AtomicBoolean(true);
	private static final AtomicBoolean pulizia = new AtomicBoolean(true);
	private static final AtomicBoolean temperatura = new AtomicBoolean(true);

	// Per eliminare un code smell
	private AgentiStatus()
	{
		throw new IllegalStateException("Utility class");
	}

	public static boolean getAllarme() {
		return AgentiStatus.allarme.get();
	}

	public static void setAllarme(boolean allarme)
	{
		AgentiStatus.allarme.set(allarme);
		AgentiStatus.luci.set(!allarme);
	}

	public static boolean getLuci() {
		return AgentiStatus.luci.get();
	}
	public static void setLuci(boolean luci) {
		AgentiStatus.luci.set(luci);
	}
	public static boolean getPericoli() {
		return AgentiStatus.pericoli.get();
	}
	public static void setPericoli(boolean pericoli) {
		AgentiStatus.pericoli.set(pericoli);
	}
	public static boolean getPulizia() {
		return AgentiStatus.pulizia.get();
	}
	public static void setPulizia(boolean pulizia) {
		AgentiStatus.pulizia.set(pulizia);
	}
	public static boolean getTemperatura() {
		return AgentiStatus.temperatura.get();
	}
	public static void setTemperatura(boolean temperatura) {
		AgentiStatus.temperatura.set(temperatura);
	}
}
