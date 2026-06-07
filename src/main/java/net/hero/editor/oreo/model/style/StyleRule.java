package net.hero.editor.oreo.model.style;

/**
 * テキストに適用する条件付きスタイルの定義を保持するレコードです。
 * 
 * @param name スタイル名（例: "keyword", "comment"）
 * @param regex マッチング条件となる正規表現
 * @param color CSS 形式の色指定（例: "#ff0000"）
 * @param bold 太字にするかどうか
 * @param italic イタリックにするかどうか
 * @param underline 下線を引くかどうか
 */
public record StyleRule(
    String name,
    String regex,
    String color,
    boolean bold,
    boolean italic,
    boolean underline
) {}
