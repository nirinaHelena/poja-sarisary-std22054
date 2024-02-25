package hei.school.sarisary.repository.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageInfoURL {
  private String originalUrl;
  private String grayscaleUrl;
}
