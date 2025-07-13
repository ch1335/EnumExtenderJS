package com.chen1335.enumExtenderJS.core;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.asm.ListGeneratorAdapter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.Logger;
import sun.misc.Unsafe;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public class JSEnumExtender {
    private static Unsafe unsafe;
    private static final Type ARRAYS = Type.getType("Ljava/util/Arrays;");

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Set<String> LOADED_EXTENDER = new HashSet<>();

    static {

        try {
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T extends Enum<T>> void reflectSetValue(Class<T> tClass, T[] values) {
        try {
            Field $VALUES = tClass.getDeclaredField("$VALUES");
            $VALUES.setAccessible(true);
            Object fieldBase = unsafe.staticFieldBase($VALUES);
            long fieldOffset = unsafe.staticFieldOffset($VALUES);
            unsafe.putObject(fieldBase, fieldOffset, values);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void process(ClassNode classNode) {
        if ((classNode.access & Opcodes.ACC_ENUM) == 0) {
            throw new IllegalStateException("Tried to extend non-enum class enum: " + classNode.name);
        }

        Type classType = Type.getObjectType(classNode.name);
        List<MethodNode> initNodes = new ArrayList<>();
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<init>")) {
                initNodes.add(method);
            }
        }


        int i = 1;
        for (MethodNode initNode : initNodes) {
            ArrayList<Type> argsType = new ArrayList<>(List.of(Type.getType(initNode.desc).getArgumentTypes()));
            argsType.removeFirst();
            argsType.removeFirst();
            String createMethodDesc = buildDesc(classType, argsType);
            MethodNode mv = (MethodNode) classNode.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "create" + i, createMethodDesc, null, null);
            GeneratorAdapter createMvGenerator = new GeneratorAdapter(mv, Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "unsafeCreate" + i, createMethodDesc);
            Label ifExtenderLoaded = createMvGenerator.newLabel();
            Label ifExtenderNotLoaded = createMvGenerator.newLabel();


            createMvGenerator.push(classNode.name.replace("/", "."));
            createMvGenerator.invokeStatic(Type.getType(JSEnumExtender.class), new Method("isExtenderLoaded", "(Ljava/lang/String;)Z"));
            createMvGenerator.visitJumpInsn(Opcodes.IFEQ,ifExtenderNotLoaded);

            createMvGenerator.mark(ifExtenderLoaded);
            createMvGenerator.visitInsn(Opcodes.ACONST_NULL);
            createMvGenerator.visitInsn(Opcodes.ARETURN);

            createMvGenerator.mark(ifExtenderNotLoaded);
            createMvGenerator.newInstance(classType);
            createMvGenerator.dup();
            createMvGenerator.loadArg(0);
            createMvGenerator.getStatic(classType, "$VALUES", Type.getType("[" + classType.getDescriptor()));
            createMvGenerator.arrayLength();
            for (int j = 0; j < argsType.size(); j++) {
                createMvGenerator.loadArg(j + 1);
            }
            createMvGenerator.invokeConstructor(classType, new Method("<init>", initNode.desc));
            int createdLocal = createMvGenerator.newLocal(classType);
            createMvGenerator.storeLocal(createdLocal);
            createMvGenerator.getStatic(classType, "$VALUES", Type.getType("[" + classType.getDescriptor()));
            createMvGenerator.dup();
            createMvGenerator.arrayLength();
            createMvGenerator.push(1);
            createMvGenerator.math(GeneratorAdapter.ADD, Type.INT_TYPE);
            createMvGenerator.invokeStatic(ARRAYS, new Method("copyOf", "([Ljava/lang/Object;I)[Ljava/lang/Object;"));
            createMvGenerator.checkCast(Type.getType("[" + classType.getDescriptor()));
            createMvGenerator.dup();
            createMvGenerator.getStatic(classType, "$VALUES", Type.getType("[" + classType.getDescriptor()));
            createMvGenerator.arrayLength();
            createMvGenerator.loadLocal(createdLocal, classType);
            createMvGenerator.arrayStore(classType);
            createMvGenerator.loadLocal(createdLocal, classType);
            createMvGenerator.invokeVirtual(classType, new Method("getClass", "()Ljava/lang/Class;"));
            createMvGenerator.swap();
            createMvGenerator.invokeStatic(Type.getType(JSEnumExtender.class), new Method("reflectSetValue", "(Ljava/lang/Class;[Ljava/lang/Enum;)V"));
            createMvGenerator.loadLocal(createdLocal, classType);
            createMvGenerator.visitInsn(Opcodes.ARETURN);
            createMvGenerator.visitMaxs(0, 0);
            createMvGenerator.visitEnd();
            i++;
        }

        Optional<MethodNode> $valuesOpt = tryFindMethod(classNode, mth -> mth.name.equals("$values"));

        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<clinit>")) {
                FieldInsnNode $valuesInsn = findFirstPutValue(method, classType.getInternalName());
                ListGeneratorAdapter scriptLoadInsn = new ListGeneratorAdapter(new InsnList());
                scriptLoadInsn.push(classNode.name.replace("/", "."));
                scriptLoadInsn.invokeStatic(Type.getObjectType("com/chen1335/enumExtenderJS/main/script/EnumExtenderJSFile"), new Method("load", "(Ljava/lang/String;)V"));
                method.instructions.insert($valuesInsn, scriptLoadInsn.insnList);
            }
        }

        printByteCod(classNode);
    }

    public static boolean isExtenderLoaded(String className) {
        return JSEnumExtender.LOADED_EXTENDER.contains(className);
    }

    private static void printByteCod(ClassNode classNode) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TraceClassVisitor traceVisitor = new TraceClassVisitor(
                null,
                new Textifier(),
                pw
        );

        // 将 ClassNode 转换为字节码文本
        classNode.accept(traceVisitor);
        LOGGER.info(sw.toString());
    }

    private static void buildPrint(ListGeneratorAdapter adapter) {
        adapter.dup();
        adapter.invokeStatic(Type.getType(JSEnumExtender.class), new Method("print", "(Ljava/lang/Object;)V"));
    }

    public static void print(Object o) {
        LOGGER.info(String.valueOf(o));
    }


    private static Optional<MethodNode> tryFindMethod(ClassNode classNode, Predicate<MethodNode> predicate) {
        return classNode.methods.stream()
                .filter(predicate)
                .findFirst();
    }

    private static String buildDesc(Type classType, ArrayList<Type> argsType) {
        Type sign = Type.getType(String.class);
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(sign.getDescriptor());
        for (Type argumentType : argsType) {
            builder.append(argumentType.getDescriptor());
        }
        builder.append(')');
        builder.append(classType.getDescriptor());
        return builder.toString();
    }

    private static FieldInsnNode findFirstPutValue(MethodNode method, String owner) {
        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode node = method.instructions.get(i);
            if (node instanceof FieldInsnNode fieldInsnNode
                    && fieldInsnNode.getOpcode() == Opcodes.PUTSTATIC
                    && fieldInsnNode.owner.equals(owner)
                    && fieldInsnNode.name.equals("$VALUES")
            ) {
                return fieldInsnNode;
            }
        }

        return null;
    }
}
