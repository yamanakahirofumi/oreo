package net.hero.editor.oreo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.hero.editor.oreo.model.config.EditorConfig;
import net.hero.editor.oreo.service.DefaultFileIOService;
import net.hero.editor.oreo.service.FileIOService;
import net.hero.editor.oreo.service.PersistenceService;
import net.hero.editor.oreo.service.SQLitePersistenceService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EditorController {

    @FXML
    private TabPane tabPane;

    @FXML
    private HBox footer;

    private final List<Label> footerSections = new ArrayList<>();

    private FileIOService fileIOService;
    private PersistenceService persistenceService;
    private EditorConfig config;

    @FXML
    public void initialize() {
        this.fileIOService = new DefaultFileIOService();
        this.persistenceService = new SQLitePersistenceService();
        loadConfig();

        // フッターのセクションを初期化 (デフォルト3分割)
        initializeFooter(3);

        // 初期化処理: 最初のタブを作成
        createNewTab("Untitled");

        // タブの選択が変更された時にステータスバーを更新
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> updateStatus());
    }

    /**
     * 設定情報を読み込みます。
     */
    private void loadConfig() {
        boolean showLineNumbers = Boolean.parseBoolean(persistenceService.getSetting("showLineNumbers", "true"));
        String fontFamily = persistenceService.getSetting("fontFamily", "Monospaced");
        double fontSize = Double.parseDouble(persistenceService.getSetting("fontSize", "14.0"));
        
        this.config = new EditorConfig(showLineNumbers, fontFamily, fontSize);
    }

    /**
     * 新しいタブを作成し、エディタを配置します。
     * @param title タブのタイトル
     */
    private void createNewTab(String title) {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter text here...");
        textArea.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1fpx;", config.fontFamily(), config.fontSize()));

        // 行番号表示用のラベル
        Label lineNumbers = new Label("1");
        lineNumbers.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1fpx; -fx-padding: 5 5 0 5; -fx-text-fill: #888; -fx-background-color: #eee;", 
                config.fontFamily(), config.fontSize()));
        lineNumbers.setMaxHeight(Double.MAX_VALUE);
        lineNumbers.setVisible(config.showLineNumbers());
        lineNumbers.setManaged(config.showLineNumbers());

        HBox editorContainer = new HBox(lineNumbers, textArea);
        HBox.setHgrow(textArea, Priority.ALWAYS);

        Tab tab = new Tab(title);
        tab.getProperties().put("baseTitle", title);
        tab.getProperties().put("modified", false);
        tab.getProperties().put("lineNumbers", lineNumbers);

        // カーソル位置の変更を監視してステータスバーを更新
        textArea.caretPositionProperty().addListener((obs, oldPos, newPos) -> updateStatus());
        // テキストの変更も監視
        textArea.textProperty().addListener((obs, oldText, newText) -> {
            if (!(boolean) tab.getProperties().getOrDefault("modified", false)) {
                setTabModified(tab, true);
            }
            updateLineNumbers(textArea, lineNumbers);
            updateStatus();
        });

        tab.setContent(editorContainer);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    /**
     * 行番号表示を更新します。
     * @param editor エディタ本体
     * @param lineNumbers 行番号表示用ラベル
     */
    private void updateLineNumbers(TextArea editor, Label lineNumbers) {
        if (!lineNumbers.isVisible()) return;
        
        String text = editor.getText();
        int rowCount = text.split("\n", -1).length;
        
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= rowCount; i++) {
            sb.append(i).append("\n");
        }
        lineNumbers.setText(sb.toString());
    }

    /**
     * タブの変更状態を更新し、タイトルに反映させます。
     * @param tab 対象のタブ
     * @param modified 変更があったかどうか
     */
    private void setTabModified(Tab tab, boolean modified) {
        tab.getProperties().put("modified", modified);
        String baseTitle = (String) tab.getProperties().get("baseTitle");
        if (modified) {
            tab.setText(baseTitle + "*");
        } else {
            tab.setText(baseTitle);
        }
    }

    /**
     * 現在アクティブなタブの内容を保存します。
     */
    public void saveActiveTab() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        TextArea textArea = getActiveTextArea();
        if (currentTab == null || textArea == null) {
            return;
        }

        Path path = (Path) currentTab.getProperties().get("filePath");
        if (path == null) {
            // 新規ファイルの場合は名前をつけて保存
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("Markdown Files", "*.md"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
            if (file == null) {
                return; // キャンセル
            }
            path = file.toPath();
        }

        try {
            fileIOService.writeFile(path, textArea.getText());

            // タブの状態を更新
            currentTab.getProperties().put("filePath", path);
            currentTab.getProperties().put("baseTitle", path.getFileName().toString());
            setTabModified(currentTab, false);
            updateStatus();

        } catch (IOException e) {
            e.printStackTrace();
            // 将来的にはここでユーザーにエラーを通知するUIを表示
        }
    }

    private void updateStatus() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        TextArea textArea = getActiveTextArea();
        if (currentTab == null || textArea == null) {
            setSectionText(0, "");
            setSectionText(1, "No active editor");
            setSectionText(2, "");
            return;
        }

        int caretPos = textArea.getCaretPosition();
        String text = textArea.getText();

        // caretPos までのテキストを取得して行・列を計算
        String subText = text.substring(0, caretPos);
        String[] lines = subText.split("\n", -1);

        int line = lines.length;
        int col = lines[line - 1].length() + 1;

        setSectionText(0, "Ready");
        setSectionText(1, currentTab.getText()); // とりあえずファイル名を表示
        setSectionText(2, String.format("Line: %d, Col: %d", line, col));
    }

    private void initializeFooter(int count) {
        footer.getChildren().clear();
        footerSections.clear();

        for (int i = 0; i < count; i++) {
            Label label = new Label();
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);
            
            // セクション間の境界線
            if (i > 0) {
                label.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 0 1; -fx-padding: 0 5 0 5;");
            } else {
                label.setStyle("-fx-padding: 0 5 0 0;");
            }
            
            footer.getChildren().add(label);
            footerSections.add(label);
        }
    }

    private void setSectionText(int index, String text) {
        if (index >= 0 && index < footerSections.size()) {
            footerSections.get(index).setText(text);
        }
    }

    /**
     * 現在アクティブな TextArea を取得します。
     * @return アクティブな TextArea、存在しない場合は null
     */
    private TextArea getActiveTextArea() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null && currentTab.getContent() instanceof HBox container) {
            return container.getChildren().stream()
                    .filter(node -> node instanceof TextArea)
                    .map(node -> (TextArea) node)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
