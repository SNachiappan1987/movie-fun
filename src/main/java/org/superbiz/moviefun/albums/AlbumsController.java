package org.superbiz.moviefun.albums;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore blobStore;
    private final Logger logger = LoggerFactory.getLogger(AlbumsController.class);

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveUploadToFile(uploadedFile,new File(String.valueOf(albumId)));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/delete")
    public String deleteCover(@PathVariable long albumId) throws IOException {
        logger.info("Indised Delete Method -------------------------");
        blobStore.delete(String.valueOf(albumId));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {

        Optional<Blob> optionalBlob = blobStore.get(String.valueOf(albumId));

        byte[] imageBytes =null;
        if(optionalBlob.isPresent()){
            imageBytes = IOUtils.toByteArray(optionalBlob.get().getInputStream());
            HttpHeaders headers = createImageHttpHeaders(optionalBlob.get().getContentType(),imageBytes);

            return new HttpEntity<>(imageBytes, headers);
        }
        else return new HttpEntity<>(imageBytes);
    }

    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
        blobStore.put(new Blob(targetFile.getName(),uploadedFile.getInputStream(),uploadedFile.getContentType()));
    }

    private HttpHeaders createImageHttpHeaders(String contentType, byte[] imageBytes) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

}
