package net.hero.editor.oreo.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * ファイルの読み込みおよび保存を担当するサービスのインターフェースです。
 */
public interface FileIOService {
    /**
     * 指定されたパスからテキストファイルを読み込みます。
     * @param path 読み込み元のパス
     * @return ファイルの内容
     * @throws IOException 読み込みに失敗した場合
     */
    String readFile(Path path) throws IOException;

    /**
     * 指定された内容をファイルに書き込みます。
     * @param path 書き込み先のパス
     * @param content 書き込む内容
     * @throws IOException 書き込みに失敗した場合
     */
    void writeFile(Path path, String content) throws IOException;
}
