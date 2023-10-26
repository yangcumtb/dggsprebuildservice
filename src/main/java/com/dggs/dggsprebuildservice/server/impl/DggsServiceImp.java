package com.dggs.dggsprebuildservice.server.impl;

import com.dggs.dggsprebuildservice.config.GDALInitializer;
import com.dggs.dggsprebuildservice.model.BuildParam;
import com.dggs.dggsprebuildservice.model.SpatialData;
import com.dggs.dggsprebuildservice.server.DggsService;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DggsServiceImp implements DggsService {


    @Override
    public void preBuild(SpatialData spatialData, BuildParam buildParam) {
        GDALInitializer.initialize();

        Dataset mydate = gdal.Open("/Users/yang/Documents/xxx项目预处理/data/GTIFF/chengnanjiedao0.tif");

        double[] c = mydate.GetGeoTransform();
        System.out.println(c);

        //冒泡排序测试
        int[] arr = new int[]{3, 2, 6, 1, 7, 9, 10};
        mydate.delete();
//
//        for (int i = 0; i < arr.length; i++) {
//            for (int j = i + 1; j < arr.length; j++) {
//                if (arr[i] > arr[j]) {
//                    int temp = arr[i];
//                    arr[i] = arr[j];
//                    arr[j] = temp;
//                }
//            }
//        }
        quicksort(arr, 0, arr.length - 1);

        System.out.println(Arrays.toString(arr));
        return;
    }

    public void quicksort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quicksort(arr, low, pivotIndex - 1);
            quicksort(arr, pivotIndex + 1, high);
        }

    }


    public static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
