# Matrix Representation

## Running the Project

The program can be compiled into a single `jar` file using the command

```shell
./gradlew shadowJar
```

The build result is then located in the `build/libs` directory and can be run from the console using the command

```shell
java -jar build/libs/oop-2022-all.jar
```

However, it is recommended to use configurations prepared by development environments,
such as Intellij, during the solution creation phase, which allow
running the `main` function from the user interface and easily debugging
our program.

## Testing

Tests from the `src/test` directory can be run with the command

```shell
./gradlew test
```

which runs all functions marked with annotations provided
by JUnit, e.g., `@Test`.
