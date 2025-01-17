package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils.like;

@Service
public class CategoryMySQLGateway implements CategoryGateway {

  private final CategoryRepository repository;

  public CategoryMySQLGateway(final CategoryRepository repository) {
    this.repository = repository;
  }

  @Override
  public Category create(final Category aCategory) {
    return save(aCategory);
  }

  @Override
  public void deleteById(final CategoryID anId) {
    final var anIdValue = anId.getValue();

    if (this.repository.existsById(anIdValue)) {
      this.repository.deleteById(anIdValue);
    }
  }

  @Override
  public Optional<Category> findById(final CategoryID anId) {
    return this.repository.findById(anId.getValue()).map(CategoryJpaEntity::toAggregate);
  }

  @Override
  public Category update(final Category aCategory) {
    return save(aCategory);
  }

  @Override
  public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
    // Pagination
    final var page = PageRequest.of(
        aQuery.page(),
        aQuery.perPage(),
        Sort.by(Direction.fromString(aQuery.direction()), aQuery.sort())
    );

    // Search by (name or description)
    final var specifications = Optional.ofNullable(aQuery.terms())
        .filter(str -> !str.isBlank())
        .map(str -> SpecificationUtils.
            <CategoryJpaEntity>like("name", str)
            .or(like("description", str))
        )
        .orElse(null);

    final var pageResult = this.repository.findAll(Specification.where(specifications), page);

    return new Pagination<Category>(
        pageResult.getNumber(),
        pageResult.getSize(),
        pageResult.getTotalElements(),
        pageResult.map(CategoryJpaEntity::toAggregate).toList()
    );
  }

  private Category save(final Category aCategory) {
    return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
  }
}
