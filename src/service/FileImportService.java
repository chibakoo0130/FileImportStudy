package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.logging.Handler;

import org.apache.commons.lang3.StringUtils;

import file.FileContents;
import file.FileContentsImpl;

/**
 * ファイル取込処理を担うクラス。<br>
 * ファイル操作処理に注目するため、<br>
 * エラー処理やDBの取込処理は簡略化・省略。
 *
 * @author chibakotaro
 * @since 2022/01/22
 */
public class FileImportService {

    private static final String FILE_SEPERATOR = File.separator;
    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String RESOURCES_PATH = CURRENT_DIR + FILE_SEPERATOR
            + "resources" + FILE_SEPERATOR;

    private static final String COMMA = ",";
    private static final String BREAK_LINE = System.getProperty("line.separator");

    /**
     * 取込ファイルからデータを読み込み、中間ファイルを生成してバルクインサートする。<br>
     * 正常に取込が終了した場合はtrue、正常に終了しなければfalseを返す。
     *
     * @param fileCd
     * @return true/false
     */
    public boolean load(String fileCd) {

        // 画面からの入力値のチェック
        if (StringUtils.isEmpty(fileCd)) {
            System.out.println("ファイルコードを入力してください。");
        }

        FileContents fileContents = new FileContentsImpl();
        Path importFilePath = Paths.get(RESOURCES_PATH + "取込サンプルファイル.csv");
        try (BufferedReader br = Files.newBufferedReader(importFilePath, StandardCharsets.UTF_8)) {
            for (String line; (line = br.readLine()) != null; ) {
                fileContents.getLines().add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 取込ファイルのヘッダーチェック
        if (!fileContents.isFileCdEquals(fileCd)) {
            System.out.println("fileCdErr");
        }

        Path tempFilePath = Paths.get(RESOURCES_PATH + "中間サンプルファイル.csv");
        Path errorFilePath = Paths.get(RESOURCES_PATH + "エラーサンプルファイル.csv");

        try {
            createFile(tempFilePath);
            createFile(errorFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // エラーファイルまたは中間ファイルに出力
        // チェックエラーがあった場合hasErrorはtrue
        boolean hasError = outputTempOrErrFile(tempFilePath, errorFilePath, fileContents);

        if (!hasError) {
            errorFilePath.toFile().delete();
        }

        // 取込処理（中間ファイルをバルクインサート）

        return true;
    }

    /**
     * ファイルを生成する。
     * @param filePath 生成したいファイルのパス
     * @throws IOException
     */
    protected static void createFile(Path filePath) throws IOException {
        if (!filePath.toFile().exists()) {
            filePath.toFile().createNewFile();
        }
    }

    /**
     * ファイルの内容を受け取ってチェックしたのち、チェック結果に応じて<br>
     * 中間ファイルかエラーファイルに出力する。
     * Java8以降で書けるtry resources文を利用する。
     *
     * @param tempFilePath 中間ファイルのパス
     * @param errFilePath エラーファイルのパス
     * @param fileContents ファイルの内容
     * @return チェックエラーがあった場合true/ない場合false
     */
    protected static boolean outputTempOrErrFile(Path tempFilePath, Path errFilePath,
            FileContents fileContents) {

        boolean hasError = false;
        try (BufferedWriter outputBw = Files.newBufferedWriter(tempFilePath,
                StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            BufferedWriter errorBw = Files.newBufferedWriter(errFilePath,
                StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 4; i < fileContents.getLines().size(); i++) {
                String[] line = fileContents.getLines().get(i).split(COMMA, -1);
                StringBuilder outputLine = new StringBuilder(Arrays.toString(line).substring(1, Arrays.toString(line).length() - 1));

                hasError = checkData(line, outputLine);

                // エラーファイルまたは中間ファイルに書き込み
                if (outputLine.toString().split(COMMA, -1).length > 3) {
                    errorBw.write(outputLine.toString() + BREAK_LINE);
                } else {
                    outputBw.write(outputLine.toString() + BREAK_LINE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasError;
    }

    /**
     * 取込ファイルの行データを受け取ってチェックする。<br>
     * チェックエラーがあった場合はtrue、エラーがない場合はfalseを返す。
     *
     * @param line 行データ
     * @param outputLine 出力データ
     * @return true/false
     */
    protected static boolean checkData(String[] line, StringBuilder outputLine) {
        boolean hasError = false;

        if (line.length != 3) {
            hasError = true;
            outputLine.append(COMMA + "この行のカラム数が不正です。");
        }
        if (StringUtils.isEmpty(line[0])) {
            hasError = true;
            outputLine.append(COMMA + "社員IDは入力必須です。");
        }
        if (StringUtils.isEmpty(line[1])) {
            hasError = true;
            outputLine.append(COMMA + "名前は入力必須です。");
        }
        return hasError;
    }
}
