package hei.school.sarisary.service.event;

import hei.school.sarisary.file.BucketComponent;
import hei.school.sarisary.repository.ImageRepository;
import hei.school.sarisary.repository.model.ImageInfoURL;
import hei.school.sarisary.repository.model.ImageModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ImageService {
  private final ImageRepository imageRepository;
  private final BucketComponent bucketComponent;
  private final Path IMAGE_BUCKET_DIRECTORY = Path.of("/grayscale");

  public void uploadImage(File image, String imageName) {
    String bucketKey = IMAGE_BUCKET_DIRECTORY + imageName;
    bucketComponent.upload(image, bucketKey);
    boolean isDelete = image.delete();
    if (!isDelete) {
      throw new RuntimeException("image" + bucketKey + "is not deleted.");
    }
  }

  public ImageInfoURL getImageInfoUrl(String imageInfoId) {
    ImageModel imageModel =
        imageRepository
            .findById(imageInfoId)
            .orElseThrow(() -> new RuntimeException("image id does not exist"));
    String originalUrl =
        bucketComponent.presign(imageModel.getOriginaleBucketKey(), Duration.ofDays(1)).toString();
    String grayscale =
        bucketComponent.presign(imageModel.getGrayscaleBucketKey(), Duration.ofDays(1)).toString();

    ImageInfoURL imageInfoURL = new ImageInfoURL(originalUrl, grayscale);
    return imageInfoURL;
  }

  public File toGrayscale(File originalimage, File grayscaleImage) {
    ImagePlus imagePlus = IJ.openImage(originalimage.getPath());
    ImageConverter converter = new ImageConverter(imagePlus);
    converter.convertToGray8();
    ij.io.FileSaver imageSaver = new ij.io.FileSaver(imagePlus);
    imageSaver.saveAsPng(grayscaleImage.getPath());
    return grayscaleImage;
  }

  private File imageFromByteArray(byte[] bytes, File file) {
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(bytes);
      return file;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public ImageInfoURL uploadAndToGrayscale(String id, byte[] image) throws IOException {
    if (image == null) {
      throw new RemoteException("Image is required");
    }
    String imageSuffix = ".png";
    String newImageName = id + imageSuffix;
    String grayscaleName = id + "-grayscale" + imageSuffix;

    File originaleImage = File.createTempFile(newImageName, imageSuffix);
    File grayscaleTempImage = File.createTempFile(grayscaleName, imageSuffix);
    imageFromByteArray(image, originaleImage);
    File grayscaleImage = toGrayscale(originaleImage, grayscaleTempImage);

    uploadImage(originaleImage, newImageName);
    uploadImage(grayscaleImage, grayscaleName);
    ImageModel imageModel =
        new ImageModel(
            id, IMAGE_BUCKET_DIRECTORY + newImageName, IMAGE_BUCKET_DIRECTORY + grayscaleName);
    imageRepository.save(imageModel);
    return getImageInfoUrl(id);
  }
}
