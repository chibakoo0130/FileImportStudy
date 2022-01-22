package file;

import java.util.List;

public interface FileContents {

    /**
     * ファイル内容のリストを返す。
     * @return ファイル内容のリスト
     */
    List<String> getLines();

    /**
     * 保持するファイル内容のファイルコードを返す。
     * @return ファイル内容のファイルコード
     */
    String getFileCd();

    /**
     * 引数のファイルコードと引数のファイルコードが一致する場合trueを返す。<br>
     * 一致しない場合はfalseを返す。
     * @return true/false
     */
    boolean isFileCdEquals(String correctFileCd);
}
