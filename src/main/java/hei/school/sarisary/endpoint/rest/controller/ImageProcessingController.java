package hei.school.sarisary.endpoint.rest.controller;

import hei.school.sarisary.file.BucketComponent;
import hei.school.sarisary.service.ImageProcessingService;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/black-and-white")
public class ImageProcessingController {
  private BucketComponent bucketComponent;
  private ImageProcessingService imageProcessingService;

  @PutMapping("/{id}")
  public ResponseEntity<String> convertToBlackAndWhite(
      @PathVariable String id, @RequestParam("image") MultipartFile imageFile) {
    return imageProcessingService.uploadTransformImage(id, imageFile);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getTransformedImages(@PathVariable String id) {
    try {
      String originalUrl =
          bucketComponent.presign("original/" + id, Duration.ofMinutes(5)).toString();
      String transformedUrl =
          bucketComponent.presign("black-and-white/" + id, Duration.ofMinutes(5)).toString();

      Map<String, String> responseBody = new HashMap<>();
      responseBody.put("original_url", originalUrl);
      responseBody.put("transformed_url", transformedUrl);

      return ResponseEntity.ok(responseBody);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
