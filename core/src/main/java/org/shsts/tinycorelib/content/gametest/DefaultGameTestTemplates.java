package org.shsts.tinycorelib.content.gametest;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.neoforge.gametest.GameTestHooks;

import java.lang.reflect.Method;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DefaultGameTestTemplates {
    private static final String PROPERTY = "tinycorelib.gameTest.defaultTemplate";

    private DefaultGameTestTemplates() {}

    public static Optional<TestFunction> apply(Method method, TestFunction testFunction) {
        if (!GameTestHooks.isGametestServer()) {
            return Optional.empty();
        }

        var gameTest = method.getAnnotation(GameTest.class);
        if (gameTest == null || !gameTest.template().isEmpty()) {
            return Optional.empty();
        }

        var defaultTemplate = System.getProperty(PROPERTY, "").trim();
        if (defaultTemplate.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new TestFunction(
            testFunction.batchName(),
            testFunction.testName(),
            structureName(method, defaultTemplate),
            testFunction.rotation(),
            testFunction.maxTicks(),
            testFunction.setupTicks(),
            testFunction.required(),
            testFunction.manualOnly(),
            testFunction.maxAttempts(),
            testFunction.requiredSuccesses(),
            testFunction.skyAccess(),
            testFunction.function()));
    }

    private static String structureName(Method method, String template) {
        var simpleName = method.getDeclaringClass().getSimpleName().toLowerCase();
        var prefix = GameTestHooks.prefixGameTestTemplate(method) ? simpleName + "." : "";
        return GameTestHooks.getTemplateNamespace(method) + ":" + prefix + template;
    }
}
