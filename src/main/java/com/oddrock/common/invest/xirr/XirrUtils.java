package com.oddrock.common.invest.xirr;

import java.util.ArrayList;
import java.util.List;

/**
 * XIRR年化收益计算公式
 * @author oddrock
 *
 */
public class XirrUtils {
	public static double computeXirr(List<String> dateStrLst, List<Double> moneyLst) {
		ArrayList<UpbaaDate> list = new ArrayList<UpbaaDate>();
		for(int i=0;i<dateStrLst.size();i++){
			list.add(new UpbaaDate(dateStrLst.get(i),moneyLst.get(i)));
		}
		double xirr = new XirrData(list).getXirr();
		return xirr;
	}
	
	//准备测试数据--日期
	private static List<String> getDates(){
		List<String> list = new ArrayList<String>();
		list.add("2021-05-17");
		list.add("2021-05-19");
		list.add("2021-05-24");
		list.add("2021-05-31");
		return list;
	}
	
	//准备测试数据--现金流(先负 后正)
	private static List<Double> getCashFlow(){
		List<Double> list = new ArrayList<Double>();
		list.add(-265.10);
		list.add(-287.80);
		list.add(-259.80);
		list.add(842.62);
		return list;
	}
	
	public static void main(String[] args) {
		System.out.println(computeXirr(getDates(), getCashFlow()));
	}
}
