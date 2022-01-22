package file;

import java.util.ArrayList;
import java.util.List;

public class FileContentsImpl extends AbstractFileContents
    implements FileContents{

    /** ファイル内容を1行1要素のリストで保持 */
    private List<String> lines;

    public FileContentsImpl() {
        lines = new ArrayList<>();
    }


    /**
     * {@inheritDoc}
     */
    public List<String> getLines() {
        return this.lines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileCd() {
        String fileHeaderData = this.lines.get(FILE_HEADER_DATA_LINE);
        return fileHeaderData.split(",", -1)[FILE_CD_IDX];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFileCdEquals(String correctFileCd) {
        return getFileCd().equals(correctFileCd);
    }



}
