package Tamanegiseoul.comeet.dto.post.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RemovePostRequest {
    @Schema(description = "삭제할 포스트 ID", example = "5")
    private Long postId;
}
