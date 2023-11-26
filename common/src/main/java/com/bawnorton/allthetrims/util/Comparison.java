package com.bawnorton.allthetrims.util;

public enum Comparison {
    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL_TO(">="),
    LESS_THAN_OR_EQUAL_TO("<=");

    private final String symbol;

    Comparison(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Comparison parseComparison(String versionPredicate) {
        if (versionPredicate.startsWith(GREATER_THAN_OR_EQUAL_TO.symbol)) {
            return GREATER_THAN_OR_EQUAL_TO;
        } else if (versionPredicate.startsWith(LESS_THAN_OR_EQUAL_TO.symbol)) {
            return LESS_THAN_OR_EQUAL_TO;
        } else if (versionPredicate.startsWith(GREATER_THAN.symbol)) {
            return GREATER_THAN;
        } else if (versionPredicate.startsWith(LESS_THAN.symbol)) {
            return LESS_THAN;
        } else if (versionPredicate.startsWith(EQUALS.symbol)) {
            return EQUALS;
        } else if (versionPredicate.startsWith(NOT_EQUALS.symbol)) {
            return NOT_EQUALS;
        } else {
            throw new IllegalArgumentException("Invalid comparison " + versionPredicate);
        }
    }

    public boolean satisfies(int compareToResult) {
        return switch (this) {
            case EQUALS -> compareToResult == 0;
            case NOT_EQUALS -> compareToResult != 0;
            case GREATER_THAN -> compareToResult > 0;
            case LESS_THAN -> compareToResult < 0;
            case GREATER_THAN_OR_EQUAL_TO -> compareToResult >= 0;
            case LESS_THAN_OR_EQUAL_TO -> compareToResult <= 0;
        };
    }
}
