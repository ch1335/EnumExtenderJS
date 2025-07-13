package com.chen1335.enumExtenderJS.main.script;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumExtenderContext extends Context {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnumExtenderLogger");
    public final ScriptableObject topLevelScope;

    public EnumExtenderContext(ContextFactory factory) {
        super(factory);
        topLevelScope = this.initSafeStandardObjects();
        addToScope(topLevelScope, "Logger", LOGGER);
        addToScope(topLevelScope, "Java", ExtenderJavaWrapper.class);


    }


    public Object loadJavaClass(String className) {
        try {
            return new NativeJavaClass(this, this.topLevelScope, Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
