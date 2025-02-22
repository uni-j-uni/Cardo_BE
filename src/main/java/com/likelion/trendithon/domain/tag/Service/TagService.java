package com.likelion.trendithon.domain.tag.Service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.trendithon.domain.card.entity.Card;
import com.likelion.trendithon.domain.tag.dto.TagDto;
import com.likelion.trendithon.domain.tag.entity.Tag;
import com.likelion.trendithon.domain.tag.repository.TagRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TagService {

  private TagRepository tagRepository;

  public void saveTag(List<TagDto> tags, Card newCard) {
    List<Tag> newTags =
        tags.stream()
            .map(
                tagDto ->
                    Tag.builder()
                        .tagTitle(tagDto.getTagTitle())
                        .tagContent(tagDto.getTagContent())
                        .card(newCard)
                        .build())
            .toList();
    tagRepository.saveAll(newTags);
  }

  @Transactional
  public List<Tag> getTags(Card card) {

    return tagRepository.findByCard(card);
  }

  @Transactional
  public void updateTags(Card card, List<TagDto> newTags) {

    List<Tag> tags = tagRepository.findByCard(card);

    // tags엔 존재하는데 newTags에 존재하지 않으면 삭제 같으면 업뎃
    for (Tag tag : tags) {
      boolean found = false;
      for (TagDto newTag : newTags) {
        if (Objects.equals(tag.getTagTitle(), newTag.getTagTitle())) {
          tag.setTagTitle(newTag.getTagTitle());
          tag.setTagContent(newTag.getTagContent());
          found = true;
        }
      }

      if (!found) {
        tagRepository.delete(tag);
      }
    }

    // newTags엔 존재하는데 tags엔 존재하지 않으면 추가
    for (TagDto newTag : newTags) {
      // 기존 [1,2,3,4] 업데이트 [2,3,6]
      boolean found = false;

      for (Tag tag : tags) {
        if (Objects.equals(tag.getTagTitle(), newTag.getTagTitle())) {
          found = true;
          break;
        }
      }

      if (!found) {

        Tag tmp =
            Tag.builder()
                .tagTitle(newTag.getTagTitle())
                .tagContent(newTag.getTagContent())
                .card(card)
                .build();
        tagRepository.save(tmp);
      }
    }
  }
}
