package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

import java.util.Objects;

import static io.vavr.API.*;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

  private final CategoryGateway categoryGateway;

  public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
    this.categoryGateway = Objects.requireNonNull(categoryGateway);
  }

  @Override
  public Either<Notification, CreateCategoryOutput> execute(final CreateCategoryCommand aCommand) {
    final var aName = aCommand.name();
    final var aDescription = aCommand.description();
    final var isActive = aCommand.isActive();

    final var notification = Notification.create();
    final var aCategory = Category.newCategory(aName, aDescription, isActive);
    aCategory.validate(notification);

    return notification.hasError() ? Left(notification) : create(aCategory);
  }

  private Either<Notification, CreateCategoryOutput> create(final Category aCategory) {
    return Try(() -> this.categoryGateway.create(aCategory))
        .toEither()
        .bimap(Notification::create, CreateCategoryOutput::from);

    //  The code above is the same as the code below
    //  try {
    //    return Right(CreateCategoryOutput.from(this.categoryGateway.create(aCategory)));
    //  } catch (Throwable t) {
    //    return Left(Notification.create(t));
    //  }
  }
}
