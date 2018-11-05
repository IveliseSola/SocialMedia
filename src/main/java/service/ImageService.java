package service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import com.acebalsola.socialmedia.model.Image;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static String UPLOAD_ROOT = "upload_dir";
    private final ResourceLoader resourceLoader;

    public ImageService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    @Bean
    CommandLineRunner setUp() throws IOException {
        return (args) ->  {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectories(Paths.get(UPLOAD_ROOT));
            FileCopyUtils.copy("test file", new FileWriter(UPLOAD_ROOT + "/photo.jpg"));
            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/PHOTO2.jpg"));
            FileCopyUtils.copy(" Test file3", new FileWriter(UPLOAD_ROOT + "/photo2.jpg"));
        };
    }

    public Flux<Image> findAllImages() {
        try{
           return  Flux.fromIterable( Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)
           )).map(path ->
                   new Image(path.toString(),
                             path.getFileName().toString()));
        } catch (IOException e){
            return Flux.empty();
        }
    }

    public Mono<Resource> findOneImage(String fileName){
        return Mono.fromSupplier(() ->
        resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + fileName));

    }
}
