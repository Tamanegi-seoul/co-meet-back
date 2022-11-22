package Tamanegiseoul.comeet.dto.post.response;

import Tamanegiseoul.comeet.domain.Posts;
import Tamanegiseoul.comeet.domain.enums.ContactType;
import Tamanegiseoul.comeet.domain.enums.RecruitStatus;
import Tamanegiseoul.comeet.domain.enums.TechStack;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchPostResponse {
    private Long postId;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private RecruitStatus recruitStatus;
    private Long recruitCapacity;
    @Enumerated(EnumType.STRING)
    private ContactType contactType;
    private String contact;
    private LocalDate startDate;
    private Long expectedTerm;
    private Long posterId;
    private String posterNickname;
    @Enumerated(EnumType.STRING)
    private List<TechStack> designatedStacks;
    private LocalDate createdDate;
    private LocalDate modifiedDate;

    public static SearchPostResponse toDto(Posts findPost) {
        return SearchPostResponse.builder()
                .postId(findPost.getId())
                .title(findPost.getTitle())
                .content(findPost.getContent())
                .posterId(findPost.getPoster().getId())
                .posterNickname(findPost.getPoster().getNickname())
                .contactType(findPost.getContactType())
                .contact(findPost.getContact())
                .recruitCapacity(findPost.getRecruitCapacity())
                .recruitStatus(findPost.getRecruitStatus())
                .startDate(findPost.getStartDate())
                .expectedTerm(findPost.getExpectedTerm())
                .createdDate(findPost.getCreatedDate())
                .modifiedDate(findPost.getModifiedDate())
                .build();
    }

    public SearchPostResponse designatedStacks(List<TechStack> stacks) {
        this.designatedStacks = stacks;
        return this;
    }


}