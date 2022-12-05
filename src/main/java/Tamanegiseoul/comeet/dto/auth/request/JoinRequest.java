package Tamanegiseoul.comeet.dto.auth.request;

import Tamanegiseoul.comeet.domain.enums.TechStack;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JoinRequest {
    private String email;
    private String password;
    private String nickname;
    private List<TechStack> preferStacks;
}