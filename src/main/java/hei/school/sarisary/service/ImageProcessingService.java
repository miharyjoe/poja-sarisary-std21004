package hei.school.sarisary.service;

import hei.school.sarisary.file.BucketComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ImageProcessingService {
  private BucketComponent bucketComponent;

  public ResponseEntity<String> uploadTransformImage(String id, MultipartFile imageFile) {
    try {

      String filename = UUID.randomUUID().toString() + ".png";

      File tempFile = File.createTempFile("temp", ".png");
      imageFile.transferTo(tempFile);

      bucketComponent.upload(tempFile, "original/" + filename);

      BufferedImage originalImage = ImageIO.read(tempFile);
      BufferedImage bwImage =
          new BufferedImage(
              originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      Graphics2D graphics = bwImage.createGraphics();
      graphics.drawImage(originalImage, 0, 0, null);
      graphics.dispose();

      File convertedFile = File.createTempFile("converted", ".png");
      ImageIO.write(bwImage, "png", convertedFile);
      bucketComponent.upload(convertedFile, "black-and-white/" + filename);

      tempFile.delete();
      convertedFile.delete();

      return ResponseEntity.ok(id);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
