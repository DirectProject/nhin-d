namespace Health.Direct.Policy
{
    public enum PolicyOpCode
    {
        EQUALS,
        NOT_EQUALS,
        GREATER,
        LESS,
        REG_EX,
        CONTAINS,
        NOT_CONTAINS,
        CONTAINS_REG_EX,
        SIZE,
        EMPTY,
        NOT_EMPTY,
        INTERSECTION, //TODO: still need to implement.
        LOGICAL_OR,
        LOGICAL_AND,
        LOGICAL_NOT,
        BITWISE_AND,
        BITWISE_OR,
        URI_VALIDATE
    }
}