package com.chen1335.enumExtenderJS.main.script;

import com.chen1335.enumExtenderJS.core.JSEnumExtender;
import com.chen1335.enumExtenderJS.core.services.ExtenderService;
import com.mojang.logging.LogUtils;
import dev.latvian.mods.rhino.ContextFactory;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

public class EnumExtenderJSFile {
    public static Path PATH = FMLPaths.GAMEDIR.get().resolve("kubejs").resolve("enum_extender_scripts");

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void load(String className) {
        Map<String, String> enumExtenderScripts = ExtenderService.ENUM_EXTENDER_SCRIPTS;
        String extenderScriptName = enumExtenderScripts.get(className) + ".js";
        Path extenderScriptFile = PATH.resolve(extenderScriptName);

        LOGGER.info("Loading EnumExtender script: {} from {}", extenderScriptName, className);
        File enumExtenderJsFile = extenderScriptFile.toFile();
        if (!enumExtenderJsFile.exists()) {
            throw new Error("script: " + extenderScriptName + " is not exists");
        }
        EnumExtenderContext context = new EnumExtenderContext(new ContextFactory().enter().factory);
        try (Reader reader = new FileReader(enumExtenderJsFile)) {
            context.evaluateReader(context.topLevelScope, reader, extenderScriptName, 0, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSEnumExtender.LOADED_EXTENDER.add(className);
    }
}
