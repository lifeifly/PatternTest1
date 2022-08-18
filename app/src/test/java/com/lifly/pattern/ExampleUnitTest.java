package com.lifly.pattern;

import com.lifly.pattern.calculator.DataGenerator;
import com.lifly.pattern.calculator.binaryfind.BinaryFind;
import com.lifly.pattern.calculator.sort.DataChecker;
import com.lifly.pattern.calculator.sort.SortManager;

import org.junit.Test;

import java.net.BindException;
import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        int[] arr= DataGenerator.generatorRandomArray(100,10);
//        DataGenerator.comparator(arr);
//        System.out.println(Arrays.toString(arr));
//        int a=BinaryFind.nearestRightIndex(arr,10);
//        int b= BinaryFind.test(arr,10);

//        System.out.println((a==b)+",a="+a+",b="+b);
        System.out.println(DataChecker.check());
    }
}