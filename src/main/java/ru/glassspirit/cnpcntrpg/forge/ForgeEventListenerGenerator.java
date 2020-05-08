package ru.glassspirit.cnpcntrpg.forge;

import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.sponge.scripting.SpongeClassGenerator;
import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

public class ForgeEventListenerGenerator {

    private static ClassLoader asmClassLoader;
    private static Class forgeEventListener;

    static {
        try {
            Field field = ASMEventHandler.class.getDeclaredField("LOADER");
            field.setAccessible(true);
            asmClassLoader = (ClassLoader) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerDynamicListener(List<ScriptObjectMirror> list) {
        if (forgeEventListener != null) {
            Log.info("Found Forge JS listener: " + forgeEventListener.getSimpleName() + " Unregistering");
            MinecraftForge.EVENT_BUS.unregister(forgeEventListener);
            forgeEventListener = null;
        }
        forgeEventListener = generateForgeListenerClass(list);
        Log.info("Registering Forge JS listener: " + forgeEventListener.getSimpleName());
        MinecraftForge.EVENT_BUS.register(forgeEventListener);
    }

    private static Class generateForgeListenerClass(List<ScriptObjectMirror> list) {
        Class c = null;
        try {
            String name = "DynamicForgeListener" + System.currentTimeMillis();

            DynamicType.Builder<Object> classBuilder = new ByteBuddy()
                    .subclass(Object.class)
                    .modifiers(Visibility.PUBLIC)
                    .name(name);

            int i = 0;
            for (ScriptObjectMirror obj : list) {
                Class<?> type = ((StaticClass) obj.get("type")).getRepresentedClass();
                Consumer consumer = (event) -> obj.callMember("consumer", event);

                EventPriority priority = EventPriority.valueOf(extract(obj, "priority", "NORMAL"));
                String methodName = extract(obj, "methodName", "e" + i);

                AnnotationDescription annotation = AnnotationDescription.Builder.ofType(SubscribeEvent.class)
                        .define("priority", priority)
                        .build();

                classBuilder = classBuilder.defineMethod(methodName, void.class, Visibility.PUBLIC, Ownership.STATIC)
                        .withParameter(type, "event")
                        .intercept(MethodDelegation.to(new SpongeClassGenerator.EventHandlerInterceptor(consumer)))
                        .annotateMethod(annotation);

                i++;
            }

            c = classBuilder.make().load(asmClassLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private static <T> T extract(ScriptObjectMirror obj, String key, T def) {
        return obj.hasMember(key) ? (T) obj.get(key) : def;
    }
}
