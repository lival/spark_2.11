import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FindDupSeqs {

    // 输出数组
    public static void printResultList(List<String> strList) {
        int listSize=strList.size();
        for (int i = 0; i < listSize; i++) {
            System.out.print("["+strList.get(i)+"]");
            if (i == listSize - 1) {
                System.out.print("\n");
            } else {
                System.out.print(",");
            }
        }
    }

    public  Map<String,Object> findLongestDupString(String s){
        Map<String,Object> resultMap=new HashMap<String,Object>();

        Set<String> postfix = new TreeSet<String>();//存储后缀数组，并排序
        int num=s.length();
        int count=0;
        postfix.add(s);
        for(int i = 0; i < num; i++){
            int splitIndex=s.indexOf(',');
            if (splitIndex>0) {
                ///System.out.println("splitIndex="+splitIndex);
                String sub=s.substring(splitIndex+1);
                postfix.add(sub);
              //  System.out.println("sub"+count+++"="+sub);
                s=sub;
            }
            ///System.out.println("\n");
        }
       // System.out.println("构造出来的排序后是后缀数组长度为："+postfix.size()+"，内容为："+postfix);

        List<String> postfixList = new ArrayList<String>(postfix);//set转换成list方便调用
        //Set<String> dupStrSet = new HashSet<String>();

        //key是目标字符串，value是重复次数。注意重复次数=出现次数-1
        Map<String, Integer> dupStrMap = new HashMap<String, Integer>();

        //key是目标字符串，value是这个字符串中的最大的id。按照目前的方式就是第一个逗号之前的那一部分
        Map<String, Integer> maxIdMap = new HashMap<String, Integer>();

        int temp=0;
        String maxDupStr="";
        int postfixListSize=postfixList.size();
        for(int i = 0; i < postfixListSize-1; i++){//循环，找出最长的重复子序列下标，最长的重复子序列截取位置
            String str1=postfixList.get(i);
            String str2=postfixList.get(i+1);
            int maxCommonlength = maxCommonlength(str1, str2);//获取后缀数组两两比较的相同子序列长度
           // System.out.println("第"+i+"轮比较：str1="+str1+";str2="+str2);
            if (maxCommonlength>0) {
                String currentResult=getResultStr(postfixList.get(i),maxCommonlength);
                /*System.out.println("第"+i+"轮比较结果：maxCommonlength="+maxCommonlength+";str1="+str1+",str2="+str2);
                System.out.println("第"+i+"轮比较结果：currentResult="+currentResult);*/
                if(maxCommonlength > temp){
                    temp = maxCommonlength;
                    maxDupStr=currentResult;
                 //   System.out.println("第"+i+"轮比较，更新maxDupStr="+maxDupStr);
                }

                if (currentResult.contains(",")) {//如果只是单个的id字符串则不用记录
                    if (dupStrMap.get(currentResult)==null) {
                        dupStrMap.put(currentResult, 1);
                        maxIdMap.put(currentResult, Integer.valueOf(currentResult.substring(0, currentResult.indexOf(","))));
                      //  System.out.println("第"+i+"轮比较，新发现currentResult="+currentResult);
                    }else {
                        dupStrMap.put(currentResult, dupStrMap.get(currentResult)+1);
                      //  System.out.println("第"+i+"轮比较，currentResult的次数加一变为"+dupStrMap.get(currentResult));
                    }
                }

/*                if (!dupStrSet.contains(currentResult)) {
                    dupStrSet.add(currentResult);
                    System.out.println("第"+i+"轮比较，将currentResult="+currentResult+"加入到结果集中");
				}*/
            }
        }
        //resultMap.put("dupStrSet", dupStrSet);
        resultMap.put("dupStrMap", dupStrMap);
        resultMap.put("maxDupStr", maxDupStr);
        resultMap.put("maxIdMap", maxIdMap);
        return resultMap;
    }

    private static int maxCommonlength(String str1, String str2) {
        char[] charArray1 = str1.toCharArray();
        char[] charArray2 = str2.toCharArray();
        int maxlen = 0;
        for(int i = 0; i < (charArray1.length > charArray2.length ? charArray2.length : charArray1.length); i++){
            if(charArray1[i] == charArray2[i]){
                if (charArray1[i]==',') {
                    continue;
                } else {
                    maxlen++;
                }
            }else {
                break;
            }
        }
        return maxlen;
    }

    /**
     * 在匹配的字符串里面根据maxCommonlength参数，找到过滤掉逗号后的正确的结果。
     * 即找出这个字符串里面除了maxCommonlength-1个逗号后的子串
     * 比如str1=1,0;str2=1,0,3,2,1,0，maxCommonlength=2;
     * 要返回的是"1,0"，而不是"1,"
     *
     * 因为后缀数组里面已经进行了排序，所以str1和str2比较可能出现三种情况：
     * 	1、两者没有公共字符；
     * 	2、str1和str2有部分公共字符；
     * 	3、str1的字符全部都在str2中。
     *
     * maxCommonlength是两者公共的字符数-这部分公共字符数的逗号个数
     * */
    private static String getResultStr(String str,int maxCommonlength) {
        //maxCommonlength小于2的时候即为1，否则不大于0就不会调用本方法
        if (maxCommonlength<2) {
            return str.substring(0,maxCommonlength);
        }
        //maxCommonlength等于2的时候，且str只有一个逗号，则直接返回当前的str
        if (maxCommonlength==2&&str.indexOf(',')==str.lastIndexOf(',')) {
            return str;
        }

        char[] charArray = str.toCharArray();
        int count=0;
        int resultIndex=0;
        for (int i = 0; i < charArray.length&&count<maxCommonlength; i++) {
            if (charArray[i]==',') {
                count++;
            }
            resultIndex=i;
        }
        return str.substring(0,resultIndex+1).endsWith(",")?str.substring(0,resultIndex):str.substring(0,resultIndex+1);
    }

    /**
     * 测试案例：100,11,10,9,8,7,6,5,4,3,2,1,0,8,7,6,5,4,3,2,1,0,3,2,1,0
     * 识别出来的应该就是【8,7,6,5,4,3,2,1,0】
     * 其他字符串【3,2,1,0】【2,1,0】【1,0】
     * */
 /*   public static void main(String[] args){
        //String str="100,11,10,9,8,7,6,5,4,3,2,1,0,8,7,6,5,4,3,2,1,0,3,2,1,0";
        String str="6,5,4,3,2,1,0,7,6,5,4,3,2,1,0";
        //String str="4,3,2,1,0,5,3,2,1,0";

        System.out.println("原始输入串："+str);
        //printResultList(findDup(str));
        System.out.println("计算结果："+findLongestDupString(str));
        *//**计算结果：{dupStrMap={1,0=1, 4,3,2,1,0=1, 5,4,3,2,1,0=1, 3,2,1,0=1, 6,5,4,3,2,1,0=1, 2,1,0=1},
         * maxIdMap={1,0=1, 4,3,2,1,0=4, 5,4,3,2,1,0=5, 3,2,1,0=3, 6,5,4,3,2,1,0=6, 2,1,0=2},
         * maxDupStr=6,5,4,3,2,1,0}
         * dupStrMap表示（1,0）出现2次，
         * *//*
        //System.out.println(getResultStr("1",1));
        //System.out.println(getResultStr("1,0",2));
        //System.out.println(getResultStr("1,0,2,3",2));
    }*/
}