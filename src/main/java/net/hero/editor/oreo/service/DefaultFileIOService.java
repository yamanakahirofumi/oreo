package net.hero.editor.oreo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FileIOService の標準的な実装クラスです。
 */
public class DefaultFileIOService implements FileIOService {

    @Override
    public String readFile(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    @Override
    public void writeFile(Path path, String content) throws IOException {
        // 必要に応じて親ディレクトリを作成
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }
}
