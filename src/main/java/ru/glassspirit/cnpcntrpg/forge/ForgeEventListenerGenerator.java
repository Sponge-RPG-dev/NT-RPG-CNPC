package ru.glassspirit.cnpcntrpg.forge;

import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.JSObject;
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
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

public class ForgeEventListenerGenerator extends ClassGenerator {

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

    public void registerDynamicListener(List<JSObject> list) {
        if (forgeEventListener != null) {
            Log.info("Found Forge JS listener: " + forgeEventListener.getSimpleName() + " Unregistering");
            MinecraftForge.EVENT_BUS.unregister(forgeEventListener);
            forgeEventListener = null;
        }
        if (list.size() > 0) {
            forgeEventListener = generateForgeListenerClass(list);
            Log.info("Registering Forge JS listener: " + forgeEventListener.getSimpleName());
            MinecraftForge.EVENT_BUS.register(forgeEventListener);
        }
    }

    private Class generateForgeListenerClass(List<JSObject> list) {
        Class c = null;
        try {
            String name = "DynamicForgeListener" + System.currentTimeMillis();

            DynamicType.Builder<Object> classBuilder = new ByteBuddy()
                    .subclass(Object.class)
                    .modifiers(Visibility.PUBLIC)
                    .name(name);

            int i = 0;
            for (JSObject object : list) {
                if (!object.hasMember("consumer") || !((JSObject) object.getMember("consumer")).isFunction()) {
                    Log.warn("JS event listener missing function consumer, skipping");
                    continue;
                }
                ScriptObjectMirror mirror = (ScriptObjectMirror) object.getMember("consumer");
                Consumer consumer = (event) -> mirror.call(mirror, event);

                if (!object.hasMember("type")) {
                    Log.warn("Js event listener missing node type, skipping");
                    continue;
                }
                String className = "";
                Object typeObj = object.getMember("type");
                StaticClass staticClass;
                if (typeObj instanceof StaticClass) {
                    staticClass = (StaticClass) typeObj;
                    className = staticClass.getRepresentedClass().getName();
                    Log.warn("JS event listener for the event " + className + ", it's no longer needed to reference the class (Java.type(...)), use only the wrapped string");
                } else {
                    className = (String) typeObj;
                }

                Class type;
                try {
                    type = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    Log.warn("JS event listener - unknown event " + className);
                    continue;
                }

                EventPriority priority = EventPriority.valueOf(extract(object, "priority", "NORMAL"));
                String methodName = extract(object, "methodName", "e" + i);

                AnnotationDescription annotation = AnnotationDescription.Builder.ofType(SubscribeEvent.class)
                        .define("priority", priority)
                        .build();

                classBuilder = classBuilder.defineMethod(methodName, void.class, Visibility.PUBLIC, Ownership.STATIC)
                        .withParameter(type, "event")
                        .intercept(MethodDelegation.to(new EventHandlerInterceptor(consumer)))
                        .annotateMethod(annotation);

                i++;
            }

            c = classBuilder.make().load(asmClassLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    protected Type getListenerSubclass() {
        // NOOP
        return Object.class;
    }

    @Override
    protected DynamicType.Builder<Object> visitImplSpecAnnListener(DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<Object> classBuilder, JSObject object) {
        // NOOP
        return null;
    }

}
