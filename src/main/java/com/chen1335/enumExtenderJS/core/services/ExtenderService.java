package com.chen1335.enumExtenderJS.core.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class ExtenderService implements ITransformationService {
    private final Set<ITransformer.Target<ClassNode>> targets = new HashSet<>();

    public static final Map<String, String> ENUM_EXTENDER_SCRIPTS = new HashMap<>();

    public static Path PATH = FMLPaths.GAMEDIR.get().resolve("kubejs").resolve("enum_extender_scripts");

    @Override
    public @NotNull String name() {
        return "ExtenderService";
    }

    @Override
    public void initialize(IEnvironment environment) {
        PATH.toFile().mkdirs();
        final Path kubejsDir = environment.getProperty(IEnvironment.Keys.GAMEDIR.get()).orElseThrow(() -> new RuntimeException("No game path found")).resolve("kubejs");
        kubejsDir.toFile().mkdirs();
        final Path config = kubejsDir.resolve("enum_extender.json");
        final File configFile = config.toFile();
        if (!configFile.exists()) {
            try (Writer writer = new FileWriter(configFile)) {
                JsonWriter jsonWriter = new JsonWriter(writer);
                JsonArray jsonArray = new JsonArray();
                Streams.write(jsonArray, jsonWriter);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (final InputStream inputStream = new FileInputStream(configFile)) {
            JsonElement jsonElement = JsonParser.parseReader(new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
            JsonArray array = jsonElement.getAsJsonArray();
            for (JsonElement element : array) {
                String target = element.getAsString();
                String[] split = target.split("-");

                String targetClass = split[0];
                targets.add(ITransformer.Target.targetClass(targetClass));
                ENUM_EXTENDER_SCRIPTS.put(targetClass, split[1]);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {

    }

    @Override
    public @NotNull List<? extends ITransformer<?>> transformers() {
        return List.of(new ExtenderTransformer(targets));
    }
}
