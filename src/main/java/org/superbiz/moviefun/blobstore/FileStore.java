package org.superbiz.moviefun.blobstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {

    private final Logger log = LoggerFactory.getLogger(FileStore.class);

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();
        try (FileOutputStream outputStream = new FileOutputStream(getCoverFile(Long.parseLong(targetFile.getName())))) {
            byte[] byteArray = new byte[blob.inputStream.available()];
            blob.inputStream.read(byteArray);
            outputStream.write(byteArray);
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException, URISyntaxException {
        Path coverFilePath = getExistingCoverPath(Long.parseLong(name));
        byte[] imageBytes = readAllBytes(coverFilePath);
        Blob blob = new Blob(name, new ByteArrayInputStream(imageBytes), null);
        return Optional.of(blob);
    }


    @Override
    public void deleteAll() {
        // ...
    }

    private Path getExistingCoverPath(long albumId) throws URISyntaxException {
        File coverFile = getCoverFile(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }

    private File getCoverFile(long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }



    @Override
    public void delete(String name) throws IOException {

    }
}
