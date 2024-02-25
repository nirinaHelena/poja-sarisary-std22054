package hei.school.sarisary.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageModel {
  @Id private String id;
  private String originaleBucketKey;
  private String grayscaleBucketKey;

  public Boolean HaveGrayscaleImage() {
    return !grayscaleBucketKey.isEmpty();
  }
}
