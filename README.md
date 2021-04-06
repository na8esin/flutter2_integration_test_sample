# flutter2_integration_test_sample

A new Flutter project.

## test lab

apkを作る
```
pushd android
flutter build apk -t lib/main.dart --no-sound-null-safety
./gradlew app:assembleAndroidTest
./gradlew app:assembleDebug -Ptarget=integration_test/main_test.dart
popd
```

test lab upload
```
gcloud --quiet config set project practice-da34f
gcloud firebase test android run --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk\
  --timeout 2m
```