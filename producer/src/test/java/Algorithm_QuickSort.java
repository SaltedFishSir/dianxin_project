public class Algorithm_QuickSort {
    public static void main(String[] args) {
        int[] data = new int[]{7, 1, 6, 4, 3, 2, 1, 23};

//        Algorithm_QuickSort.quickSort(data);
//        Algorithm_QuickSort.quick_sort(data,0,data.length-1);
//
//        StringBuilder playnum = new StringBuilder();
//        for (int i : data) {
//            playnum.append(i + ",");
//        }
//        System.out.println(playnum.toString());



        int a[] = new int[]{1,3,4,5};
        int n = 4;
        int b[] = new int[]{2,8,9};
        int m = 3;
        int c[] = new int[7];

        mergeArray(a,n,b,m,c);
        StringBuilder playnum = new StringBuilder();
        for (int i : c) {
            playnum.append(i + ",");
        }
        System.out.println(playnum.toString());


    }

    //第一步，查找一个基准位
    public static int getPart(int low, int high, int[] data) {

        int comData = data[low]; //设定一个初始基准
        while (low < high) {
            while (low < high && data[high] >= comData) {//从右向左查找
                high--;
            }
            if (low < high) {
                data[low] = data[high]; //小于基准的移到低端
            }

            while (low < high && data[low] <= comData) { //从左向右查找
                low++;
            }
            if (low < high) {
                data[high] = data[low]; //大于基准的移到高端
            }
        }
        data[low] = comData; //基准位置不再变化时
        return low;
    }

    //第二步，采用递归的方式处理基准左右两堆
    public static void quick_Recursion(int low, int high, int[] data) {

        int base;
        if (low < high) {
            base = getPart(low, high, data);//获取基准
            quick_Recursion(low, base - 1, data);//对低基准位段进行递归排序
            quick_Recursion(base + 1, high, data);//对高基准位段进行递归排序
        }
    }

    //第三步，打印排序的序列
    public static void quickSort(int[] data) {
        quick_Recursion(0, data.length - 1, data);

        StringBuilder playnum = new StringBuilder();
        for (int i : data) {
            playnum.append(i + ",");
        }
        System.out.println(playnum.toString());
    }


    /**
     * 快速排序
     * @param s
     * @param l
     * @param r
     */
    public static void quick_sort(int s[], int l, int r) {
        if (l < r) {
            //Swap(s[l], s[(l + r) / 2]); //将中间的这个数和第一个数交换 参见注1
            int i = l, j = r, x = s[l];
            while (i < j) {
                while (i < j && s[j] >= x) // 从右向左找第一个小于x的数
                    j--;
                if (i < j)
                    s[i++] = s[j];

                while (i < j && s[i] < x) // 从左向右找第一个大于等于x的数
                    i++;
                if (i < j)
                    s[j--] = s[i];
            }
            s[i] = x;
            quick_sort(s, l, i - 1); // 递归调用
            quick_sort(s, i + 1, r);
        }

    }


    /**
     * 归并
     * @param a
     * @param n
     * @param b
     * @param m
     * @param c
     */
    //将有序数组a[]和b[]合并到c[]中
    public static void mergeArray(int a[], int n, int b[], int m, int c[])
    {
        int i, j, k;

        i = j = k = 0;
        while (i < n && j < m)
        {
            if (a[i] < b[j])
                c[k++] = a[i++];
            else
                c[k++] = b[j++];
        }

        while (i < n)
            c[k++] = a[i++];

        while (j < m)
            c[k++] = b[j++];
    }

}