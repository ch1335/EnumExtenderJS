package com.chen1335.enumExtenderJS.core.services;

import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.JarContents;
import net.neoforged.neoforgespi.locating.*;
import org.slf4j.Logger;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class EnumExtenderModDependencyLocator implements IDependencyLocator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MAIN_MOD_NAME = "enum_extender_js-2101.1.0.jar";

    @Override
    public void scanMods(List<IModFile> loadedMods, IDiscoveryPipeline pipeline) {
        URL mainModURL = this.getClass().getClassLoader().getResource("META-INF/mainMod/" + MAIN_MOD_NAME);

        if (mainModURL == null) {
            LOGGER.error("[EnumExtenderJS] main mod not find!");
            throw new Error();
        } else {
            LOGGER.info("main mod find!");
            try {
                LOGGER.info("[EnumExtenderJS] try load mainMod form:{}", Path.of(mainModURL.toURI()).toAbsolutePath());
                pipeline.addJarContent(JarContents.of(Path.of(mainModURL.toURI())), ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.WARN_ALWAYS);
            } catch (URISyntaxException e) {
                LOGGER.error(e.toString());
                throw new RuntimeException(e);
            }
        }
    }
}
