package com.example.sandeshapplication1.encryption;

import java.util.HashMap;

public class Decryption {

    public static int[][] invertMatrix(int[][] a) {
        int det = a[0][0] * a[1][1] - a[0][1] * a[1][0];
        if (det == 0) {
            return null;
        }

        int[][] b = new int[2][2];
        b[0][0] = a[1][1];
        b[0][1] = -a[0][1];
        b[1][0] = -a[1][0];
        b[1][1] = a[0][0];

        int[][] bInverse = new int[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                bInverse[i][j] = b[i][j] / det;
            }
        }

        return bInverse;
    }

    public int[] caeserCipherDecrypt(int arr[]) {

        int s=4;


        int arr3[]= PrimeNumberIndexingDecrypt(arr);
        int arr2[]= HillCipherDecrypt(arr3);
        for(int i=0;i<arr3.length;i++)
        {
            arr2[i]=arr2[i]-s;
            arr2[i]=arr2[i]%26;
        }
        String Decode="";
        for(int i=0;i<arr2.length;i++)
        {
            Decode=Decode+(char)(arr2[i]);
        }


        return arr3;
    }

     public int[] HillCipherDecrypt(int arr2[])
     {
         //functionality
         int[][] key = {{3, 4}, {5, 6}};


         if (arr2.length % 2 == 0) {
             for (int i = 0; i < arr2.length; i = i + 2) {
                 int[][] a = new int[1][2];
                 a[0][0] = arr2[i];
                 a[0][1] = arr2[i + 1];
                 int[][] c = new int[1][2];
                 //c = invertMatrix(a, key);
                 arr2[i] = c[0][0];
                 arr2[i] = c[0][1];
             }
         }
         return arr2;
     }



    public int[] PrimeNumberIndexingDecrypt(int arr2[])
    {
        HashMap<Integer,Integer > h= new HashMap<>();
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
            arr2[i]=arr2[i]- h.get(i);

        }
        return arr2;
    }

}
