echo -e "Running tests\n"

INTERPRETER_PATH="./janeklang/dist-newstyle/build/x86_64-linux/ghc-9.0.2/janeklang-0.1.0.0/x/janeklang/build/janeklang/janeklang"

TMP_OUT=/tmp/out.tmp
TMP_ERR=/tmp/err.tmp

total_tests=0
passed_tests=0

run_test() {
    file=$1
    let total_tests+=1  # Increment total tests here, assuming each file represents a single test
    echo -e "\nTesting $file"

    $INTERPRETER_PATH $file > $TMP_OUT 2> $TMP_ERR

    test_passed=true  # Flag to track if all checks within this test passed

    # Check the output files
    if diff ${file%.janek}.out $TMP_OUT >/dev/null; then
        echo -e "Output OK"
    else
        echo -e "ERROR IN OUTPUT ${file%.janek}.out"
        echo "Expected:"
        cat ${file%.janek}.out
        echo "Got:"
        cat $TMP_OUT
        test_passed=false
    fi

    # Check the error files
    if diff ${file%.janek}.err $TMP_ERR >/dev/null; then
        echo -e "Error Output OK"
    else
        echo -e "ERROR IN ERROR ${file%.janek}.err"
        echo "Expected error:"
        cat ${file%.janek}.err
        echo "Got error:"
        cat $TMP_ERR
        test_passed=false
    fi

    if [ "$test_passed" = true ]; then
        let passed_tests+=1
    fi
}

# Run tests for good cases
if ls ./tests/good/*.janek 1> /dev/null 2>&1; then
    for file in ./tests/good/*.janek; do
        run_test $file
    done
else
    echo "No tests found in ./tests/good"
fi

# Run tests for bad cases
if ls ./tests/bad/*.janek 1> /dev/null 2>&1; then
    for file in ./tests/bad/*.janek; do
        run_test $file
    done
else
    echo "No tests found in ./tests/bad"
fi

# Summary of results
echo -e "\nTotal tests: $total_tests"
echo -e "Passed tests: $passed_tests"
echo -e "Failed tests: $((total_tests - passed_tests))"
