package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@IntegrationTest
public class GetCategoryByIdUseCaseIT {

  @Autowired
  private GetCategoryByIdUseCase useCase;

  @Autowired
  private CategoryRepository categoryRepository;

  @SpyBean
  private CategoryGateway categoryGateway;

  @Test
  public void givenAValidId_whenCallsGetCategory_thenShouldReturnCategory() {
    final var expectedName = "Filmes";
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = true;

    final var aCategory =
        Category.newCategory(expectedName, expectedDescription, expectedIsActive);

    final var expectedId = aCategory.getId();

    save(aCategory);

    final var actualCategory = useCase.execute(expectedId.getValue());

    Assertions.assertEquals(expectedId, actualCategory.id());
    Assertions.assertEquals(expectedName, actualCategory.name());
    Assertions.assertEquals(expectedDescription, actualCategory.description());
    Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
    Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
    Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
    Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());
  }

  private void save(final Category... theCategories) {
    categoryRepository.saveAllAndFlush(
        Arrays.stream(theCategories).map(CategoryJpaEntity::from).toList()
    );
  }

  @Test
  public void givenAnInvalidId_whenCallsGetCategory_thenShouldReturnNotFound() {
    final var expectedId = CategoryID.from("123");
    final var expectedErrorMessage = "Category with ID 123 was not found";

    final var actualException =
        Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

    Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
  }

  @Test
  public void givenAValidId_whenGatewayThrowsException_thenShouldReturnException() {
    final var expectedId = CategoryID.from("123");
    final var expectedErrorMessage = "Gateway error";

    doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).findById(eq(expectedId));

    final var actualException =
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

    Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
  }
}
