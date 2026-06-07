package net.hero.editor.oreo.model.config;

/**
 * エディタの設定情報を保持するデータ構造です。
 * 不変（Immutable）な record として定義されています。
 */
public record EditorConfig(
    boolean showLineNumbers,
    String fontFamily,
    double fontSize
) {
    /**
     * デフォルト設定を生成します。
     */
    public static EditorConfig defaultConfig() {
        return new EditorConfig(true, "Monospaced", 14.0);
    }
}
