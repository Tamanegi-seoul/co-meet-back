package Tamanegiseoul.comeet.dto.member.response;

import Tamanegiseoul.comeet.domain.ImageData;
import Tamanegiseoul.comeet.utils.ImageUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageDto {
    private Long imageId;
    private String fileName;
    private String fileType;
    private byte[] imageData;

    public static ImageDto toDto(ImageData file) {
        // 등록된 이미지가 없는 경우, null 반환
        if(file==null)
            return null;
        // 등록된 이미지가 있는 경우, 넘겨받은 데이터 기반으로 DTO 생성
        return ImageDto.builder()
                .imageId(file.getImageId())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .imageData(ImageUtil.decompressImage(file.getImageData()))
                .build();
    }
}
