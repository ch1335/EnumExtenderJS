package com.chen1335.enumExtenderJS.main.script;

import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;

public class ExtenderJavaWrapper {
    public static final ClassFilter CF = new ClassFilter(null);

    public static Object loadClass(EnumExtenderContext cx, String className) {
        if (CF.isAllowed(className)) {
            return cx.loadJavaClass(className);
        } else {
            throw new Error(className + "is not allowed to be load");
        }
    }

    static {
        ClassFilter filter = CF;
        //kubejs
        filter.deny("java.lang");
        filter.allow("java.lang.Number");
        filter.allow("java.lang.String");
        filter.allow("java.lang.Character");
        filter.allow("java.lang.Byte");
        filter.allow("java.lang.Short");
        filter.allow("java.lang.Integer");
        filter.allow("java.lang.Long");
        filter.allow("java.lang.Float");
        filter.allow("java.lang.Double");
        filter.allow("java.lang.Boolean");
        filter.allow("java.lang.Runnable");
        filter.allow("java.lang.Iterable");
        filter.allow("java.lang.Comparable");
        filter.allow("java.lang.CharSequence");
        filter.allow("java.lang.Void");
        filter.allow("java.lang.Package");
        filter.allow("java.lang.Appendable");
        filter.allow("java.lang.AutoCloseable");
        filter.allow("java.lang.Comparable");
        filter.allow("java.lang.Iterable");
        filter.allow("java.lang.Object");
        filter.allow("java.lang.Runnable");
        filter.allow("java.lang.StringBuilder");
        filter.allow("java.math.BigInteger");
        filter.allow("java.math.BigDecimal");
        filter.deny("java.io");
        filter.allow("java.io.Closeable");
        filter.allow("java.io.Serializable");
        filter.deny("java.nio");
        filter.allow("java.nio.ByteOrder");
        filter.allow("java.util");
        filter.deny("java.util.jar");
        filter.deny("java.util.zip");
        filter.allow("it.unimi.dsi.fastutil");
        filter.allow("dev.latvian.mods.kubejs");
        filter.deny("dev.latvian.mods.kubejs.script");
        filter.deny("dev.latvian.mods.kubejs.plugin");
        filter.deny("dev.latvian.mods.kubejs.mixin");
        filter.deny(KubeJSPlugin.class);
        filter.deny(KubeJSPlugins.class);
        filter.allow("net.minecraft");
        filter.allow("com.mojang.authlib.GameProfile");
        filter.allow("com.mojang.util.UUIDTypeAdapter");
        filter.allow("com.mojang.brigadier");
        filter.allow("com.mojang.blaze3d");
        filter.allow("dev.architectury");
        filter.deny("java.net");
        filter.deny("sun");
        filter.deny("com.sun");
        filter.deny("io.netty");
        filter.deny("org.objectweb.asm");
        filter.deny("org.spongepowered.asm");
        filter.deny("org.openjdk.nashorn");
        filter.deny("jdk.nashorn");
        filter.deny("org.lwjgl.system");
        filter.allow("net.neoforged");
        filter.deny("net.neoforged.fml");
        filter.deny("net.neoforged.accesstransformer");
        filter.deny("net.neoforged.coremod");
        filter.deny("cpw.mods.modlauncher");
        filter.deny("cpw.mods.gross");
        filter.allow("mezz.jei");
        //probejs
        filter.deny("org.jetbrains.java.decompiler");
        filter.deny("com.github.javaparser");
        filter.deny("org.java_websocket");
    }
}
