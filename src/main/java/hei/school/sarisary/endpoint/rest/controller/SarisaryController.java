package hei.school.sarisary.endpoint.rest.controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import javax.imageio.ImageIO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.hei.poja.file.BucketComponent;

@RestController
@RequestMapping("/black-and-white")
public class SarisaryController {
  private static BucketComponent bucketComponent;

  private BufferedImage to_grayscale(File imageFile) throws IOException {
    BufferedImage image = ImageIO.read(imageFile);
    BufferedImage grayscaleImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics2D = grayscaleImage.createGraphics();
    graphics2D.drawImage(image, 0, 0, null);
    graphics2D.dispose();
    return grayscaleImage;
  }

  private File convert_buffered_buffered_to_file(BufferedImage image) throws IOException {
    File tempFile = File.createTempFile("image", ".png");
    FileOutputStream outputStream = new FileOutputStream(tempFile);
    ImageIO.write(image, "png", outputStream);
    outputStream.close();
    return tempFile;
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> process_to_grayscale(@PathVariable String id, @RequestBody File image)
      throws IOException {
    BufferedImage bwImage = to_grayscale(image);
    File grayscaleImage = convert_buffered_buffered_to_file(bwImage);
    String originalKey = id + "-original.png";
    String grayscalKey = id + "-grayscal.png";
    bucketComponent.upload(image, originalKey);
    bucketComponent.upload(grayscaleImage, grayscalKey);

    String originalUrl = String.valueOf(bucketComponent.presign(originalKey, Duration.ofDays(1)));
    String transformedUrl =
        String.valueOf(bucketComponent.presign(grayscalKey, Duration.ofDays(1)));

    // Envoi de la réponse
    return ResponseEntity.ok(new BlackAndWhiteResponse(originalUrl, transformedUrl));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable String id) {
    // Récupération des URLs pré-signées depuis le stockage
    String originalKey = id + "-original.png";
    String transformedKey = id + "-transformed.png";
    String originalUrl = String.valueOf(bucketComponent.presign(originalKey, Duration.ofDays(1)));
    String transformedUrl =
        String.valueOf(bucketComponent.presign(transformedKey, Duration.ofDays(1)));

    // Envoi de la réponse
    return ResponseEntity.ok(new BlackAndWhiteResponse(originalUrl, transformedUrl));
  }
}
