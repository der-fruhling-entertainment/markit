package net.derfruhling.minecraft.markit;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Markit {
	public static final String MOD_ID = "markit";
	private static final Logger LOG = LogUtils.getLogger();

	public static void init() {
		LOG.info("marking it");
	}
}
