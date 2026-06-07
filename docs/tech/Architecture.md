# アーキテクチャ設計

本アプリケーションでは、テキスト情報の効率的な編集と、将来的な機能拡張（構文強調、プラグイン対応など）を考慮し、標準的な MVC (Model-View-Controller) パターンを採用して各層の責務を明確に分離します。

## 1. ディレクトリ・パッケージ構造
標準的な Maven 構造および JavaFX のモジュール・システム（JPMS）に準拠した構成を採用します。

```
.
├── pom.xml                # プロジェクト構成 (Maven)
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── module-info.java  # モジュール定義
│   │   │   └── net.hero.editor.oreo
│   │   │       ├── Main.java     # エントリーポイント
│   │   │       ├── model/        # ドメインモデル・ビジネスロジック
│   │   │       │   ├── document/ # テキストドキュメント、バッファ管理
│   │   │       │   └── config/   # エディタ設定（テーマ、フォント等）
│   │   │       ├── view/         # JavaFX FXML および カスタムコントロール
│   │   │       ├── controller/   # UI 制御（FXML Controller）
│   │   │       ├── service/      # 外部リソース・ファイル入出力
│   │   │       └── util/         # 共通ユーティリティ（エンコーディング判別等）
│   │   └── resources
│   │       └── net.hero.editor.oreo
│   │           ├── fxml/         # UI レイアウト
│   │           └── css/          # スタイルシート
│   └── test
│       └── java
│           └── net.hero.editor.oreo # ユニットテスト
```

## 2. 主要コンポーネントの責務

### 2.1 Model 層
- **TextDocument**: 編集中のテキストデータとその状態（変更フラグ、カーソル位置など）を管理します。
- **EditorConfig**: ユーザー設定（フォントサイズ、テーマ、タブ幅など）を保持します。
- **BufferManager**: 大規模ファイルの効率的な読み込みとメモリ管理を担当します。

### 2.2 View 層
- **JavaFX FXML**: `EditorWindow.fxml`, `SettingsDialog.fxml` などにより、UI の構造を定義します。
- **StyledTextArea**: 構文強調や行番号表示など、テキストエディタ特有の描画を行うカスタムコンポーネント。JavaFX の `TextFlow` またはサードパーティ製ライブラリ（RichTextFX 等）をベースに、メモリ効率を考慮した拡張を行います。
- **PreviewPane**: Markdown などをレンダリングして表示するための WebView またはカスタムコンポーネント。

### 2.3 Controller 層
- **EditorController**: タブの管理、テキスト入力、ショートカットキー、メニュー操作などのイベントを処理します。複数の `Tab` およびその中の `TextArea` を制御します。
- **CommandManager**: キー入力とコマンドの紐付け、マルチストローク・キー（Ctrl-x 等）の管理、およびコマンドの実行を制御します。
- **SettingsController**: 設定変更の UI 操作を Model に反映します。
- **PreviewController**: エディタの変更を検知し、PreviewPane への再描画を制御します。

### 2.4 Command システム (実現方式)
キーボード中心の操作を実現するため、エディタへの入力は全て一度 `CommandManager` を通過します。
1. **Key Event Capture**: すべてのキーイベントをグローバル（またはエディタ単位）でキャプチャします。
2. **Key Mapping**: `KeyStroke` と `Command`（例: `save-file`, `next-line`）を紐付けます。マルチストローク（Ctrl-x の後の Ctrl-s など）の状態も管理します。
3. **Command Execution**: 紐付けられたコマンドを `EditorController` や `Service` 層のメソッドとして実行します。
4. **Mini-buffer Feedback**: コマンドの実行結果や次の入力を促すメッセージをミニバッファへ送信します。

### 2.5 Service 層
- **FileIOService**: ファイルの保存・読み込み、文字コードの自動判別を担当します。
- **PersistenceService**: 設定情報や履歴情報の保存を担当します。詳細は [データ永続化方針](./Database-Selection.md) を参照。
- **PluginManager**: プラグインのロード、ライフサイクル管理、および拡張ポイントの提供を担当します。

## 3. 拡張性・プラグイン設計
将来的な拡張（軽量なJava開発補助など）を可能にするため、以下の設計方針を導入します。

- **拡張ポイントの定義**: エディタのメニュー、コマンド、描画、ドキュメントライフサイクル（オープン、保存、クローズ）に対してフック（Hook）を提供します。
- **Java Service Provider Interface (SPI)**: 標準的な Java SPI または独自のアノテーションスキャンにより、外部 Jar からの機能追加を容易にします。
- **疎結合なコンポーネント**: `CommandManager` や `StyledTextArea` は特定の言語仕様に依存せず、プラグインから挙動を動的に変更できるインターフェースを提供します。

## 4. 設計方針
- **リソース効率（メモリ使用量抑制）**: プロジェクトの重要ポリシーに基づき、常にメモリ消費を最小限に抑える設計を行います。BufferManager による Piece Table 等の採用や、不要なオブジェクト生成の抑制を徹底します。
- **応答性の確保**: ファイルの読み書きや複雑な検索処理は非同期（JavaFX Service/Task）で行い、UI スレッドをブロックしないように設計します。
- **リアクティブなUI更新**: JavaFX の `Property` を活用し、設定変更（フォントサイズ変更など）が即座に View に反映されるようにします。
- **例外処理の統一化**: ファイルアクセスエラーや不正な文字コードなどに対し、[例外処理方針](../tech/Error-Handling-Policy.md) に基づき適切にハンドリングします。
