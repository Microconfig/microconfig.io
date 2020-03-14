package io.microconfig.domain.impl.properties.resolvers.expression;

import io.microconfig.domain.StatementResolver;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class ExpressionResolver implements StatementResolver {
    private final Pattern expressionPattern = compile("#\\{(?<value>[^{]+?)}");

    @Override
    public Optional<Statement> findStatementIn(CharSequence line) {
        Matcher matcher = expressionPattern.matcher(line);
        return matcher.find() ? of(toExpression(matcher)) : empty();
    }

    private Expression toExpression(Matcher matcher) {
        return new Expression(matcher.group("value"), matcher.start(), matcher.end());
    }
}