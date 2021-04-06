// @dart=2.9

import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import 'package:flutter2_integration_test_sample/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('smoke test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    app.main();
    await Future.delayed(Duration(milliseconds: 2000));

    // Trigger a frame.
    await tester.pumpAndSettle();
    // waitを入れるとtest labで動画に写りやすくなる
    await Future.delayed(Duration(milliseconds: 2000));

    final Finder fab = find.byTooltip('Increment');
    await tester.tap(fab);
    await tester.pumpAndSettle();
    await Future.delayed(Duration(milliseconds: 2000));

    expect(find.text('1'), findsOneWidget);
  });
}
