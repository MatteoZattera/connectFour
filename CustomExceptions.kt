package connectfour

class IllegalDimensionException(dimension: String) : Exception("Board $dimension should be from $MIN_DIMENSION to $MAX_DIMENSION")
class IllegalInputException : Exception("Invalid input")
class ColumnOutOfRangeException(columns: Int) : Exception("The column number is out of range (1 - $columns)")
class FullColumnException(column: Int) : Exception("Column $column is full")
