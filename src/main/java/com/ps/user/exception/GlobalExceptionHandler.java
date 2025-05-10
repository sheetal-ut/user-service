package com.ps.user.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.web.bind.annotation.ControllerAdvice;
import reactor.core.publisher.Mono;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler implements DataFetcherExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        GraphQLError error;

        if (exception instanceof UserNotFoundException || exception instanceof DuplicateUserException) {
            logger.warn("Expected error occurred : {}", exception.getMessage());
            error = GraphqlErrorBuilder.newError(environment)
                    .message(exception.getMessage())
                    .errorType(graphql.ErrorType.DataFetchingException)
                    .build();
        } else {
            logger.error("Unexpected error :", exception);
            error = GraphqlErrorBuilder.newError(environment)
                    .message("Internal server error")
                    .errorType(graphql.ErrorType.DataFetchingException)
                    .build();
        }

        return Mono.just(List.of(error));
    }
}
