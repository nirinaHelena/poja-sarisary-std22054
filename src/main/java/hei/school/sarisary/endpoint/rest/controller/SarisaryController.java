package hei.school.sarisary.endpoint.rest.controller;

import hei.school.sarisary.repository.model.ImageInfoURL;
import hei.school.sarisary.service.event.ImageService;
import java.awt.*;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class SarisaryController {
  private final ImageService imageService;

  @PutMapping("/black-and-white/{id}")
  public ImageInfoURL getGrayscaleIMage(
      @PathVariable(name = "id") String id, @RequestBody(required = false) byte[] image) {
    try {
      return imageService.uploadAndToGrayscale(id, image);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping("/black-and-white/{id}")
  public ImageInfoURL getImageUrl(@PathVariable(name = "id") String id) {
    try {
      return imageService.getImageInfoUrl(id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
