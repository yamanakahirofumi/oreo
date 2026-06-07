# ドキュメント一覧

このディレクトリには、OREO (**Overpowered Rich Editor for Overlords**) に関する詳細なドキュメントが格納されています。

## 1. フォルダ構成と配置

ドキュメントは内容に応じて以下のいずれかに分類して配置します。

- **`docs/features/`**：機能仕様、ビジネスルール、UI/UXデザインなど、ユーザーの要求に近い内容。
- **`docs/tech/`**：技術スタック、アーキテクチャ、コーディング規約、CI/CDなど、一般的な技術・開発設定に関する内容。
- **`docs/implementation/`**：特定機能の実装方法、データ構造、最適化手法など、詳細な実装に関する内容。

---

## 2. 機能・仕様 (`docs/features/`)
- [コアコンセプト](features/Core-Concepts.md)：OREOの設計思想
- [機能一覧](features/Feature-List.md)：搭載機能のまとめ
- [エディタコンポーネントの比較](tech/Editor-Component-Comparison.md)：基盤ライブラリの検討
- [Markdown プレビューの実現方式](tech/Markdown-Preview-Implementation.md)：軽量レンダリングと WebView の比較
- [条件付きスタイル表示](features/Conditional-Styling.md)：テキスト装飾とスタイルルールの定義
- [カーソル・行の視認性向上](features/Cursor-Visibility.md)：編集位置の把握を助ける視覚効果
- [キーボード操作の詳細](features/Keyboard-Interactions.md)：キーバインドと操作体系の具体案
- [システム要件](features/System-Requirements.md)：動作環境とスペック

## 3. 一般的な技術・開発設定 (`docs/tech/`)
- [アーキテクチャ設計](tech/Architecture.md)：システムのパッケージ構造と主要クラスの責務
- [データ永続化方針](tech/Database-Selection.md)：設定や履歴の保存方法とデータベースの選定理由
- [エラーハンドリング方針](tech/Error-Handling-Policy.md)：基本方針と各ケースでの対応
- [ロギング方針](tech/Logging-Policy.md)：デバッグおよび保守のためのログ出力指針
- [技術スタック](tech/Tech-Stack.md)：使用している言語、ライブラリ、ツールなどの情報
- [CI 設定](tech/CI-Setting.md)：GitHub Actions を利用した自動ビルドとテストの設定について
- [テストルール](tech/Test-Rule.md)：テストケース作成の一般的なガイドライン
- [品質方針](tech/Quality-Policy.md)：フェーズ（仕様未確定/確定）に応じた品質の考え方と到達目標
- [配布方法](tech/Distribution-Method.md)：カスタム JRE による配布パッケージの作成について
- [コーディング規約](tech/Coding-Convention.md)：クラス作成基準（record, final の使用等）について
- [仕様書の書き方ルール](tech/Specification-Rule.md)：本プロジェクトにおけるドキュメント作成基準
- [TODOリストの書き方ルール](tech/TODO-Rule.md)：検討事項の追加・更新ルール

## 4. 特定機能の実装方法 (`docs/implementation/`)
- [JUnit 5 ルール](implementation/JUnit-Rule.md)：JUnit 5 を使用したテストの実装方法

## 5. 検討事項（TODOリスト）
開発を進めるにあたって検討・具体化が必要な事項のリストです。
追加・変更を含む詳細な内容は [検討事項・TODOリスト](TODO-Details.md) を参照してください。
以降の検討事項の更新は、詳細ファイルのみで行います。
