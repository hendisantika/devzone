package com.sivalabs.devzone.links.adapter.repositories;

import com.sivalabs.devzone.common.PagedResult;
import com.sivalabs.devzone.common.exceptions.ResourceNotFoundException;
import com.sivalabs.devzone.links.adapter.entities.CategoryEntity;
import com.sivalabs.devzone.links.adapter.entities.LinkEntity;
import com.sivalabs.devzone.links.adapter.mappers.LinkMapper;
import com.sivalabs.devzone.links.domain.models.Category;
import com.sivalabs.devzone.links.domain.models.Link;
import com.sivalabs.devzone.links.domain.repositories.CategoryRepository;
import com.sivalabs.devzone.links.domain.repositories.LinkRepository;
import com.sivalabs.devzone.users.adapter.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class LinkRepositoryImpl implements LinkRepository {
    private static final Integer PAGE_SIZE = 15;
    private final JpaLinkRepository jpaLinkRepository;
    private final JpaLinkCreatorRepository jpaLinkCreatorRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final LinkMapper linkMapper;

    public LinkRepositoryImpl(
            JpaLinkRepository jpaLinkRepository,
            JpaLinkCreatorRepository jpaLinkCreatorRepository,
            JpaCategoryRepository jpaCategoryRepository,
            CategoryRepository categoryRepository,
            LinkMapper linkMapper) {
        this.jpaLinkRepository = jpaLinkRepository;
        this.jpaLinkCreatorRepository = jpaLinkCreatorRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.linkMapper = linkMapper;
    }

    @Override
    public Optional<Link> findById(Long id) {
        return jpaLinkRepository.findById(id).map(linkMapper::toModel);
    }

    @Override
    public Link save(Link link) {
        LinkEntity entity = linkMapper.toEntity(link);
        UserEntity createdBy =
                jpaLinkCreatorRepository.findById(link.createdBy().id()).orElseThrow();
        CategoryEntity category = this.getOrCreateCategory(link.category());
        entity.setCreatedBy(createdBy);
        entity.setCategory(category);
        var savedLink = jpaLinkRepository.save(entity);
        return linkMapper.toModel(savedLink);
    }

    @Override
    public PagedResult<Link> getAllLinks(Integer page) {
        Pageable pageable = getPageable(page);
        Page<LinkEntity> linksPage = jpaLinkRepository.findLinks(pageable);
        return getLinks(pageable, linksPage);
    }

    @Override
    public PagedResult<Link> searchLinks(String query, Integer page) {
        Pageable pageable = getPageable(page);
        Page<LinkEntity> linksPage =
                jpaLinkRepository.findLinksByTitleContainingIgnoreCase(query, pageable);
        return getLinks(pageable, linksPage);
    }

    @Override
    public PagedResult<Link> getLinksByCategory(String category, Integer page) {
        Optional<CategoryEntity> categoryOptional = jpaCategoryRepository.findByName(category);
        if (categoryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Category " + category + " not found");
        }
        Pageable pageable = getPageable(page);
        Page<LinkEntity> linksPage = jpaLinkRepository.findLinksByCategory(category, pageable);
        return getLinks(pageable, linksPage);
    }

    @Override
    public void deleteAll() {
        jpaLinkRepository.deleteAllInBatch();
    }

    @Override
    public void deleteById(Long id) {
        jpaLinkRepository.deleteById(id);
    }

    private static Pageable getPageable(Integer page) {
        int pageNo = page > 0 ? page - 1 : 0;
        return PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private PagedResult<Link> getLinks(Pageable pageable, Page<LinkEntity> linkEntitiesPage) {
        if (linkEntitiesPage.isEmpty()) {
            return new PagedResult<>(Page.empty());
        }
        var links = linkEntitiesPage.stream().map(linkMapper::toModel).toList();
        Page<Link> linksPage = new PageImpl<>(links, pageable, linkEntitiesPage.getTotalElements());
        return new PagedResult<>(linksPage);
    }

    private CategoryEntity getOrCreateCategory(Category category) {
        categoryRepository.upsert(category);
        return jpaCategoryRepository.findByName(category.name()).orElseThrow();
    }
}
