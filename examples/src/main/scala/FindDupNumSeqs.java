import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class FindDupNumSeqs {
			
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
            
    public static Map<String,Object> findLongestDupString(String s){
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
            	System.out.println("sub"+count+++"="+sub);
            	s=sub;
			}
        	///System.out.println("\n");
        }
        System.out.println("构造出来的排序后是后缀数组长度为："+postfix.size()+"，内容为："+postfix);
        
        List<String> postfixList = new ArrayList<String>(postfix);//set转换成list方便调用
        //Set<String> dupStrSet = new HashSet<String>();
        
        //key是目标字符串，value是重复次数。注意重复次数=出现次数-1。
        Map<String, Integer> dupStrMap = new HashMap<String, Integer>();
        //key是目标字符串，value是出现次数次数。注意出现次数=重复次数+1。
        Map<String, Integer> numCountMap = new HashMap<String, Integer>();
        
        //key是目标字符串，value是这个字符串中的最大的id。按照目前的方式就是第一个逗号之前的那一部分
        Map<String, Integer> maxIdMap = new HashMap<String, Integer>();
        
        int temp=0;
        String maxDupStr="";
        int postfixListSize=postfixList.size();
        for(int i = 0; i < postfixListSize-1; i++){//循环，找出最长的重复子序列下标，最长的重复子序列截取位置
        	String str1=postfixList.get(i);
        	String str2=postfixList.get(i+1);
            System.out.println("第"+i+"轮比较：str1="+str1+";str2="+str2);
            int maxCommonlength = maxCommonlength(str1, str2);//获取后缀数组两两比较的相同子序列长度            
            if (maxCommonlength>0) {
            	 String currentResult=getResultStr(postfixList.get(i),maxCommonlength);
            	 System.out.println("第"+i+"轮比较结果：maxCommonlength="+maxCommonlength+";str1="+str1+",str2="+str2);
            	 System.out.println("第"+i+"轮比较结果：currentResult="+currentResult);
                if(maxCommonlength > temp){
                    temp = maxCommonlength;
                    maxDupStr=currentResult;
                    System.out.println("第"+i+"轮比较，更新maxDupStr="+maxDupStr);
                }
                
                if (currentResult.contains(",")) {//如果只是单个的id字符串则不用记录
                    if (dupStrMap.get(currentResult)==null) {
                    	dupStrMap.put(currentResult, 1);
                    	numCountMap.put(currentResult, 2);
                    	maxIdMap.put(currentResult, Integer.valueOf(currentResult.substring(0, currentResult.indexOf(","))));
                    	System.out.println("第"+i+"轮比较，新发现currentResult="+currentResult);
    				}else {
    					dupStrMap.put(currentResult, dupStrMap.get(currentResult)+1);
    					numCountMap.put(currentResult, numCountMap.get(currentResult)+1);
    					System.out.println("第"+i+"轮比较，currentResult的次数加一变为"+dupStrMap.get(currentResult));
    				}
                    

				}
                               
/*                if (!dupStrSet.contains(currentResult)) {
                    dupStrSet.add(currentResult);
                    System.out.println("第"+i+"轮比较，将currentResult="+currentResult+"加入到结果集中");
				}*/
			}
        }
        //resultMap.put("dupStrSet", dupStrSet);
        resultMap.put("numCountMap", numCountMap);
        resultMap.put("dupStrMap", dupStrMap);
        resultMap.put("maxDupStr", maxDupStr);
        resultMap.put("maxIdMap", maxIdMap);
		resultMap.put("recoSeqIdMap", processLongestDupMap(resultMap));
        return resultMap;
    }
    
    public static Map<String,Object> processLongestDupMap(Map<String,Object> resultMap){
    	Map<String,Object> processedResultMap=new HashMap<String,Object>();
    	Map<String, Integer> numCountMap = (HashMap<String,Integer>)resultMap.get("numCountMap");
    	String maxDupStr= (String)resultMap.get("maxDupStr");
    	processedResultMap.put(maxDupStr,numCountMap.get(maxDupStr));
    	for (Entry<String, Integer> entry : numCountMap.entrySet()) {
    		//如果结果串在最长的重复子串中，而且出现的次数和最长的重复子串相同，则不用单独再返回。意味着它也出现在最长的重复子串的那个路径中
    		if (maxDupStr.contains(entry.getKey())&&numCountMap.get(maxDupStr).equals(entry.getValue())) {
				continue;
			} else {
				processedResultMap.put(entry.getKey(), entry.getValue());
			}
    	}    	
    	return processedResultMap;
    }
    
    /**
     * 下面这个是第一版的，有bug，如下：
     * str1=10,1,0;str2=100,97,96
     * maxCommonlength=2
     * */
    private static int maxCommonlengthBug(String str1, String str2) {
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
        
    //str1=90;str2=90,102,91,90,91,90
    private static int maxCommonlength(String str1, String str2) {
    	String[] list1=str1.split(",");
    	String[] list2=str2.split(",");       
        int maxlen = 0;
        for(int i = 0; i < (list1.length > list2.length ? list2.length : list1.length); i++){
            if(Integer.valueOf(list1[i]) == Integer.valueOf(list2[i])){
            	 maxlen++;
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
    public static void main(String[] args){
    	//String str="100,11,10,9,8,7,6,5,4,3,2,1,0,8,7,6,5,4,3,2,1,0,3,2,1,0";
    	//String str="6,5,4,3,2,1,0,7,6,5,4,3,2,1,0";
    	///String str="4,3,2,1,0,5,3,2,1,0";
    	//String str="5,4,3,2,1,0,11,10,9,8,7,6,17,16,15,14,13,10,9,8,7,6,12,10,9,8,7,6,21,20,19,18,15,14,13,10,9,8,7,6,12,10,9,8,7,6,25,24,23,22,15,14,13,10,9,8,7,6,12,10,9,8,7,6,29,28,27,26,15,14,13,10,9,8,7,6,12,10,9,8,7,6,33,32,31,30,15,14,13,10,9,8,7,6,12,10,9,8,7,6,37,36,35,34,15,14,13,10,9,8,7,6,12,10,9,8,7,6,41,40,39,38,15,14,13,10,9,8,7,6,12,10,9,8,7,6,45,44,43,42,15,14,13,10,9,8,7,6,12,10,9,8,7,6,49,48,47,46,15,14,13,10,9,8,7,6,12,10,9,8,7,6,53,52,51,50,15,14,13,10,9,8,7,6,12,10,9,8,7,6,57,56,55,54,15,14,13,10,9,8,7,6,12,10,9,8,7,6,61,60,59,58,15,14,13,10,9,8,7,6,12,10,9,8,7,6,65,64,63,62,15,14,13,10,9,8,7,6,12,10,9,8,7,6,69,68,67,66,15,14,13,10,9,8,7,6,12,10,9,8,7,6,73,72,71,70,15,14,13,10,9,8,7,6,12,10,9,8,7,6,77,76,75,74,15,14,13,10,9,8,7,6,12,10,9,8,7,6,81,80,79,78,15,14,13,10,9,8,7,6,12,10,9,8,7,6,85,84,83,82,15,14,13,10,9,8,7,6,12,10,9,8,7,6,89,88,87,86,15,14,13,10,9,8,7,6,12,10,9,8,7,6,93,92,91,90,15,14,13,10,9,8,7,6,12,10,9,8,7,6,97,96,95,94,15,14,13,10,9,8,7,6,12,10,9,8,7,6,107,106,105,104,103,102,101";
    	
    	//String str="10,1,0,11,7,6,5,4,1,0,22,19,18,17,16,13,12,19,18,17,16,13,12,24,13,12,13,12,36,33,32,31,30,27,26,37,27,26,48,45,44,43,42,39,38,45,44,43,42,39,38,50,39,38,39,38,62,59,58,57,56,53,52,63,53,52,74,71,70,69,68,65,64,71,70,69,68,65,64,76,65,64,65,64,88,85,84,83,82,79,78,89,79,78,100,97,96,95,94,91,90,97,96,95,94,91,90,102,91,90,91,90";
		String str="3,2,1,0,9,8,7,6,5,2,1,0,4,2,1,0,13,12,11,10,7,6,5,2,1,0,4,2,1,0,17,16,15,14,7,6,5,2,1,0,4,2,1,0,21,20,19,18,7,6,5,2,1,0,4,2,1,0,25,24,23,22,7,6,5,2,1,0,4,2,1,0,29,28,27,26,7,6,5,2,1,0,4,2,1,0,33,32,31,30,7,6,5,2,1,0,4,2,1,0,37,36,35,34,7,6,5,2,1,0,4,2,1,0,41,40,39,38,7,6,5,2,1,0,4,2,1,0,45,44,43,42,7,6,5,2,1,0,4,2,1,0,49,48,47,46,7,6,5,2,1,0,4,2,1,0,53,52,51,50,7,6,5,2,1,0,4,2,1,0,57,56,55,54,7,6,5,2,1,0,4,2,1,0,61,60,59,58,7,6,5,2,1,0,4,2,1,0,65,64,63,62,7,6,5,2,1,0,4,2,1,0,69,68,67,66,7,6,5,2,1,0,4,2,1,0,73,72,71,70,7,6,5,2,1,0,4,2,1,0,77,76,75,74,7,6,5,2,1,0,4,2,1,0,81,80,79,78,7,6,5,2,1,0,4,2,1,0,85,84,83,82,7,6,5,2,1,0,4,2,1,0,89,88,87,86,7,6,5,2,1,0,4,2,1,0,96,95,94,93,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,100,99,98,97,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,104,103,102,101,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,108,107,106,105,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,112,111,110,109,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,116,115,114,113,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,120,119,118,117,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,124,123,122,121,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,128,127,126,125,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,132,131,130,129,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,136,135,134,133,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,140,139,138,137,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,144,143,142,141,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,148,147,146,145,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,152,151,150,149,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,156,155,154,153,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,160,159,158,157,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,164,163,162,161,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,168,167,166,165,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,172,171,170,169,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,179,178,177,176,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,183,182,181,180,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,187,186,185,184,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,191,190,189,188,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,195,194,193,192,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,199,198,197,196,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,203,202,201,200,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,207,206,205,204,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,211,210,209,208,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,215,214,213,212,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,219,218,217,216,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,223,222,221,220,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,227,226,225,224,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,231,230,229,228,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,235,234,233,232,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,239,238,237,236,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,243,242,241,240,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,247,246,245,244,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,251,250,249,248,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,255,254,253,252,175,174,173,92,91,90,7,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,6,5,2,1,0,4,2,1,0,259,2,1,0";

    	System.out.println("原始输入串："+str);
    	//printResultList(findDup(str));  	
    	
    	Map<String,Object> resultMap= findLongestDupString(str);
    	System.out.println("计算结果："+resultMap);
    	System.out.println("最后返回的结果："+processLongestDupMap(resultMap));
		String recoSeq=resultMap.get("recoSeqIdMap").toString();
		System.out.println("recoSeq："+recoSeq);


    	//System.out.println(getResultStr("1",1));
    	//System.out.println(getResultStr("1,0",2));
    	//System.out.println(getResultStr("1,0,2,3",2));    	    	
    }
}