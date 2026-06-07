package net.hero.editor.oreo.service;

public interface PersistenceService {
    /**
     * 指定されたキーの設定値を保存します。
     * @param key 設定キー
     * @param value 設定値
     */
    void saveSetting(String key, String value);

    /**
     * 指定されたキーの設定値を取得します。
     * @param key 設定キー
     * @return 設定値（存在しない場合はnull）
     */
    String getSetting(String key);

    /**
     * 指定されたキーの設定値を取得します。値が存在しない場合はデフォルト値を返します。
     * @param key 設定キー
     * @param defaultValue デフォルト値
     * @return 設定値
     */
    default String getSetting(String key, String defaultValue) {
        String value = getSetting(key);
        return (value != null) ? value : defaultValue;
    }
}
