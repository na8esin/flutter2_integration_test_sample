package com.example.flutter2_integration_test_sample;

import android.util.Log;
import androidx.test.rule.ActivityTestRule;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import dev.flutter.plugins.integration_test.IntegrationTestPlugin;

public class FlutterTestRunner extends Runner {

  private static final String TAG = "FlutterTestRunner";

  final Class testClass;
  TestRule rule = null;

  public FlutterTestRunner(Class<?> testClass) {
    super();
    this.testClass = testClass;

    // Look for an `ActivityTestRule` annotated `@Rule` and invoke `launchActivity()`
    Method[] methods = testClass.getDeclaredMethods();
    for (Method method : methods) {
      if (method.getAnnotation(Rule.class) != null) {
        try {
          Object instance = testClass.newInstance();
          if (method.getDefaultValue() instanceof ActivityTestRule) {
            rule = (TestRule) method.invoke(instance);
            break;
          }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
          // This might occur if the developer did not make the rule public.
          // We could call field.setAccessible(true) but it seems better to throw.
          throw new RuntimeException("Unable to access activity rule", e);
        }
      }
    }
  }

  @Override
  public Description getDescription() {
    return Description.createTestDescription(testClass, "Flutter Tests");
  }

  @Override
  public void run(RunNotifier notifier) {
    if (rule == null) {
      throw new RuntimeException("Unable to run tests due to missing activity rule");
    }
    try {
      if (rule instanceof ActivityTestRule) {
        ((ActivityTestRule) rule).launchActivity(null);
      }
    } catch (RuntimeException e) {
      Log.v(TAG, "launchActivity failed, possibly because the activity was already running. " + e);
      Log.v(
          TAG,
          "Try disabling auto-launch of the activity, e.g. ActivityTestRule<>(MainActivity.class, true, false);");
    }
    Map<String, String> results = null;
    try {
      results = IntegrationTestPlugin.testResults.get();
    } catch (ExecutionException | InterruptedException e) {
      throw new IllegalThreadStateException("Unable to get test results");
    }

    for (String name : results.keySet()) {
      Description d = Description.createTestDescription(testClass, name);
      notifier.fireTestStarted(d);
      String outcome = results.get(name);
      if (!outcome.equals("success")) {
        Exception dummyException = new Exception(outcome);
        notifier.fireTestFailure(new Failure(d, dummyException));
      }
      notifier.fireTestFinished(d);
    }
  }
}