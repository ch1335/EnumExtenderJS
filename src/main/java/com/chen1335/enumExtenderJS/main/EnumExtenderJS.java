package com.chen1335.enumExtenderJS.main;

import com.chen1335.enumExtenderJS.main.script.EnumExtenderContext;
import com.chen1335.enumExtenderJS.main.script.EnumExtenderJSFile;
import com.mojang.logging.LogUtils;
import dev.latvian.mods.rhino.ContextFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(EnumExtenderJS.MODID)
public class EnumExtenderJS {
    public static final String MODID = "enum_extender_js";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnumExtenderJS(IEventBus modEventBus, ModContainer modContainer) {

    }
}
