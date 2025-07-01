package kr.me.seesaw.core.file;

import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 파일 입출력 서비스
 */
public interface FileIOService {

    /**
     * 파일 쓰기
     *
     * @param absolutePath 절대경로
     * @param data         파일 데이터
     * @throws IOException 입출력 예외
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void write(String absolutePath, byte[] data) throws IOException {
        File file = new File(absolutePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileCopyUtils.copy(data, file);
    }

    /**
     * 파일 읽기
     *
     * @param absolutePath 절대경로
     * @return 파일 데이터
     * @throws IOException 입출력 예외
     */
    static ByteArrayInputStream read(String absolutePath) throws IOException {
        Path path = Paths.get(absolutePath);
        InputStream inputStream = Files.newInputStream(path);
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 파일 삭제
     *
     * @param absolutePath 절대경로
     * @return 삭제 성공 여부
     */
    static boolean delete(String absolutePath) {
        File file = new File(absolutePath);
        return file.delete();
    }

}