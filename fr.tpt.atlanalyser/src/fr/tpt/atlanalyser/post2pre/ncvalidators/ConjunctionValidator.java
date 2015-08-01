package fr.tpt.atlanalyser.post2pre.ncvalidators;

import java.util.stream.Stream;

import org.eclipse.emf.henshin.model.NestedCondition;

import com.google.common.base.Joiner;

public class ConjunctionValidator implements NestedConditionValidator {

    private NestedConditionValidator[] validators;

    public ConjunctionValidator(NestedConditionValidator... validators) {
        this.validators = validators;
    }

    @Override
    public boolean isValid(NestedCondition nc) {
        for (NestedConditionValidator validator : validators) {
            if (!validator.isValid(nc)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConjunctionValidator("
                + Joiner.on(", ").join(
                        Stream.of(validators)
                                .map(NestedConditionValidator::toString)
                                .toArray()) + ")";
    }

}
