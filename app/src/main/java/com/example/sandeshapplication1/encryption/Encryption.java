package com.example.sandeshapplication1.encryption;

import com.google.firestore.admin.v1.Index;

import java.util.HashMap;

public class Encryption {



    public int[] caeserCipher(String text) {

        int arr1[]=new int[text.length()];

        int s=4;

        for (int i = 0; i < text.length(); i++)
        {
            int num = ((int)text.charAt(i)+s)%26;
            arr1[i]=num;
        }
       int arr2[]= HillCipher(arr1);
        int arr3[]= PrimeNumberIndexing(arr2);

        return arr3;
    }

    public  int[][] multiplyMatrices(int[][] a, int[][] b) {
        int[][] c = new int[1][2];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }
    public int[]  HillCipher (int arr[]) {
        int[][] key = {{3, 4}, {5, 6}};

        int arr2[] = new int[arr.length];
        if (arr.length % 2 == 0) {
            for (int i = 0; i < arr.length; i = i + 2) {
                int[][] a = new int[1][2];
                a[0][0] = arr[i];
                a[0][1] = arr[i + 1];
                int[][] c = new int[1][2];
                c = multiplyMatrices(a, key);
                arr2[i] = c[0][0];
                arr2[i] = c[0][1];
            }
        }
        return  arr2;
    }

    public int[] PrimeNumberIndexing(int arr2[])
    {
        HashMap <Integer,Integer > h= new HashMap<>();
        h.put(0, 2);
        h.put(1, 5);
        h.put(2, 7);
        h.put(3, 11);
        h.put(4, 13);
        h.put(5, 17);
        h.put(6, 19);
        h.put(7, 23);
        h.put(8, 29);
        h.put(9, 31);
        h.put(10, 37);
        h.put(11, 41);
        h.put(12, 43);
        h.put(13, 47);
        h.put(14, 53);
        h.put(15, 59);
        h.put(16, 61);
        h.put(17, 67);
        h.put(18, 71);
        h.put(19, 73);
        h.put(20, 79);
        h.put(21, 83);
        h.put(22, 89);
        h.put(23, 97);



        for(int i=0;i<arr2.length;i++)
        {
            arr2[i]=arr2[i]+ h.get(i);

        }
        return arr2;
    }



}
