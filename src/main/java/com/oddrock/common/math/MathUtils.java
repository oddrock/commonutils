package com.oddrock.common.math;

import java.util.Random;
import java.util.regex.Pattern;

public class MathUtils {
	private static final Pattern pattern = Pattern.compile("[0-9]*"); 
	
	/**
	 * 检查字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
	    return pattern.matcher(str).matches();    
	}
	
	public static Double round(Double input, int reservedDigit) {
		int x = 1;
		for(int i=0;i<reservedDigit;i++) {
			x = x * 10;
		}
		return (double) Math.round(input * x) / x;		
	}
	
	/**
	 * 生成指定范围随机数
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(int min, int max){
	    Random random = new Random();
	    int s = random.nextInt(max) % (max - min + 1) + min;
	    return s;
	 
	}
	
	//方差s^2=[(x1-x)^2 +...(xn-x)^2]/n 或者s^2=[(x1-x)^2 +...(xn-x)^2]/(n-1)
	/**
	 * 计算方差
	 * @param x
	 * @return
	 */
    public static double variance(double[] x) {
        int m=x.length;
        double sum=0;
        for(int i=0;i<m;i++){//求和
            sum+=x[i];
        }
        double dAve=sum/m;//求平均值
        double dVar=0;
        for(int i=0;i<m;i++){//求方差
            dVar+=(x[i]-dAve)*(x[i]-dAve);
        }
        return dVar/m;
    }

    //标准差σ=sqrt(s^2)
    /**
     * 计算标差
     * @param x
     * @return
     */
    public static double standardDiviation(double[] x) {
        return Math.sqrt(variance(x));
    }
    
    /**
     * 计算整数数组的标差
     * @param x
     * @return
     */
    public static double standardDiviation(int[] x) {
    	double[] y = new double[x.length];
    	for(int i=0; i<x.length; i++) {
    		y[i] = Double.valueOf(x[i]);
    	}
        return Math.sqrt(variance(y));
    }
    
    public static boolean isEqualDouble(Double a, Double b) {
    	return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }
	
	public static void main(String[] args) {
		//System.out.println(round(3049.8757,2));
		//System.out.println(round(2940.1916,2));
		Double a = 0.009;
		Double b = 0.009;
		System.out.println(a==b);
		System.out.println(isEqualDouble(a,b));
	}
}
